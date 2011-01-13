#!/bin/sh
# ****************************************************************************
# DICOM2MINT
# ****************************************************************************

progName=`basename $0`
if [ $# -lt 4 ]
then 
  echo "Usage: $progName <once|daemon> <xml|gpb> <dicom_dir> <hostname> <options>"
  echo "  Options:"
  echo "    binThreshold: Minimum size tag stored in binaryitems instead of metadata."
  echo "    nodelete:     Do not delete DICOM files after conversion."
  echo "    forcecreate:  Never update existing studies."   
  exit 1
fi

EXECUTE=$1
ENCODING=$2
DICOM=$3
HOSTNAME=$4

CLASSPATH=build/DICOM2MINT.jar
java -Xms32m -Xmx512m -cp $CLASSPATH org.nema.medical.mint.dcmimport.DCMImportMain $EXECUTE $ENCODING $DICOM http://${HOSTNAME}:8080/MINTServer $5 $6 $7
exit $?
