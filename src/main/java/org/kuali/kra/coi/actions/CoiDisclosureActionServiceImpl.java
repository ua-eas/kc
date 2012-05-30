/*
 * Copyright 2005-2010 The Kuali Foundation
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
package org.kuali.kra.coi.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kra.bo.CoeusModule;
import org.kuali.kra.bo.KraPersistableBusinessObjectBase;
import org.kuali.kra.coi.CoiDiscDetail;
import org.kuali.kra.coi.CoiDisclProject;
import org.kuali.kra.coi.CoiDisclosure;
import org.kuali.kra.coi.CoiDisclosureDocument;
import org.kuali.kra.coi.CoiDisclosureForm;
import org.kuali.kra.coi.CoiDisclosureHistory;
import org.kuali.kra.coi.CoiDisclosureStatus;
import org.kuali.kra.coi.CoiUserRole;
import org.kuali.kra.coi.certification.SubmitDisclosureAction;
import org.kuali.kra.coi.disclosure.MasterDisclosureBean;
import org.kuali.kra.coi.notesandattachments.attachments.CoiDisclosureAttachment;
import org.kuali.kra.coi.notesandattachments.notes.CoiDisclosureNotepad;
import org.kuali.kra.coi.notification.CoiNotificationContext;
import org.kuali.kra.coi.notification.DisclosureCertifiedNotificationRenderer;
import org.kuali.kra.coi.notification.DisclosureCertifiedNotificationRequestBean;
import org.kuali.kra.common.notification.service.KcNotificationService;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.questionnaire.answer.Answer;
import org.kuali.kra.questionnaire.answer.AnswerHeader;
import org.kuali.kra.questionnaire.answer.QuestionnaireAnswerService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * 
 * This class implement methods defined in CoiDisclosureActionService.
 * This is mostly for disclosure actions page.
 */
public class CoiDisclosureActionServiceImpl implements CoiDisclosureActionService {

    private BusinessObjectService businessObjectService;
    private DocumentService documentService;
    private KcNotificationService kcNotificationService;
    private static final Log LOG = LogFactory.getLog(CoiDisclosureActionServiceImpl.class);
    private static final String PROTOCOL_TAB = "protocol";
    private QuestionnaireAnswerService questionnaireAnswerService;
    private static final String MODULE_ITEM_CODE = "moduleItemCode";
    private static final String MODULE_ITEM_KEY = "moduleItemKey";
    private static final String MODULE_SUB_ITEM_KEY = "moduleSubItemKey";

    /**
     * copy disc details from previous master disclosure if it exists.
     * create a disclosure history methods.
     * set current disclosure flag for this approved disclosure, and reset it for previous mater disclosure
     * @throws WorkflowException 
     * @see org.kuali.kra.coi.actions.CoiDisclosureActionService#approveDisclosure(org.kuali.kra.coi.CoiDisclosure, java.lang.String)
     */
    public void approveDisclosure(CoiDisclosure coiDisclosure, String coiDispositionCode) throws WorkflowException {
        CoiDisclosure masterCoiDisclosure = getMasterDisclosure(coiDisclosure.getCoiDisclosureNumber());
        List<KraPersistableBusinessObjectBase> disclosures = new ArrayList<KraPersistableBusinessObjectBase>();
        coiDisclosure.setDisclosureDispositionCode(coiDispositionCode);
        coiDisclosure.setDisclosureStatusCode(CoiDisclosureStatus.APPROVED);
        disclosures.add(coiDisclosure);
     
        if (masterCoiDisclosure != null) {
            if (!coiDisclosure.isUpdateEvent()) {
                if (!(coiDisclosure.isAnnualEvent() && coiDisclosure.isAnnualUpdate())) {
                     copyCollections(masterCoiDisclosure, coiDisclosure);
                }
            }
            masterCoiDisclosure.setCurrentDisclosure(false);
//            coiDisclosure.setCurrentDisclosure(true);
            disclosures.add(masterCoiDisclosure);

        } 
        coiDisclosure.setCurrentDisclosure(true);
        documentService.approveDocument(coiDisclosure.getCoiDisclosureDocument(), "Document approved.", new ArrayList<AdHocRouteRecipient>());
            
        disclosures.add(createDisclosureHistory(coiDisclosure));
        businessObjectService.save(disclosures);
        
    }
    
