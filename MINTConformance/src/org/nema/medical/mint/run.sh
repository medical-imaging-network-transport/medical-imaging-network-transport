#!/bin/sh

# ---
# Configure 
# ---
MC_HOME=/sandbox/medical-imaging-network-transport
MC_SRC=MINTConformance/src/org/nema/medical/mint

MC_DICOM=MINTConformance/testdata/DICOM
MC_MINT=MINTConformance/testdata/MINT
PYTHONPATH=$MC_HOME/MINTConformance/src

# ---
# Simple diff and cmp.
# ---
$MC_HOME/$MC_SRC/MintStudyCompare.sh $MC_HOME/$MC_MINT/metadata.xml $MC_HOME/$MC_MINT/metadata.xml

# ---
# Parse a DICOM file.
# ---
python $MC_HOME/$MC_SRC/DicomSeries.py $MC_HOME/$MC_DICOM/1.2.392.200036.9116.2.2.2.1762660474.1026398161.357037.dcm

# ---
# Parse a DICOM Study.
# ---
python $MC_HOME/$MC_SRC/DicomStudy.py $MC_HOME/$MC_DICOM$

# ---
# Parse a MINT Study.
# ---
python $MC_HOME/$MC_SRC/MintStudy.py $MC_HOME/$MC_MINT/Meta.xml

# ---
# Compare identical MINT Studies.
# ---
python $MC_HOME/$MC_SRC/MintStudyCompare.py $MC_HOME/$MC_MINT/Meta.xml $MC_HOME/$MC_MINT/Meta.xml

# ---
# Compare different MINT Studies.
# ---
python $MC_HOME/$MC_SRC/MintStudyCompare.py $MC_HOME/$MC_MINT/Meta.xml $MC_HOME/$MC_MINT/metadata.xml
