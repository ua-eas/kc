/*
 * Copyright 2006-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.s2s.service.impl;

import gov.grants.apply.system.metaGrantApplication.GrantApplicationDocument;
import gov.grants.apply.system.metaGrantApplication.GrantApplicationDocument.GrantApplication.Forms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.kuali.kra.bo.SponsorFormTemplate;
import org.kuali.kra.bo.SponsorFormTemplateList;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.printing.Printable;
import org.kuali.kra.printing.PrintingException;
import org.kuali.kra.printing.service.PrintingService;
import org.kuali.kra.proposaldevelopment.bo.AttachmentDataSource;
import org.kuali.kra.proposaldevelopment.bo.Narrative;
import org.kuali.kra.proposaldevelopment.bo.ProposalPersonBiography;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.s2s.S2SException;
import org.kuali.kra.s2s.bo.S2sAppAttachments;
import org.kuali.kra.s2s.bo.S2sAppSubmission;
import org.kuali.kra.s2s.bo.S2sApplication;
import org.kuali.kra.s2s.bo.S2sOppForms;
import org.kuali.kra.s2s.formmapping.FormMappingInfo;
import org.kuali.kra.s2s.formmapping.FormMappingLoader;
import org.kuali.kra.s2s.generator.S2SBaseFormGenerator;
import org.kuali.kra.s2s.generator.S2SFormGenerator;
import org.kuali.kra.s2s.generator.S2SGeneratorNotFoundException;
import org.kuali.kra.s2s.generator.bo.AttachmentData;
import org.kuali.kra.s2s.printing.print.S2SFormPrint;
import org.kuali.kra.s2s.service.PrintService;
import org.kuali.kra.s2s.service.S2SFormGeneratorService;
import org.kuali.kra.s2s.service.S2SUtilService;
import org.kuali.kra.s2s.service.S2SValidatorService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.AuditCluster;
import org.kuali.rice.kns.util.AuditError;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * This class is implementation of PrintService. It provides the functionality
 * to generate PDF for all forms along with their attachments
 */
public class PrintServiceImpl implements PrintService {
	private static final Logger LOG = Logger.getLogger(PrintServiceImpl.class);

	private BusinessObjectService businessObjectService;
	private S2SFormGeneratorService s2SFormGeneratorService;
	private S2SValidatorService s2SValidatorService;
	private static final String NARRATIVE_ATTACHMENT_LIST = "narrativeAttachmentList";
	private static final String SPONSOR_CODE_DB_KEY = "sponsorCode";
	private static final String PAGE_NUMBER_DB_KEY = "pageNumber";

	private S2SUtilService s2SUtilService;
	private PrintingService printingService;

	/**
	 * Prints the proposal sponsor forms by passing the given proposal
	 * information to {@link ProposalPrintReader}
	 * 
	 * @param proposalNumber
	 *            proposal number.
	 * @param sponsorFormTemplates
	 *            list of SponsorFormTemplate.
	 * @return byte array of forms corresponding to the proposal number and
	 *         SponsorFormTemplate objects.
	 * @throws S2SException
	 * @see org.kuali.kra.s2s.service.PrintService#printProposalSponsorForms(java.lang.String,
	 *      java.util.List)
	 */
	public byte[] printProposalSponsorForms(String proposalNumber,
			List<SponsorFormTemplate> sponsorFormTemplates) throws S2SException {
		throw new RuntimeException("Unsupported functionality");
	}

