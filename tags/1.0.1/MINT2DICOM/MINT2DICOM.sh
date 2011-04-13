#!/bin/sh
# ****************************************************************************
# MINT2DICOM
# ****************************************************************************

progName=`basename $0`
if [ $# != 4 ]
then 
  echo "Usage: $progName <hostname> <study_uuid> <output_dir> <use_bulk_loading=true|false>"
  exit 1
fi

HOSTNAME=$1
STUDY=$2
OUTDIR=$3
USE_BULK=$4

CLASSPATH=build/MINT2DICOM.jar
java -Xms32m -Xmx512m -cp $CLASSPATH org.nema.medical.mint.MINT2DICOM http://${HOSTNAME}:8080/MINTServer/studies/$STUDY/DICOM/ $OUTDIR $USE_BULK
exit $?
