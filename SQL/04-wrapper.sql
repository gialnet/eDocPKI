+set serveroutput on size 1000000;
exec dbms_java.set_output(1000000);


CREATE OR REPLACE PACKAGE Utils AS

	Function HashBlobSH1(xBLOB IN BLOB) RETURN RAW;
	
END Utils;
/

CREATE OR REPLACE PACKAGE BODY Utils AS

	FUNCTION HashBlobSH1(xBLOB IN BLOB) RETURN RAW
	AS
		xTipo PLS_INTEGER := DBMS_CRYPTO.HASH_SH1;
	BEGIN
	
		RETURN DBMS_CRYPTO.Hash (src => xBLOB, typ => xTipo);
		
	END;

END Utils;
/



CREATE OR REPLACE PACKAGE Signer AS

	-- ******************************************************************************************************
	--												PKCS7
	-- ******************************************************************************************************
	

	FUNCTION PKCS7(keystore BLOB, password VARCHAR2, document BLOB) RETURN BLOB;
	
	FUNCTION PKCS7ByIDStore(xFile in Blob, xIDCert in Integer) return blob;
	
	FUNCTION PKCS7ByIDStoreUser(xFile in Blob, xIDCert in Integer,passwd in varchar2) return blob;
	
	FUNCTION PKCS7ByNameCert(xFile in Blob, xNameCert in varchar2) return blob;
	
	FUNCTION PKCS7ByNameCertUser(xFile in Blob, xNameCert in varchar2,passwd in varchar2) return blob;
	
		
	 -- ******************************************************************************************************
     -- 											Firmas PDF
     -- ******************************************************************************************************
	
	
	FUNCTION PDF(keystore BLOB, password VARCHAR2, document BLOB) RETURN BLOB;

	FUNCTION PdfByIDStore(xFile in Blob, xIDCert in Integer) return blob;
	
	FUNCTION PdfByIDStoreUser(xFile in Blob, xIDCert in Integer,passwd in varchar2) return blob;
	
	FUNCTION PdfByNameCert(xFile in Blob, xNameCert in varchar2) return blob;
	
	FUNCTION PdfByNameCertUser(xFile in Blob, xNameCert in varchar2,passwd in varchar2) return blob;
	
	 -- ******************************************************************************************************
     -- 											Firmas XADESBES Enveloped
     -- ******************************************************************************************************
	
	
	FUNCTION XAdESBESEnveloped(keystore BLOB, keystoreType VARCHAR2, keystorepassword VARCHAR2, privateKeyPassword VARCHAR2, document BLOB, name VARCHAR2, description VARCHAR2, mimeType VARCHAR2) RETURN BLOB;
	
	FUNCTION XBEnvelopedByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	FUNCTION XBEnvelopedByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	FUNCTION XBEnvelopedByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	FUNCTION XBEnvelopedByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	-- ******************************************************************************************************
     -- 											Firmas XADESBES Enveloping
     -- ******************************************************************************************************
	
	FUNCTION XAdESBESEnveloping(keystore BLOB, keystoreType VARCHAR2, keystorepassword VARCHAR2, privateKeyPassword VARCHAR2, document BLOB, name VARCHAR2, description VARCHAR2, mimeType VARCHAR2) RETURN BLOB;
	
	FUNCTION XBEnvelopingByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	FUNCTION XBEnvelopingByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	FUNCTION XBEnvelopingByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	FUNCTION XBEnvelopingByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	-- ******************************************************************************************************
     -- 											Firmas XADESBES Detached
     -- ******************************************************************************************************
		
	FUNCTION XAdESBESDetached(keystore BLOB, keystoreType VARCHAR2, keystorepassword VARCHAR2, privateKeyPassword VARCHAR2, document BLOB, name VARCHAR2, description VARCHAR2, mimeType VARCHAR2) RETURN BLOB;
	
	FUNCTION XBDetachedByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	FUNCTION XBDetachedByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	FUNCTION XBDetachedByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	FUNCTION XBDetachedByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	-- ******************************************************************************************************
     -- 											Firmas XADEST Authenticated Enveloped 
     -- ******************************************************************************************************	
	
	FUNCTION XAdESTAuthenticatedEnveloped(keystore BLOB, keystoreType VARCHAR2, keystorepassword VARCHAR2, privateKeyPassword VARCHAR2, document BLOB, name VARCHAR2, description VARCHAR2, mimeType VARCHAR2, tsaUrl VARCHAR2, tsaUserName VARCHAR2, tsaPassword VARCHAR2) RETURN BLOB;
	
	FUNCTION XTAuthEnvelopedByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTAuthEnvelopedByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTAuthEnvelopedByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTAuthEnvelopedByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	-- ******************************************************************************************************
     -- 											Firmas XADEST Authenticated Enveloping 
     -- ******************************************************************************************************	
	
	
	FUNCTION XAdESTAuthenticatedEnveloping(keystore BLOB, keystoreType VARCHAR2, keystorepassword VARCHAR2, privateKeyPassword VARCHAR2, document BLOB, name VARCHAR2, description VARCHAR2, mimeType VARCHAR2, tsaUrl VARCHAR2, tsaUserName VARCHAR2, tsaPassword VARCHAR2) RETURN BLOB;
	
	FUNCTION XTAuthEnvelopingByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTAuthEnvelopingByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTAuthEnvelopingByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTAuthEnvelopingByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	 -- ******************************************************************************************************
     -- 											Firmas XADEST Authenticated Detached 
     -- ******************************************************************************************************	
	
     
	FUNCTION XAdESTAuthenticatedDetached(keystore BLOB, keystoreType VARCHAR2, keystorepassword VARCHAR2, privateKeyPassword VARCHAR2, document BLOB, name VARCHAR2, description VARCHAR2, mimeType VARCHAR2, tsaUrl VARCHAR2, tsaUserName VARCHAR2, tsaPassword VARCHAR2) RETURN BLOB;
	
	FUNCTION XTAuthDetachedByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTAuthDetachedByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTAuthDetachedByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTAuthDetachedByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	
	-- ******************************************************************************************************
     -- 											Firmas XADEST Enveloped
     -- ******************************************************************************************************	
	
 	FUNCTION XAdESTEnveloped(keystore BLOB, keystoreType VARCHAR2, keystorepassword VARCHAR2, privateKeyPassword VARCHAR2, document BLOB, name VARCHAR2, description VARCHAR2, mimeType VARCHAR2, tsaUrl VARCHAR2) RETURN BLOB;
	
 	FUNCTION XTEnvelopedByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTEnvelopedByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTEnvelopedByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTEnvelopedByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
 	-- ******************************************************************************************************
     -- 											Firmas XADEST Enveloping
     -- ******************************************************************************************************	
	
 	
	FUNCTION XAdESTEnveloping(keystore BLOB, keystoreType VARCHAR2, keystorepassword VARCHAR2, privateKeyPassword VARCHAR2, document BLOB, name VARCHAR2, description VARCHAR2, mimeType VARCHAR2, tsaUrl VARCHAR2) RETURN BLOB;
	
	FUNCTION XTEnvelopingByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTEnvelopingByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTEnvelopingByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTEnvelopingByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	
	-- ******************************************************************************************************
     -- 											Firmas XADEST Detached
     -- ******************************************************************************************************	
	
	FUNCTION XAdESTDetached(keystore BLOB, keystoreType VARCHAR2, keystorepassword VARCHAR2, privateKeyPassword VARCHAR2, document BLOB, name VARCHAR2, description VARCHAR2, mimeType VARCHAR2, tsaUrl VARCHAR2) RETURN BLOB;
	
	FUNCTION XTDetachedByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTDetachedByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTDetachedByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTDetachedByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	
	
END Signer;
/

