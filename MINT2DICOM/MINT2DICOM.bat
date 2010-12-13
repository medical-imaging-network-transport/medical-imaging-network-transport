@echo off
set HOSTNAME=%1
set STUDY=%2
set OUTDIR=%3
set USE_BULK=%4

if {%4}=={} goto USAGE

set CLASSPATH="build\MINT2DICOM.jar;..\MINTServer\WebContent\WEB-INF\lib\dcm4che-core-2.0.23.jar;..\MINTJavaSDK\build\mint-0.9.12.jar;..\MINTJavaSDK\lib\jibx-run-1.2.2.jar;..\MINTJavaSDK\lib\protobuf-java-2.3.0.jar;..\MINTServer\WebContent\WEB-INF\lib\slf4j-api-1.6.0.jar;..\MINTServer\WebContent\WEB-INF\lib\slf4j-log4j12-1.6.0.jar;..\MINTServer\WebContent\WEB-INF\lib\log4j-1.2.16.jar;"
@java -Xms32m -Xmx512m -cp %CLASSPATH% org.nema.medical.mint.MINT2DICOM http://%HOSTNAME%:8080/MINTServer/studies/%STUDY%/DICOM/ %OUTDIR% %USE_BULK%
GOTO:EOF

:USAGE
echo "Usage: java -jar MINT2DICOM <hostname> <study_uuid> <output_dir> <use_bulk_loading=true|false>"
