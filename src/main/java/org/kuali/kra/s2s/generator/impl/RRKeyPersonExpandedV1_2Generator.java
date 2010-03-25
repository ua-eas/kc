/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
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
package org.kuali.kra.s2s.generator.impl;

import gov.grants.apply.forms.rrKeyPersonExpanded12V12.PersonProfileDataType;
import gov.grants.apply.forms.rrKeyPersonExpanded12V12.ProjectRoleDataType;
import gov.grants.apply.forms.rrKeyPersonExpanded12V12.RRKeyPersonExpanded12Document;
import gov.grants.apply.forms.rrKeyPersonExpanded12V12.PersonProfileDataType.Profile;
import gov.grants.apply.forms.rrKeyPersonExpanded12V12.PersonProfileDataType.Profile.OtherProjectRoleCategory;
import gov.grants.apply.forms.rrKeyPersonExpanded12V12.RRKeyPersonExpanded12Document.RRKeyPersonExpanded12;
import gov.grants.apply.forms.rrKeyPersonExpanded12V12.RRKeyPersonExpanded12Document.RRKeyPersonExpanded12.AdditionalProfilesAttached;
import gov.grants.apply.forms.rrKeyPersonExpanded12V12.RRKeyPersonExpanded12Document.RRKeyPersonExpanded12.BioSketchsAttached;
import gov.grants.apply.forms.rrKeyPersonExpanded12V12.RRKeyPersonExpanded12Document.RRKeyPersonExpanded12.SupportsAttached;
import gov.grants.apply.system.attachmentsV10.AttachedFileDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.kuali.kra.proposaldevelopment.bo.DevelopmentProposal;
import org.kuali.kra.proposaldevelopment.bo.Narrative;
import org.kuali.kra.proposaldevelopment.bo.ProposalPerson;
import org.kuali.kra.proposaldevelopment.bo.ProposalPersonComparator;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.s2s.util.S2SConstants;