CREATE OR REPLACE PACKAGE BODY Signer AS

	FUNCTION PKCS7(keystore BLOB, password VARCHAR2, document BLOB) RETURN BLOB AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/dsig/PKCS7.createSignature(oracle.sql.BLOB, java.lang.String, oracle.sql.BLOB)
	return oracle.sql.BLOB';
	
	FUNCTION PDF(keystore BLOB, password VARCHAR2, document BLOB) RETURN BLOB AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/dsig/PDF.createSignature(oracle.sql.BLOB, java.lang.String, oracle.sql.BLOB)
	return oracle.sql.BLOB';	

	FUNCTION XAdESBESEnveloped(keystore BLOB, keystoreType VARCHAR2, keystorepassword VARCHAR2, privateKeyPassword VARCHAR2, document BLOB, name VARCHAR2, description VARCHAR2, mimeType VARCHAR2) RETURN BLOB
	AS LANGUAGE JAVA 
	NAME 'es/redmoon/pl/dsig/XAdES.createEnvelopedBES(oracle.sql.BLOB, java.lang.String, java.lang.String, java.lang.String, oracle.sql.BLOB, java.lang.String, java.lang.String, java.lang.String)
	return oracle.sql.BLOB';	
	
	FUNCTION XAdESBESEnveloping(keystore BLOB, keystoreType VARCHAR2, keystorepassword VARCHAR2, privateKeyPassword VARCHAR2, document BLOB, name VARCHAR2, description VARCHAR2, mimeType VARCHAR2) RETURN BLOB
	AS LANGUAGE JAVA 
	NAME 'es/redmoon/pl/dsig/XAdES.createEnvelopingBES(oracle.sql.BLOB, java.lang.String, java.lang.String, java.lang.String, oracle.sql.BLOB, java.lang.String, java.lang.String, java.lang.String)
	return oracle.sql.BLOB';
	
	FUNCTION XAdESBESDetached(keystore BLOB, keystoreType VARCHAR2, keystorepassword VARCHAR2, privateKeyPassword VARCHAR2, document BLOB, name VARCHAR2, description VARCHAR2, mimeType VARCHAR2) RETURN BLOB
	AS LANGUAGE JAVA 
	NAME 'es/redmoon/pl/dsig/XAdES.createDetachedBES(oracle.sql.BLOB, java.lang.String, java.lang.String, java.lang.String, oracle.sql.BLOB, java.lang.String, java.lang.String, java.lang.String)
	return oracle.sql.BLOB';
	
	FUNCTION XAdESTAuthenticatedEnveloped(keystore BLOB, keystoreType VARCHAR2, keystorepassword VARCHAR2, privateKeyPassword VARCHAR2, document BLOB, name VARCHAR2, description VARCHAR2, mimeType VARCHAR2, tsaUrl VARCHAR2, tsaUserName VARCHAR2, tsaPassword VARCHAR2) RETURN BLOB
	AS LANGUAGE JAVA 
	NAME 'es/redmoon/pl/dsig/XAdES.createEnvelopedBES_T(oracle.sql.BLOB, java.lang.String, java.lang.String, java.lang.String, oracle.sql.BLOB, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	return oracle.sql.BLOB';
	
	FUNCTION XAdESTAuthenticatedEnveloping(keystore BLOB, keystoreType VARCHAR2, keystorepassword VARCHAR2, privateKeyPassword VARCHAR2, document BLOB, name VARCHAR2, description VARCHAR2, mimeType VARCHAR2, tsaUrl VARCHAR2, tsaUserName VARCHAR2, tsaPassword VARCHAR2) RETURN BLOB
	AS LANGUAGE JAVA 
	NAME 'es/redmoon/pl/dsig/XAdES.createEnvelopingBES_T(oracle.sql.BLOB, java.lang.String, java.lang.String, java.lang.String, oracle.sql.BLOB, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	return oracle.sql.BLOB';
	
	FUNCTION XAdESTAuthenticatedDetached(keystore BLOB, keystoreType VARCHAR2, keystorepassword VARCHAR2, privateKeyPassword VARCHAR2, document BLOB, name VARCHAR2, description VARCHAR2, mimeType VARCHAR2, tsaUrl VARCHAR2, tsaUserName VARCHAR2, tsaPassword VARCHAR2) RETURN BLOB
	AS LANGUAGE JAVA 
	NAME 'es/redmoon/pl/dsig/XAdES.createDetachedBES_T(oracle.sql.BLOB, java.lang.String, java.lang.String, java.lang.String, oracle.sql.BLOB, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	return oracle.sql.BLOB';
	
	FUNCTION XAdESTEnveloped(keystore BLOB, keystoreType VARCHAR2, keystorepassword VARCHAR2, privateKeyPassword VARCHAR2, document BLOB, name VARCHAR2, description VARCHAR2, mimeType VARCHAR2, tsaUrl VARCHAR2) RETURN BLOB AS
	BEGIN
		RETURN XAdESTAuthenticatedEnveloped(keystore, keystoreType, keystorepassword, privateKeyPassword, document, name, description, mimeType, tsaUrl, NULL, NULL);
	END;
	
	FUNCTION XAdESTEnveloping(keystore BLOB, keystoreType VARCHAR2, keystorepassword VARCHAR2, privateKeyPassword VARCHAR2, document BLOB, name VARCHAR2, description VARCHAR2, mimeType VARCHAR2, tsaUrl VARCHAR2) RETURN BLOB AS
	BEGIN
		RETURN XAdESTAuthenticatedEnveloping(keystore, keystoreType, keystorepassword, privateKeyPassword, document, name, description, mimeType, tsaUrl, NULL, NULL);
	END;
	
	FUNCTION XAdESTDetached(keystore BLOB, keystoreType VARCHAR2, keystorepassword VARCHAR2, privateKeyPassword VARCHAR2, document BLOB, name VARCHAR2, description VARCHAR2, mimeType VARCHAR2, tsaUrl VARCHAR2) RETURN BLOB AS
	BEGIN
		RETURN XAdESTAuthenticatedDetached(keystore, keystoreType, keystorepassword, privateKeyPassword, document, name, description, mimeType, tsaUrl, NULL, NULL);
	END;
	
	
	-- ******************************************************************************************************
	--												PKCS7
	-- ******************************************************************************************************
	
	--
	-- Firmar a trav�s del ID del almac�n de certificados para una firma de Servidor
	--
	FUNCTION PKCS7ByIDStore(xFile in Blob, xIDCert in Integer) return blob
	as
		xCer BLOB := empty_blob();
		xPass varchar2(250);
	begin
	
		-- certificado y pass de certificates_store
		begin
		select certificado,pass into xCer,xPass from certificates_store where id=xIDCert;
		exception when no_data_found then
			begin
				return null;
			end;
		end;
		
		-- invocar a pkcs7
		return PKCS7(xCer,xPass,xFile);
		
	end;
	
	--
	-- Firmar a trav�s del ID del almac�n de certificados para una firma de Usuario Interno
	--
	FUNCTION PKCS7ByIDStoreUser(xFile in Blob, xIDCert in Integer,passwd in varchar2) return blob
	as
               xCer BLOB := empty_blob();
               
       begin
       
               -- certificado certificates_store
               begin

               select certificado into xCer from certificates_store
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a PKCS7
               return PKCS7(xCer,passwd,xFile);
               
       end;
	
	--
	-- Firmar por nombre de certificado en el almac�n
	--
   FUNCTION PKCS7ByNameCert(xFile in Blob, xNameCert in varchar2) return blob
       as
               xCer BLOB := empty_blob();
               xPass varchar2(250);
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado,pass into xCer,xPass from certificates_store
                       where NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a PKCS7
               return PKCS7(xCer,xPass,xFile);
               
       end;
       
	--
	-- Firma PKCS7 por nombre de certificado, pasa la contrase�a el usuario firmante
	--
	FUNCTION PKCS7ByNameCertUser(xFile in Blob, xNameCert in varchar2, passwd in varchar2) return blob
	as
               xCer BLOB := empty_blob();
               
       begin
       
               -- certificado certificates_store
               begin

               select certificado into xCer from certificates_store
                       where NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a PKCS7
               return PKCS7(xCer,passwd,xFile);
               
       end;
       
     -- ******************************************************************************************************
     -- 											Firmas PDF
     -- ******************************************************************************************************
	
	Function PdfByIDStore(xFile in Blob, xIDCert in Integer) return blob
	as
		xCer BLOB := empty_blob();
		xPass varchar2(250);
	begin
	
		-- certificado y pass de certificates_store
		begin
		select certificado,pass into xCer,xPass from certificates_store where id=xIDCert;
		exception when no_data_found then
			begin
				return null;
			end;
		end;
		
		-- invocar a pdf
		return pdf(xCer,xPass,xFile);
		
	end;
	
	--
	
	FUNCTION PdfByIDStoreUser(xFile in Blob, xIDCert in Integer,passwd in varchar2) return blob
	as
               xCer BLOB := empty_blob();
               
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado into xCer from certificates_store
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a PDF
               return Pdf(xCer,passwd,xFile);
               
       end;
	
	
	-- Firmar PDF por nombre de certificado en el almac�n
   FUNCTION PdfByNameCert(xFile in Blob, xNameCert in varchar2) return blob
       as
               xCer BLOB := empty_blob();
               xPass varchar2(250);
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado,pass into xCer,xPass from certificates_store
                       where NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a PDF
               return pdf(xCer,xPass,xFile);
               
       end;
	
	FUNCTION PdfByNameCertUser(xFile in Blob, xNameCert in varchar2,passwd in varchar2) return blob
	as
               xCer BLOB := empty_blob();
               
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado into xCer from certificates_store
                       where NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a PKCS7
               return Pdf(xCer,passwd,xFile);
               
       end;
	
     -- ******************************************************************************************************
     -- 											Firmas XadesBesEnveloped
     -- ******************************************************************************************************
	
       
    FUNCTION XBEnvelopedByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2) return BLOB
    as
               xCer BLOB := empty_blob();
               xPass varchar2(250);
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado,pass into xCer,xPass from certificates_store
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESBESEnveloped
               return XAdESBESEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
       end; 
	
	FUNCTION XBEnvelopedByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2) return BLOB
	 as
               xCer BLOB := empty_blob();
              
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado into xCer from certificates_store
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESBESEnveloped
               return XAdESBESEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
       end; 
	
	FUNCTION XBEnvelopedByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2) return BLOB
	 as
               xCer BLOB := empty_blob();
               xPass varchar2(250);
       begin
       
               -- certificado y pass de certificates_store
               begin

              select certificado,pass into xCer,xPass from certificates_store
                       where NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESBESEnveloped
               return XAdESBESEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
       end; 
	
	FUNCTION XBEnvelopedByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2) return BLOB
	as
               xCer BLOB := empty_blob();
              
       begin
       
               -- certificado y pass de certificates_store
               begin

              select certificado into xCer from certificates_store
                       where NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESBESEnveloped
               return XAdESBESEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
       end; 
       
       -- ******************************************************************************************************
     -- 											Firmas XADESBES Enveloping
     -- ******************************************************************************************************
	
	
	FUNCTION XBEnvelopingByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2) return BLOB
	  as
               xCer BLOB := empty_blob();
               xPass varchar2(250);
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado,pass into xCer,xPass from certificates_store
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESBESEnveloped
               return XAdESBESEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
       end; 
	
	FUNCTION XBEnvelopingByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2) return BLOB
	 as
               xCer BLOB := empty_blob();
              
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado into xCer from certificates_store
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESBESEnveloped
               return XAdESBESEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
       end; 
       
	FUNCTION XBEnvelopingByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2) return BLOB
	 as
               xCer BLOB := empty_blob();
               xPass varchar2(250);
       begin
       
               -- certificado y pass de certificates_store
               begin

              select certificado,pass into xCer,xPass from certificates_store
                       where NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESBESEnveloped
               return XAdESBESEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
       end; 
       
       
	FUNCTION XBEnvelopingByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2) return BLOB
	as
               xCer BLOB := empty_blob();
              
       begin
       
               -- certificado y pass de certificates_store
               begin

              select certificado into xCer from certificates_store
                       where NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESBESEnveloped
               return XAdESBESEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
       end; 
       
       -- ******************************************************************************************************
     -- 											Firmas XADESBES Detached
     -- ******************************************************************************************************
		
	FUNCTION XBDetachedByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2) return BLOB
	 as
               xCer BLOB := empty_blob();
               xPass varchar2(250);
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado,pass into xCer,xPass from certificates_store
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESBESEnveloped
               return XAdESBESDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
       end; 
	
	FUNCTION XBDetachedByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2) return BLOB
	 as
               xCer BLOB := empty_blob();
              
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado into xCer from certificates_store
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESBESEnveloped
               return XAdESBESDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
       end; 
       
	FUNCTION XBDetachedByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2) return BLOB
	 as
               xCer BLOB := empty_blob();
               xPass varchar2(250);
       begin
       
               -- certificado y pass de certificates_store
               begin

              select certificado,pass into xCer,xPass from certificates_store
                       where NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESBESEnveloped
               return XAdESBESDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
       end; 
	
	FUNCTION XBDetachedByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2) return BLOB
	as
               xCer BLOB := empty_blob();
              
       begin
       
               -- certificado y pass de certificates_store
               begin

              select certificado into xCer from certificates_store
                       where NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESBESDetached
               return XAdESBESDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
       end; 
	
       
	 -- ******************************************************************************************************
     -- 											Firmas XADEST Authenticated Enveloped 
     -- ******************************************************************************************************	
	
	FUNCTION XTAuthEnvelopedByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
	as
               xCer BLOB := empty_blob();
               xPass varchar2(250);
               xTSAurl varchar2(250);
               xUserTSA varchar2(50);
               xPassTSA varchar2(50);
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado,pass into xCer,xPass from certificates_store
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
                -- tsa
               begin

               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from datos_tsa
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return XAdESTAuthenticatedEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
       end; 
       
       
	FUNCTION XTAuthEnvelopedByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
	 as
               xCer BLOB := empty_blob();
               xTSAurl varchar2(250);
               xUserTSA varchar2(50);
               xPassTSA varchar2(50);
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado into xCer from certificates_store
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
                  -- tsa
               begin

               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return XAdESTAuthenticatedEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
       end; 
       
	FUNCTION XTAuthEnvelopedByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
	 as
               xCer BLOB := empty_blob();
               xPass varchar2(250);
               xTSAurl varchar2(250);
               xUserTSA varchar2(50);
               xPassTSA varchar2(50);
               
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado,pass into xCer,xPass from certificates_store
                        where NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
                  -- tsa
               begin

               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return XAdESTAuthenticatedEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
       end; 
       
	FUNCTION XTAuthEnvelopedByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
	as
               xCer BLOB := empty_blob();
              
               xTSAurl varchar2(250);
               xUserTSA varchar2(50);
               xPassTSA varchar2(50);
               
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado into xCer from certificates_store
                       where  NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
                  -- tsa
               begin

               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return XAdESTAuthenticatedEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
       end; 
       
      
	-- ******************************************************************************************************
     -- 											Firmas XADEST Authenticated Enveloping -
     -- ******************************************************************************************************	
	
	FUNCTION XTAuthEnvelopingByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
	as
               xCer BLOB := empty_blob();
               xPass varchar2(250);
               xTSAurl varchar2(250);
               xUserTSA varchar2(50);
               xPassTSA varchar2(50);
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado,pass into xCer,xPass from certificates_store
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- tsa
                begin
               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return XAdESTAuthenticatedEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
       end; 
	
	FUNCTION XTAuthEnvelopingByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
	 as
               xCer BLOB := empty_blob();
               xTSAurl varchar2(250);
               xUserTSA varchar2(50);
               xPassTSA varchar2(50);
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado into xCer from certificates_store
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
                  -- tsa
               begin

               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return XAdESTAuthenticatedEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
       end; 
	
	FUNCTION XTAuthEnvelopingByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
	as
               xCer BLOB := empty_blob();
               xPass varchar2(250);
               xTSAurl varchar2(250);
               xUserTSA varchar2(50);
               xPassTSA varchar2(50);
               
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado,pass into xCer,xPass from certificates_store
                       where  NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
                  -- tsa
               begin

               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return XAdESTAuthenticatedEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
       end; 
	
	FUNCTION XTAuthEnvelopingByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB 
	as
               xCer BLOB := empty_blob();
              
               xTSAurl varchar2(250);
               xUserTSA varchar2(50);
               xPassTSA varchar2(50);
               
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado into xCer from certificates_store
                       where  NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
                  -- tsa
               begin

               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloping
              
               return XAdESTAuthenticatedEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
       end; 
       
	 -- ******************************************************************************************************
     -- 											Firmas XADEST Authenticated Detached 
     -- ******************************************************************************************************	
	
	
	FUNCTION XTAuthDetachedByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
	as
               xCer BLOB := empty_blob();
               xPass varchar2(250);
               xTSAurl varchar2(250);
               xUserTSA varchar2(50);
               xPassTSA varchar2(50);
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado,pass into xCer,xPass from certificates_store
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
                -- tsa
                begin
               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return XAdESTAuthenticatedDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
       end; 
	
	FUNCTION XTAuthDetachedByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
	as
               xCer BLOB := empty_blob();
               xTSAurl varchar2(250);
               xUserTSA varchar2(50);
               xPassTSA varchar2(50);
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado into xCer from certificates_store
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
                  -- tsa
               begin

               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return XAdESTAuthenticatedDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
       end; 
       
	FUNCTION XTAuthDetachedByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
	as
               xCer BLOB := empty_blob();
               xPass varchar2(250);
               xTSAurl varchar2(250);
               xUserTSA varchar2(50);
               xPassTSA varchar2(50);
               
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado,pass into xCer,xPass from certificates_store
                        where NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
                  -- tsa
               begin

               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return XAdESTAuthenticatedDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
       end; 
	FUNCTION XTAuthDetachedByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
	as
               xCer BLOB := empty_blob();
              
               xTSAurl varchar2(250);
               xUserTSA varchar2(50);
               xPassTSA varchar2(50);
               
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado into xCer from certificates_store
                        where NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
                  -- tsa
               begin

               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloping
              
               return XAdESTAuthenticatedDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
       end; 
	
       
       -- ******************************************************************************************************
     -- 											Firmas XADEST Enveloped
     -- ******************************************************************************************************	
	
 	
	
 	FUNCTION XTEnvelopedByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
 	as
               xCer BLOB := empty_blob();
               xPass varchar2(250);
               xTSAurl varchar2(250);
               
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado,pass into xCer,xPass from certificates_store
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
                -- tsa
                begin
               select direccion into xTSAurl from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return XAdESTEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
       end;  
	
	FUNCTION XTEnvelopedByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
	as
               xCer BLOB := empty_blob();
               xTSAurl varchar2(250);
             
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado into xCer from certificates_store
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
                -- tsa
                begin
               select direccion into xTSAurl from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return XAdESTEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
       end; 
	
	FUNCTION XTEnvelopedByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
		as
               xCer BLOB := empty_blob();
               xPass varchar2(250);
               xTSAurl varchar2(250);
               
               
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado,pass into xCer,xPass from certificates_store
                        where NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
              
               -- tsa
                begin
               select direccion into xTSAurl from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return XAdESTEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
       end; 
	
	FUNCTION XTEnvelopedByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
	as
               xCer BLOB := empty_blob();
              
               xTSAurl varchar2(250);
             
               
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado into xCer from certificates_store
                      where NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
                 -- tsa
                begin
               select direccion into xTSAurl from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return XAdESTEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
       end; 
       
    -- ******************************************************************************************************
     -- 											Firmas XADEST Enveloping
     -- ******************************************************************************************************	
	

 	FUNCTION XTEnvelopingByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
 	as
               xCer BLOB := empty_blob();
               xPass varchar2(250);
               xTSAurl varchar2(250);
               
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado,pass into xCer,xPass from certificates_store
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
                -- tsa
                begin
               select direccion into xTSAurl from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return XAdESTEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
       end;  
	
	FUNCTION XTEnvelopingByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
	as
               xCer BLOB := empty_blob();
               xTSAurl varchar2(250);
             
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado into xCer from certificates_store
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
                -- tsa
                begin
               select direccion into xTSAurl from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return XAdESTEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
       end; 
	
	FUNCTION XTEnvelopingByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
		as
               xCer BLOB := empty_blob();
               xPass varchar2(250);
               xTSAurl varchar2(250);
               
               
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado,pass into xCer,xPass from certificates_store
                        where NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
              
               -- tsa
                begin
               select direccion into xTSAurl from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return XAdESTEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
       end; 
	
	FUNCTION XTEnvelopingByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
	as
               xCer BLOB := empty_blob();
              
               xTSAurl varchar2(250);
             
               
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado into xCer from certificates_store
                        where NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
                 -- tsa
                begin
               select direccion into xTSAurl from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return XAdESTEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
       end; 
       
       -- ******************************************************************************************************
     -- 											Firmas XADEST Detached
     -- ******************************************************************************************************	
	
	
       
 	FUNCTION XTDetachedByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
 	as
               xCer BLOB := empty_blob();
               xPass varchar2(250);
               xTSAurl varchar2(250);
               
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado,pass into xCer,xPass from certificates_store
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
                -- tsa
                begin
               select direccion into xTSAurl from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return XAdESTDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
       end;  
	
	FUNCTION XTDetachedByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
	as
               xCer BLOB := empty_blob();
               xTSAurl varchar2(250);
             
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado into xCer from certificates_store
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
                -- tsa
                begin
               select direccion into xTSAurl from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTDetached
              
               return XAdESTDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
       end; 
	
	FUNCTION XTDetachedByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
		as
               xCer BLOB := empty_blob();
               xPass varchar2(250);
               xTSAurl varchar2(250);
               
               
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado,pass into xCer,xPass from certificates_store
                        where NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
              
               -- tsa
                begin
               select direccion into xTSAurl from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTDetached
              
               return XAdESTDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
       end; 
	
	FUNCTION XTDetachedByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB
	as
               xCer BLOB := empty_blob();
              
               xTSAurl varchar2(250);
             
               
       begin
       
               -- certificado y pass de certificates_store
               begin

               select certificado into xCer from certificates_store
                        where NOMBRE_CERT=xNameCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
                 -- tsa
                begin
               select direccion into xTSAurl from datos_tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTDetached
              
               return XAdESTDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
       end; 
	