	/**
	 * 
	 * This method is to get templates for generic sponsor code.
	 * 
	 * @param sponsorFormTemplates
	 *            list of SponsorFormTemplateList.
	 * @param sponsorCode
	 *            code for the sponsor.
	 */
	public void populateSponsorForms(
			List<SponsorFormTemplateList> sponsorFormTemplates,
			String sponsorCode) {
		// check if sponsor forms isEmpty
		if (!sponsorFormTemplates.isEmpty()) {
			// if exists - check if sponsor code has changed
			if (!sponsorCode.equalsIgnoreCase(sponsorFormTemplates.get(0)
					.getSponsorCode())) {
				sponsorFormTemplates.clear();
			}
		}

		if (sponsorFormTemplates.isEmpty()) {
			// fetch all templates for proposal sponsor code
			Collection<SponsorFormTemplateList> clsponsorFormTemplates = getSponsorTemplatesList(sponsorCode);
			sponsorFormTemplates.addAll(clsponsorFormTemplates);
			// fetch all templates for generic sponsor code
			String genericSponsorCode = s2SUtilService
					.getParameterValue(Constants.GENERIC_SPONSOR_CODE);
			clsponsorFormTemplates = getSponsorTemplatesList(genericSponsorCode);
			sponsorFormTemplates.addAll(clsponsorFormTemplates);
		} else {
			resetSelectedFormList(sponsorFormTemplates);
		}
		Collections.sort(sponsorFormTemplates);
	}

	/**
	 * 
	 * This method is to reset the selected form list.
	 * 
	 * @param sponsorFormTemplates
	 *            list of SponsorFormTemplateList.
	 */
	private void resetSelectedFormList(
			List<SponsorFormTemplateList> sponsorFormTemplates) {
		for (SponsorFormTemplateList sponsorFormTemplateList : sponsorFormTemplates) {
			sponsorFormTemplateList.setSelectToPrint(false);
		}
	}

	/**
	 * This method gets the sponsor form template from the given sponsor form
	 * template list
	 * 
	 * @param sponsorFormTemplateLists -
	 *            list of sponsor form template list
	 * @return list of sponsor form template
	 * @see org.kuali.kra.s2s.service.PrintService#getSponsorFormTemplates(java.util.List)
	 */
	public List<SponsorFormTemplate> getSponsorFormTemplates(
			List<SponsorFormTemplateList> sponsorFormTemplateLists) {
		List<SponsorFormTemplate> sponsorFormTemplates = new ArrayList<SponsorFormTemplate>();
		Set<String> sponsorCodes = new HashSet<String>();
		Set<String> formKeys = new HashSet<String>();
		for (SponsorFormTemplateList sponsorFormTemplateList : sponsorFormTemplateLists) {
			if (sponsorFormTemplateList.getSelectToPrint()) {
				if (!sponsorCodes.contains(sponsorFormTemplateList
						.getSponsorCode())) {
					sponsorCodes.add(sponsorFormTemplateList.getSponsorCode());
				}
			} else {
				StringBuilder fKeys = new StringBuilder();
				fKeys.append(sponsorFormTemplateList.getSponsorCode());
				fKeys.append(sponsorFormTemplateList.getPackageNumber()
						.toString());
				fKeys
						.append(sponsorFormTemplateList.getPageNumber()
								.toString());
				if (!formKeys.contains(fKeys.toString())) {
					formKeys.add(fKeys.toString());
				}
			}
		}

		String sponsorCode = null;
		Iterator<String> iter = sponsorCodes.iterator();
		// fetch all the templates for applicable sponsor codes
		while (iter.hasNext()) {
			sponsorCode = iter.next();
			Collection<SponsorFormTemplate> clsponsorFormTemplates = getSponsorTemplates(sponsorCode);
			sponsorFormTemplates.addAll(clsponsorFormTemplates);
			iter.remove();
		}

		// verify selected package
		List<SponsorFormTemplate> printFormTemplates = new ArrayList<SponsorFormTemplate>();
		HashMap<String, SponsorFormTemplate> formTemplates = new HashMap<String, SponsorFormTemplate>();
		for (SponsorFormTemplate sponsorFormTemplate : sponsorFormTemplates) {
			StringBuilder templateKey = new StringBuilder();
			templateKey.append(sponsorFormTemplate.getSponsorCode());
			templateKey.append(sponsorFormTemplate.getPackageNumber()
					.toString());
			templateKey.append(sponsorFormTemplate.getPageNumber().toString());
			formTemplates.put(templateKey.toString(), sponsorFormTemplate);
		}
		Iterator<String> keyIter = formKeys.iterator();
		while (keyIter.hasNext()) {
			String formKey = keyIter.next();
			formTemplates.remove(formKey);
			keyIter.remove();
		}
		printFormTemplates.addAll(formTemplates.values());
		Collections.sort(printFormTemplates);
		resetSelectedFormList(sponsorFormTemplateLists);
		return printFormTemplates;
	}

