CREATE TABLE PROTOCOL_ATTACHMENT_PROTOCOL (
    PA_PROTOCOL_ID NUMBER (12, 0) NOT NULL ENABLE, 
    PROTOCOL_ID_FK NUMBER (12, 0) NOT NULL, 
    PROTOCOL_NUMBER VARCHAR2 (20) NOT NULL, 
    SEQUENCE_NUMBER NUMBER (4, 0) NOT NULL ENABLE, 
    TYPE_CD VARCHAR2 (3) NOT NULL ENABLE, 
    DOCUMENT_ID NUMBER (4, 0) NOT NULL ENABLE, 
    FILE_ID NUMBER (22, 0) NOT NULL ENABLE, 
    DESCRIPTION VARCHAR2 (200) , 
    STATUS_CD VARCHAR2 (3) , 
    CONTACT_NAME VARCHAR2 (30) , 
    EMAIL_ADDRESS VARCHAR2 (60) , 
    PHONE_NUMBER VARCHAR2 (20) , 
    COMMENTS VARCHAR2 (300) , 
    VER_NBR NUMBER (8, 0) DEFAULT 1 NOT NULL ENABLE, 
    OBJ_ID VARCHAR2 (36) DEFAULT SYS_GUID () NOT NULL ENABLE, 
    UPDATE_TIMESTAMP DATE NOT NULL ENABLE, 
    UPDATE_USER VARCHAR2 (60) NOT NULL ENABLE) ;

ALTER TABLE PROTOCOL_ATTACHMENT_PROTOCOL 
    ADD CONSTRAINT PK_PROTOCOL_ATTACH_PROTOCOL 
            PRIMARY KEY (PA_PROTOCOL_ID) ;