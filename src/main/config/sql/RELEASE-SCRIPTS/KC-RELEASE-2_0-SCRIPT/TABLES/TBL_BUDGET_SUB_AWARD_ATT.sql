ALTER TABLE BUDGET_SUB_AWARD_ATT ADD BUDGET_ID NUMBER (12, 0) ;

ALTER TABLE BUDGET_SUB_AWARD_ATT DROP PRIMARY KEY;

ALTER TABLE BUDGET_SUB_AWARD_ATT MODIFY PROPOSAL_NUMBER NULL;

ALTER TABLE BUDGET_SUB_AWARD_ATT MODIFY VERSION_NUMBER NULL;

UPDATE BUDGET_SUB_AWARD_ATT 
SET BUDGET_ID=(SELECT BUDGET_ID FROM BUDGET 
               WHERE BUDGET.PROPOSAL_NUMBER = BUDGET_SUB_AWARD_ATT.PROPOSAL_NUMBER
                     AND BUDGET.VERSION_NUMBER = BUDGET_SUB_AWARD_ATT.VERSION_NUMBER) ;

COMMIT;

ALTER TABLE BUDGET_SUB_AWARD_ATT 
    ADD CONSTRAINT PK_BUDGET_SUB_AWARD_ATT 
            PRIMARY KEY (BUDGET_ID, SUB_AWARD_NUMBER, CONTENT_ID) ;

ALTER TABLE BUDGET_SUB_AWARD_ATT 
    ADD CONSTRAINT FK1_BUDGET_SUB_AWARD_ATT 
            FOREIGN KEY (BUDGET_ID, SUB_AWARD_NUMBER) 
                REFERENCES BUDGET_SUB_AWARD_FILES;