	/**
	 * 
	 * This method is used to get the Sponsor template for the given sponsor
	 * code.
	 * 
	 * @param sponsorCode
	 *            code number of the sponsor.
	 * @return Collection<SponsorFormTemplate> collection of
	 *         SponsorFormTemplate for the given sponsor code.
	 */
	private Collection<SponsorFormTemplate> getSponsorTemplates(
			String sponsorCode) {
		Map<String, String> sponsorCodeMap = new HashMap<String, String>();
		sponsorCodeMap.put(SPONSOR_CODE_DB_KEY, sponsorCode);
		Collection<SponsorFormTemplate> sponsorFormTemplates = getBusinessObjectService()
				.findMatchingOrderBy(SponsorFormTemplate.class, sponsorCodeMap,
						PAGE_NUMBER_DB_KEY, false);
		return sponsorFormTemplates;
	}

	/**
	 * 
	 * This method is used for the printing of forms in PDF format. It generates
	 * PDF forms and puts it into {@link AttachmentDataSource}
	 * 
	 * @param pdDoc(ProposalDevelopmentDocument)
	 * @return {@link AttachmentDataSource} which contains all information
	 *         related to the generated PDF
	 * @throws
	 * @throws S2SException
	 * 
	 * @see org.kuali.kra.s2s.service.PrintService#printForm(org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument)
	 */
	public org.kuali.kra.proposaldevelopment.bo.AttachmentDataSource printForm(
			ProposalDevelopmentDocument pdDoc) throws S2SException,
			PrintingException {
		List<Printable> printableList = null;
		S2sAppSubmission s2sAppSubmission = getLatestS2SAppSubmission(pdDoc);
		if (s2sAppSubmission != null
				&& s2sAppSubmission.getGgTrackingId() != null) {
			printableList = getSubmittedPDFStream(pdDoc);
		} else {
			printableList = getPDFStream(pdDoc);
		}
		AttachmentDataSource attachmentDataSource = printingService
				.print(printableList);
		attachmentDataSource.setFileName(getFileNameForFormPrinting(pdDoc));
		return attachmentDataSource;
	}

	private String getFileNameForFormPrinting(ProposalDevelopmentDocument pdDoc) {
		StringBuilder fileName = new StringBuilder();
		fileName.append(pdDoc.getDocumentNumber());
		fileName.append(pdDoc.getDevelopmentProposal()
				.getProgramAnnouncementNumber());
		fileName.append(Constants.PDF_FILE_EXTENSION);
		return fileName.toString();
	}

	/**
	 * 
	 * This method is used to get the Sponsor template list for the given
	 * sponsor code.
	 * 
	 * @param sponsorCode
	 *            code number of the sponsor.
	 * @return Collection<SponsorFormTemplateList> collection of
	 *         SponsorFormTemplateList for the given sponsor code.
	 */
	private Collection<SponsorFormTemplateList> getSponsorTemplatesList(
			String sponsorCode) {
		Map<String, String> sponsorCodeMap = new HashMap<String, String>();
		sponsorCodeMap.put(SPONSOR_CODE_DB_KEY, sponsorCode);
		Collection<SponsorFormTemplateList> sponsorFormTemplates = getBusinessObjectService()
				.findMatching(SponsorFormTemplateList.class, sponsorCodeMap);
		return sponsorFormTemplates;
	}

	/**
	 * Gets the businessObjectService attribute.
	 * 
	 * @return Returns the businessObjectService.
	 */
	public BusinessObjectService getBusinessObjectService() {
		return businessObjectService;
	}

	/**
	 * Sets the businessObjectService attribute value.
	 * 
	 * @param businessObjectService
	 *            The businessObjectService to set.
	 */
	public void setBusinessObjectService(
			BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}

