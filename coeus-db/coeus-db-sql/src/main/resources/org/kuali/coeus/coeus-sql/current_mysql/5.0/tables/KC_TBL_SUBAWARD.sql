DELIMITER /
ALTER TABLE SUBAWARD_AMT_RELEASED ADD DOCUMENT_NUMBER VARCHAR(40)
/

ALTER TABLE SUBAWARD_AMT_RELEASED MODIFY CREATED_BY VARCHAR(60)
/

ALTER TABLE SUBAWARD_AMT_RELEASED ADD INVOICE_STATUS VARCHAR(3)
/

DELIMITER ;