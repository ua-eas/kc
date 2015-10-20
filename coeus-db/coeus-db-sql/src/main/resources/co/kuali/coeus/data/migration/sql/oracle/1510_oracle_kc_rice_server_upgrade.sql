--
-- Kuali Coeus, a comprehensive research administration system for higher education.
--
-- Copyright 2005-2015 Kuali, Inc.
--
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as
-- published by the Free Software Foundation, either version 3 of the
-- License, or (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU Affero General Public License for more details.
--
-- You should have received a copy of the GNU Affero General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.
--

set define off
set sqlblanklines on

spool 1510_oracle_kc_rice_server_upgrade.sql.log
@./rice/bootstrap/V1510_001__RESKC-771-CanadianProvinces.sql
@./rice/bootstrap/V1510_002__RESOPS-373.sql
@./rice/bootstrap/V1510_007__project_push_auth.sql
@./rice/bootstrap/V1510_008__RESKC-909-ProtocolDuplicatePersonAllowed.sql
@./rice/bootstrap/V1510_009__RESOPS-291.sql
@./rice/bootstrap/V1510_010__RESMER-84.sql
@./rice/bootstrap/V1510_013__RESMER-58_EnableIPGenerationPrompt.sql
@./rice/bootstrap/V1510_014__RESMER-84.sql
commit;