	/**
	 * 
	 * This method is to write the submitted application data to a pdfStream
	 * 
	 * @param pdDoc
	 *            Proposal Development Document.
	 * @return ByteArrayOutputStream[] of the submitted application data.
	 * @throws S2SException
	 */
	private List<Printable> getSubmittedPDFStream(
			ProposalDevelopmentDocument pdDoc) throws S2SException {
		GrantApplicationDocument submittedDocument;
		try {
		    S2sAppSubmission s2sAppSubmission = getLatestS2SAppSubmission(pdDoc);
		    String submittedApplication = findSubmittedXml(s2sAppSubmission);
		    
			submittedDocument = GrantApplicationDocument.Factory.parse(submittedApplication);
		} catch (XmlException e) {
			LOG.error(e.getMessage(), e);
			throw new S2SException(e);
		}
		FormMappingInfo info = null;
		S2SFormGenerator s2sFormGenerator = null;
		List<AuditError> errors = new ArrayList<AuditError>();
		List<String> sortedNameSpaces = getSortedNameSpaces(pdDoc
				.getDevelopmentProposal().getS2sOppForms());

		List<Printable> formPrintables = new ArrayList<Printable>();

		for (String namespace : sortedNameSpaces) {
			XmlObject formFragment = null;
			try {
				info = new FormMappingLoader().getFormInfo(namespace);
				formFragment = getFormObject(submittedDocument, info);
				s2sFormGenerator = s2SFormGeneratorService.getS2SGenerator(info
						.getNameSpace());
			} catch (S2SGeneratorNotFoundException e) {
				// Form generator not available for the namespace
				continue;
			}

//			XmlObject formObject = s2sFormGenerator.getFormObject(formFragment);
//			if (s2SValidatorService.validate(formObject, errors)) {

				byte[] formXmlBytes = formFragment.xmlText().getBytes();
				S2SFormPrint formPrintable = new S2SFormPrint();

				ArrayList<Source> templates = new ArrayList<Source>();
				Source xsltSource = new StreamSource(getClass()
						.getResourceAsStream("/" + info.getStyleSheet()));
				templates.add(xsltSource);
				formPrintable.setXSLT(templates);

				// Linkedhashmap is used to preserve the order of entry.
				Map<String, byte[]> formXmlDataMap = new LinkedHashMap<String, byte[]>();
				formXmlDataMap.put(info.getFormName(), formXmlBytes);
				formPrintable.setXmlDataMap(formXmlDataMap);
//				S2sAppSubmission submittedS2SAppSubmission = getLatestS2SAppSubmission(pdDoc);
				S2sApplication s2sApplciation = getBusinessObjectService().findBySinglePrimaryKey(S2sApplication.class, pdDoc.getDevelopmentProposal().getProposalNumber());//submittedS2SAppSubmission.getS2sApplication();
				List<S2sAppAttachments> attachmentList = s2sApplciation.getS2sAppAttachmentList();

				Map<String, byte[]> formAttachments = new LinkedHashMap<String, byte[]>();

				if (attachmentList != null && !attachmentList.isEmpty()) {
					for (S2sAppAttachments attAppAttachments : attachmentList) {
						ByteArrayOutputStream attStream = new ByteArrayOutputStream();
						try {
							attStream.write(getAttContent(pdDoc,
									attAppAttachments.getContentId()));
						} catch (IOException e) {
							LOG.error(e.getMessage(), e);
							throw new S2SException(e);
						}
						StringBuilder attachment = new StringBuilder();
						attachment.append("   ATT : ");
						attachment.append(attAppAttachments.getContentId());
						formAttachments.put(attachment.toString(),
								getAttContent(pdDoc, attAppAttachments
										.getContentId()));
					}
				}
				formPrintable.setAttachments(formAttachments);
				formPrintables.add(formPrintable);
//			}
		}
		return formPrintables;
	}