    /**
     * This disapproves the document and sets the disclosure and disposition statuses.
     * @see org.kuali.kra.coi.actions.CoiDisclosureActionService#disapproveDisclosure(org.kuali.kra.coi.CoiDisclosure, java.lang.String)
     */
    public void disapproveDisclosure(CoiDisclosure coiDisclosure, String coiDispositionCode) throws Exception {
        CoiDisclosure masterCoiDisclosure = getMasterDisclosure(coiDisclosure.getCoiDisclosureNumber());
        
        coiDisclosure.setDisclosureDispositionCode(coiDispositionCode);
        coiDisclosure.setDisclosureStatusCode(CoiDisclosureStatus.DISAPPROVED);
      
        businessObjectService.save(coiDisclosure);
        businessObjectService.save(createDisclosureHistory(coiDisclosure));
        documentService.disapproveDocument(coiDisclosure.getCoiDisclosureDocument(), "Document approved.");       
    }

    /**
     * this only changes the disclosure status and the disposition status, no routing
     * @see org.kuali.kra.coi.actions.CoiDisclosureActionService#setStatus(org.kuali.kra.coi.CoiDisclosure, java.lang.String)
     */
    public void setStatus(CoiDisclosure coiDisclosure, String coiDispositionCode) {
        coiDisclosure.setDisclosureDispositionCode(coiDispositionCode);
        coiDisclosure.setDisclosureStatusCode(CoiDisclosureStatus.ROUTED_FOR_REVIEW);
        coiDisclosure.refreshReferenceObject("coiDispositionStatus");
        coiDisclosure.refreshReferenceObject("coiDisclosureStatus");
        businessObjectService.save(coiDisclosure);
    }

    public KcNotificationService getKcNotificationService() {
        return kcNotificationService;
    }

    public void setKcNotificationService(KcNotificationService kcNotificationService) {
        this.kcNotificationService = kcNotificationService;
    }

    public void addCoiUserRole(CoiDisclosure coiDisclosure, CoiUserRole coiUserRole) {
        coiDisclosure.getCoiUserRoles().add(coiUserRole);
        businessObjectService.save(coiDisclosure);
    }
    
    public void deleteCoiUserRole(CoiDisclosure coiDisclosure, int index) {
        if (index >= 0 && index < coiDisclosure.getCoiUserRoles().size()) {
            coiDisclosure.getCoiUserRoles().remove(index);
            
            businessObjectService.save(coiDisclosure);
        }
    }

    /*
     * retrieve current master disclosure
     */
    @SuppressWarnings("unchecked")
    private CoiDisclosure getMasterDisclosure(String coiDisclosureNumber) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put("coiDisclosureNumber", coiDisclosureNumber);
        fieldValues.put("currentDisclosure", "Y");

