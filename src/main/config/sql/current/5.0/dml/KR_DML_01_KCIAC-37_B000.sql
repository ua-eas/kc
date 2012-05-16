UPDATE KRIM_PERM_T set NM = 'Submit IACUC Protocol' WHERE NM = 'Submit IACUC  protocol for Review'
/
UPDATE KRIM_PERM_T SET DESC_TXT='Submit an IACUC Protocol to committee for review' WHERE NM = 'Submit IACUC Protocol'
/
UPDATE KRIM_PERM_T set NM = 'Add IACUC Protocol Notes' WHERE NM = 'Add IACUC  Protocol Notes'
/
INSERT INTO KRIM_ROLE_T (ACTV_IND,DESC_TXT,KIM_TYP_ID,LAST_UPDT_DT,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','IACUC Administrator',(SELECT KIM_TYP_ID FROM KRIM_TYP_T WHERE NM = 'UnitHierarchy'),SYSDATE,'KC-UNT',SYS_GUID(),KRIM_ROLE_ID_BS_S.NEXTVAL,'IACUC Administrator',1)
/
INSERT INTO KRIM_ROLE_PERM_T (ROLE_PERM_ID,ROLE_ID,PERM_ID,ACTV_IND,OBJ_ID,VER_NBR)
  VALUES (KRIM_ROLE_PERM_ID_BS_S.NEXTVAL,KRIM_ROLE_ID_BS_S.CURRVAL,(SELECT PERM_ID FROM KRIM_PERM_T WHERE NM = 'Modify IACUC Protocol'),'Y',SYS_GUID(),1)
/
INSERT INTO KRIM_ROLE_PERM_T (ROLE_PERM_ID,ROLE_ID,PERM_ID,ACTV_IND,OBJ_ID,VER_NBR)
  VALUES (KRIM_ROLE_PERM_ID_BS_S.NEXTVAL,KRIM_ROLE_ID_BS_S.CURRVAL,(SELECT PERM_ID FROM KRIM_PERM_T WHERE NM = 'Submit IACUC Protocol'),'Y',SYS_GUID(),1)
/
INSERT INTO KRIM_ROLE_PERM_T (ROLE_PERM_ID,ROLE_ID,PERM_ID,ACTV_IND,OBJ_ID,VER_NBR)
  VALUES (KRIM_ROLE_PERM_ID_BS_S.NEXTVAL,KRIM_ROLE_ID_BS_S.CURRVAL,(SELECT PERM_ID FROM KRIM_PERM_T WHERE NM = 'Create IACUC Protocol'),'Y',SYS_GUID(),1)
/
INSERT INTO KRIM_ROLE_PERM_T (ROLE_PERM_ID,ROLE_ID,PERM_ID,ACTV_IND,OBJ_ID,VER_NBR)
  VALUES (KRIM_ROLE_PERM_ID_BS_S.NEXTVAL,KRIM_ROLE_ID_BS_S.CURRVAL,(SELECT PERM_ID FROM KRIM_PERM_T WHERE NM = 'Add IACUC Protocol Notes'),'Y',SYS_GUID(),1)
/
INSERT INTO KRIM_ROLE_T (ACTV_IND,DESC_TXT,KIM_TYP_ID,LAST_UPDT_DT,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','IACUC Reviewer',(SELECT KIM_TYP_ID FROM KRIM_TYP_T WHERE NM = 'UnitHierarchy'),SYSDATE,'KC-UNT',SYS_GUID(),KRIM_ROLE_ID_BS_S.NEXTVAL,'IACUC Reviewer',1)
/
INSERT INTO KRIM_ROLE_PERM_T (ROLE_PERM_ID,ROLE_ID,PERM_ID,ACTV_IND,OBJ_ID,VER_NBR)
  VALUES (KRIM_ROLE_PERM_ID_BS_S.NEXTVAL,(SELECT ROLE_ID FROM KRIM_ROLE_T WHERE ROLE_NM = 'IACUC Protocol Viewer' AND NMSPC_CD='KC-IACUC'),
    (SELECT PERM_ID FROM KRIM_PERM_T WHERE NM = 'View IACUC Protocol'),'Y',SYS_GUID(),1)
/
INSERT INTO KRIM_ROLE_PERM_T (ROLE_PERM_ID,ROLE_ID,PERM_ID,ACTV_IND,OBJ_ID,VER_NBR)
  VALUES (KRIM_ROLE_PERM_ID_BS_S.NEXTVAL,(SELECT ROLE_ID FROM KRIM_ROLE_T WHERE ROLE_NM = 'IACUC Administrator' AND NMSPC_CD='KC-UNT'),
    (SELECT PERM_ID FROM KRIM_PERM_T WHERE NM = 'View IACUC Protocol'),'Y',SYS_GUID(),1)
/
