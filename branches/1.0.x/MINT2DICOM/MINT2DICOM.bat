@echo off
REM **************************************************************************
REM MINT2DICOM
REM **************************************************************************

set HOSTNAME=%1
set STUDY=%2
set OUTDIR=%3
set USE_BULK=%4

if {%4}=={} (
  echo "Usage: MINT2DICOM <hostname> <study_uuid> <output_dir> <use_bulk_loading=true|false>"
  goto:eof
)

set CLASSPATH=build/MINT2DICOM.jar
@java -Xms32m -Xmx512m -cp %CLASSPATH% org.nema.medical.mint.MINT2DICOM http://%HOSTNAME%:8080/MINTServer/studies/%STUDY%/DICOM/ %OUTDIR% %USE_BULK%