END Signer;
/

--
-- Examples: 
--

SELECT Signer.PKCS7(almacen, clave, documento) FROM prueba WHERE id=1;
SELECT Signer.PDF(almacen, clave, documento) FROM prueba WHERE id=1;
SELECT Signer.XAdESBESEnveloped(almacen, keystore_type, clave, private_key_password, documento, nombre, descripcion, mime) FROM prueba WHERE id=1;
SELECT Signer.XAdESBESEnveloping(almacen, keystore_type, clave, private_key_password, documento, nombre, descripcion, mime) FROM prueba WHERE id=1;
SELECT Signer.XAdESBESDetached(almacen, keystore_type, clave, private_key_password, documento, nombre, descripcion, mime) FROM prueba WHERE id=1;

SELECT Signer.XAdESTAuthenticatedEnveloped(almacen, keystore_type, clave, private_key_password, documento, nombre, descripcion, mime, tsa, tsa_user, tsa_password) FROM prueba WHERE id=2;
SELECT Signer.XAdESTAuthenticatedEnveloping(almacen, keystore_type, clave, private_key_password, documento, nombre, descripcion, mime, tsa, tsa_user, tsa_password) FROM prueba WHERE id=2;
SELECT Signer.XAdESTAuthenticatedDetached(almacen, keystore_type, clave, private_key_password, documento, nombre, descripcion, mime, tsa, tsa_user, tsa_password) FROM prueba WHERE id=2;

