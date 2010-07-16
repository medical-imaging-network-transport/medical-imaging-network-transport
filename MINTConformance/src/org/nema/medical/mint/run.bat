@echo off

REM --- 
REM Configure 
REM ---
set PYTHON=\Python25\python.exe
set MC_HOME=\sandbox\medical-imaging-network-transport

set MC_SRC=MINTConformance\src\org\nema\medical\mint
set MC_DICOM=MINTConformance\testdata\DICOM
set MC_MINT=MINTConformance\testdata\MINT
set PYTHONPATH=%MC_HOME%\MINTConformance\src

REM --- 
REM Parse a DICOM file.
REM ---
%PYTHON% %MC_HOME%\%MC_SRC%\DicomSeries.py %MC_HOME%\%MC_DICOM%\1.2.392.200036.9116.2.2.2.1762660474.1026398161.357037.dcm

REM --- 
REM Parse a DICOM Study.
REM ---
%PYTHON% %MC_HOME%\%MC_SRC%\DicomStudy.py %MC_HOME%\%MC_DICOM%

REM --- 
REM Parse a MINT Study.
REM ---
%PYTHON% %MC_HOME%\%MC_SRC%\MintStudy.py %MC_HOME%\%MC_MINT%\Meta.xml

REM --- 
REM Compare identical MINT Studies.
REM ---
%PYTHON% %MC_HOME%\%MC_SRC%\MintStudyCompare.py %MC_HOME%\%MC_MINT%\Meta.xml %MC_HOME%\%MC_MINT%\Meta.xml

REM --- 
REM Compare different MINT Studies.
REM ---
%PYTHON% %MC_HOME%\%MC_SRC%\MintStudyCompare.py %MC_HOME%\%MC_MINT%\metadata.xml %MC_HOME%\%MC_MINT%\Meta.xml
