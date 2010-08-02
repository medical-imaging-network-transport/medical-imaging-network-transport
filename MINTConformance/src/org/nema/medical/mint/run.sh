#!/bin/sh

MC_HOME=${MC_HOME:-$HOME/sandbox/medical-imaging-network-transport}
if [ ! -d $MC_HOME ]
then
   echo "$MC_HOME does not exist. Is MC_HOME defined?"
   exit 1
fi

MC_SRC=MINTConformance/src/org/nema/medical/mint
MC_DICOM=MINTConformance/testdata/DICOM
MC_MINT=MINTConformance/testdata/MINT/7cc45edf-d5a6-4264-b8ae-1d24d857f04b/DICOM
PYTHONPATH=$MC_HOME/MINTConformance/src

dirs="$MC_SRC $MC_DICOM $MC_MINT $PYTHONPATH"
for dir in $dirs
do
  if [ ! -d $dir ]
  then
     echo "$dir does not exist. Is the subversion workspace up to date?"
     exit 1
  fi
done

echo "Simple diff and cmp..."
$MC_HOME/$MC_SRC/MintStudyCompare.sh $MC_HOME/$MC_MINT/metadata.xml $MC_HOME/$MC_MINT/metadata.xml

echo "Parse a DICOM Instance..."
python $MC_HOME/$MC_SRC/DicomInstance.py $MC_HOME/$MC_DICOM/1.2.392.200036.9116.2.2.2.1762660474.1026398161.357037.dcm

echo "Parse a DICOM Study..."
python $MC_HOME/$MC_SRC/DicomStudy.py $MC_HOME/$MC_DICOM

echo "Parse a MINT Study..."
python $MC_HOME/$MC_SRC/MintStudy.py $MC_HOME/$MC_MINT/metadata.xml

echo "Compare MINT Studies..."
python $MC_HOME/$MC_SRC/MintStudyCompare.py $MC_HOME/$MC_MINT/metadata.xml $MC_HOME/$MC_MINT/metadata.xml

echo "Compare DICOM to MINT..."
python $MC_HOME/$MC_SRC/MintDicomCompare.py $MC_HOME/$MC_DICOM $MC_HOME/$MC_MINT/metadata.xml