SELECT Signer.XAdESTEnveloped(almacen, keystore_type, clave, private_key_password, documento, nombre, descripcion, mime, tsa) FROM prueba WHERE id=1;
SELECT Signer.XAdESTEnveloping(almacen, keystore_type, clave, private_key_password, documento, nombre, descripcion, mime, tsa) FROM prueba WHERE id=1;
SELECT Signer.XAdESTDetached(almacen, keystore_type, clave, private_key_password, documento, nombre, descripcion, mime, tsa) FROM prueba WHERE id=1;



CREATE OR REPLACE PACKAGE Xades AS

	FUNCTION getSubject(signature BLOB) RETURN VARCHAR2;
	
	FUNCTION getSerial(signature BLOB) RETURN VARCHAR2;
	
	FUNCTION getNotBefore(signature BLOB) RETURN VARCHAR2;
	
	FUNCTION getNotAfter(signature BLOB) RETURN VARCHAR2;
	
END Xades;
/

CREATE OR REPLACE PACKAGE BODY Xades AS

	FUNCTION getSubject(signature BLOB) RETURN VARCHAR2 AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/dsig/XAdESSignature.getSubject(oracle.sql.BLOB) return java.lang.String';
	
	FUNCTION getSerial(signature BLOB) RETURN VARCHAR2 AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/dsig/XAdESSignature.getSerial(oracle.sql.BLOB) return java.lang.String';
	
	FUNCTION getNotBefore(signature BLOB) RETURN VARCHAR2 AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/dsig/XAdESSignature.getNotBefore(oracle.sql.BLOB) return java.lang.String';
	
	FUNCTION getNotAfter(signature BLOB) RETURN VARCHAR2 AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/dsig/XAdESSignature.getNotAfter(oracle.sql.BLOB) return java.lang.String';
	
END Xades;
/

--
-- Examples:
--
SELECT Xades.getSubject(firma_xades) FROM prueba where id=1;
SELECT Xades.getSerial(firma_xades) FROM prueba where id=1;
SELECT Xades.getNotBefore(firma_xades) FROM prueba where id=1;
SELECT Xades.getNotAfter(firma_xades) FROM prueba where id=1;



