


CREATE OR REPLACE PACKAGE Signer AS

	-- ******************************************************************************************************
	--												PKCS7
	-- ******************************************************************************************************
	
	FUNCTION PKCS7ByIDStore(xFile in Blob, xIDCert in Integer) return blob;
	
	FUNCTION PKCS7ByIDStoreUser(xFile in Blob, xIDCert in Integer,passwd in varchar2) return blob;
	
	FUNCTION PKCS7ByNameCert(xFile in Blob, xNameCert in varchar2) return blob;
	
	FUNCTION PKCS7ByNameCertUser(xFile in Blob, xNameCert in varchar2,passwd in varchar2) return blob;
	
		
	 -- ******************************************************************************************************
     -- 											Firmas PDF
     -- ******************************************************************************************************
	
	
	FUNCTION PdfByIDStore(xFile in Blob, xIDCert in Integer) return blob;
	
	FUNCTION PdfByIDStoreUser(xFile in Blob, xIDCert in Integer,passwd in varchar2) return blob;
	
	FUNCTION PdfByNameCert(xFile in Blob, xNameCert in varchar2) return blob;
	
	FUNCTION PdfByNameCertUser(xFile in Blob, xNameCert in varchar2,passwd in varchar2) return blob;
	
	 -- ******************************************************************************************************
     -- 											Firmas XADESBES Enveloped
     -- ******************************************************************************************************
	
	FUNCTION XBEnvelopedByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	FUNCTION XBEnvelopedByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	FUNCTION XBEnvelopedByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	FUNCTION XBEnvelopedByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	-- ******************************************************************************************************
     -- 											Firmas XADESBES Enveloping
     -- ******************************************************************************************************

	FUNCTION XBEnvelopingByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	FUNCTION XBEnvelopingByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	FUNCTION XBEnvelopingByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	FUNCTION XBEnvelopingByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	-- ******************************************************************************************************
     -- 											Firmas XADESBES Detached
     -- ******************************************************************************************************

	FUNCTION XBDetachedByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	FUNCTION XBDetachedByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	FUNCTION XBDetachedByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	FUNCTION XBDetachedByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2) return BLOB; 
	
	-- ******************************************************************************************************
     -- 											Firmas XADEST Authenticated Enveloped 
     -- ******************************************************************************************************	

	FUNCTION XTAuthEnvelopedByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTAuthEnvelopedByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTAuthEnvelopedByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTAuthEnvelopedByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	-- ******************************************************************************************************
     -- 											Firmas XADEST Authenticated Enveloping 
     -- ******************************************************************************************************	

	FUNCTION XTAuthEnvelopingByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTAuthEnvelopingByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTAuthEnvelopingByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTAuthEnvelopingByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	 -- ******************************************************************************************************
     -- 											Firmas XADEST Authenticated Detached 
     -- ******************************************************************************************************	
	

	FUNCTION XTAuthDetachedByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTAuthDetachedByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTAuthDetachedByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTAuthDetachedByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	
	-- ******************************************************************************************************
     -- 											Firmas XADEST Enveloped
     -- ******************************************************************************************************	

 	FUNCTION XTEnvelopedByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTEnvelopedByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTEnvelopedByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTEnvelopedByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
 	-- ******************************************************************************************************
     -- 											Firmas XADEST Enveloping
     -- ******************************************************************************************************	

	FUNCTION XTEnvelopingByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTEnvelopingByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTEnvelopingByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTEnvelopingByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	
	-- ******************************************************************************************************
     -- 											Firmas XADEST Detached
     -- ******************************************************************************************************	

	FUNCTION XTDetachedByIDStore(xFile in BLOB, xIDCert in Integer,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTDetachedByIDStoreUser(xFile in BLOB, xIDCert in Integer,xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTDetachedByNameCert(xFile in BLOB, xNameCert in Varchar2, xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	FUNCTION XTDetachedByNameCertUser(xFile in BLOB, xNameCert in Varchar2, xPass in varchar2,xFileName in varchar2, xMimeType in varchar2, xIDTSA in Integer) return BLOB; 
	
	
	
END Signer;
/
create or replace
PACKAGE BODY Signer AS


	
	
	-- ******************************************************************************************************
	--												PKCS7
	-- ******************************************************************************************************
	
	--
	-- Firmar a través del ID del almacén de certificados para una firma de Servidor
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
		return EDOCPKI_V1.SIGNER.PKCS7(xCer,xPass,xFile);
		
	end;
	
	--
	-- Firmar a través del ID del almacén de certificados para una firma de Usuario Interno
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
               return EDOCPKI_V1.SIGNER.PKCS7(xCer,passwd,xFile);
               
       end;
	
	--
	-- Firmar por nombre de certificado en el almacén
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
               return EDOCPKI_V1.SIGNER.PKCS7(xCer,xPass,xFile);
               
       end;
       
	--
	-- Firma PKCS7 por nombre de certificado, pasa la contraseña el usuario firmante
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
               return EDOCPKI_V1.SIGNER.PKCS7(xCer,passwd,xFile);
               
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
		return EDOCPKI_V1.SIGNER.pdf(xCer,xPass,xFile);
		
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
               return EDOCPKI_V1.SIGNER.Pdf(xCer,passwd,xFile);
               
       end;
	
	
	-- Firmar PDF por nombre de certificado en el almacén
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
               return EDOCPKI_V1.SIGNER.pdf(xCer,xPass,xFile);
               
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
               
               -- invocar a PDF
               return EDOCPKI_V1.SIGNER.Pdf(xCer,passwd,xFile);
               
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
               return EDOCPKI_V1.SIGNER.XAdESBESEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
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
               return EDOCPKI_V1.SIGNER.XAdESBESEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
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
               return EDOCPKI_V1.SIGNER.XAdESBESEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
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
               return EDOCPKI_V1.SIGNER.XAdESBESEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
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
               return EDOCPKI_V1.SIGNER.XAdESBESEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
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
               return EDOCPKI_V1.SIGNER.XAdESBESEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
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
               return EDOCPKI_V1.SIGNER.XAdESBESEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
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
               return EDOCPKI_V1.SIGNER.XAdESBESEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
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
               return EDOCPKI_V1.SIGNER.XAdESBESDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
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
               return EDOCPKI_V1.SIGNER.XAdESBESDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
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
               return EDOCPKI_V1.SIGNER.XAdESBESDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
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
               return EDOCPKI_V1.SIGNER.XAdESBESDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType);
               
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

               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from tsa
                       where id=xIDCert;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return EDOCPKI_V1.SIGNER.XAdESTAuthenticatedEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
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

               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return EDOCPKI_V1.SIGNER.XAdESTAuthenticatedEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
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

               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return EDOCPKI_V1.SIGNER.XAdESTAuthenticatedEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
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

               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return EDOCPKI_V1.SIGNER.XAdESTAuthenticatedEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
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
               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return EDOCPKI_V1.SIGNER.XAdESTAuthenticatedEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
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

               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return EDOCPKI_V1.SIGNER.XAdESTAuthenticatedEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
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

               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return EDOCPKI_V1.SIGNER.XAdESTAuthenticatedEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
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

               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloping
              
               return EDOCPKI_V1.SIGNER.XAdESTAuthenticatedEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
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
               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return EDOCPKI_V1.SIGNER.XAdESTAuthenticatedDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
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

               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return EDOCPKI_V1.SIGNER.XAdESTAuthenticatedDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
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

               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return EDOCPKI_V1.SIGNER.XAdESTAuthenticatedDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
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

               select direccion,usuario,password into xTSAurl,xUserTSA,xPassTSA from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloping
              
               return EDOCPKI_V1.SIGNER.XAdESTAuthenticatedDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl,xUserTSA,xPassTSA);
               
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
               select direccion into xTSAurl from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return EDOCPKI_V1.SIGNER.XAdESTEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
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
               select direccion into xTSAurl from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return EDOCPKI_V1.SIGNER.XAdESTEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
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
               select direccion into xTSAurl from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return EDOCPKI_V1.SIGNER.XAdESTEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
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
               select direccion into xTSAurl from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return EDOCPKI_V1.SIGNER.XAdESTEnveloped(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
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
               select direccion into xTSAurl from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return EDOCPKI_V1.SIGNER.XAdESTEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
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
               select direccion into xTSAurl from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return EDOCPKI_V1.SIGNER.XAdESTEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
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
               select direccion into xTSAurl from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return EDOCPKI_V1.SIGNER.XAdESTEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
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
               select direccion into xTSAurl from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return EDOCPKI_V1.SIGNER.XAdESTEnveloping(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
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
               select direccion into xTSAurl from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTAuthenticatedEnveloped
              
               return EDOCPKI_V1.SIGNER.XAdESTDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
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
               select direccion into xTSAurl from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTDetached
              
               return EDOCPKI_V1.SIGNER.XAdESTDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
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
               select direccion into xTSAurl from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTDetached
              
               return EDOCPKI_V1.SIGNER.XAdESTDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
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
               select direccion into xTSAurl from tsa
                       where id=xIDTSA;

               exception when no_data_found then
                       begin
                          return null;
                       end;
               end;
               
               -- invocar a XAdESTDetached
              
               return EDOCPKI_V1.SIGNER.XAdESTDetached(xCer, 'PKCS12',xPass , xPass, xFile, xFileName, '', xMimeType,xTSAurl);
               
       end; 
	
END Signer;



CREATE OR REPLACE PACKAGE TStamp AS


	
	FUNCTION tsBlobSH1byIDTsa(document BLOB, xIDTsa Integer) return BLOB;
	
	FUNCTION tsBlobXMLSH1byIDTsa(document BLOB, xIDTsa Integer) return BLOB;
	
	FUNCTION tsAuthBlobSH1byIDTsa(document BLOB, xIDTsa Integer) return BLOB;
	
	FUNCTION tsAuthBlobXMLSH1byIDTsa(document BLOB, xIDTsa Integer) return BLOB;
		
	
END TStamp;
/

CREATE OR REPLACE PACKAGE BODY TStamp AS
		
	--
	--	
	--
	FUNCTION tsBlobSH1byIDTsa(document BLOB, xIDTsa Integer) return BLOB
	AS
		xUrl varchar2(250);
	BEGIN
		begin
		Select direccion into xUrl from tsa where id=xIDTsa;
		exception when no_data_found then
			begin
				return null;
			end;
		end;
		return EDOCPKI_V1.TStamp.createTS_BlobSH1(document,xUrl);
	END;
	
	--
	--
	--
	FUNCTION tsBlobXMLSH1byIDTsa(document BLOB, xIDTsa Integer) return BLOB
	AS
		xUrl varchar2(250);
	BEGIN
		begin
		Select direccion into xUrl from tsa where id=xIDTsa;
		exception when no_data_found then
			begin
				return null;
			end;
		end;
		return EDOCPKI_V1.TStamp.tsBlobXMLSH1byIDTsa(document,xUrl);
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
		Select direccion,usuario,password into xUrl,xUser,xPass from tsa where id=xIDTsa;
		exception when no_data_found then
			begin
				return null;
			end;
		end;
		return EDOCPKI_V1.TStamp.createAuthTS_BlobSH1(document,xUrl,xUser,xPass);
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
		Select direccion,usuario,password into xUrl,xUser,xPass from tsa where id=xIDTsa;
		exception when no_data_found then
			begin
				return null;
			end;
		end;
		return EDOCPKI_V1.TStamp.createAuthXMLTS_BlobSH1(document,xUrl,xUser,xPass);
	END;
	
END TStamp;
/





CREATE OR REPLACE PACKAGE S3 AS

	FUNCTION createBucket(xIDS3 Integer, bucketName VARCHAR2) return NUMBER;
	FUNCTION deleteBucket(xIDS3 Integer, bucketName VARCHAR2) return NUMBER;
	FUNCTION putObject(xIDS3 Integer, bucketName VARCHAR2, object BLOB, objectKey VARCHAR2) return NUMBER;
	FUNCTION getObject(xIDS3 Integer, bucketName VARCHAR2, objectKey VARCHAR2) return BLOB;
	FUNCTION deleteObject(xIDS3 Integer, bucketName VARCHAR2, objectKey VARCHAR2) return NUMBER;
	
	
END S3;
/

CREATE OR REPLACE PACKAGE BODY S3 AS
	
	--
	--
	--
	FUNCTION createBucket(xIDS3 Integer, bucketName VARCHAR2) return NUMBER
	AS
		xACCESKEY 	VARCHAR2(40);
		xSECRETKEY 	VARCHAR2(90);
	BEGIN
		begin
			select ACCESKEY, SECRETKEY into xACCESKEY, xSECRETKEY from S3_CONFIG where id=xIDS3;
			exception when no_data_found then
			begin
				return null;
			end;
		end;
		
		return edocpki_v1.S3.createBucket(xACCESKEY, xSECRETKEY, bucketName);
	END;
	
	--
	--
	--
	FUNCTION deleteBucket(xIDS3 Integer, bucketName VARCHAR2) return NUMBER
	AS
		xACCESKEY 	VARCHAR2(40);
		xSECRETKEY 	VARCHAR2(90);
	BEGIN
		begin
			select ACCESKEY, SECRETKEY into xACCESKEY, xSECRETKEY from S3_CONFIG where id=xIDS3;
			exception when no_data_found then
			begin
				return null;
			end;
		end;
		
		return edocpki_v1.S3.deleteBucket(xACCESKEY, xSECRETKEY, bucketName);
	END;
	
	--
	--
	--
	FUNCTION putObject(xIDS3 Integer, bucketName VARCHAR2, object BLOB, objectKey VARCHAR2) return NUMBER
	AS
		xACCESKEY 	VARCHAR2(40);
		xSECRETKEY 	VARCHAR2(90);
	BEGIN
		begin
			select ACCESKEY, SECRETKEY into xACCESKEY, xSECRETKEY from S3_CONFIG where id=xIDS3;
			exception when no_data_found then
			begin
				return null;
			end;
		end;
		
		return edocpki_v1.S3.putObject(xACCESKEY, xSECRETKEY, bucketName,object,objectKey);
	END;
	--
	--
	--
	FUNCTION getObject(xIDS3 Integer, bucketName VARCHAR2, objectKey VARCHAR2) return BLOB
	AS
		xACCESKEY 	VARCHAR2(40);
		xSECRETKEY 	VARCHAR2(90);
	BEGIN
		begin
			select ACCESKEY, SECRETKEY into xACCESKEY, xSECRETKEY from S3_CONFIG where id=xIDS3;
			exception when no_data_found then
			begin
				return null;
			end;
		end;
		
		return edocpki_v1.S3.getObject(xACCESKEY, xSECRETKEY, bucketName,objectKey);
	END;
	--
	--
	--
	FUNCTION deleteObject(xIDS3 Integer, bucketName VARCHAR2, objectKey VARCHAR2) return NUMBER
	AS
		xACCESKEY 	VARCHAR2(40);
		xSECRETKEY 	VARCHAR2(90);
	BEGIN
		begin
			select ACCESKEY, SECRETKEY into xACCESKEY, xSECRETKEY from S3_CONFIG where id=xIDS3;
			exception when no_data_found then
			begin
				return null;
			end;
		end;
		
		return edocpki_v1.S3.deleteObject(xACCESKEY, xSECRETKEY, bucketName,objectKey);
	END;
	
	
END S3;
/