	private String findSubmittedXml(S2sAppSubmission appSubmission) {
	    S2sApplication s2sApplication = getBusinessObjectService().findBySinglePrimaryKey(S2sApplication.class, appSubmission.getProposalNumber());
	    return s2sApplication.getApplication();
    }

    /**
	 * This method is used to generate byte stream of forms
	 * 
	 * @param pdDoc
	 *            ProposalDevelopmentDocument
	 * @param bookmarksList
	 * @return ByteArrayOutputStream[] PDF byte Array
	 * @throws S2SException
	 */
	private List<Printable> getPDFStream(ProposalDevelopmentDocument pdDoc)
			throws S2SException {
		FormMappingInfo info = null;
		S2SBaseFormGenerator s2sFormGenerator = null;
		List<AuditError> errors = new ArrayList<AuditError>();
		List<String> sortedNameSpaces = getSortedNameSpaces(pdDoc
				.getDevelopmentProposal().getS2sOppForms());

		List<Printable> formPrintables = new ArrayList<Printable>();

		for (String namespace : sortedNameSpaces) {
			try {
				info = new FormMappingLoader().getFormInfo(namespace);
				s2sFormGenerator = (S2SBaseFormGenerator)s2SFormGeneratorService.getS2SGenerator(info
						.getNameSpace());
			} catch (S2SGeneratorNotFoundException e) {
				LOG.info("Form not found for namespace: " + namespace);
				continue;
			}
			s2sFormGenerator.setAuditErrors(errors);
			XmlObject formObject = s2sFormGenerator.getFormObject(pdDoc);
			if (s2SValidatorService.validate(formObject, errors)) {

				byte[] formXmlBytes = formObject.xmlText().getBytes();
				S2SFormPrint formPrintable = new S2SFormPrint();

				// Linkedhashmap is used to preserve the order of entry.
				Map<String, byte[]> formXmlDataMap = new LinkedHashMap<String, byte[]>();
				formXmlDataMap.put(info.getFormName(), formXmlBytes);
				formPrintable.setXmlDataMap(formXmlDataMap);

				ArrayList<Source> templates = new ArrayList<Source>();
				Source xsltSource = new StreamSource(getClass()
						.getResourceAsStream("/" + info.getStylesheet()));
				templates.add(xsltSource);
				formPrintable.setXSLT(templates);

				List<AttachmentData> attachmentList = s2sFormGenerator
						.getAttachments();
				Map<String, byte[]> formAttachments = new LinkedHashMap<String, byte[]>();
				if (attachmentList != null && !attachmentList.isEmpty()) {
					for (AttachmentData attachmentData : attachmentList) {
						if (!isPdfType(attachmentData.getContent()))
							continue;
						StringBuilder attachment = new StringBuilder();
						attachment.append("   ATT : ");
						attachment.append(attachmentData.getContentId());
						formAttachments.put(attachment.toString(),
								attachmentData.getContent());
					}
				}
				if (formAttachments.size() > 0) {
					formPrintable.setAttachments(formAttachments);
				}
				formPrintables.add(formPrintable);
			}
		}
		if (!errors.isEmpty()) {
			setValidationErrorMessage(errors);
		}
		return formPrintables;
	}

	/**
	 * 
	 * This method is to put validation errors on UI
	 * 
	 * @param errors
	 *            List of validation errors which has to be displayed on UI.
	 */

	private void setValidationErrorMessage(List<AuditError> errors) {
		LOG.info("Error list size:" + errors.size() + errors.toString());
		List<AuditError> auditErrors = new ArrayList<AuditError>();
		for (AuditError error : errors) {
			auditErrors.add(new AuditError(error.getErrorKey(),
					Constants.GRANTS_GOV_GENERIC_ERROR_KEY, error.getLink(),
					new String[] { error.getMessageKey() }));
		}
		if (!auditErrors.isEmpty()) {
			GlobalVariables.getAuditErrorMap().put(
					"grantsGovAuditErrors",
					new AuditCluster(Constants.GRANTS_GOV_OPPORTUNITY_PANEL,
							auditErrors, Constants.GRANTSGOV_ERRORS));
		}
	}

