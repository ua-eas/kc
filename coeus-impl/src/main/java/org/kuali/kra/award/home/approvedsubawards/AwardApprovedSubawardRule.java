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
package org.kuali.kra.award.home.approvedsubawards;

import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * 
 * This interface declares the rule method associated with <code>AwardApprovedSubaward</code> 
 * Business Object.
 */
public interface AwardApprovedSubawardRule extends BusinessRule {
    /**
     * Rule invoked upon adding a Award Report Term Recipient
     * <code>{@link org.kuali.kra.award.document.AwardDocument}</code>
     *
     * @return boolean
     */
    boolean processApprovedSubawardBusinessRules(AwardApprovedSubawardRuleEvent 
                                                    awardApprovedSubawardRuleEvent);
}
