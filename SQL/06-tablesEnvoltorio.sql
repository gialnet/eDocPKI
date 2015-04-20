

--------------------------------------------------------
-- S3_CONFIG
--------------------------------------------------------

CREATE SEQUENCE  s_S3_CONFIG  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

CREATE TABLE S3_CONFIG (
	ID 			NUMBER, 
	NOMBRE 		VARCHAR2(40),
	ACCESKEY 	VARCHAR2(40),
	SECRETKEY 	VARCHAR2(90)
);


CREATE OR REPLACE TRIGGER Trg_S3_CONFIG
	before insert on S3_CONFIG
	for each row 
begin  
	if inserting then 
		if :NEW.ID is null then 
			select s_S3_CONFIG.nextval into :NEW.ID from dual; 
		end if; 
	end if; 
end;
/

--------------------------------------------------------
-- S3_OBJECT
--------------------------------------------------------

CREATE SEQUENCE  s_S3_OBJECT  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

CREATE TABLE S3_OBJECT (
	ID 			NUMBER, 
	IDS3CONFIG	NUMBER,
	NAME 		VARCHAR2(250 BYTE)
);

CREATE OR REPLACE TRIGGER Trg_S3_OBJECT
	before insert on S3_OBJECT
	for each row 
begin  
	if inserting then 
		if :NEW.ID is null then 
			select s_S3_OBJECT.nextval into :NEW.ID from dual; 
		end if; 
	end if; 
end;
/

--------------------------------------------------------
-- TSA
--------------------------------------------------------

CREATE SEQUENCE  s_TSA  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

CREATE TABLE TSA (
	ID		   		NUMBER NOT NULL,
    NOMBRE 		VARCHAR2(100),
    CERTIFICADO 	BLOB,
    FECHA_INICIO 	DATE,
    FECHA_FIN 	DATE,
    N_SERIE 		VARCHAR2(40),
    LOGO 			BLOB,
    DIRECCION 	VARCHAR2(250),
    USUARIO   	VARCHAR2(50),
    PASSWORD  	VARCHAR2(50),
    METODO   	VARCHAR2(20),
    CONSTRAINT "TSA_PK" PRIMARY KEY (ID)
);

CREATE OR REPLACE TRIGGER Trg_TSA
	before insert on TSA
	for each row 
begin  
	if inserting then 
		if :NEW.ID is null then 
			select s_TSA.nextval into :NEW.ID from dual; 
		end if; 
	end if; 
end;
/

--------------------------------------------------------
-- Certification_Authorities
--------------------------------------------------------

DROP TABLE CERTIFICATION_AUTHORITIES cascade constraints;
DROP SEQUENCE CA_SEQUENCE;

CREATE SEQUENCE  CA_SEQUENCE  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

CREATE TABLE CERTIFICATION_AUTHORITIES (
	ID NUMBER, 
	NAME VARCHAR2(50 BYTE), 
	CANONICAL_NAME VARCHAR2(250 BYTE), 
	CERTIFICATE BLOB, 
	NOT_BEFORE DATE, 
	NOT_AFTER DATE, 
	SERIAL_NUMBER VARCHAR2(100 BYTE), 
	METHOD VARCHAR2(4 BYTE), 
	OCSP_URL VARCHAR2(250 BYTE), 
	COMMENTS VARCHAR2(512 BYTE)
);

ALTER TABLE CERTIFICATION_AUTHORITIES ADD CONSTRAINT CERTIFICATION_AUTHORITIES_PK PRIMARY KEY (ID);
ALTER TABLE CERTIFICATION_AUTHORITIES MODIFY (ID NOT NULL ENABLE);

CREATE OR REPLACE TRIGGER AC_TRIGGER
	before insert on CERTIFICATION_AUTHORITIES
	for each row 
begin  
	if inserting then 
		if :NEW.ID is null then 
			select CA_SEQUENCE.nextval into :NEW.ID from dual; 
		end if; 
	end if; 
end;
/

ALTER TRIGGER AC_TRIGGER ENABLE;
/

--------------------------------------------------------
-- CERTIFICATES_STORE
--------------------------------------------------------
CREATE SEQUENCE s_CERTIFICATES_STORE  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

CREATE TABLE CERTIFICATES_STORE
  (
    ID NUMBER NOT NULL ENABLE,
    CERTIFICADO BLOB,
    PASS        VARCHAR2(250),
    NIF        VARCHAR2(10),
    TIPO        VARCHAR2(20) DEFAULT 'SERVER',
    NOMBRE_CERT VARCHAR2(40) NOT NULL,
    CONSTRAINT CERTIFICATES_STORE_UK1 UNIQUE ("NOMBRE_CERT"),
    CONSTRAINT CERTIFICATES_STORE_PK PRIMARY KEY ("ID") 
  );
  
  

CREATE OR REPLACE TRIGGER trg_CERTIFICATES_STORE
	before insert on CERTIFICATES_STORE
	for each row 
begin  
	if inserting then 
		if :NEW.ID is null then 
			select s_CERTIFICATES_STORE.nextval into :NEW.ID from dual; 
		end if; 
	end if; 
end;
/