CREATE OR REPLACE PACKAGE Barcode AS

	FUNCTION createBarcode(format VARCHAR2, resolution NUMBER, width NUMBER, height NUMBER, text VARCHAR2) return BLOB;
		
END Barcode;
/


CREATE OR REPLACE PACKAGE BODY Barcode AS

	FUNCTION createBarcode(format VARCHAR2, resolution NUMBER, width NUMBER, height NUMBER, text VARCHAR2) return BLOB AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/barcodes/Barcode.createBarcode(java.lang.String, int, int, int, java.lang.String)
	return oracle.sql.BLOB';
	
END Barcode;
/

--
-- Examples:
--
SELECT Barcode.createBarcode('PDF417', 300, 300, 60, 'Some text') FROM dual;
SELECT Barcode.createBarcode('QR', 300, 75, 75, 'Some text') FROM dual;
SELECT Barcode.createBarcode('CODE39', 600, 300, 100, 'Some text') FROM dual;
SELECT Barcode.createBarcode('CODE128', 300, 350, 100, 'Some text') FROM dual;
SELECT Barcode.createBarcode('DATAMATRIX', 500, 0, 0, 'Some text') FROM dual;



CREATE OR REPLACE PACKAGE PDFForm AS

	FUNCTION getFormFields(form BLOB) return BLOB;
	
	FUNCTION fillForm(form BLOB, xml BLOB) return BLOB;
		
END PDFForm;
/

CREATE OR REPLACE PACKAGE BODY PDFForm AS

	FUNCTION getFormFields(form BLOB) return BLOB AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/forms/PDFForm.getFormFields(oracle.sql.BLOB)
	return oracle.sql.BLOB';

	FUNCTION fillForm(form BLOB, xml BLOB) return BLOB AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/forms/PDFForm.fillForm(oracle.sql.BLOB, oracle.sql.BLOB)
	return oracle.sql.BLOB';
	
END PDFForm;
/

--
-- Examples:
--
SELECT PDFForm.getFormFields(formulario) FROM formularios where id=1;
SELECT PDFForm.fillForm(formulario, xml_datos) FROM formularios where id=1;



CREATE OR REPLACE PACKAGE TStamp AS

	FUNCTION createTimeStamp(digest RAW, algorithm VARCHAR2, url VARCHAR2) RETURN BLOB;
	
	FUNCTION createXMLTimeStamp(digest RAW, algorithm VARCHAR2, url VARCHAR2) RETURN BLOB;
	
	FUNCTION createAuthTimeStamp(digest RAW, algorithm VARCHAR2, url VARCHAR2, userName VARCHAR2, userPassword VARCHAR2) RETURN BLOB;
	
	FUNCTION createAuthXMLTimeStamp(digest RAW, algorithm VARCHAR2, url VARCHAR2, userName VARCHAR2, userPassword VARCHAR2) RETURN BLOB;
	
	FUNCTION createRedSARATimeStamp(digest RAW, policy VARCHAR2, centro VARCHAR2) RETURN BLOB;
	
	FUNCTION createRedSARAXMLTimeStamp(digest RAW, policy VARCHAR2, centro VARCHAR2) RETURN BLOB;
			
	FUNCTION getGenTime(timeStamp BLOB) RETURN VARCHAR2;
	
	FUNCTION getSerialNumber(timeStamp BLOB) RETURN VARCHAR2;

	-- BLOB TimeStamp
	FUNCTION createTS_BlobSH1(document BLOB, url VARCHAR2) RETURN BLOB;
	
	FUNCTION tsBlobSH1byIDTsa(document BLOB, xIDTsa Integer) return BLOB;
	
	FUNCTION createXMLTS_BlobSH1(document BLOB, url VARCHAR2) RETURN BLOB;
	
	FUNCTION tsBlobXMLSH1byIDTsa(document BLOB, xIDTsa Integer) return BLOB;
	
	FUNCTION createAuthTS_BlobSH1(document BLOB, url VARCHAR2, userName VARCHAR2, userPassword VARCHAR2) RETURN BLOB;
	
	FUNCTION tsAuthBlobSH1byIDTsa(document BLOB, xIDTsa Integer) return BLOB;
	
	FUNCTION createAuthXMLTS_BlobSH1(document BLOB, url VARCHAR2, userName VARCHAR2, userPassword VARCHAR2) RETURN BLOB;
	
	FUNCTION tsAuthBlobXMLSH1byIDTsa(document BLOB, xIDTsa Integer) return BLOB;
		
	FUNCTION createRedSARATS_BlobSH1(document BLOB, policy VARCHAR2, centro VARCHAR2) RETURN BLOB;
	
	FUNCTION createRedSARAXMLTS_BlobSH1(document BLOB, policy VARCHAR2, centro VARCHAR2) RETURN BLOB;
	
END TStamp;
/

