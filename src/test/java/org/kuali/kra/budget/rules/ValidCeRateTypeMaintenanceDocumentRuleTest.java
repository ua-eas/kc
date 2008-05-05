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
package org.kuali.kra.budget.rules;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.RiceKeyConstants;
import org.kuali.core.UserSession;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.util.ErrorMessage;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kra.budget.bo.ValidCeRateType;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.maintenance.MaintenanceRuleTestBase;

public class ValidCeRateTypeMaintenanceDocumentRuleTest extends MaintenanceRuleTestBase {
    private ValidCeRateTypeMaintenanceDocumentRule rule = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        rule = new ValidCeRateTypeMaintenanceDocumentRule();
        GlobalVariables.setUserSession(new UserSession("quickstart"));
    }

    @After
    public void tearDown() throws Exception {
        rule = null;
        super.tearDown();
    }

    @Test
    public void testOK() throws Exception {

        ValidCeRateType validCeRateType = new ValidCeRateType();
        
        validCeRateType.setRateClassCode("10");
        validCeRateType.setRateTypeCode("1");
        validCeRateType.setCostElement("420111");
        MaintenanceDocument validCeRateTypeDocument = newMaintDoc(validCeRateType);
        assertTrue(rule.processCustomRouteDocumentBusinessRules(validCeRateTypeDocument));
        assertTrue(rule.processCustomApproveDocumentBusinessRules(validCeRateTypeDocument));
    }

    /**
     * 
     * This method to test rate type does not exist.
     * @throws Exception
     */
    @Test
    public void testRateTypeNotExist() throws Exception {

        ValidCeRateType validCeRateType = new ValidCeRateType();
        
        validCeRateType.setRateClassCode("10");
        validCeRateType.setRateTypeCode("2");
        validCeRateType.setCostElement("420111");
        MaintenanceDocument validCeRateTypeDocument = newMaintDoc(validCeRateType);
        assertFalse(rule.processCustomRouteDocumentBusinessRules(validCeRateTypeDocument));
        TypedArrayList errors = GlobalVariables.getErrorMap().getMessages("document.newMaintainableObject.rateTypeCode");
        assertTrue(errors.size() == 1);
        ErrorMessage message = (ErrorMessage) errors.get(0);
        assertEquals(message.getErrorKey(), KeyConstants.ERROR_RATE_TYPE_NOT_EXIST);

        // approve will have the same error too.
        assertFalse(rule.processCustomApproveDocumentBusinessRules(validCeRateTypeDocument));
        errors = GlobalVariables.getErrorMap().getMessages("document.newMaintainableObject.rateTypeCode");
        assertTrue(errors.size() == 1);
        message = (ErrorMessage) errors.get(0);
        assertEquals(message.getErrorKey(), KeyConstants.ERROR_RATE_TYPE_NOT_EXIST);

    }

    @Test
    public void testCostElementNotExist() throws Exception {

        ValidCeRateType validCeRateType = new ValidCeRateType();
        
        validCeRateType.setRateClassCode("10");
        validCeRateType.setRateTypeCode("1");
        validCeRateType.setCostElement("1x");
        MaintenanceDocument validCeRateTypeDocument = newMaintDoc(validCeRateType);
        assertFalse(rule.processCustomRouteDocumentBusinessRules(validCeRateTypeDocument));
        TypedArrayList errors = GlobalVariables.getErrorMap().getMessages("document.newMaintainableObject.costElement");
        assertTrue(errors.size() == 1);
        ErrorMessage message = (ErrorMessage) errors.get(0);
        assertEquals(message.getErrorKey(), RiceKeyConstants.ERROR_EXISTENCE);

        // approve will have the same error too.
        assertFalse(rule.processCustomApproveDocumentBusinessRules(validCeRateTypeDocument));
        errors = GlobalVariables.getErrorMap().getMessages("document.newMaintainableObject.costElement");
        assertTrue(errors.size() == 1);
        message = (ErrorMessage) errors.get(0);
        assertEquals(message.getErrorKey(), RiceKeyConstants.ERROR_EXISTENCE);

    }


}
