/*
 * Kuali Coeus, a comprehensive research administration system for higher education.
 * 
 * Copyright 2005-2015 Kuali, Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.coeus.common.committee.impl.lookup;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.committee.impl.bo.CommitteeBase;
import org.kuali.coeus.common.committee.impl.bo.CommitteeMembershipBase;
import org.kuali.coeus.common.committee.impl.bo.CommitteeMembershipRole;
import org.kuali.coeus.common.committee.impl.bo.CommitteeResearchAreaBase;
import org.kuali.coeus.common.committee.impl.document.CommitteeDocumentBase;
import org.kuali.coeus.common.framework.auth.perm.KcAuthorizationService;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.irb.ResearchArea;
import org.kuali.kra.lookup.KraLookupableHelperServiceImpl;
import org.kuali.coeus.common.questionnaire.framework.question.Question;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.routeheader.service.RouteHeaderService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 
 * This class is for committee lookup.
 */
public abstract class CommitteeLookupableHelperServiceImplBase<CMT extends CommitteeBase<CMT, CD, ?>,
                                                           CD extends CommitteeDocumentBase<CD, CMT, ?>> 

                                                           extends KraLookupableHelperServiceImpl {

    private static final String COMMITTEE_TYPE_CODE_FIELD_NAME = "committeeTypeCode";
    private static final String PERSON_NAME = "personName";
    private static final String RESEARCH_AREA_CODE = "researchAreaCode";

    private Map<String, String> latestFinalCommitteeMap = new HashMap<>();

    private transient RouteHeaderService routeHeaderService;
    private transient KcAuthorizationService kcAuthorizationService;

    @Override
    public List<CMT> getSearchResults(Map<String, String> fieldValues) {
        // we set the lookup to only list committees of type chosen
        fieldValues.put(COMMITTEE_TYPE_CODE_FIELD_NAME, getCommitteeTypeCodeHook());

        @SuppressWarnings("unchecked")
        final List<CMT> results = (List<CMT>) super.getSearchResultsUnbounded(fieldValues);
        final List<CMT> unapprovedCommittees = getUnapprovedCommittees(fieldValues);
        final List<CMT> all = Stream.concat(results.stream(), unapprovedCommittees.stream()).collect(Collectors.toList());

        final List<CMT> activeCommittees = getUniqueList(all);
        latestFinalCommitteeMap = getLatestFinalDocumentNumber(all);
        final long matchingResultsCount = (long) activeCommittees.size();
        final Integer searchResultsLimit = LookupUtils.getSearchResultsLimit(Question.class);
        
        if (matchingResultsCount <= searchResultsLimit) {
            return new CollectionIncomplete<>(activeCommittees, 0L);
        } 
        else {
            return new CollectionIncomplete<>(trimResult(activeCommittees, searchResultsLimit), matchingResultsCount);
        }
    }

    
    protected abstract String getCommitteeTypeCodeHook();

    @Override
    public List<Row> getRows() {
        List<Row> rows =  super.getRows();
        for (Row row : rows) {
            for (Field field : row.getFields()) {
                if (field.getPropertyName().equals("committeeResearchAreas.researchAreaCode")) {
                    super.updateLookupField(field,RESEARCH_AREA_CODE,ResearchArea.class.getName());
                } else if (field.getPropertyName().equals("committeeMemberships.personName")) {
                    super.updateLookupField(field,PERSON_NAME, getCommitteeMembershipFullyQualifiedClassNameHook());
                }
            }
        }
        return rows;
    }

    protected abstract String getCommitteeMembershipFullyQualifiedClassNameHook();

    
    /*
     * remove duplicates and get only the one with the highest sequence number from the search results
     */
    protected List<CMT> getUniqueList(List<CMT> committees) {

        List<CMT> uniqueResults = new ArrayList<>();
        List<String> committeeIds = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(committees)) {
            Collections.sort(committees, Collections.reverseOrder());
            committees.stream().filter(committee -> !committeeIds.contains(committee.getCommitteeId())).forEach(committee -> {
                committee.getCommitteeChair();
                uniqueResults.add(committee);
                committeeIds.add(committee.getCommitteeId());
            });
        }
        return uniqueResults;
    }

    protected Map<String, String> getLatestFinalDocumentNumber(List<CMT> committees) {
         return committees.stream()
                 .filter(committee -> committee.getCommitteeDocument().getDocStatusCode().equals(KewApiConstants.ROUTE_HEADER_FINAL_CD))
             .collect(Collectors.groupingBy(committee -> committee.getCommitteeId())).entrySet().stream()
                 .collect(Collectors.toMap(Map.Entry::getKey, value -> value.getValue().stream()
                         .max(Comparator.comparingInt(committee -> committee.getSequenceNumber())).get().getCommitteeDocument().getDocumentNumber()));
    }

    @Override
    protected Properties getViewLinkProperties(Document document) {
        Properties p =  super.getViewLinkProperties(document);
        p.put("docId", latestFinalCommitteeMap.get(((CommitteeDocumentBase<?,?,?>)document).getCommitteeId()));
        return p;
    }

    protected abstract String getHtmlAction();
    protected abstract String getDocumentTypeName();
    protected abstract String getCustomResumeEditUrl(final String editCommitteeDocId);
    
    protected String getKeyFieldName() {
        return "committeeId";
    }
    
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> htmlDataList = new ArrayList<>();
        String editCommitteeDocId = getEditedCommitteeDocId((CMT) businessObject);
        boolean isUnappprovedCommittee = false;
        if (KewApiConstants.ROUTE_HEADER_SAVED_CD.equals((((CMT) businessObject).getCommitteeDocument().getDocStatusCode())) 
                && ((CMT) businessObject).getSequenceNumber() == 1) {
            isUnappprovedCommittee = true;
            editCommitteeDocId = ((CMT) businessObject).getCommitteeDocument().getDocumentNumber();
        }

        if (getKraAuthorizationService().hasPermission(getUserIdentifier(), (CMT) businessObject, getModifyCommitteePermissionNameHook())) {   
            htmlDataList = super.getCustomActionUrls(businessObject, pkNames);
            if (StringUtils.isNotBlank(editCommitteeDocId)) {
                AnchorHtmlData htmlData = (AnchorHtmlData) htmlDataList.get(0);
                htmlData.setHref(getCustomResumeEditUrl(editCommitteeDocId));
                htmlData.setDisplayText("resume edit");
            }
        }
        if (!isUnappprovedCommittee && getKraAuthorizationService().hasPermission(getUserIdentifier(), (CMT) businessObject, getViewCommitteePermissionNameHook())) {
            AnchorHtmlData htmlData = getViewLink(((CMT) businessObject).getCommitteeDocument());
            htmlData.setDisplayText("view active");
            htmlDataList.add(htmlData);
        }
        return htmlDataList;
    }

    protected abstract String getViewCommitteePermissionNameHook();

    protected abstract String getModifyCommitteePermissionNameHook();


    /*
     * get the document number of the committee that is being edited.
     */
    protected String getEditedCommitteeDocId(CMT committee) {
        String docId = null;
        List<CD> documents = getCommitteeDocuments(committee.getCommitteeId());
        if (CollectionUtils.isNotEmpty(documents)) {
            docId = documents.get(0).getDocumentNumber();
        }
        return docId;
    }
    
    /*
     * get saved committee documents of the committeeId specified.
     * should only have one if exists
     */
    protected List<CD> getCommitteeDocuments(String committeeId) {
        Map<String, String> fieldValues = new HashMap<>();
        fieldValues.put("committeeId", committeeId);
        List<CD> documents = (List<CD>) getBusinessObjectService().findMatching(getCommitteeDocumentBOClassHook(), fieldValues);
        return documents.stream().filter(commDoc -> KewApiConstants.ROUTE_HEADER_SAVED_CD.equals(commDoc.getDocStatusCode())).collect(Collectors.toList());
    }
    
    protected abstract Class<CD> getCommitteeDocumentBOClassHook();

    
    /*
     * Get the committee that is saved, but not approved yet.  basically this is sequence = 1
     */
    protected List<CMT> getUnapprovedCommittees(Map<String, String> criterias) {

        Map<String, String> fieldValues = new HashMap<>();
        fieldValues.put("docStatusCode", "S");

        List<CD> documents = (List<CD>) getBusinessObjectService().findMatching(getCommitteeDocumentBOClassHook(), fieldValues);
        List<CMT> result = new ArrayList<>();
        List<String> committeeIds = getCommitteeIds();
        // Get XML of workflow document
        // Create committee from XML and add to the document
        documents.stream().filter(commDoc -> !committeeIds.contains(commDoc.getCommitteeId())).forEach(commDoc -> {
            try {
                // Get XML of workflow document
                String content = getRouteHeaderService().getContent(
                        commDoc.getDocumentNumber()).getDocumentContent();

                // Create committee from XML and add to the document

                commDoc.getCommitteeList().add(populateCommitteeFromXmlDocumentContents(content));
                if (isCriteriaMatched(commDoc.getCommittee(), criterias)) {
                    commDoc.getCommittee().setCommitteeDocument(commDoc);
                    result.add(commDoc.getCommittee());
                }
            } catch (Exception e) {
                LOG.warn("CommitteeBase Doc " + commDoc.getDocumentNumber() + " parsing error", e);
            }
        });
        return result;

    }

    /*
     * This is to check the committee, which is not approved yet, matches the search criteria specified.
     * This is a new committee and not persisted to DB yet.  So, this tedious criteria check is needed.
     */
    protected boolean isCriteriaMatched(CMT committee, Map<String, String> criterias) {
        boolean isMatch = isMatching(criterias.get("committeeId"), committee.getCommitteeId())
                && isMatching(criterias.get("committeeName"), committee.getCommitteeName())
                && isMatching(criterias.get("homeUnitNumber"), committee.getHomeUnitNumber())
                && isMatching(criterias.get("committeeDescription"), committee.getCommitteeDescription())
                && isMatchingCode(criterias.get("committeeTypeCode"), committee.getCommitteeTypeCode())
                && isMatchingCode(criterias.get("reviewTypeCode"), committee.getReviewTypeCode());

        // membership check
        if (isMatch
                && (StringUtils.isNotBlank(criterias.get("committeeMemberships.personName")) || StringUtils.isNotBlank(criterias
                        .get("committeeMemberships.membershipRoles.membershipRoleCode")))) {
            if (CollectionUtils.isNotEmpty(committee.getCommitteeMemberships())) {
                if (StringUtils.isNotBlank(criterias.get("committeeMemberships.personName"))) {
                    isMatch = isMemberNameMatch(criterias.get("committeeMemberships.personName"), committee
                            .getCommitteeMemberships());

                }
                if (isMatch && StringUtils.isNotBlank(criterias.get("committeeMemberships.membershipRoles.membershipRoleCode"))) {
                    isMatch = isMemberRoleMatch(criterias.get("committeeMemberships.membershipRoles.membershipRoleCode"), committee
                            .getCommitteeMemberships());
                }
            } else {
                isMatch = false;
            }
        }

        // researchareacode check
        if (isMatch && (StringUtils.isNotBlank(criterias.get("committeeResearchAreas.researchAreaCode")))) {
            isMatch = CollectionUtils.isNotEmpty(committee.getCommitteeResearchAreas()) && isAreaResearchMatch(criterias.get("committeeResearchAreas.researchAreaCode"), committee.getCommitteeResearchAreas());
        }

        if (isMatch) {
            committee.refreshReferenceObject("homeUnit");
        }
        return isMatch;
    }
    
    /*
     * check if any member name matched the criteria
     */
    protected boolean isMemberNameMatch(String matchmember, List<CommitteeMembershipBase> members) {
        boolean isMatch = false;
        for (CommitteeMembershipBase member : members) {
            if (isMatching(matchmember, member.getPersonName())) {
                isMatch = true;
                break;
            }
        }
        return isMatch;
    }

    /*
     * check if any member role code matched the selected role code
     */
    protected boolean isMemberRoleMatch(String matchRoleCode, List<CommitteeMembershipBase> members) {
        boolean isMatch = false;
        for (CommitteeMembershipBase member : members) {
            for (CommitteeMembershipRole role : member.getMembershipRoles()) {
                if (isMatchingCode(matchRoleCode, role.getMembershipRoleCode())) {
                    isMatch = true;
                    break;
                }
            }
        }
        return isMatch;
    }
    
    /*
     * check if any committee research area code matched search criteria
     */
    protected boolean isAreaResearchMatch(String matchArea, List<CommitteeResearchAreaBase> researchAreas) {
        boolean isMatch = false;
        for (CommitteeResearchAreaBase researchArea : researchAreas) {
            if (isMatching(matchArea, researchArea.getResearchAreaCode())) {
                isMatch = true;
                break;
            }
        }
        return isMatch;
    }
    
    /*
     * using reg expression to check if pattern matched
     */
    protected boolean isMatching(String patternString, String value) {
        final boolean isMatch;
        if (StringUtils.isBlank(patternString)) {
            isMatch = true;
        }
        else {
            patternString = patternString.replaceAll("\\?", "\\\\?");
            patternString = patternString.replaceAll("\\.", "\\\\.");
            if (patternString.indexOf("*") == 0) {
                patternString = patternString.replaceFirst("\\*", "^*");
            }
            if (!patternString.endsWith("*")) {
                patternString = patternString + "$";
            }
            Pattern p = Pattern.compile(patternString.toUpperCase());
            Matcher m = p.matcher(value.toUpperCase());
            isMatch = m.find();
        }
        return isMatch;

    }

    /*
     * check if the selected code matched
     */
    protected boolean isMatchingCode(String selectedCode, String value) {
        return StringUtils.isBlank(selectedCode) || selectedCode.equals(value);
    }

    /*
     * Create a CommitteeBase object and populate it from the xml.
     */
    protected CMT populateCommitteeFromXmlDocumentContents(String xmlDocumentContents) {
        CMT committee = null;
        if (!StringUtils.isEmpty(xmlDocumentContents)) {

            committee = (CMT) getBusinessObjectFromXML(xmlDocumentContents, getCommitteeBOClassHook().getName());
        }
        return committee;
    }
    
    protected abstract Class<CMT> getCommitteeBOClassHook();


    /*
     * Retrieves substring of document contents from maintainable tag name. Then use xml service to translate xml into a business
     * object.
     */
    protected PersistableBusinessObject getBusinessObjectFromXML(String xmlDocumentContents, String objectTagName) {
        String objXml = StringUtils.substringBetween(xmlDocumentContents, "<" + objectTagName + ">", "</" + objectTagName + ">");
        objXml = "<" + objectTagName + ">" + objXml + "</" + objectTagName + ">";
        if (objXml.contains("itemDesctiption")) {
            objXml = objXml.replaceAll("itemDesctiption", "itemDescription");
        }
        return (PersistableBusinessObject) KRADServiceLocator.getXmlObjectSerializerService().fromXml(objXml);
    }
    
    /*
     * get the existing approved committee id
     */
    protected List<String> getCommitteeIds() {
        List<CMT> committees = (List<CMT>) getBusinessObjectService().findAll(getCommitteeBOClassHook());
        List<String> result = new ArrayList<>();
        committees.stream().filter(committee -> !result.contains(committee.getCommitteeId())).forEach(committee -> {
            result.add(committee.getCommitteeId());
        });
        return result;
    }

    protected String getUserIdentifier() {
         return GlobalVariables.getUserSession().getPrincipalId();
    }

    /**
     * This method trims the search result.
     * @param result, the result set to be trimmed
     * @param trimSize, the maximum size of the trimmed result set
     * @return the trimmed result set
     */
    protected List<CMT> trimResult(List<CMT> result, Integer trimSize) {
        List<CMT> trimmedResult = new ArrayList<>();
        result.stream().filter(committee -> trimmedResult.size() < trimSize).forEach(trimmedResult::add);
        return trimmedResult;
    }

    protected KcAuthorizationService getKraAuthorizationService() {
        if (kcAuthorizationService == null) {
            kcAuthorizationService = KcServiceLocator.getService(KcAuthorizationService.class);
        }
        return kcAuthorizationService;
    }

    public void setKcAuthorizationService(KcAuthorizationService kcAuthorizationService) {
        this.kcAuthorizationService = kcAuthorizationService;
    }

    public RouteHeaderService getRouteHeaderService() {
        if (routeHeaderService == null) {
            routeHeaderService = KcServiceLocator.getService(RouteHeaderService.class);
        }

        return routeHeaderService;
    }

    public void setRouteHeaderService(RouteHeaderService routeHeaderService) {
        this.routeHeaderService = routeHeaderService;
    }
}
