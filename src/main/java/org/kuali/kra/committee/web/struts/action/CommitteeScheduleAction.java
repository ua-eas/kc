/*
 * Copyright 2006-2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.committee.web.struts.action;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.kra.committee.bo.CommitteeSchedule;
import org.kuali.kra.committee.rule.event.AddCommitteeScheduleDateConflictEvent;
import org.kuali.kra.committee.rule.event.AddCommitteeScheduleStartAndEndDateEvent;
import org.kuali.kra.committee.rule.event.CommitteeScheduleEvent.Event;
import org.kuali.kra.committee.service.CommitteeScheduleService;
import org.kuali.kra.committee.web.struts.form.CommitteeForm;
import org.kuali.kra.committee.web.struts.form.schedule.ScheduleData;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;

public class CommitteeScheduleAction extends CommitteeAction {
    
    @SuppressWarnings("unused")
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CommitteeScheduleAction.class);
    
    private static final String DELETE_QUESTION = "Are you sure you want to delete?";
    
    private static final String DELETE_QUESTION_ID = "committeeSchedule.delete.question";
    
    /**
     * @see org.kuali.kra.committee.web.struts.action.CommitteeAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ActionForward actionForward = super.execute(mapping, form, request, response);
        
        ((CommitteeForm)form).getCommitteeScheduleHelper().prepareView();
        
        return actionForward;
    }    
    
    /**
     * @see org.kuali.kra.committee.web.struts.action.CommitteeAction#save(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {    
        
        ActionForward actionForward = mapping.findForward(Constants.MAPPING_BASIC);
        
        CommitteeForm committeeForm = (CommitteeForm) form;
        List<CommitteeSchedule> committeeSchedules = committeeForm.getCommitteeDocument().getCommittee().getCommitteeSchedules();
        
        if(applyRules(new AddCommitteeScheduleDateConflictEvent(Constants.EMPTY_STRING, committeeForm.getDocument(), null, committeeSchedules, Event.HARDERROR))){
            actionForward = super.save(mapping, form, request, response);
        }
        
        return actionForward;
    }    
    
    /**
     * This method us UI hook to add new schedules to list of schedules.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addEvent(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        CommitteeForm committeeForm = (CommitteeForm) form;
        ScheduleData scheduleData = committeeForm.getScheduleData();
        
        if(applyRules(new AddCommitteeScheduleStartAndEndDateEvent(Constants.EMPTY_STRING, committeeForm.getDocument(), scheduleData, null, Event.HARDERROR))){
            CommitteeScheduleService service  = getCommitteeScheduleService();
            service.addSchedule(scheduleData, committeeForm.getCommitteeDocument().getCommittee());
            
            applyRules(new AddCommitteeScheduleDateConflictEvent(Constants.EMPTY_STRING, committeeForm.getDocument(), scheduleData, null, Event.SOFTERROR));
        }        
        scheduleData.populateStyleClass();        
        return mapping.findForward(Constants.MAPPING_BASIC );
    }

    /**
     * This method is UI hook to delete CommitteeSchedule from list.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteCommitteeSchedule(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        CommitteeForm committeeForm = (CommitteeForm) form;          
        Object question = request.getParameter(KNSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String methodToCall = committeeForm.getMethodToCall();
        if (question == null) {
            return performQuestionWithoutInput(mapping, form, request, response, DELETE_QUESTION_ID, DELETE_QUESTION, KNSConstants.CONFIRMATION_QUESTION, methodToCall, "");
        }
        else {
            Object buttonClicked = request.getParameter(KNSConstants.QUESTION_CLICKED_BUTTON);
            if ((DELETE_QUESTION_ID.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {              
                int lineToDelete = getLineToDelete(request);
                committeeForm.getCommitteeDocument().getCommittee().getCommitteeSchedules().remove(lineToDelete);                   
            }
        }
        return mapping.findForward(Constants.MAPPING_BASIC );
    }    
    
    /**
     * This method is UI hook to filter dates in between start and end date.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward filterCommitteeScheduleDates(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        CommitteeForm committeeForm = (CommitteeForm) form;  
        ScheduleData scheduleData = committeeForm.getScheduleData();
        Date startDate = scheduleData.getFilterStartDate();
        Date endDate = scheduleData.getFilerEndDate();
        committeeForm.getCommitteeScheduleHelper().prepareFilterDatesView(startDate, endDate);
        return mapping.findForward(Constants.MAPPING_BASIC );
    }

    /**
     * This method is UI hook to reset filtered dates.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward resetCommitteeScheduleDates(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        CommitteeForm committeeForm = (CommitteeForm) form;        
        committeeForm.getCommitteeScheduleHelper().resetFilterDatesView();
        return mapping.findForward(Constants.MAPPING_BASIC );
    } 
    
    /**
     * This method is UI hook to load recurrence, in case javascript is turned off on browser.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadRecurrence(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CommitteeForm committeeForm = (CommitteeForm) form;
        committeeForm.getScheduleData().populateStyleClass();
        return mapping.findForward(Constants.MAPPING_BASIC );
    }  
    
    /**
     * This method retrieve CommitteeScheduleService.
     * @return
     */
    private CommitteeScheduleService getCommitteeScheduleService(){
        return KraServiceLocator.getService(CommitteeScheduleService.class);
    }
}