	/**
	 * 
	 * This method gets formObject from submitted Application
	 * 
	 * @param submittedXml
	 *            GrantApplicationDocument object of the submitted form.
	 * @param info
	 *            form mapping information of the form.
	 * @return XmlObject form object corresponding to the
	 *         GrantApplicationDocument and FormMappingInfo objects.
	 * @throws S2SException
	 */

	private XmlObject getFormObject(GrantApplicationDocument submittedXml,
			FormMappingInfo info) {
		XmlObject formObject = null;
		Forms forms = submittedXml.getGrantApplication().getForms();
		return forms.newCursor().getObject();
//		if (forms != null) {
//			XmlCursor formCursor = forms.newCursor();
//			formCursor.toNextToken();
//			do {
//				if (formCursor.getName().getNamespaceURI().equals(
//						info.getNameSpace())) {
//					formObject = formCursor.getObject();
//					break;
//				}
//			} while (formCursor.toNextSibling());
//		}
		
//		return formObject;
	}

	/**
	 * 
	 * This method gets attachment contents from narrative based on content ID
	 * 
	 * @param pdDoc
	 *            Proposal Development Document.
	 * @param contentId
	 *            for the particular attachment in the Narrative.
	 * @return byte[] byte array of attachments based on the contentId object.
	 */

	private byte[] getAttContent(ProposalDevelopmentDocument pdDoc,
			String contentId) {
		String[] contentIds = contentId.split("-");
		String[] contentDesc = contentIds[1].split("_");
		if (StringUtils.equals(contentIds[0], "N")) {
    		for (Narrative narrative : pdDoc.getDevelopmentProposal()
    				.getNarratives()) {
				if (narrative.getModuleNumber().equals(Integer.valueOf(contentDesc[0]))) {
				    narrative.refreshReferenceObject(NARRATIVE_ATTACHMENT_LIST);
				    return narrative.getNarrativeAttachmentList().get(0).getContent();
				}
    		}
		} else if (StringUtils.equals(contentIds[0], "B")){
		    for (ProposalPersonBiography biography : pdDoc.getDevelopmentProposal().getPropPersonBios()) {
		        if (biography.getProposalPersonNumber().equals(Integer.valueOf(contentDesc[0]))
		                && biography.getBiographyNumber().equals(Integer.valueOf(contentDesc[1]))) {
		            biography.refreshReferenceObject("personnelAttachmentList");
		            return biography.getPersonnelAttachmentList().get(0).getContent();
		        }
		    }
    	}
		return null;
	}

	/**
	 * 
	 * This method gets the latest S2SAppSubmission record from the list of
	 * S2SAppSubmissions. It iterates through the list and returns the record
	 * that has highest SubmissionNo
	 * 
	 * @param pdDoc
	 *            {@link ProposalDevelopmentDocument}
	 * @return {@link S2sAppSubmission}
	 */
	private S2sAppSubmission getLatestS2SAppSubmission(
			ProposalDevelopmentDocument pdDoc) {
		S2sAppSubmission s2sSubmission = null;
		int submissionNo = 0;
		for (S2sAppSubmission s2sAppSubmission : pdDoc.getDevelopmentProposal()
				.getS2sAppSubmission()) {
			if (s2sAppSubmission.getSubmissionNumber() != null
					&& s2sAppSubmission.getSubmissionNumber().intValue() > submissionNo) {
				s2sSubmission = s2sAppSubmission;
				submissionNo = s2sAppSubmission.getSubmissionNumber().intValue();
			}
		}
		return s2sSubmission;
	}



