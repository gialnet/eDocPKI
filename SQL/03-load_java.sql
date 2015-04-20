-- Load JDK missing classes
loadjava -nodefiner -r -v -s -g PUBLIC -u SYS/clave 00-java-rt.jar


-- Load eDocPKI dependencies
loadjava -nodefiner -r -v -u eDocPKI/clave 01-bcprov-jdk15-1.43.jar
loadjava -nodefiner -r -v -u eDocPKI/clave 02-bcmail-jdk15-1.43.jar
loadjava -nodefiner -r -v -u eDocPKI/clave 03-bctsp-jdk15-1.43.jar
loadjava -nodefiner -r -v -u eDocPKI/clave 04-commons-codec-1.2.jar
loadjava -nodefiner -r -v -u eDocPKI/clave 05-commons-logging-1.1.jar
loadjava -nodefiner -r -v -u eDocPKI/clave 06-commons-lang-2.4.jar
loadjava -nodefiner -r -v -u eDocPKI/clave 07-commons-httpclient-3.0.1.jar
loadjava -nodefiner -r -v -u eDocPKI/clave 08-serializer-2.7.1.jar
loadjava -nodefiner -r -v -u eDocPKI/clave 09-xalan-2.7.1.jar
loadjava -nodefiner -r -v -u eDocPKI/clave 10-xmlsec-1.5.2.jar
loadjava -nodefiner -r -v -u eDocPKI/clave 11-itext-2.1.7.jar
loadjava -nodefiner -r -v -u eDocPKI/clave 12-avalon-framework-4.2.0.jar
loadjava -nodefiner -r -v -u eDocPKI/clave 13-zxing-core-1.7.jar
loadjava -nodefiner -r -v -u eDocPKI/clave 14-barcode4j.jar
loadjava -nodefiner -r -v -u eDocPKI/clave 15-httpcore-4.1.jar
loadjava -nodefiner -r -v -u eDocPKI/clave 16-httpclient-4.1.1.jar
loadjava -nodefiner -r -v -u eDocPKI/clave 17-aws-java-sdk-1.3.15-modified.jar
loadjava -nodefiner -r -v -u eDocPKI/clave 18-jaf-1.1.1.jar
loadjava -nodefiner -r -v -u eDocPKI/clave 19-javamail-1.4.5.jar


-- Load eDocPKI library
loadjava -nodefiner -r -v -u eDocPKI/clave eDocPKI.jar
