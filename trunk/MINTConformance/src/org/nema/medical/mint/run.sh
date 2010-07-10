#!/bin/sh

# *** 
# Configure 
# ***
MC_HOME=/sandbox/medical-imaging-network-transport
MC_SRC=MINTConformance/src/org/nema/medical/mint

MC_DICOM=MINTConformance/testdata/DICOM
MC_MINT=MINTConformance/testdata/MINT
PYTHONPATH=$MC_HOME/MINTConformance/src

python $MC_HOME/$MC_SRC/DicomSeries.py $MC_HOME/$MC_DICOM/1.2.392.200036.9116.2.2.2.1762660474.1026398161.357037.dcm
python $MC_HOME/$MC_SRC/DicomStudy.py $MC_HOME/$MC_DICOM$
python $MC_HOME/$MC_SRC/MintStudy.py $MC_HOME/$MC_MINT/Meta.xml
