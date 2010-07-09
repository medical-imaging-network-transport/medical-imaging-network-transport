@echo off

REM *** 
REM Configure 
REM ***
set PYTHON=\Python25\python.exe
set MC_HOME=\sandbox\medical-imaging-network-transport

set MC_SRC=MINTConformance\src\org\nema\medical\mint
set MC_DICOM=MINTConformance\testdata\DICOM
set MC_MINT=MINTConformance\testdata\MINT
set PYTHONPATH=%MINT_HOME%\MINTConformance\src

%PYTHON% %MC_HOME%\%MC_SRC%\DicomSeries.py %MC_HOME%\%MC_DICOM%\1.2.392.200036.9116.2.2.2.1762660474.1026398161.357037.dcm
%PYTHON% %MC_HOME%\%MC_SRC%\DicomStudy.py %MC_HOME%\%MC_DICOM%
%PYTHON% %MC_HOME%\%MC_SRC%\MintStudy.py %MC_HOME%\%MC_MINT%\Meta.xml