        List<CoiDisclosure> disclosures = (List<CoiDisclosure>)businessObjectService.findMatching(CoiDisclosure.class, fieldValues);
        if (CollectionUtils.isNotEmpty(disclosures)) {
            return disclosures.get(0);
        } else {
            return null;
        }
    }
    
    private void  copyCollections(CoiDisclosure masterCoiDisclosure, CoiDisclosure coiDisclosure) {
        
        copyDisclosureProjects(masterCoiDisclosure, coiDisclosure);
        copyDisclosureNotePads(masterCoiDisclosure, coiDisclosure);
        copyDisclosureAttachments(masterCoiDisclosure, coiDisclosure);
        copyDisclosureQuestionnaire(masterCoiDisclosure, coiDisclosure);
    }
    
    private void copyDisclosureQuestionnaire(CoiDisclosure masterCoiDisclosure, CoiDisclosure coiDisclosure) {
        // versioning questionnaire answer
//        if (masterCoiDisclosure.getCoiDisclosureDocument().getDocumentHeader().getWorkflowDocument() == null) {
            try {
            CoiDisclosureDocument coiDisclosureDocument = (CoiDisclosureDocument) KraServiceLocator
                    .getService(DocumentService.class).getByDocumentHeaderId(masterCoiDisclosure.getCoiDisclosureDocument().getDocumentNumber());
            // Get XML of workflow document
            masterCoiDisclosure.setCoiDisclosureDocument(coiDisclosureDocument);
            } catch (Exception e) {
                
            }
//        }

        //    List<AnswerHeader> newAnswerHeaders = questionnaireAnswerService.versioningQuestionnaireAnswer(new DisclosureModuleQuestionnaireBean(masterCoiDisclosure)
        //    , coiDisclosure.getSequenceNumber());
            List<AnswerHeader> newAnswerHeaders = versioningQuestionnaireAnswer(masterCoiDisclosure);
         if (!newAnswerHeaders.isEmpty()) {
             for (AnswerHeader answerHeader : newAnswerHeaders) {
                 if (answerHeader.getOriginalCoiDisclosureId() == null) {
                     answerHeader.setOriginalCoiDisclosureId(masterCoiDisclosure.getCoiDisclosureId());
                 }                 
                 answerHeader.setModuleSubItemKey(coiDisclosure.getSequenceNumber().toString());
             }
            businessObjectService.save(newAnswerHeaders);
        }
    
    }
    
    private List<AnswerHeader> versioningQuestionnaireAnswer(CoiDisclosure coiDisclosure) {
        List<AnswerHeader> newAnswerHeaders = new ArrayList<AnswerHeader>();
        for (AnswerHeader answerHeader : retrieveAnswerHeaders(coiDisclosure)) {
               AnswerHeader copiedAnswerHeader = (AnswerHeader) ObjectUtils.deepCopy(answerHeader);
//                copiedAnswerHeader.setModuleSubItemKey(coiDisclosure.getSequenceNumber().toString());
                copiedAnswerHeader.setAnswerHeaderId(null);
                for (Answer answer : copiedAnswerHeader.getAnswers()) {
                    answer.setId(null);
                }
                newAnswerHeaders.add(copiedAnswerHeader);
        }
        return newAnswerHeaders;
    }

    private List<AnswerHeader> retrieveAnswerHeaders(CoiDisclosure coiDisclosure) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(MODULE_ITEM_CODE, CoeusModule.COI_DISCLOSURE_MODULE_CODE);
        fieldValues.put(MODULE_ITEM_KEY, coiDisclosure.getCoiDisclosureNumber());
        fieldValues.put(MODULE_SUB_ITEM_KEY, coiDisclosure.getSequenceNumber());
        return (List<AnswerHeader>) businessObjectService.findMatching(AnswerHeader.class, fieldValues);
    }
    

    /*
     * copy disclosure details of current master disclosure to the disclosure that is bing approved
     * because the current disclosure becomes the master.
     */
    /*
    private void copyDisclosureDetails(CoiDisclosure masterCoiDisclosure, CoiDisclosure coiDisclosure) {
        for (CoiDiscDetail coiDiscDetail : masterCoiDisclosure.getCoiDiscDetails()) {
            if (!isDisclosureDetailExist(coiDisclosure, coiDiscDetail)) {
                CoiDiscDetail copiedDiscDetail = (CoiDiscDetail) ObjectUtils.deepCopy(coiDiscDetail);
                copiedDiscDetail.setCopiedCoiDiscDetailId(copiedDiscDetail.getCoiDiscDetailId());
                copiedDiscDetail.setSequenceNumber(coiDisclosure.getSequenceNumber());
                copiedDiscDetail.setCoiDiscDetailId(null);
                if (copiedDiscDetail.getOriginalCoiDisclosureId() == null) {
                    copiedDiscDetail.setOriginalCoiDisclosureId(masterCoiDisclosure.getCoiDisclosureId());
                }
                coiDisclosure.getCoiDiscDetails().add(copiedDiscDetail);
            }
        }
    }
    */
    
    private void copyDisclosureDetails(List<CoiDiscDetail> originalDiscDetails, CoiDisclProject copiedDisclProject) {
        List<CoiDiscDetail> copiedDiscDetails = new ArrayList<CoiDiscDetail>();
        for (CoiDiscDetail coiDiscDetail : originalDiscDetails) {
            CoiDiscDetail copiedDiscDetail = (CoiDiscDetail) ObjectUtils.deepCopy(coiDiscDetail);
            copiedDiscDetail.setCopiedCoiDiscDetailId(copiedDiscDetail.getCoiDiscDetailId());
            copiedDiscDetail.setSequenceNumber(copiedDisclProject.getSequenceNumber());
            copiedDiscDetail.setCoiDiscDetailId(null);
            copiedDiscDetail.setCoiDisclProjectId(null);
            if (copiedDiscDetail.getOriginalCoiDisclosureId() == null) {
                copiedDiscDetail.setOriginalCoiDisclosureId(copiedDisclProject.getCoiDisclosureId());
            }
            copiedDiscDetails.add(copiedDiscDetail);
        }
        copiedDisclProject.setCoiDiscDetails(copiedDiscDetails);
        if (copiedDisclProject.getOriginalCoiDisclosureId() == null) {
        copiedDisclProject.setOriginalCoiDisclosureId(copiedDisclProject.getCoiDisclosureId());
        }
    }
    
    //TODO: finish project copy and work in subsequent details gettting copied
    private void copyDisclosureProjects(CoiDisclosure masterCoiDisclosure, CoiDisclosure coiDisclosure) {
        List<CoiDisclProject> copiedDisclProjects = new ArrayList<CoiDisclProject>();
        for (CoiDisclProject coiDisclProject : masterCoiDisclosure.getCoiDisclProjects()) {
//            if (!coiDisclProject.getCoiDisclosureEventType().isExcludeFromMasterDisclosure()) {
                List<CoiDiscDetail> coiDiscDetails = coiDisclProject.getCoiDiscDetails();
                // coiDisclProject.setCoiDiscDetails(null);
                CoiDisclProject copiedDisclProject = (CoiDisclProject) ObjectUtils.deepCopy(coiDisclProject);
                copiedDisclProject.setSequenceNumber(coiDisclosure.getSequenceNumber());
                copiedDisclProject.setCoiDisclProjectsId(null);

                // copy disc details
                copyDisclosureDetails(coiDiscDetails, copiedDisclProject);
                copiedDisclProjects.add(copiedDisclProject);
                copiedDisclProject.setCoiDisclosureId(null);
//            }
        }
        coiDisclosure.getCoiDisclProjects().addAll(copiedDisclProjects);
    }

    private void copyDisclosureNotePads(CoiDisclosure masterCoiDisclosure, CoiDisclosure coiDisclosure) {
        // may also need to add note/attachment to new master disclosure
//        CoiDisclosure copiedDisclosure = (CoiDisclosure) ObjectUtils.deepCopy(masterCoiDisclosure);
        for (CoiDisclosureNotepad coiDisclosureNotepad : masterCoiDisclosure.getCoiDisclosureNotepads()) {
//            if (!isDisclosureNotePadExist(coiDisclosure, coiDisclosureNotepad)) {
                // TODO implement the if check when originaldisclosureid is added to notepad
                CoiDisclosureNotepad copiedCoiDisclosureNotepad = (CoiDisclosureNotepad) ObjectUtils.deepCopy(coiDisclosureNotepad);
                copiedCoiDisclosureNotepad.setSequenceNumber(coiDisclosure.getSequenceNumber());
                copiedCoiDisclosureNotepad.setId(null);
                if (copiedCoiDisclosureNotepad.getOriginalCoiDisclosureId() == null) {
                    copiedCoiDisclosureNotepad.setOriginalCoiDisclosureId(masterCoiDisclosure.getCoiDisclosureId());
                }
                coiDisclosure.getCoiDisclosureNotepads().add(copiedCoiDisclosureNotepad);
//            }
        }
    }

    private void copyDisclosureAttachments(CoiDisclosure masterCoiDisclosure, CoiDisclosure coiDisclosure) {
        // may also need to add note/attachment to new master disclosure
//        CoiDisclosure copiedDisclosure = (CoiDisclosure) ObjectUtils.deepCopy(masterCoiDisclosure);
        for (CoiDisclosureAttachment coiDisclosureAttachment : masterCoiDisclosure.getCoiDisclosureAttachments()) {
//            if (!isDisclosureNotePadExist(coiDisclosure, coiDisclosureNotepad)) {
                // TODO implement the if check when originaldisclosureid is added to notepad
                CoiDisclosureAttachment copiedCoiDisclosureAttachment = (CoiDisclosureAttachment) ObjectUtils.deepCopy(coiDisclosureAttachment);
                copiedCoiDisclosureAttachment.setSequenceNumber(coiDisclosure.getSequenceNumber());
                copiedCoiDisclosureAttachment.setAttachmentId(null);
                copiedCoiDisclosureAttachment.setFile(coiDisclosureAttachment.getFile());
                copiedCoiDisclosureAttachment.setFileId(coiDisclosureAttachment.getFileId());
                if (copiedCoiDisclosureAttachment.getOriginalCoiDisclosureId() == null) {
                    copiedCoiDisclosureAttachment.setOriginalCoiDisclosureId(masterCoiDisclosure.getCoiDisclosureId());
                }
                coiDisclosure.getCoiDisclosureAttachments().add(copiedCoiDisclosureAttachment);
//            }
        }
    }

    /*
     * check if disclosure detail is exist in the disclosure being approved
     * if it is, then there is no need to copy over.
     */
    private boolean isDisclosureDetailExist(CoiDisclosure coiDisclosure,CoiDiscDetail coiDiscDetail) {
        boolean isExist = false;
        for (CoiDisclProject disclProject : coiDisclosure.getCoiDisclProjects()) {
            for (CoiDiscDetail discDetail : disclProject.getCoiDiscDetails()) {
                if (StringUtils.equals(discDetail.getProjectType(), coiDiscDetail.getProjectType()) && StringUtils.equals(discDetail.getProjectIdFk(), coiDiscDetail.getProjectIdFk()) && discDetail.getPersonFinIntDisclosureId().equals(coiDiscDetail.getPersonFinIntDisclosureId())) {
                    isExist = true;
                    break;
                }
            }
        }
        return isExist;
    }
    
    /*
     * create a disclosure history record for the disclosure being approved
     */
    private CoiDisclosureHistory createDisclosureHistory(CoiDisclosure coiDisclosure) {
        CoiDisclosureHistory coiDisclosureHistory = new CoiDisclosureHistory();
        coiDisclosureHistory.setCoiDisclosureId(coiDisclosure.getCoiDisclosureId());
        coiDisclosureHistory.setCoiDisclosureNumber(coiDisclosure.getCoiDisclosureNumber());
        coiDisclosureHistory.setSequenceNumber(coiDisclosure.getSequenceNumber());
        coiDisclosureHistory.setDisclosureDispositionStatus(coiDisclosure.getDisclosureDispositionCode());
        coiDisclosureHistory.setDisclosureStatus(coiDisclosure.getDisclosureStatusCode());
        return coiDisclosureHistory;

    }

    /**
     * This method submits a disclosure to workflow
     * @param coiDisclosure
     * @param submitDisclosureAction
     */
     public void submitToWorkflow(CoiDisclosureDocument coiDisclosureDocument, CoiDisclosureForm coiDisclosureForm, SubmitDisclosureAction submitDisclosureAction) {
        try {
            documentService.routeDocument(coiDisclosureDocument, "Disclosure has been certified and submitted.", new ArrayList<AdHocRouteRecipient>());
        } catch (WorkflowException e) {
            String errorString = "WorkflowException certifying Disclosure for user col %s" + coiDisclosureDocument.getCoiDisclosure().getAuthorPersonName(); 
            LOG.error(errorString, e);
            throw new RuntimeException(errorString, e);
        }
    }

    public ActionForward sendCertificationNotifications(CoiDisclosureDocument coiDisclosureDocument, CoiDisclosureForm coiDisclosureForm, SubmitDisclosureAction submitDisclosureAction, ActionMapping mapping) {
        DisclosureCertifiedNotificationRenderer renderer = new DisclosureCertifiedNotificationRenderer(coiDisclosureDocument.getCoiDisclosure(), CoiDisclosure.CERTIFIED);
        DisclosureCertifiedNotificationRequestBean disclosureCertifiedNotificationBean = getDisclosureCertifiedRequestBean(coiDisclosureDocument.getCoiDisclosure(), submitDisclosureAction.getReviewers());
        
        CoiNotificationContext context = new CoiNotificationContext(coiDisclosureDocument.getCoiDisclosure(), 
                                                                    disclosureCertifiedNotificationBean.getActionType(), 
                                                                    disclosureCertifiedNotificationBean.getDescription(), renderer);
//        if (coiDisclosureForm.getNotificationHelper().getPromptUserForNotificationEditor(context)) {
            return checkToSendNotification(mapping, mapping.findForward(PROTOCOL_TAB), coiDisclosureForm, renderer, context, disclosureCertifiedNotificationBean);
//        }
//        return null;
    }
    
    private DisclosureCertifiedNotificationRequestBean getDisclosureCertifiedRequestBean(CoiDisclosure coiDisclosure, List<CoiUserRole> userRoles) {
        DisclosureCertifiedNotificationRequestBean newBean = new DisclosureCertifiedNotificationRequestBean(coiDisclosure, userRoles);
        return newBean;
    }

    private ActionForward checkToSendNotification(ActionMapping mapping, ActionForward forward, CoiDisclosureForm coiDisclosureForm, 
                                                  DisclosureCertifiedNotificationRenderer renderer, CoiNotificationContext context, 
                                                  DisclosureCertifiedNotificationRequestBean notificationRequestBean) {
        
        if (coiDisclosureForm.getNotificationHelper().getPromptUserForNotificationEditor(context)) {
            coiDisclosureForm.getNotificationHelper().initializeDefaultValues(context);
            return mapping.findForward("protocolNotificationEditor");
        } else {
            getKcNotificationService().sendNotification(context);
            return null;
        }
    }
    
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setQuestionnaireAnswerService(QuestionnaireAnswerService questionnaireAnswerService) {
        this.questionnaireAnswerService = questionnaireAnswerService;
    }

}
