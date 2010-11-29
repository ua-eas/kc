INSERT INTO KRIM_PERM_ID_S VALUES (NULL);

INSERT INTO KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND)
SELECT MAX(ID), UUID(), 1, (SELECT PERM_TMPL_ID FROM KRIM_PERM_TMPL_T WHERE NMSPC_CD = 'KC-IDM' and NM = 'View Document Section'), 'KC-PROTOCOL',
    'Maintain Protocol Notes', 'permission for maintaining notes on a submitted protocol', 'Y' FROM KRIM_PERM_ID_S;

INSERT INTO KRIM_PERM_ID_S VALUES (NULL);

INSERT INTO KRIM_PERM_T (ACTV_IND,DESC_TXT,NM,NMSPC_CD,OBJ_ID,PERM_ID, PERM_TMPL_ID,VER_NBR)
SELECT 'Y','Create Award Account','Create Award Account','KC-AWARD',UUID(),MAX(ID),
(SELECT PERM_TMPL_ID FROM KRIM_PERM_TMPL_T WHERE NMSPC_CD = 'KC-IDM' AND NM = 'Perform Document Action'),1 from KRIM_PERM_ID_S;

COMMIT;
