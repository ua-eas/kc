-- committeewebtest needs this principal
INSERT INTO KRIM_ENTITY_T (ACTV_IND,ENTITY_ID,LAST_UPDT_DT,OBJ_ID,VER_NBR)
  VALUES ('Y','1141',sysdate,sys_guid(),1)
;

INSERT INTO KRIM_PRNCPL_T (ACTV_IND,ENTITY_ID,LAST_UPDT_DT,OBJ_ID,PRNCPL_ID,PRNCPL_NM,VER_NBR)
  VALUES ('Y','1141',sysdate,sys_guid(),'aslusar','aslusar',1)
;

INSERT INTO KREN_RECIP_DELIV_T (CHNL,NM,RECIP_DELIV_ID,RECIP_ID,VER_NBR)
  VALUES ('KEW','mock',17,'aslusar',0)
;

insert into KRIM_ENTITY_ENT_TYP_T (OBJ_ID,VER_NBR,ENT_TYP_CD,ENTITY_ID,ACTV_IND,LAST_UPDT_DT)
 values(sys_guid(),1,'PERSON','1141','Y', sysdate)
;

INSERT INTO KRIM_ENTITY_EMAIL_T (ACTV_IND,DFLT_IND,EMAIL_ADDR,EMAIL_TYP_CD,ENTITY_EMAIL_ID,ENTITY_ID,ENT_TYP_CD,LAST_UPDT_DT,OBJ_ID,VER_NBR)
  VALUES ('Y','Y','aslusar@cornell.edu','CAMPUS','1241','1141','PERSON',sysdate,sys_guid(),1)
;
INSERT INTO KRIM_ENTITY_NM_T (ACTV_IND,DFLT_IND,ENTITY_ID,ENTITY_NM_ID,FIRST_NM,LAST_NM,LAST_UPDT_DT,NM_TYP_CD,OBJ_ID,VER_NBR)
  VALUES ('Y','Y','1141','1253','Andy ','Slusar',sysdate,'PFRD',sys_guid(),1)
;
commit;