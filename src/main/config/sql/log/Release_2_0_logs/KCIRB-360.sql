
DROP TABLE PROTOCOL_CORRESPONDENCE;
DROP TABLE PROTOCOL_ACTIONS;
DROP TABLE PROTOCOL_SUBMISSION;

/* PROTOCOL_SUBMISSION */

CREATE TABLE PROTOCOL_SUBMISSION (
    SUBMISSION_ID              NUMBER(12) NOT NULL,
    PROTOCOL_NUMBER            VARCHAR2(20) NOT NULL,
    SEQUENCE_NUMBER            NUMBER(4) NOT NULL,  
    SUBMISSION_NUMBER          NUMBER(4) NOT NULL,  
    SCHEDULE_ID                VARCHAR2(10),
    COMMITTEE_ID               VARCHAR2(15),
    PROTOCOL_ID                NUMBER(12,0) NOT NULL,
    SCHEDULE_ID_FK             NUMBER(12),
    COMMITTEE_ID_FK            NUMBER(12),
    SUBMISSION_TYPE_CODE       VARCHAR2(3) NOT NULL,
    SUBMISSION_TYPE_QUAL_CODE  VARCHAR2(3),
    SUBMISSION_STATUS_CODE     VARCHAR2(3) NOT NULL,  
    PROTOCOL_REVIEW_TYPE_CODE  VARCHAR2(3) NOT NULL,  
    SUBMISSION_DATE            DATE NOT NULL,
    COMMENTS                   VARCHAR2(2000),
    YES_VOTE_COUNT             NUMBER(3),
    NO_VOTE_COUNT              NUMBER(3),
    ABSTAINER_COUNT            NUMBER(3),
    VOTING_COMMENTS            VARCHAR2(2000),
    UPDATE_TIMESTAMP           DATE,
    UPDATE_USER                VARCHAR2(60),
    VER_NBR                    NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID                     VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

ALTER TABLE PROTOCOL_SUBMISSION 
ADD CONSTRAINT PK_PROTOCOL_SUBMISSION
PRIMARY KEY (SUBMISSION_ID);

ALTER TABLE PROTOCOL_SUBMISSION
ADD CONSTRAINT FK_PROTOCOL_SUBMISSION1
FOREIGN KEY (PROTOCOL_ID) 
REFERENCES PROTOCOL (PROTOCOL_ID);

ALTER TABLE PROTOCOL_SUBMISSION
ADD CONSTRAINT FK_PROTOCOL_SUBMISSION2
FOREIGN KEY (SCHEDULE_ID_FK) 
REFERENCES COMM_SCHEDULE (ID);

ALTER TABLE PROTOCOL_SUBMISSION
ADD CONSTRAINT FK_PROTOCOL_SUBMISSION3
FOREIGN KEY (COMMITTEE_ID_FK) 
REFERENCES COMMITTEE (ID);

ALTER TABLE PROTOCOL_SUBMISSION
ADD CONSTRAINT FK_PROTOCOL_SUBMISSION4
FOREIGN KEY (PROTOCOL_REVIEW_TYPE_CODE) 
REFERENCES PROTOCOL_REVIEW_TYPE (PROTOCOL_REVIEW_TYPE_CODE);

ALTER TABLE PROTOCOL_SUBMISSION
ADD CONSTRAINT FK_PROTOCOL_SUBMISSION5
FOREIGN KEY (SUBMISSION_STATUS_CODE) 
REFERENCES SUBMISSION_STATUS (SUBMISSION_STATUS_CODE);

ALTER TABLE PROTOCOL_SUBMISSION
ADD CONSTRAINT FK_PROTOCOL_SUBMISSION6
FOREIGN KEY (SUBMISSION_TYPE_CODE) 
REFERENCES SUBMISSION_TYPE (SUBMISSION_TYPE_CODE);

ALTER TABLE PROTOCOL_SUBMISSION
ADD CONSTRAINT FK_PROTOCOL_SUBMISSION7
FOREIGN KEY (SUBMISSION_TYPE_QUAL_CODE) 
REFERENCES SUBMISSION_TYPE_QUALIFIER (SUBMISSION_TYPE_QUAL_CODE);

CREATE or REPLACE VIEW OSP$PROTOCOL_SUBMISSION AS SELECT
    PROTOCOL_NUMBER,
    SEQUENCE_NUMBER,
    SUBMISSION_NUMBER,
    SCHEDULE_ID,
    COMMITTEE_ID,
    SUBMISSION_TYPE_CODE,
    SUBMISSION_TYPE_QUAL_CODE,
    PROTOCOL_REVIEW_TYPE_CODE
    SUBMISSION_STATUS_CODE,
    SUBMISSION_DATE,
    COMMENTS,
    YES_VOTE_COUNT,
    NO_VOTE_COUNT,
    ABSTAINER_COUNT,
    VOTING_COMMENTS,
    UPDATE_TIMESTAMP,
    UPDATE_USER
FROM PROTOCOL_SUBMISSION;

/* PROTOCOL_ACTIONS */

CREATE TABLE PROTOCOL_ACTIONS (
    PROTOCOL_ACTION_ID NUMBER(12,0) NOT NULL,
    PROTOCOL_NUMBER    VARCHAR2(20) NOT NULL,
    SEQUENCE_NUMBER    NUMBER(4) NOT NULL,
    SUBMISSION_NUMBER  NUMBER(4),
    ACTION_ID          NUMBER(6) NOT NULL,
    PROTOCOL_ACTION_TYPE_CODE VARCHAR2(3) NOT NULL,
    PROTOCOL_ID        NUMBER(12,0) NOT NULL,
    SUBMISSION_ID_FK   NUMBER(12,0),
    COMMENTS           VARCHAR2 (2000),
    ACTION_DATE        DATE,
    UPDATE_TIMESTAMP   DATE,
    UPDATE_USER        VARCHAR2(60),
    VER_NBR            NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID             VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

ALTER TABLE PROTOCOL_ACTIONS
ADD CONSTRAINT PK_PROTOCOL_ACTIONS
PRIMARY KEY (PROTOCOL_ACTION_ID);

ALTER TABLE PROTOCOL_ACTIONS
ADD CONSTRAINT FK_PROTOCOL_ACTIONS1
FOREIGN KEY (PROTOCOL_ID) 
REFERENCES PROTOCOL (PROTOCOL_ID);  

ALTER TABLE PROTOCOL_ACTIONS
ADD CONSTRAINT FK_PROTOCOL_ACTIONS2
FOREIGN KEY (SUBMISSION_ID_FK)
REFERENCES PROTOCOL_SUBMISSION (SUBMISSION_ID);

ALTER TABLE PROTOCOL_ACTIONS
ADD CONSTRAINT FK_PROTOCOL_ACTIONS3
FOREIGN KEY (PROTOCOL_ACTION_TYPE_CODE)
REFERENCES PROTOCOL_ACTION_TYPE (PROTOCOL_ACTION_TYPE_CODE);

CREATE or REPLACE VIEW OSP$PROTOCOL_ACTIONS AS SELECT
    PROTOCOL_NUMBER,
    SEQUENCE_NUMBER,
    SUBMISSION_NUMBER,
    ACTION_ID,
    COMMENTS,
    ACTION_DATE,
    UPDATE_TIMESTAMP,
    UPDATE_USER
FROM PROTOCOL_ACTIONS;

/* PROTOCOL_CORRESPONDENCE */

CREATE TABLE PROTOCOL_CORRESPONDENCE (
    ID                       NUMBER(12,0) NOT NULL,
    PROTOCOL_NUMBER          VARCHAR2(20) NOT NULL,
    SEQUENCE_NUMBER          NUMBER(4) NOT NULL,
    ACTION_ID                NUMBER(6) NOT NULL,
    PROTOCOL_ID              NUMBER(12,0) NOT NULL,
    ACTION_ID_FK             NUMBER(12,0) NOT NULL,
    PROTO_CORRESP_TYPE_CODE  VARCHAR2(3) NOT NULL,
    CORRESPONDENCE           BLOB DEFAULT EMPTY_BLOB(),
    FINAL_FLAG               CHAR(1) NOT NULL,
    UPDATE_TIMESTAMP         DATE,
    UPDATE_USER              VARCHAR2(60),
    VER_NBR                  NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID                   VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

ALTER TABLE PROTOCOL_CORRESPONDENCE
ADD CONSTRAINT PK_PROTOCOL_CORRESPONDENCE
PRIMARY KEY (ID);

ALTER TABLE PROTOCOL_CORRESPONDENCE
ADD CONSTRAINT FK_PROTOCOL_CORRESPONDENCE1
FOREIGN KEY (PROTOCOL_ID) 
REFERENCES PROTOCOL (PROTOCOL_ID);

ALTER TABLE PROTOCOL_CORRESPONDENCE
ADD CONSTRAINT FK_PROTOCOL_CORRESPONDENCE2
FOREIGN KEY (ACTION_ID_FK) 
REFERENCES PROTOCOL_ACTIONS (PROTOCOL_ACTION_ID);

ALTER TABLE PROTOCOL_CORRESPONDENCE
ADD CONSTRAINT FK_PROTOCOL_CORRESPONDENCE3
FOREIGN KEY (PROTO_CORRESP_TYPE_CODE) 
REFERENCES PROTO_CORRESP_TYPE (PROTO_CORRESP_TYPE_CODE);

CREATE or REPLACE VIEW OSP$PROTOCOL_CORRESPONDENCE AS SELECT
    PROTOCOL_NUMBER,
    SEQUENCE_NUMBER,
    ACTION_ID,
    PROTO_CORRESP_TYPE_CODE,
    CORRESPONDENCE,
    FINAL_FLAG,
    UPDATE_TIMESTAMP,
    UPDATE_USER
FROM PROTOCOL_CORRESPONDENCE;

/* PROTOCOL_REVIEWERS */

CREATE TABLE PROTOCOL_REVIEWERS ( 
    PROTOCOL_REVIEWER_ID NUMBER(12,0) NOT NULL,
    PROTOCOL_ID NUMBER(12,0) NOT NULL,
    SUBMISSION_ID_FK NUMBER(12,0) NOT NULL,
    PROTOCOL_NUMBER VARCHAR2(20) NOT NULL,
    SEQUENCE_NUMBER NUMBER(4,0) NOT NULL,
    SUBMISSION_NUMBER NUMBER(4,0) NOT NULL,
    PERSON_ID VARCHAR2(60) NOT NULL,
    NON_EMPLOYEE_FLAG CHAR(1) NOT NULL,
    REVIEWER_TYPE_CODE VARCHAR2(3) NOT NULL,
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);
    
ALTER TABLE PROTOCOL_REVIEWERS 
ADD CONSTRAINT PK_PROTOCOL_REVIEWERS 
PRIMARY KEY (PROTOCOL_REVIEWER_ID);

ALTER TABLE PROTOCOL_REVIEWERS
ADD CONSTRAINT FK_PROTOCOL_REVIEWERS1
FOREIGN KEY (PROTOCOL_ID)
REFERENCES PROTOCOL (PROTOCOL_ID);

ALTER TABLE PROTOCOL_REVIEWERS
ADD CONSTRAINT FK_PROTOCOL_REVIEWERS2
FOREIGN KEY (SUBMISSION_ID_FK)
REFERENCES PROTOCOL_SUBMISSION (SUBMISSION_ID);

ALTER TABLE PROTOCOL_REVIEWERS
ADD CONSTRAINT FK_PROTOCOL_REVIEWERS3
FOREIGN KEY (PERSON_ID)
REFERENCES PERSON (PERSON_ID);

ALTER TABLE PROTOCOL_REVIEWERS
ADD CONSTRAINT FK_PROTOCOL_REVIEWERS4
FOREIGN KEY (REVIEWER_TYPE_CODE)
REFERENCES PROTOCOL_REVIEWER_TYPE (REVIEWER_TYPE_CODE);

CREATE OR REPLACE VIEW OSP$PROTOCOL_REVIEWERS AS SELECT
    PROTOCOL_NUMBER,
    SEQUENCE_NUMBER,
    SUBMISSION_NUMBER,
    PERSON_ID,
    NON_EMPLOYEE_FLAG, 
    REVIEWER_TYPE_CODE,
    UPDATE_TIMESTAMP,
    UPDATE_USER
FROM PROTOCOL_REVIEWERS;

/* PROTOCOL_EXEMPT_CHKLST */

CREATE TABLE PROTOCOL_EXEMPT_CHKLST ( 
    PROTOCOL_EXEMPT_CHKLST_ID NUMBER(12,0) NOT NULL,
    PROTOCOL_ID NUMBER(12,0) NOT NULL,
    SUBMISSION_ID_FK NUMBER(12,0) NOT NULL,
    PROTOCOL_NUMBER VARCHAR2(20) NOT NULL,
    SEQUENCE_NUMBER NUMBER(4,0) NOT NULL,
    SUBMISSION_NUMBER NUMBER(4,0) NOT NULL,
    EXEMPT_STUDIES_CHECKLIST_CODE VARCHAR2(3) NOT NULL,
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);
    
ALTER TABLE PROTOCOL_EXEMPT_CHKLST
ADD CONSTRAINT PK_PROTOCOL_EXEMPT_CHKLST 
PRIMARY KEY (PROTOCOL_EXEMPT_CHKLST_ID);

ALTER TABLE PROTOCOL_EXEMPT_CHKLST
ADD CONSTRAINT FK_PROTOCOL_EXEMPT_CHKLST1
FOREIGN KEY (PROTOCOL_ID)
REFERENCES PROTOCOL (PROTOCOL_ID);

ALTER TABLE PROTOCOL_EXEMPT_CHKLST
ADD CONSTRAINT FK_PROTOCOL_EXEMPT_CHKLST2
FOREIGN KEY (SUBMISSION_ID_FK)
REFERENCES PROTOCOL_SUBMISSION (SUBMISSION_ID);

ALTER TABLE PROTOCOL_EXEMPT_CHKLST
ADD CONSTRAINT FK_PROTOCOL_EXEMPT_CHKLST3
FOREIGN KEY (EXEMPT_STUDIES_CHECKLIST_CODE)
REFERENCES EXEMPT_STUDIES_CHECKLIST (EXEMPT_STUDIES_CHECKLIST_CODE);

CREATE OR REPLACE VIEW OSP$PROTOCOL_EXEMPT_CHKLST AS SELECT
    PROTOCOL_NUMBER,
    SEQUENCE_NUMBER,
    SUBMISSION_NUMBER,
    EXEMPT_STUDIES_CHECKLIST_CODE,
    UPDATE_TIMESTAMP,
    UPDATE_USER
FROM PROTOCOL_EXEMPT_CHKLST;

/* PROTOCOL_EXPIDITED_CHKLST */

CREATE TABLE PROTOCOL_EXPIDITED_CHKLST ( 
    PROTOCOL_EXPEDITED_CHKLST_ID NUMBER(12,0) NOT NULL,
    PROTOCOL_ID NUMBER(12,0) NOT NULL,
    SUBMISSION_ID_FK NUMBER(12,0) NOT NULL,
    PROTOCOL_NUMBER VARCHAR2(20) NOT NULL,
    SEQUENCE_NUMBER NUMBER(4,0) NOT NULL,
    SUBMISSION_NUMBER NUMBER(4,0) NOT NULL,
    EXPEDITED_REV_CHKLST_CODE VARCHAR2(3) NOT NULL,
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);
    
ALTER TABLE PROTOCOL_EXPIDITED_CHKLST
ADD CONSTRAINT PK_PROTOCOL_EXPIDITED_CHKLST 
PRIMARY KEY (PROTOCOL_EXPEDITED_CHKLST_ID);

ALTER TABLE PROTOCOL_EXPIDITED_CHKLST
ADD CONSTRAINT FK_PROTOCOL_EXPIDITED_CHKLST1
FOREIGN KEY (PROTOCOL_ID)
REFERENCES PROTOCOL (PROTOCOL_ID);

ALTER TABLE PROTOCOL_EXPIDITED_CHKLST
ADD CONSTRAINT FK_PROTOCOL_EXPIDITED_CHKLST2
FOREIGN KEY (SUBMISSION_ID_FK)
REFERENCES PROTOCOL_SUBMISSION (SUBMISSION_ID);

ALTER TABLE PROTOCOL_EXPIDITED_CHKLST
ADD CONSTRAINT FK_PROTOCOL_EXPIDITED_CHKLST3
FOREIGN KEY (EXPEDITED_REV_CHKLST_CODE)
REFERENCES EXPEDITED_REVIEW_CHECKLIST (EXPEDITED_REV_CHKLST_CODE);

CREATE OR REPLACE VIEW OSP$PROTOCOL_EXPIDITED_CHKLST AS SELECT
    PROTOCOL_NUMBER,
    SEQUENCE_NUMBER,
    SUBMISSION_NUMBER,
    EXPEDITED_REV_CHKLST_CODE,
    UPDATE_TIMESTAMP,
    UPDATE_USER
FROM PROTOCOL_EXPIDITED_CHKLST;

COMMIT;