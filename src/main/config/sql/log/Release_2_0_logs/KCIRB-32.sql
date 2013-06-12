Insert into SH_PARM_NMSPC_T
( SH_PARM_NMSPC_CD, OBJ_ID, VER_NBR, SH_PARM_NMSPC_NM, ACTIVE_IND)
Values 
('KC-PROTOCOL', '40B51A08D06DA044E043814FD881A041', 1, 'KC IRB Protocol', 'Y');

Insert into SH_PARM_DTL_TYP_T 
( SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, OBJ_ID, VER_NBR, SH_PARM_DTL_TYP_NM, ACTIVE_IND)
Values 
('KC-PROTOCOL','D','1EA4E50A05844D36964A0FCBB2992881',1,'Document','Y');

Insert into SH_PARM_T 
( SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, OBJ_ID, VER_NBR, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM, ACTIVE_IND)
Values 
('KC-PROTOCOL','D','irb.protocol.referenceID1','F1C228F9D4D8408A8E0BBC801C9525ab',1,'CONFG','Reference ID1','Referece id is configurable at impl time','A','WorkflowAdmin','Y');

Insert into SH_PARM_T 
( SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, OBJ_ID, VER_NBR, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM, ACTIVE_IND)
Values
('KC-PROTOCOL','D','irb.protocol.referenceID2','F1C228F9D4D8408A8E0BBC801C9525az',1,'CONFG','Reference ID2','Referece id is configurable at impl time','A','WorkflowAdmin','Y');

commit;