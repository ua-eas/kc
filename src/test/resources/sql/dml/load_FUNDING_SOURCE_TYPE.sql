DELETE FROM FUNDING_SOURCE_TYPE;
INSERT INTO FUNDING_SOURCE_TYPE ( FUNDING_SOURCE_TYPE_CODE, DESCRIPTION, UPDATE_TIMESTAMP, UPDATE_USER, FUNDING_SOURCE_TYPE_FLAG ) 
VALUES ( 1, 'Sponsor', sysdate, user, null ); 
INSERT INTO FUNDING_SOURCE_TYPE ( FUNDING_SOURCE_TYPE_CODE, DESCRIPTION, UPDATE_TIMESTAMP, UPDATE_USER, FUNDING_SOURCE_TYPE_FLAG ) 
VALUES ( 2, 'Unit', sysdate, user, null ); 
INSERT INTO FUNDING_SOURCE_TYPE ( FUNDING_SOURCE_TYPE_CODE, DESCRIPTION, UPDATE_TIMESTAMP, UPDATE_USER, FUNDING_SOURCE_TYPE_FLAG ) 
VALUES ( 3, 'Other', sysdate, user, null ); 
INSERT INTO FUNDING_SOURCE_TYPE ( FUNDING_SOURCE_TYPE_CODE, DESCRIPTION, UPDATE_TIMESTAMP, UPDATE_USER, FUNDING_SOURCE_TYPE_FLAG ) 
VALUES ( 4, 'Development Proposal', sysdate, user, null ); 
INSERT INTO FUNDING_SOURCE_TYPE ( FUNDING_SOURCE_TYPE_CODE, DESCRIPTION, UPDATE_TIMESTAMP, UPDATE_USER, FUNDING_SOURCE_TYPE_FLAG ) 
VALUES ( 5, 'Institute Proposal', sysdate, user, null ); 
INSERT INTO FUNDING_SOURCE_TYPE ( FUNDING_SOURCE_TYPE_CODE, DESCRIPTION, UPDATE_TIMESTAMP, UPDATE_USER, FUNDING_SOURCE_TYPE_FLAG ) 
VALUES ( 6, 'Award', sysdate, user, null );