	/**
	 * 
	 * This method sorts all the forms in order as specified in
	 * S2sFormBinding.xml and returns the list of namespaces in sorted order.
	 * 
	 * @param s2sOppForms
	 *            list of S2sOppForms.
	 * @return List<String> list of sorted name spaces.
	 */
	private List<String> getSortedNameSpaces(List<S2sOppForms> s2sOppForms) {
		List<String> orderedNamespaces = new ArrayList<String>();
		List<String> namespaces;
		FormMappingLoader formMappingLoader = new FormMappingLoader();
		formMappingLoader.getBindings();
		Map<Integer, List<String>> sortedNamespaces = formMappingLoader
				.getSortedNameSpaces();
		List<Integer> sortedIndices = new ArrayList<Integer>(sortedNamespaces
				.keySet());
		int index = 0;
		for (Integer sortedIndex : sortedIndices) {
			namespaces = sortedNamespaces.get(sortedIndex);
			for (String namespace : namespaces) {
				for (S2sOppForms oppForm : s2sOppForms) {
					if (namespace.equals(oppForm.getOppNameSpace())) {
						if (Boolean.TRUE.equals(oppForm.getSelectToPrint())) {
							orderedNamespaces.add(index++, namespace);
						}
					}
				}
			}
		}
		return orderedNamespaces;
	}

	/**
	 * 
	 * Setter for {@link S2SFormGeneratorService}
	 * 
	 * @param s2SFormGeneratorService
	 */
	public void setS2SFormGeneratorService(
			S2SFormGeneratorService s2SFormGeneratorService) {
		this.s2SFormGeneratorService = s2SFormGeneratorService;
	}

	/**
	 * 
	 * Setter for {@link S2SFormGeneratorService}
	 * 
	 * @param s2SValidatorService
	 */
	public void setS2SValidatorService(S2SValidatorService s2SValidatorService) {
		this.s2SValidatorService = s2SValidatorService;
	}

	private boolean isPdfType(byte[] data) {
		final int ATTRIBUTE_CHUNK_SIZE = 1200;// increased for ppt
		final String PRE_HEXA = "0x";

		boolean retValue = false;
		String str[] = { "25", "50", "44", "46" };
		byte byteCheckArr[] = new byte[4];
		byte byteDataArr[] = new byte[4];

		for (int byteIndex = 0; byteIndex < byteCheckArr.length; byteIndex++) {
			byteCheckArr[byteIndex] = Integer.decode(PRE_HEXA + str[byteIndex])
					.byteValue();
		}

		int startPoint, endPoint;

		startPoint = 0;
		endPoint = (ATTRIBUTE_CHUNK_SIZE > (data.length / 2)) ? data.length / 2
				: ATTRIBUTE_CHUNK_SIZE;

		for (int forwardIndex = startPoint; forwardIndex < endPoint
				- str.length; forwardIndex++) {
			if (forwardIndex == 0) {
				// Fill All Data
				for (int fillIndex = 0; fillIndex < str.length; fillIndex++) {
					byteDataArr[fillIndex] = toUnsignedByte(data[fillIndex]);
				}
			} else {
				// Push Data, Fill last index
				for (int fillIndex = 0; fillIndex < str.length - 1; fillIndex++) {
					byteDataArr[fillIndex] = byteDataArr[fillIndex + 1];
				}
				byteDataArr[str.length - 1] = toUnsignedByte(data[str.length
						- 1 + forwardIndex]);
			}

			if (new String(byteCheckArr).equals(new String(byteDataArr))) {
				retValue = true;
			}
		}

		return retValue;
	}

	/**
	 * convert int to unsigned byte
	 */
	private static byte toUnsignedByte(int intVal) {
		byte byteVal;
		if (intVal > 127) {
			int temp = intVal - 256;
			byteVal = (byte) temp;
		} else {
			byteVal = (byte) intVal;
		}
		return byteVal;
	}

	/**
	 * Gets the s2SUtilService attribute.
	 * 
	 * @return Returns the s2SUtilService.
	 */
	public S2SUtilService getS2SUtilService() {
		return s2SUtilService;
	}

	/**
	 * Sets the s2SUtilService attribute value.
	 * 
	 * @param utilService
	 *            The s2SUtilService to set.
	 */
	public void setS2SUtilService(S2SUtilService utilService) {
		s2SUtilService = utilService;
	}

	public PrintingService getPrintingService() {
		return printingService;
	}

	public void setPrintingService(PrintingService printingService) {
		this.printingService = printingService;
	}
}