CREATE OR REPLACE PACKAGE BODY TStamp AS
	FUNCTION createTimeStamp(digest RAW, algorithm VARCHAR2, url VARCHAR2) RETURN BLOB AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/ts/TimeStamp.createTimeStamp(byte[], java.lang.String, java.lang.String)
	return oracle.sql.BLOB';
	
	FUNCTION createXMLTimeStamp(digest RAW, algorithm VARCHAR2, url VARCHAR2) RETURN BLOB AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/ts/TimeStamp.createXMLTimeStamp(byte[], java.lang.String, java.lang.String)
	return oracle.sql.BLOB';

	FUNCTION createAuthTimeStamp(digest RAW, algorithm VARCHAR2, url VARCHAR2, userName VARCHAR2, userPassword VARCHAR2) RETURN BLOB AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/ts/TimeStamp.createAuthenticatedTimeStamp(byte[], java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	return oracle.sql.BLOB';
	
	FUNCTION createAuthXMLTimeStamp(digest RAW, algorithm VARCHAR2, url VARCHAR2, userName VARCHAR2, userPassword VARCHAR2) RETURN BLOB AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/ts/TimeStamp.createXMLAuthenticatedTimeStamp(byte[], java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	return oracle.sql.BLOB';
	
	FUNCTION createRedSARATimeStamp(digest RAW, policy VARCHAR2, centro VARCHAR2) RETURN BLOB AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/ts/TimeStamp.createRedSARATimeStamp(byte[], java.lang.String, java.lang.String)
	return oracle.sql.BLOB';
	
	FUNCTION createRedSARAXMLTimeStamp(digest RAW, policy VARCHAR2, centro VARCHAR2) RETURN BLOB AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/ts/TimeStamp.createXMLRedSARATimeStamp(byte[], java.lang.String, java.lang.String)
	return oracle.sql.BLOB';
	
	FUNCTION getGenTime(timeStamp BLOB) RETURN VARCHAR2 AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/ts/TimeStamp.getGenTime(oracle.sql.BLOB)
	return java.lang.String';
	
	FUNCTION getSerialNumber(timeStamp BLOB) RETURN VARCHAR2 AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/ts/TimeStamp.getSerialNumber(oracle.sql.BLOB)
	return java.lang.String';

	-- BLOB TimeStamp
	FUNCTION createTS_BlobSH1(document BLOB, url VARCHAR2) RETURN BLOB AS
	BEGIN
		RETURN createTimeStamp(Utils.HashBlobSH1(document), 'SHA-1', url);
	END;
	
	FUNCTION createXMLTS_BlobSH1(document BLOB, url VARCHAR2) RETURN BLOB AS
	BEGIN
		RETURN createXMLTimeStamp(Utils.HashBlobSH1(document), 'SHA-1', url);
	END;
	
	FUNCTION createAuthTS_BlobSH1(document BLOB, url VARCHAR2, userName VARCHAR2, userPassword VARCHAR2) RETURN BLOB AS
	BEGIN
		RETURN createAuthTimeStamp(Utils.HashBlobSH1(document), 'SHA-1', url, userName, userPassword);
	END;
	
	FUNCTION createAuthXMLTS_BlobSH1(document BLOB, url VARCHAR2, userName VARCHAR2, userPassword VARCHAR2) RETURN BLOB AS
	BEGIN
		RETURN createAuthXMLTimeStamp(Utils.HashBlobSH1(document), 'SHA-1', url, userName, userPassword);
	END;
	
	FUNCTION createRedSARATS_BlobSH1(document BLOB, policy VARCHAR2, centro VARCHAR2) RETURN BLOB AS
	BEGIN
		RETURN createRedSARATimeStamp(Utils.HashBlobSH1(document), policy, centro);
	END;
	
	FUNCTION createRedSARAXMLTS_BlobSH1(document BLOB, policy VARCHAR2, centro VARCHAR2) RETURN BLOB AS
	BEGIN
		RETURN createRedSARAXMLTimeStamp(Utils.HashBlobSH1(document), policy, centro);
	END;
	
	--
	--	
	--
	FUNCTION tsBlobSH1byIDTsa(document BLOB, xIDTsa Integer) return BLOB
	AS
		xUrl varchar2(250);
	BEGIN
		begin
		Select direccion into xUrl from datos_tsa where id=xIDTsa;
		exception when no_data_found then
			begin
				return null;
			end;
		end;
		return createTS_BlobSH1(document,xUrl);
	END;
	
	--
	--
	--
	FUNCTION tsBlobXMLSH1byIDTsa(document BLOB, xIDTsa Integer) return BLOB
	AS
		xUrl varchar2(250);
	BEGIN
		begin
		Select direccion into xUrl from datos_tsa where id=xIDTsa;
		exception when no_data_found then
			begin
				return null;
			end;
		end;
		return tsBlobXMLSH1byIDTsa(document,xUrl);
	END;
	
	--
	--
	--		
	FUNCTION tsAuthBlobSH1byIDTsa(document BLOB, xIDTsa Integer) return BLOB
	AS
		xUrl varchar2(250);
		xUser varchar2(50);
		xPass varchar2(50);
	BEGIN
		begin
		Select direccion,usuario,password into xUrl,xUser,xPass from datos_tsa where id=xIDTsa;
		exception when no_data_found then
			begin
				return null;
			end;
		end;
		return createAuthTS_BlobSH1(document,xUrl,xUser,xPass);
	END;
	

	--
	--
	--		
	FUNCTION tsAuthBlobXMLSH1byIDTsa(document BLOB, xIDTsa Integer) return BLOB
	AS
		xUrl varchar2(250);
		xUser varchar2(50);
		xPass varchar2(50);
	BEGIN
		begin
		Select direccion,usuario,password into xUrl,xUser,xPass from datos_tsa where id=xIDTsa;
		exception when no_data_found then
			begin
				return null;
			end;
		end;
		return createAuthXMLTS_BlobSH1(document,xUrl,xUser,xPass);
	END;
	
END TStamp;
/

--
-- Examples:
--
SELECT TStamp.createTimeStamp(digest, 'sha1', tsa) FROM prueba WHERE id=1;
SELECT TStamp.createXMLTimeStamp(digest, 'sha1', tsa) FROM prueba WHERE id=1;
SELECT TStamp.createAuthTimeStamp(digest, 'sha1', tsa, tsa_user, tsa_password) FROM prueba WHERE id=2;
SELECT TStamp.createAuthXMLTimeStamp(digest, 'sha1', tsa, tsa_user, tsa_password) FROM prueba WHERE id=2;
SELECT TStamp.createRedSARATimeStamp(digest, 'sha1', 'politica', 'nombre_entidad') FROM prueba WHERE id=1;
SELECT TStamp.createRedSARAXMLTimeStamp(digest, 'sha1', 'politica', 'nombre_entidad') FROM prueba WHERE id=1;

-- Blob TimeStamp
SELECT TStamp.createTS_BlobSH1(documento, tsa) FROM prueba WHERE id=1;
SELECT TStamp.createXMLTS_BlobSH1(documento, tsa) FROM prueba WHERE id=1;
SELECT TStamp.createAuthTS_BlobSH1(documento, tsa, tsa_user, tsa_password) FROM prueba WHERE id=2;
SELECT TStamp.createAuthXMLTS_BlobSH1(documento, tsa, tsa_user, tsa_password) FROM prueba WHERE id=2;
SELECT TStamp.createRedSARATS_BlobSH1(documento, 'politica', 'nombre_entidad') FROM prueba WHERE id=1;
SELECT TStamp.createRedSARAXMLTS_BlobSH1(documento, 'politica', 'nombre_entidad') FROM prueba WHERE id=1;



CREATE OR REPLACE PACKAGE Whois AS

	FUNCTION getCountry(ip VARCHAR2) return VARCHAR2;
	
	FUNCTION getNameISP(ip VARCHAR2) return VARCHAR2;
	
END Whois;
/

CREATE OR REPLACE PACKAGE BODY Whois AS

FUNCTION getCountry(ip VARCHAR2) return VARCHAR2 AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/network/Whois.getCountry(java.lang.String) return java.lang.String';
	
FUNCTION getNameISP(ip VARCHAR2) return VARCHAR2 AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/network/Whois.getNameISP(java.lang.String) return java.lang.String';
	
END Whois;
/

--
-- Examples:
--

SELECT Whois.getCountry('173.194.34.240') from dual;
SELECT Whois.getNameISP('173.194.34.240') from dual;



CREATE OR REPLACE PACKAGE Cert AS

	FUNCTION validateCertificate(certificate BLOB) return NUMBER;
	FUNCTION validateXAdESSignature(xades BLOB) return NUMBER;
	FUNCTION getIDXAdESSignature(signature BLOB) RETURN VARCHAR2;
	FUNCTION getIDCertificate(certificate BLOB) RETURN VARCHAR2;
	
END Cert;
/

CREATE OR REPLACE PACKAGE BODY Cert AS

	FUNCTION validateCertificate(certificate BLOB) return NUMBER AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/certificates/Certificate.validate(oracle.sql.BLOB) return int';

	FUNCTION validateXAdESSignature(xades BLOB) return NUMBER AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/certificates/Certificate.validateXAdESSignature(oracle.sql.BLOB) return int';
	
	FUNCTION getIDXAdESSignature(signature BLOB) RETURN VARCHAR2 AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/certificates/Certificate.getIDXAdESSignature(oracle.sql.BLOB)
	return java.lang.String';
	
	FUNCTION getIDCertificate(certificate BLOB) RETURN VARCHAR2 AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/certificates/Certificate.getIDFromCertificate(oracle.sql.BLOB)
	return java.lang.String';

END Cert;
/

--
-- Examples:
--

SELECT Cert.validateCertificate(certificado) FROM firmas WHERE id=1;
SELECT Cert.validateXAdESSignature(firma_XAdES) FROM firmas WHERE id=1;
SELECT Cert.getIDXAdESSignature(firma_XAdES) FROM firmas WHERE id=1;
SELECT Cert.getIDCertificate(certificado) FROM firmas WHERE id=1;


CREATE OR REPLACE PACKAGE PDF AS

	FUNCTION addBarcode(original BLOB, format VARCHAR2, resolution NUMBER, width NUMBER, height NUMBER, test VARCHAR2, absoluteX FLOAT, absoluteY FLOAT) return BLOB;
		
END PDF;
/

CREATE OR REPLACE PACKAGE BODY PDF AS

	FUNCTION addBarcode(original BLOB, format VARCHAR2, resolution NUMBER, width NUMBER, height NUMBER, test VARCHAR2, absoluteX FLOAT, absoluteY FLOAT) return BLOB AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/pdfs/PDF.addBarcode(oracle.sql.BLOB, java.lang.String, int, int, int, java.lang.String, float, float)
	return oracle.sql.BLOB';

END PDF;
/

--
-- Examples:
--

SELECT PDF.addBarcode(template, 'PDF417', 300, 200, 50, 'Hello world', 150, 75) from pdf_templates WHERE id = 1;
SELECT PDF.addBarcode(template, 'QR', 300, 75, 75, 'Hello world', 400, 275) from pdf_templates WHERE id = 1;
SELECT PDF.addBarcode(template, 'CODE39', 200, 0, 0 'Hello world', 0, 275) from pdf_templates WHERE id = 1;
SELECT PDF.addBarcode(template, 'CODE128', 270, 0, 0, 'Hello world', 0, 350) from pdf_templates WHERE id = 1;
SELECT PDF.addBarcode(template, 'DATAMATRIX', 300, 0 , 0, 'Hello world', 425, 175) from pdf_templates WHERE id = 1;



CREATE OR REPLACE PACKAGE S3 AS

	-- PL/SQL functions
	FUNCTION createBucketPL(accessKey VARCHAR2, secretKey VARCHAR2, bucketName VARCHAR2) return BOOLEAN;
	FUNCTION deleteBucketPL(accessKey VARCHAR2, secretKey VARCHAR2, bucketName VARCHAR2) return BOOLEAN;
	FUNCTION putObjectPL(accessKey VARCHAR2, secretKey VARCHAR2, bucketName VARCHAR2, object BLOB, objectKey VARCHAR2) return BOOLEAN;
	FUNCTION getObjectPL(accessKey VARCHAR2, secretKey VARCHAR2, bucketName VARCHAR2, objectKey VARCHAR2) return BLOB;
	FUNCTION deleteObjectPL(accessKey VARCHAR2, secretKey VARCHAR2, bucketName VARCHAR2, objectKey VARCHAR2) return BOOLEAN;
	
	-- SQL functions
	FUNCTION createBucket(accessKey VARCHAR2, secretKey VARCHAR2, bucketName VARCHAR2) return NUMBER;
	FUNCTION deleteBucket(accessKey VARCHAR2, secretKey VARCHAR2, bucketName VARCHAR2) return NUMBER;
	FUNCTION putObject(accessKey VARCHAR2, secretKey VARCHAR2, bucketName VARCHAR2, object BLOB, objectKey VARCHAR2) return NUMBER;
	FUNCTION getObject(accessKey VARCHAR2, secretKey VARCHAR2, bucketName VARCHAR2, objectKey VARCHAR2) return BLOB;
	FUNCTION deleteObject(accessKey VARCHAR2, secretKey VARCHAR2, bucketName VARCHAR2, objectKey VARCHAR2) return NUMBER;
	
END S3;
/

CREATE OR REPLACE PACKAGE BODY S3 AS
	
	-- PL/SQL functions
	FUNCTION createBucketPL(accessKey VARCHAR2, secretKey VARCHAR2, bucketName VARCHAR2) return BOOLEAN AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/amazon/S3.createBucket(java.lang.String, java.lang.String, java.lang.String)
	return boolean';
	
	FUNCTION deleteBucketPL(accessKey VARCHAR2, secretKey VARCHAR2, bucketName VARCHAR2) return BOOLEAN AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/amazon/S3.deleteBucket(java.lang.String, java.lang.String, java.lang.String)
	return boolean';
	
	FUNCTION putObjectPL(accessKey VARCHAR2, secretKey VARCHAR2, bucketName VARCHAR2, object BLOB, objectKey VARCHAR2) return BOOLEAN AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/amazon/S3.putObject(java.lang.String, java.lang.String, java.lang.String, oracle.sql.BLOB, java.lang.String)
	return boolean';
	
	FUNCTION getObjectPL(accessKey VARCHAR2, secretKey VARCHAR2, bucketName VARCHAR2, objectKey VARCHAR2) return BLOB AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/amazon/S3.getObject(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	return oracle.sql.BLOB';
	
	FUNCTION deleteObjectPL(accessKey VARCHAR2, secretKey VARCHAR2, bucketName VARCHAR2, objectKey VARCHAR2) return BOOLEAN AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/amazon/S3.deleteObject(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	return boolean';
	
	
	-- SQL functions
	FUNCTION createBucket(accessKey VARCHAR2, secretKey VARCHAR2, bucketName VARCHAR2) return NUMBER AS
	BEGIN
		IF (createBucketPL(accessKey, secretKey, bucketName)) THEN
			RETURN 1;
		ELSE
			RETURN 0;
		END IF;			
	END;
	
	FUNCTION deleteBucket(accessKey VARCHAR2, secretKey VARCHAR2, bucketName VARCHAR2) return NUMBER AS
	BEGIN
		IF (deleteBucketPL(accessKey, secretKey, bucketName)) THEN
			RETURN 1;
		ELSE
			RETURN 0;
		END IF;			
	END;
	
	FUNCTION putObject(accessKey VARCHAR2, secretKey VARCHAR2, bucketName VARCHAR2, object BLOB, objectKey VARCHAR2) return NUMBER AS
	BEGIN
		IF (putObjectPL(accessKey, secretKey, bucketName, object, objectKey)) THEN
			RETURN 1;
		ELSE
			RETURN 0;
		END IF;			
	END;
	
	FUNCTION getObject(accessKey VARCHAR2, secretKey VARCHAR2, bucketName VARCHAR2, objectKey VARCHAR2) return BLOB AS
	BEGIN
		RETURN getObjectPL(accessKey, secretKey, bucketName, objectKey);
	END;
	
	FUNCTION deleteObject(accessKey VARCHAR2, secretKey VARCHAR2, bucketName VARCHAR2, objectKey VARCHAR2) return NUMBER AS
	BEGIN
		IF (deleteObjectPL(accessKey, secretKey, bucketName, objectKey)) THEN
			RETURN 1;
		ELSE
			RETURN 0;
		END IF;			
	END;
	
	
END S3;
/

--
-- Examples:
--

SELECT S3.createBucket('0H8T70J0T1AJFR4WQGR2', 'UgzE0fxyyWG4Oc1s64ehib8jzDcKoz3FGiCZZyH9', 'test-angel') from dual;
SELECT S3.deleteBucket('0H8T70J0T1AJFR4WQGR2', 'UgzE0fxyyWG4Oc1s64ehib8jzDcKoz3FGiCZZyH9', 'test-angel') from dual;
SELECT S3.putObject('0H8T70J0T1AJFR4WQGR2', 'UgzE0fxyyWG4Oc1s64ehib8jzDcKoz3FGiCZZyH9', 'test-angel', documento, 'originalFile') from prueba where id = 1;
SELECT S3.getObject('0H8T70J0T1AJFR4WQGR2', 'UgzE0fxyyWG4Oc1s64ehib8jzDcKoz3FGiCZZyH9', 'test-angel', 'originalFile') from dual;
SELECT S3.deleteObject('0H8T70J0T1AJFR4WQGR2', 'UgzE0fxyyWG4Oc1s64ehib8jzDcKoz3FGiCZZyH9', 'test-angel', 'originalFile') from dual;



CREATE OR REPLACE PACKAGE Mail AS

	FUNCTION createContent(mailType VARCHAR2, text VARCHAR2) RETURN BLOB;
	FUNCTION createContent(mailType VARCHAR2, text CLOB) RETURN BLOB;
	FUNCTION addAttachment (mailContent BLOB, mailType VARCHAR2, attachment BLOB, name VARCHAR2, mimeType VARCHAR2) RETURN BLOB;
	FUNCTION sendMail (fromAddress VARCHAR2, toAddresses VARCHAR2, ccAddresses VARCHAR2, bccAddresses VARCHAR2, subject VARCHAR2, host VARCHAR2, port VARCHAR2, serverSecurity VARCHAR2, userName VARCHAR2, passwd VARCHAR2, mailContent BLOB, mailType VARCHAR2) RETURN BOOLEAN;

	FUNCTION createTextContent(text VARCHAR2) RETURN BLOB;
	FUNCTION createTextContent(text CLOB) RETURN BLOB;
	FUNCTION addTextAttachment (mailContent BLOB, attachment BLOB, name VARCHAR2, mimeType VARCHAR2) RETURN BLOB;
	
	FUNCTION createHtmlContent(html VARCHAR2) RETURN BLOB;
	FUNCTION createHtmlContent(html CLOB) RETURN BLOB;
	FUNCTION addHtmlAttachment (mailContent BLOB, attachment BLOB, name VARCHAR2, mimeType VARCHAR2) RETURN BLOB;
	FUNCTION addHtmlImage (mailContent BLOB, image BLOB, cid VARCHAR2, name VARCHAR2, mimeType VARCHAR2) RETURN BLOB;
	
	FUNCTION loadNewMails (accountID NUMBER, folder VARCHAR2) RETURN NUMBER;
	
	-- PL/SQL functions
	FUNCTION sendTextMailPL (accountID NUMBER, toAddresses VARCHAR2, ccAddresses VARCHAR2, bccAddresses VARCHAR2, subject VARCHAR2, mailContent BLOB) RETURN BOOLEAN;
	
	FUNCTION sendHtmlMailPL (accountID NUMBER, toAddresses VARCHAR2, ccAddresses VARCHAR2, bccAddresses VARCHAR2, subject VARCHAR2, mailContent BLOB) RETURN BOOLEAN; 
	
	-- SQL functions
	FUNCTION sendTextMail (accountID NUMBER, toAddresses VARCHAR2, ccAddresses VARCHAR2, bccAddresses VARCHAR2, subject VARCHAR2, mailContent BLOB) RETURN NUMBER;
	
	FUNCTION sendHtmlMail (accountID NUMBER, toAddresses VARCHAR2, ccAddresses VARCHAR2, bccAddresses VARCHAR2, subject VARCHAR2, mailContent BLOB) RETURN NUMBER;
	
END Mail;
/

CREATE OR REPLACE PACKAGE BODY Mail AS

	FUNCTION createContent (mailType VARCHAR2, text VARCHAR2) RETURN BLOB AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/mail/Mail.createContent(java.lang.String, java.lang.String)
	return oracle.sql.BLOB';
	
	FUNCTION createContent (mailType VARCHAR2, text CLOB) RETURN BLOB AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/mail/Mail.createContent(java.lang.String, oracle.sql.CLOB)
	return oracle.sql.BLOB';
	
	FUNCTION addAttachment (mailContent BLOB, mailType VARCHAR2, attachment BLOB, name VARCHAR2, mimeType VARCHAR2) RETURN BLOB AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/mail/Mail.addAttachment (oracle.sql.BLOB, java.lang.String, oracle.sql.BLOB, java.lang.String, java.lang.String)
	return oracle.sql.BLOB';
	
	FUNCTION sendMail (fromAddress VARCHAR2, toAddresses VARCHAR2, ccAddresses VARCHAR2, bccAddresses VARCHAR2, subject VARCHAR2, host VARCHAR2, port VARCHAR2, serverSecurity VARCHAR2, userName VARCHAR2, passwd VARCHAR2, mailContent BLOB, mailType VARCHAR2) RETURN BOOLEAN AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/mail/Mail.sendMessage (java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, oracle.sql.BLOB, java.lang.String)
	return boolean';
	
	FUNCTION createTextContent(text VARCHAR2) RETURN BLOB AS
	BEGIN
		RETURN createContent('mixed', text);
	END;
	
	FUNCTION createTextContent(text CLOB) RETURN BLOB AS
	BEGIN
		RETURN createContent('mixed', text);
	END;
	
	FUNCTION addTextAttachment (mailContent BLOB, attachment BLOB, name VARCHAR2, mimeType VARCHAR2) RETURN BLOB AS
	BEGIN
		RETURN addAttachment (mailContent, 'mixed', attachment, name, mimeType);
	END;
	
	FUNCTION createHtmlContent(html VARCHAR2) RETURN BLOB AS
	BEGIN
		RETURN createContent('related', html);
	END;
	
	FUNCTION createHtmlContent(html CLOB) RETURN BLOB AS
	BEGIN
		RETURN createContent('related', html);
	END;
	
	FUNCTION addHtmlAttachment (mailContent BLOB, attachment BLOB, name VARCHAR2, mimeType VARCHAR2) RETURN BLOB AS
	BEGIN
		RETURN addAttachment (mailContent, 'related', attachment, name, mimeType);
	END;
	
	FUNCTION addHtmlImage (mailContent BLOB, image BLOB, cid VARCHAR2, name VARCHAR2, mimeType VARCHAR2) RETURN BLOB AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/mail/Mail.addHtmlImage (oracle.sql.BLOB, oracle.sql.BLOB, java.lang.String, java.lang.String, java.lang.String)
	return oracle.sql.BLOB';
	
	FUNCTION loadNewMails (accountID NUMBER, folder VARCHAR2) RETURN NUMBER AS LANGUAGE JAVA
	NAME 'es/redmoon/pl/mail/Mail.loadNewMails (long, java.lang.String)
	return long';
	
	-- PL/SQL functions
	
	FUNCTION sendTextMailPL (accountID NUMBER, toAddresses VARCHAR2, ccAddresses VARCHAR2, bccAddresses VARCHAR2, subject VARCHAR2, mailContent BLOB) RETURN BOOLEAN AS
		xFromAddress	VARCHAR2(250);
		xHost		VARCHAR2(250);
		xPort		VARCHAR2(10);
		xUserName	VARCHAR2(250);
		xPasswd 		VARCHAR2(250);
		xServerSecurity	VARCHAR2(10);
	BEGIN
		SELECT email, host, port, user_name, passwd, server_security INTO xFromAddress, xHost, xPort, xUserName, xPasswd, xServerSecurity FROM outgoing_mail_configuration WHERE id = accountID;
		
		RETURN sendMail (xFromAddress, toAddresses, ccAddresses, bccAddresses, subject, xHost, xPort, xServerSecurity, xUserName, xPasswd, mailContent, 'mixed');
		
		EXCEPTION WHEN NO_DATA_FOUND THEN
			RETURN FALSE;
	END;
	
	FUNCTION sendHtmlMailPL (accountID NUMBER, toAddresses VARCHAR2, ccAddresses VARCHAR2, bccAddresses VARCHAR2, subject VARCHAR2, mailContent BLOB) RETURN BOOLEAN AS
		xFromAddress	VARCHAR2(250);
		xHost		VARCHAR2(250);
		xPort		VARCHAR2(10);
		xUserName	VARCHAR2(250);
		xPasswd 		VARCHAR2(250);
		xServerSecurity	VARCHAR2(10);
	BEGIN
		SELECT email, host, port, user_name, passwd, server_security INTO xFromAddress, xHost, xPort, xUserName, xPasswd, xServerSecurity FROM outgoing_mail_configuration WHERE id = accountID;
		
		RETURN sendMail (xFromAddress, toAddresses, ccAddresses, bccAddresses, subject, xHost, xPort, xServerSecurity, xUserName, xPasswd, mailContent, 'related');
		
		EXCEPTION WHEN NO_DATA_FOUND THEN
			RETURN FALSE;
	END;
	
	-- SQL functions
	
	FUNCTION sendTextMail (accountID NUMBER, toAddresses VARCHAR2, ccAddresses VARCHAR2, bccAddresses VARCHAR2, subject VARCHAR2, mailContent BLOB) RETURN NUMBER AS
		xFromAddress	VARCHAR2(250);
		xHost		VARCHAR2(250);
		xPort		VARCHAR2(10);
		xUserName	VARCHAR2(250);
		xPasswd 		VARCHAR2(250);
		xServerSecurity	VARCHAR2(10);
	BEGIN
		SELECT email, host, port, user_name, passwd, server_security INTO xFromAddress, xHost, xPort, xUserName, xPasswd, xServerSecurity FROM outgoing_mail_configuration WHERE id = accountID;
		
		IF (sendMail (xFromAddress, toAddresses, ccAddresses, bccAddresses, subject, xHost, xPort, xServerSecurity, xUserName, xPasswd, mailContent, 'mixed')) THEN
			RETURN 1;
		ELSE
			RETURN 0;
		END IF;
		
		EXCEPTION WHEN NO_DATA_FOUND THEN
			RETURN 0;
	END;
	
	FUNCTION sendHtmlMail (accountID NUMBER, toAddresses VARCHAR2, ccAddresses VARCHAR2, bccAddresses VARCHAR2, subject VARCHAR2, mailContent BLOB) RETURN NUMBER AS
		xFromAddress	VARCHAR2(250);
		xHost		VARCHAR2(250);
		xPort		VARCHAR2(10);
		xUserName	VARCHAR2(250);
		xPasswd 		VARCHAR2(250);
		xServerSecurity	VARCHAR2(10);
	BEGIN
		SELECT email, host, port, user_name, passwd, server_security INTO xFromAddress, xHost, xPort, xUserName, xPasswd, xServerSecurity FROM outgoing_mail_configuration WHERE id = accountID;
		
		IF (sendMail (xFromAddress, toAddresses, ccAddresses, bccAddresses, subject, xHost, xPort, xServerSecurity, xUserName, xPasswd, mailContent, 'related')) THEN
			RETURN 1;
		ELSE
			RETURN 0;
		END IF;
		
		EXCEPTION WHEN NO_DATA_FOUND THEN
			return 0;
	END;
	
END Mail;
/

--
-- Examples:
--

-- Send mail
DECLARE
	html CLOB;
	contenido BLOB;
	imagen BLOB;
	adjunto BLOB;
BEGIN
	SELECT cuerpo_mail INTO html FROM prueba WHERE id = 3;
	contenido := mail.createHtmlContent(html);

	SELECT documento INTO imagen FROM prueba WHERE id = 3;
	contenido := mail.addHtmlImage(contenido, imagen, 'figura1', 'bidon.jpg', 'image/jpeg');

	SELECT documento INTO adjunto FROM prueba WHERE id = 1;
	contenido := mail.addHtmlAttachment(contenido, adjunto, 'ejemplo.pdf', 'application/pdf');

	IF (mail.sendHtmlMailPL (1, 'redmoon.granada@gmail.com', NULL, NULL, 'Prueba desde PL/SQL', contenido)) THEN
		dbms_output.put_line('Exito');
	ELSE
		dbms_output.put_line('Fallo');
	END IF;
END;


-- Load new mails
DECLARE
	nMails NUMBER;
BEGIN	
	nMails := mail.loadNewMails (1, 'INBOX');
	dbms_output.put_line( 'Número de mails: ' || nMails);
END;