/**
 * This class generates RRKeyPersonExpanded xml object. It uses xmlbeans for
 * generation of the form. Form is generated based on RRKeyPersonExpanded
 * version 1.2 schema.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class RRKeyPersonExpandedV1_2Generator extends
		RRKeyPersonExpandedBaseGenerator {

	private static final Logger LOG = Logger
			.getLogger(RRKeyPersonExpandedV1_2Generator.class);

	/*
	 * This method gives details of Principal Investigator,KeyPersons and the
	 * corresponding attachments for RRKeyPersons
	 * 
	 */
	private RRKeyPersonExpanded12Document getRRKeyPersonExpanded() {
		RRKeyPersonExpanded12Document rrKeyPersonExpandedDocument = RRKeyPersonExpanded12Document.Factory
				.newInstance();
		RRKeyPersonExpanded12 rrKeyPersonExpanded = RRKeyPersonExpanded12.Factory
				.newInstance();
		setRRKeyPersonExpandedAttributes(rrKeyPersonExpanded);
		rrKeyPersonExpandedDocument
				.setRRKeyPersonExpanded12(rrKeyPersonExpanded);
		return rrKeyPersonExpandedDocument;
	}

	/*
	 * This method is used to set all attributes of
	 * RRkeyPersonExpandedAttributes object
	 */
	private void setRRKeyPersonExpandedAttributes(
			RRKeyPersonExpanded12 rrKeyPersonExpanded) {
		rrKeyPersonExpanded.setFormVersion(S2SConstants.FORMVERSION_1_2);
		rrKeyPersonExpanded.setPDPI(getPersonProfilePI());
		PersonProfileDataType[] keyPersonArray = getpersonProfileKeyPerson();
		if (keyPersonArray.length > 0) {
			rrKeyPersonExpanded.setKeyPersonArray(keyPersonArray);
		}
		if (extraPersons.size() > 0) {
			for (ProposalPerson extraPerson : extraPersons) {
				setBioSketchAttchment(rrKeyPersonExpanded, extraPerson);
				setCurrentPendingTypeAttachment(rrKeyPersonExpanded,
						extraPerson);
			}
			for (Narrative narrative : pdDoc.getDevelopmentProposal()
					.getNarratives()) {
				if (narrative.getNarrativeTypeCode() != null) {
					if (Integer.parseInt(narrative.getNarrativeTypeCode()) == PROFILE_TYPE) {
						setProfileTypeAttachment(rrKeyPersonExpanded, narrative);
					}
				}
			}
		}
	}

	/*
	 * This method is used to add profile type attachment to rrKeyPersonExpanded
	 */
	private void setProfileTypeAttachment(
			RRKeyPersonExpanded12 rrKeyPersonExpanded, Narrative narrative) {
		AdditionalProfilesAttached additionalProfilesAttached = AdditionalProfilesAttached.Factory
				.newInstance();
		additionalProfilesAttached
				.setAdditionalProfileAttached(getAttachedFileType(narrative));
		rrKeyPersonExpanded
				.setAdditionalProfilesAttached(additionalProfilesAttached);
	}

	/*
	 * This method is used to add current type pending attachment to
	 * rrKeyPersonExpanded
	 */
	private void setCurrentPendingTypeAttachment(
			RRKeyPersonExpanded12 rrKeyPersonExpanded,
			ProposalPerson extraPerson) {
		AttachedFileDataType supportAttachment = getPernonnelAttachments(pdDoc,
				extraPerson.getPersonId(), extraPerson.getRolodexId(),
				CURRENT_PENDING_TYPE);
		if (supportAttachment != null) {
			SupportsAttached supportsAttached = SupportsAttached.Factory
					.newInstance();
			supportsAttached.setSupportAttached(supportAttachment);
			rrKeyPersonExpanded.setSupportsAttached(supportsAttached);
		}
	}

	/*
	 * This method is used to add biosketch type attachment to
	 * rrKeyPersonExpanded
	 */
	private void setBioSketchAttchment(
			RRKeyPersonExpanded12 rrKeyPersonExpanded,
			ProposalPerson extraPerson) {
		BioSketchsAttached personBioSketch = BioSketchsAttached.Factory
				.newInstance();
		AttachedFileDataType bioSketchAttachment = getPernonnelAttachments(
				pdDoc, extraPerson.getPersonId(), extraPerson.getRolodexId(),
				BIOSKETCH_TYPE);
		personBioSketch.setBioSketchAttached(bioSketchAttachment);
		rrKeyPersonExpanded.setBioSketchsAttached(personBioSketch);
	}

	/*
	 * This method is used to get PersonProfile details of Principal
	 * Investigator.It also gives the information about the attachments related
	 * to the principal investigator.
	 * 
	 */
	private PersonProfileDataType getPersonProfilePI() {
		PersonProfileDataType profileDataType = PersonProfileDataType.Factory
				.newInstance();
		Profile profile = Profile.Factory.newInstance();
		ProposalPerson PI = s2sUtilService.getPrincipalInvestigator(pdDoc);
		if (PI != null) {
			setPersonalProfileDetailsToProfile(profileDataType, profile, PI);
		}
		return profileDataType;
	}

	/*
	 * This method is used to add Person Profile details of Principal
	 * Investigator and attachments to profile
	 */
	private void setPersonalProfileDetailsToProfile(
			PersonProfileDataType profileDataType, Profile profile,
			ProposalPerson PI) {
		assignRolodexId(PI);
		profile.setName(globLibV20Generator.getHumanNameDataType(PI));
		setDirectoryTitleToProfile(profile, PI);
		profile.setAddress(globLibV20Generator.getAddressDataType(PI));
		profile.setPhone(PI.getOfficePhone());
		if (PI.getFaxNumber() != null) {
			profile.setFax(PI.getFaxNumber());
		}
		if (PI.getDegree() != null) {
			profile.setDegreeType(PI.getDegree());
		}
		if (PI.getYearGraduated() != null) {
			profile.setDegreeYear(PI.getYearGraduated());
		}
		profile.setEmail(PI.getEmailAddress());
		DevelopmentProposal developmentProposal = pdDoc
				.getDevelopmentProposal();
		setOrganizationName(profile, developmentProposal);
		setDepartmentNameToProfile(profile);
		String divisionName = s2sUtilService.getDivisionName(pdDoc);
		if (divisionName != null) {
			profile.setDivisionName(divisionName);
		}
		if (PI.getEraCommonsUserName() != null) {
			profile.setCredential(PI.getEraCommonsUserName());
		}
		profile.setProjectRole(ProjectRoleDataType.PD_PI);
		set(profile, PI);
		profileDataType.setProfile(profile);
	}

	/*
	 * This method is used to add department name to profile
	 */
	private void setDepartmentNameToProfile(Profile profile) {
		String departmentName;
		DevelopmentProposal developmentProposal = pdDoc
				.getDevelopmentProposal();
		if (developmentProposal.getOwnedByUnit() != null) {
			departmentName = developmentProposal.getOwnedByUnit().getUnitName();
			if (departmentName != null) {
				if (departmentName.length() > DEPARTMENT_NAME_MAX_LENGTH) {
					profile.setDepartmentName(departmentName.substring(0,
							DEPARTMENT_NAME_MAX_LENGTH));
				} else {
					profile.setDepartmentName(departmentName);
				}
			}
		}
	}

	/*
	 * This method is used to add directory title to profile
	 */
	private void setDirectoryTitleToProfile(Profile profile, ProposalPerson PI) {
		if (PI.getDirectoryTitle() != null) {
			if (PI.getDirectoryTitle().length() > DIRECTORY_TITLE_MAX_LENGTH) {
				profile.setTitle(PI.getDirectoryTitle().substring(0,
						DIRECTORY_TITLE_MAX_LENGTH));
			} else {
				profile.setTitle(PI.getDirectoryTitle());
			}
		}
	}

	/*
	 * This method is used to assign rolodex id
	 */
	private void assignRolodexId(ProposalPerson PI) {
		if (PI.getPersonId() != null) {
			pIPersonOrRolodexId = PI.getPersonId();
		} else if (PI.getRolodexId() != null) {
			pIPersonOrRolodexId = PI.getRolodexId().toString();
		}
	}

	/*
	 * This method is used to add attachments related to principle indicator
	 */
	private void set(Profile profile, ProposalPerson PI) {
		setBioSketchAttachment(profile, PI);
		setCurrentPendingAttachment(profile, PI);
	}

	/*
	 * This method is used to add the current pending type attachment to profile
	 */
	private void setCurrentPendingAttachment(Profile profile, ProposalPerson PI) {
		AttachedFileDataType supportAttachment = getPernonnelAttachments(pdDoc,
				PI.getPersonId(), PI.getRolodexId(), CURRENT_PENDING_TYPE);
		if (supportAttachment != null) {
			PersonProfileDataType.Profile.SupportsAttached supportsAttached = PersonProfileDataType.Profile.SupportsAttached.Factory
					.newInstance();
			supportsAttached.setSupportAttached(supportAttachment);
			profile.setSupportsAttached(supportsAttached);
		}
	}

	/*
	 * This method is used to add the bioskectch attachment to profile
	 */
	private void setBioSketchAttachment(Profile profile, ProposalPerson PI) {
		PersonProfileDataType.Profile.BioSketchsAttached personBioSketch = PersonProfileDataType.Profile.BioSketchsAttached.Factory
				.newInstance();
		AttachedFileDataType bioSketchAttachment = getPernonnelAttachments(
				pdDoc, PI.getPersonId(), PI.getRolodexId(), BIOSKETCH_TYPE);
		personBioSketch.setBioSketchAttached(bioSketchAttachment);
		profile.setBioSketchsAttached(personBioSketch);
	}

	/*
	 * This method returns an array of PersonProfileDataType which contains the
	 * PersonProfile details and corresponding attachments for a particular Key
	 * person. The PersonProfileDataType array will have maximum of 39 key
	 * persons.
	 * 
	 */
	private PersonProfileDataType[] getpersonProfileKeyPerson() {
		List<PersonProfileDataType> personProfileDataTypeList = new ArrayList<PersonProfileDataType>();
		DevelopmentProposal developmentProposal = pdDoc
				.getDevelopmentProposal();
		List<ProposalPerson> keyPersons = developmentProposal
				.getProposalPersons();
		if (keyPersons != null) {
			Collections.sort(keyPersons, new ProposalPersonComparator());
		}
		List<ProposalPerson> nKeyPersons = s2sUtilService.getNKeyPersons(
				keyPersons, true, MAX_KEY_PERSON_COUNT);
		extraPersons = s2sUtilService.getNKeyPersons(keyPersons, false,
				MAX_KEY_PERSON_COUNT);
		if (nKeyPersons.size() > 0) {
			setKeyPersonToPersonProfileDataType(personProfileDataTypeList,
					nKeyPersons);
		}
		PersonProfileDataType[] personProfileDataArray = new PersonProfileDataType[0];
		personProfileDataArray = personProfileDataTypeList
				.toArray(personProfileDataArray);
		return personProfileDataArray;
	}

	/*
	 * This method is used to add key person to person profile data type
	 */
	private void setKeyPersonToPersonProfileDataType(
			List<PersonProfileDataType> personProfileDataTypeList,
			List<ProposalPerson> nKeyPersons) {
		for (ProposalPerson keyPerson : nKeyPersons) {
			if (pIPersonOrRolodexId != null) {
				// Don't add PI to keyperson list
				if (keyPerson.getPersonId() != null
						&& keyPerson.getPersonId().equals(pIPersonOrRolodexId)) {
					continue;
				} else if ((keyPerson.getRolodexId() != null)
						&& pIPersonOrRolodexId.equals(keyPerson.getRolodexId()
								.toString())) {
					continue;
				}
			}
			Profile profileKeyPerson = Profile.Factory.newInstance();
			setAllkeyPersonDetailsToKeyPerson(keyPerson, profileKeyPerson);
			set(profileKeyPerson, keyPerson);
			PersonProfileDataType personProfileDataTypeKeyPerson = PersonProfileDataType.Factory
					.newInstance();
			personProfileDataTypeKeyPerson.setProfile(profileKeyPerson);
			personProfileDataTypeList.add(personProfileDataTypeKeyPerson);
		}
	}

	/*
	 * This method is used to add all key person details to key person
	 */
	private void setAllkeyPersonDetailsToKeyPerson(ProposalPerson keyPerson,
			Profile profileKeyPerson) {
		profileKeyPerson.setName(globLibV20Generator
				.getHumanNameDataType(keyPerson));
		setDirectoryTitleToProfile(profileKeyPerson, keyPerson);
		profileKeyPerson.setAddress(globLibV20Generator
				.getAddressDataType(keyPerson));
		profileKeyPerson.setPhone(keyPerson.getOfficePhone());
		if (keyPerson.getFaxNumber() != null) {
			profileKeyPerson.setFax(keyPerson.getFaxNumber());
		}
		if (keyPerson.getDegree() != null) {
			profileKeyPerson.setDegreeType(keyPerson.getDegree());
		}
		if (keyPerson.getYearGraduated() != null) {
			profileKeyPerson.setDegreeYear(keyPerson.getYearGraduated());
		}
		profileKeyPerson.setEmail(keyPerson.getEmailAddress());
		DevelopmentProposal developmentProposal = pdDoc
				.getDevelopmentProposal();
		setOrganizationName(profileKeyPerson, developmentProposal);
		setDepartmentNameToProfile(profileKeyPerson);
		String divisionName = s2sUtilService.getDivisionName(pdDoc);
		if (divisionName != null) {
			profileKeyPerson.setDivisionName(divisionName);
		}
		if (keyPerson.getEraCommonsUserName() != null) {
			profileKeyPerson.setCredential(keyPerson.getEraCommonsUserName());
		}
		if (keyPerson.getProposalPersonRoleId().equals(CO_INVESTIGATOR)) {
			if(NIH.equals(pdDoc.getDevelopmentProposal().getSponsorName())){
				profileKeyPerson.setProjectRole(ProjectRoleDataType.PD_PI);
			}else{
				profileKeyPerson.setProjectRole(ProjectRoleDataType.CO_PD_PI);
			}
		} else {
			setProjectRoleCategoryToProfile(keyPerson, profileKeyPerson);
		}
	}

	private void setOrganizationName(Profile profileKeyPerson,
			DevelopmentProposal developmentProposal) {
		if (developmentProposal.getApplicantOrganization() != null
				&& developmentProposal.getApplicantOrganization()
						.getOrganization() != null) {
			profileKeyPerson.setOrganizationName(developmentProposal
					.getApplicantOrganization().getOrganization()
					.getOrganizationName());
		}
	}

	/*
	 * This method is used to add project role category to profile
	 */
	private void setProjectRoleCategoryToProfile(ProposalPerson keyPerson,
			Profile profileKeyPerson) {
		profileKeyPerson.setProjectRole(ProjectRoleDataType.OTHER_SPECIFY);
		OtherProjectRoleCategory otherProjectRole = OtherProjectRoleCategory.Factory
				.newInstance();
		String otherRole;
		if (keyPerson.getProjectRole() != null) {
			if (keyPerson.getProjectRole().length() > ROLE_DESCRIPTION_MAX_LENGTH) {
				otherRole = keyPerson.getProjectRole().substring(0,
						ROLE_DESCRIPTION_MAX_LENGTH);
			} else {
				otherRole = keyPerson.getProjectRole();
			}
		} else {
			otherRole = S2SConstants.VALUE_UNKNOWN;
		}
		otherProjectRole.setStringValue(otherRole);
		profileKeyPerson.setOtherProjectRoleCategory(otherProjectRole);
	}

	/**
	 * This method creates {@link XmlObject} of type
	 * {@link RRKeyPersonExpanded12Document} by populating data from the given
	 * {@link ProposalDevelopmentDocument}
	 * 
	 * @param proposalDevelopmentDocument
	 *            for which the {@link XmlObject} needs to be created
	 * @return {@link XmlObject} which is generated using the given
	 *         {@link ProposalDevelopmentDocument}
	 * @see org.kuali.kra.s2s.generator.S2SFormGenerator#getFormObject(ProposalDevelopmentDocument)
	 */
	public XmlObject getFormObject(
			ProposalDevelopmentDocument proposalDevelopmentDocument) {
		this.pdDoc = proposalDevelopmentDocument;
		return getRRKeyPersonExpanded();
	}

	/**
	 * This method typecasts the given {@link XmlObject} to the required
	 * generator type and returns back the document of that generator type.
	 * 
	 * @param xmlObject
	 *            which needs to be converted to the document type of the
	 *            required generator
	 * @return {@link XmlObject} document of the required generator type
	 * @see org.kuali.kra.s2s.generator.S2SFormGenerator#getFormObject(XmlObject)
	 */
	public XmlObject getFormObject(XmlObject xmlObject) {
		RRKeyPersonExpanded12 rrKeyPersonExpanded = (RRKeyPersonExpanded12) xmlObject;
		RRKeyPersonExpanded12Document rrKeyPersonExpandedDocument = RRKeyPersonExpanded12Document.Factory
				.newInstance();
		rrKeyPersonExpandedDocument
				.setRRKeyPersonExpanded12(rrKeyPersonExpanded);
		return rrKeyPersonExpandedDocument;
	}
}
