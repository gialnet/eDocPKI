
CREATE USER EDOC_ELEJIDO PROFILE "DEFAULT" IDENTIFIED BY "clave" DEFAULT TABLESPACE EDOCPKI 
TEMPORARY TABLESPACE "TEMP" ACCOUNT UNLOCK;

GRANT RESOURCE, CONNECT TO EDOC_ELEJIDO;

GRANT EXECUTE ON "SYS"."DBMS_CRYPTO" TO EDOC_ELEJIDO;
GRANT EXECUTE ON "EDOCPKI_V1"."BARCODE" TO EDOC_ELEJIDO;
GRANT EXECUTE ON "EDOCPKI_V1"."CERT" TO EDOC_ELEJIDO;
GRANT EXECUTE ON "EDOCPKI_V1"."MAIL" TO EDOC_ELEJIDO;
GRANT EXECUTE ON "EDOCPKI_V1"."PDF" TO EDOC_ELEJIDO;
GRANT EXECUTE ON "EDOCPKI_V1"."PDFFORM" TO EDOC_ELEJIDO;
GRANT EXECUTE ON "EDOCPKI_V1"."S3" TO EDOC_ELEJIDO;
GRANT EXECUTE ON "EDOCPKI_V1"."SIGNER" TO EDOC_ELEJIDO;
GRANT EXECUTE ON "EDOCPKI_V1"."TSTAMP" TO EDOC_ELEJIDO;
GRANT EXECUTE ON "EDOCPKI_V1"."UTILS" TO EDOC_ELEJIDO;
GRANT EXECUTE ON "EDOCPKI_V1"."WHOIS" TO EDOC_ELEJIDO;
GRANT EXECUTE ON "EDOCPKI_V1"."XADES" TO EDOC_ELEJIDO;