@echo off
REM **************************************************************************
REM DICOM2MINT
REM **************************************************************************

set EXECUTE=%1
set ENCODING=%2
set DICOM=%3
set HOSTNAME=%4

if {%4}=={} (
  echo "Usage: DICOM2MINT <once|daemon> <xml|gpb> <dicom_dir> <hostname> <options>"
  echo    Options:
  echo      binThreshold: Minimum size tag stored in binaryitems instead of metadata.
  echo      nodelete:     Do not delete DICOM files after conversion.
  echo      forcecreate:  Never update existing studies.
 
  goto:eof
)

set CLASSPATH=build/DICOM2MINT.jar
@java -Xms32m -Xmx512m -cp %CLASSPATH% org.nema.medical.mint.dcmimport.DCMImportMain %EXECUTE% %ENCODING% %DICOM% http://%HOSTNAME%:8080/MINTServer %5 %6 %7
