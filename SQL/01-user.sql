CREATE SMALLFILE TABLESPACE "EDOCPKI" DATAFILE '/u01/app/oracle/oradata/edocpki/edocpki.dbf' SIZE 100M AUTOEXTEND ON NEXT 100M 
MAXSIZE UNLIMITED LOGGING EXTENT MANAGEMENT LOCAL SEGMENT SPACE MANAGEMENT AUTO;

CREATE USER EDOCPKI PROFILE "DEFAULT" IDENTIFIED BY "clave" DEFAULT TABLESPACE EDOCPKI 
TEMPORARY TABLESPACE "TEMP" ACCOUNT UNLOCK;


GRANT RESOURCE, CONNECT, JAVA_ADMIN, JAVAUSERPRIV, JAVASYSPRIV, DBA TO EDOCPKI;
GRANT EXECUTE ON "SYS"."DBMS_CRYPTO" TO EDOCPKI;


begin
	dbms_java.grant_permission( 'EDOCPKI', 'SYS:java.net.SocketPermission', '*', 'connect,resolve' );
	dbms_java.grant_permission( 'EDOCPKI', 'SYS:java.net.NetPermission', 'setDefaultAuthenticator', '' );
	dbms_java.grant_permission( 'EDOCPKI', 'SYS:java.security.SecurityPermission', 'putProviderProperty.BC', '' );
	dbms_java.grant_permission( 'EDOCPKI', 'SYS:java.security.SecurityPermission', 'insertProvider.BC', '' );
	dbms_java.grant_permission( 'EDOCPKI', 'SYS:java.io.FilePermission','<<ALL FILES>>','execute');
	dbms_java.grant_permission( 'EDOCPKI', 'SYS:java.lang.RuntimePermission','writeFileDescriptor','*' );
	dbms_java.grant_permission( 'EDOCPKI', 'SYS:java.lang.RuntimePermission','readFileDescriptor','*' );
	dbms_java.grant_permission( 'EDOCPKI', 'SYS:java.security.SecurityPermission', 'putProviderProperty.ApacheXMLDSig', '' );
end;
/

-- Create tables
-- 02-tables.sql script

-- Load java libraries
-- 03-load_java.sql script

-- Create wrappers
-- 04-wrapper.sql script