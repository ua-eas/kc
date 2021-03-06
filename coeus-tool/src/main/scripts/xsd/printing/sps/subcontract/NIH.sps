<?xml version="1.0" encoding="UTF-8"?>
<structure version="7" xsltversion="1" cssmode="quirks" relativeto="*SPS" encodinghtml="UTF-8" encodingrtf="ISO-8859-1" encodingpdf="UTF-8" embed-images="1">
	<predefinedformats>
		<format string="###,##0" datatype="decimal"/>
	</predefinedformats>
	<predefinedformats>
		<format string="MM / DD / YYYY" datatype="date"/>
	</predefinedformats>
	<parameters/>
	<schemasources>
		<namespaces>
			<nspair prefix="award" uri="http://subcontractFdpReports.bean.xml.utils.coeus.mit.edu/award"/>
			<nspair prefix="subcontract" uri="http://subcontractFdpReports.bean.xml.utils.coeus.mit.edu/subcontract"/>
		</namespaces>
		<schemasources>
			<xsdschemasource name="$XML" main="1" schemafile="D:\VSS\SVN\coeusSource\Branches\4.5\Printing\Subcontract FDP Printing\Schema\SubcontractFdpReports.xsd" workingxmlfile="C:\Documents and Settings\coeusdev\Desktop\Stylesheets on nibc606\Subcontract_Fdp_Report.xml">
				<xmltablesupport/>
				<textstateicons/>
			</xsdschemasource>
		</schemasources>
	</schemasources>
	<modules/>
	<flags>
		<scripts/>
		<globalparts/>
		<designfragments/>
		<pagelayouts/>
	</flags>
	<scripts>
		<script language="javascript"/>
	</scripts>
	<globalstyles/>
	<mainparts>
		<children>
			<globaltemplate match="/" matchtype="named" parttype="main">
				<children>
					<newline/>
					<template match="$XML" matchtype="schemasource">
						<editorproperties elementstodisplay="1"/>
						<children>
							<newline/>
							<newline/>
							<table>
								<properties border="1" cellpadding="0" cellspacing="0" width="100%"/>
								<styles font-family="Times New Roman" font-size="12pt" font-weight="bold"/>
								<children>
									<tablebody>
										<children>
											<tablerow>
												<children>
													<tablecell>
														<children>
															<paragraph paragraphtag="center">
																<children>
																	<text fixtext="Attachment 2"/>
																	<newline/>
																	<text fixtext="Research Subaward Agreement"/>
																	<newline/>
																	<text fixtext="Prime Award Terms and Conditions"/>
																	<newline/>
																	<text fixtext="NIH"/>
																	<newline/>
																</children>
															</paragraph>
														</children>
													</tablecell>
												</children>
											</tablerow>
										</children>
									</tablebody>
								</children>
							</table>
							<newline/>
							<table>
								<properties border="0" cellpadding="0" width="100%"/>
								<styles font-family="Times New Roman"/>
								<children>
									<tablebody>
										<children>
											<tablerow>
												<styles font-size="12pt" font-weight="bold"/>
												<children>
													<tablecell>
														<children>
															<text fixtext="Agency-Specific Certifications/Assurances"/>
															<paragraph paragraphtag="p"/>
														</children>
													</tablecell>
												</children>
											</tablerow>
											<tablerow>
												<styles font-size="10pt" font-weight="normal"/>
												<children>
													<tablecell>
														<children>
															<text fixtext=" By signing this Research Subaward Agreement Subrecipient makes the certifications and assurances specified in the Research Terms and Conditions Appendix C found at "/>
															<link>
																<children>
																	<text fixtext="http://www.nsf.gov/bfa/dias/policy/rtc/appc.pdf"/>
																</children>
																<action>
																	<none/>
																</action>
																<bookmark/>
																<hyperlink>
																	<fixtext value="nsf.gov/bfa/dias/policy/rtc/appc.pdf"/>
																</hyperlink>
															</link>
														</children>
													</tablecell>
												</children>
											</tablerow>
										</children>
									</tablebody>
								</children>
							</table>
							<paragraph paragraphtag="p"/>
							<table>
								<properties border="0" width="100%"/>
								<children>
									<tablebody>
										<children>
											<tablerow>
												<styles font-family="Times New Roman" font-size="12pt" font-weight="bold"/>
												<children>
													<tablecell>
														<children>
															<text fixtext="General terms and conditions:"/>
														</children>
													</tablecell>
												</children>
											</tablerow>
										</children>
									</tablebody>
								</children>
							</table>
							<table>
								<properties border="0" width="100%"/>
								<styles font-family="time" font-size="10pt"/>
								<children>
									<tablebody>
										<children>
											<tablerow>
												<children>
													<tablecell>
														<children>
															<list ordered="1">
																<children>
																	<listrow>
																		<styles font-family="time" font-size="10pt" font-weight="normal"/>
																		<children>
																			<text fixtext="The restrictions on the expenditure of federal funds in appropriations acts are applicable to this subaward to the extent those restrictions are pertinent."/>
																		</children>
																	</listrow>
																	<listrow>
																		<styles font-family="time" font-size="10pt" font-weight="normal"/>
																		<children>
																			<text fixtext="45 CFR Part 74 or 45 CFR Part 92 as applicable."/>
																		</children>
																	</listrow>
																	<listrow>
																		<styles font-family="time" font-size="10pt" font-weight="normal"/>
																		<children>
																			<text fixtext="The NIH Grants Policy Statement, including addenda in effect as of the beginning date of the period of performance."/>
																		</children>
																	</listrow>
																	<listrow>
																		<styles font-family="time" font-size="10pt" font-weight="normal"/>
																		<children>
																			<text fixtext="Subrecipient assures, by signing this Subaward Agreement, that all Subrecipient’s personnel who are responsible for the design and conduct of projects involving human research participants have successfully completed their institutional training in accordance with the NIH Guide, Notice OD-00-039."/>
																		</children>
																	</listrow>
																	<listrow>
																		<styles font-family="time" font-size="10pt" font-weight="normal"/>
																		<children>
																			<text fixtext="Research Terms and Conditions found at "/>
																			<link>
																				<children>
																					<text fixtext="http://www.nsf.gov/bfa/dias/policy/rtc/terms.pdf"/>
																				</children>
																				<action>
																					<none/>
																				</action>
																				<bookmark/>
																				<hyperlink>
																					<fixtext value="nsf.gov/bfa/dias/policy/rtc/terms.pdf"/>
																				</hyperlink>
																			</link>
																			<text fixtext=" and Agency Specific Requirements found at "/>
																			<link>
																				<children>
																					<text fixtext="http://www.nsf.gov/pubs/policydocs/rtc/nih_708.pdf"/>
																				</children>
																				<action>
																					<none/>
																				</action>
																				<bookmark/>
																				<hyperlink>
																					<fixtext value="nsf.gov/pubs/policydocs/rtc/nih_708.pdf"/>
																				</hyperlink>
																			</link>
																			<text fixtext=", except for the following:"/>
																			<list ordered="1">
																				<properties type="a"/>
																				<children>
																					<listrow>
																						<children>
																							<text fixtext="The right to initiate an automatic one-time extension of the end date provided by Article 25(c)(2) of the Research Terms and Conditions is replaced by the need to obtain prior written approval from the Prime Recipient;"/>
																						</children>
																					</listrow>
																					<listrow>
																						<children>
																							<text fixtext="The payment mechanism described in Article 22 and the financial reporting requirements in Article 52 of the Research Terms and Conditions and Article 8 of the Agency-Specific Requirements are replaced with Terms and Conditions (1) through (4) of this Subaward Agreement; and"/>
																						</children>
																					</listrow>
																					<listrow>
																						<children>
																							<text fixtext="Any prior approvals are to be sought from the Prime Recipient and not the Federal Awarding Agency."/>
																						</children>
																					</listrow>
																				</children>
																			</list>
																		</children>
																	</listrow>
																	<listrow>
																		<children>
																			<text fixtext="Title to equipment costing $5,000 or more that is purchased or fabricated with research funds or Subrecipient cost sharing funds, as direct costs of the project or program, shall unconditionally vest in the Subrecipient upon acquisition without further obligation   to the Federal Awarding Agency subject to the conditions specified in Article 34(a) of the Research Terms and Conditions. "/>
																		</children>
																	</listrow>
																	<listrow>
																		<children>
																			<text fixtext="Treatment of Program Income: "/>
																			<condition>
																				<children>
																					<conditionbranch xpath="subcontract:SubContractData/subcontract:SubcontractTemplateInfo/subcontract:TreatmentPrgmIncomeAdditive = &quot;Y&quot;">
																						<children>
																							<image>
																								<properties height="8" width="8"/>
																								<target>
																									<xpath value="subcontract:SubContractData/subcontract:PrintRequirement/subcontract:ImageCheckedPath"/>
																									<fixtext value="checked.gif"/>
																								</target>
																								<imagesource>
																									<xpath value="subcontract:SubContractData/subcontract:PrintRequirement/subcontract:ImageCheckedPath"/>
																									<fixtext value="checked.gif"/>
																								</imagesource>
																							</image>
																						</children>
																					</conditionbranch>
																					<conditionbranch xpath="subcontract:SubContractData/subcontract:SubcontractTemplateInfo/subcontract:TreatmentPrgmIncomeAdditive = &quot;N&quot;">
																						<children>
																							<image>
																								<properties height="8" width="7"/>
																								<target>
																									<xpath value="subcontract:SubContractData/subcontract:PrintRequirement/subcontract:ImageUncheckedPath"/>
																									<fixtext value="checkbox.gif"/>
																								</target>
																								<imagesource>
																									<xpath value="subcontract:SubContractData/subcontract:PrintRequirement/subcontract:ImageUncheckedPath"/>
																									<fixtext value="checkbox.gif"/>
																								</imagesource>
																							</image>
																						</children>
																					</conditionbranch>
																				</children>
																			</condition>
																			<text fixtext=" Additive"/>
																		</children>
																	</listrow>
																</children>
															</list>
														</children>
													</tablecell>
												</children>
											</tablerow>
										</children>
									</tablebody>
								</children>
							</table>
							<newline/>
							<table>
								<properties border="0" width="100%"/>
								<styles font-family="time"/>
								<children>
									<tablebody>
										<children>
											<tablerow>
												<children>
													<tablecell>
														<properties width="28%"/>
														<styles font-family="times" font-size="12pt" font-weight="bold"/>
														<children>
															<text fixtext="Special terms and conditions: "/>
														</children>
													</tablecell>
													<tablecell>
														<styles font-family="time" font-size="10pt"/>
														<children>
															<text fixtext="[Institutions may include the following optional clauses.]"/>
														</children>
													</tablecell>
												</children>
											</tablerow>
											<tablerow>
												<styles font-family="time" font-size="10pt"/>
												<children>
													<tablecell>
														<properties colspan="2" width="28%"/>
														<styles font-family="times" font-size="12pt" font-weight="bold"/>
														<children>
															<list ordered="1">
																<styles font-family="time" font-size="10pt" font-weight="normal"/>
																<children>
																	<listrow>
																		<children>
																			<text fixtext="Copyrights"/>
																			<newline/>
																			<text fixtext="Subrecipient "/>
																			<condition>
																				<children>
																					<conditionbranch xpath="subcontract:SubContractData/subcontract:SubcontractTemplateInfo/subcontract:CopyRights = &quot;1&quot;">
																						<children>
																							<text fixtext="grants"/>
																						</children>
																					</conditionbranch>
																					<conditionbranch xpath="subcontract:SubContractData/subcontract:SubcontractTemplateInfo/subcontract:CopyRights = &quot;2&quot;">
																						<children>
																							<text fixtext="shall grant"/>
																						</children>
																					</conditionbranch>
																				</children>
																			</condition>
																			<text fixtext=" to Prime Recipient an irrevocable, royalty-free, non-transferable, non-exclusive right and license to use, reproduce, make derivative works, display, and perform publicly any copyrights or copyrighted material (including any computer software and its documentation and/or databases) first developed and delivered under this Subaward Agreement solely for the purpose of and only to the extent required to meet Prime Recipient’s obligations to the Federal Government under its Prime Award."/>
																		</children>
																	</listrow>
																	<listrow>
																		<children>
																			<text fixtext="Data Rights"/>
																			<newline/>
																			<text fixtext="Subrecipient "/>
																			<condition>
																				<children>
																					<conditionbranch xpath="subcontract:SubContractData/subcontract:SubcontractTemplateInfo/subcontract:CopyRights = &quot;1&quot;">
																						<children>
																							<text fixtext="grants"/>
																						</children>
																					</conditionbranch>
																					<conditionbranch xpath="subcontract:SubContractData/subcontract:SubcontractTemplateInfo/subcontract:CopyRights = &quot;2&quot;">
																						<children>
																							<text fixtext="shall grant"/>
																						</children>
																					</conditionbranch>
																				</children>
																			</condition>
																			<text fixtext=" to Prime Recipient the right to use data created in the performance of this Subaward Agreement solely for the purpose of and only to the extent required to meet Prime Recipient’s obligations to the Federal Government under its Prime Award."/>
																			<newline/>
																		</children>
																	</listrow>
																	<listrow>
																		<children>
																			<text fixtext="Automatic Carry Forward:  "/>
																			<condition>
																				<children>
																					<conditionbranch xpath="subcontract:SubContractData/subcontract:SubcontractTemplateInfo/subcontract:AutomaticCarryForward = &apos;Y&apos;">
																						<children>
																							<text fixtext="Yes"/>
																						</children>
																					</conditionbranch>
																					<conditionbranch xpath="subcontract:SubContractData/subcontract:SubcontractTemplateInfo/subcontract:AutomaticCarryForward = &apos;N&apos;">
																						<children>
																							<text fixtext="No"/>
																							<newline/>
																							<text fixtext="Carry Forward requests must be sent to Prime Recipient’s - "/>
																							<template match="subcontract:SubContractData" matchtype="schemagraphitem">
																								<editorproperties elementstodisplay="5"/>
																								<children>
																									<template match="subcontract:SubcontractTemplateInfo" matchtype="schemagraphitem">
																										<editorproperties elementstodisplay="5"/>
																										<children>
																											<template match="subcontract:CarryForwardRequestsSentToDescription" matchtype="schemagraphitem">
																												<editorproperties elementstodisplay="5"/>
																												<children>
																													<content>
																														<format datatype="string"/>
																													</content>
																												</children>
																											</template>
																										</children>
																									</template>
																								</children>
																							</template>
																							<text fixtext=", as shown in Attachment 3."/>
																							<newline/>
																						</children>
																					</conditionbranch>
																				</children>
																			</condition>
																			<text fixtext="     "/>
																			<newline/>
																		</children>
																	</listrow>
																</children>
															</list>
														</children>
													</tablecell>
												</children>
											</tablerow>
										</children>
									</tablebody>
								</children>
							</table>
							<newline/>
							<newline/>
							<newline/>
							<newline/>
						</children>
					</template>
					<newline/>
					<newline/>
					<newline/>
				</children>
			</globaltemplate>
		</children>
	</mainparts>
	<globalparts/>
	<pagelayout>
		<properties pagemultiplepages="0" pagenumberingformat="1" pagenumberingstartat="1" pageorientation="landscape" pageorientationportrait="portrait" paperheight="11in" papermarginbottom="0.79in" papermarginleft="0.2in" papermarginright="0.2in" papermargintop="0.5in" paperwidth="8.5in"/>
		<children>
			<globaltemplate match="#headerall" matchtype="named" parttype="headerall">
				<children>
					<template match="$XML" matchtype="schemasource">
						<editorproperties elementstodisplay="1"/>
					</template>
					<newline/>
				</children>
			</globaltemplate>
		</children>
	</pagelayout>
	<designfragments/>
</structure>
