Set your python path to point to your python source directory.

   > set MINT_HOME=C:\sandbox\medical-imaging-network-transport
   > set PYTHONPATH=%MINT_HOME%\MINTConformance\src
   > cd %MINT_HOME%
   
To read a single dicom file:

   > C:\Python26\python.exe MINTConformance\src\org\nema\medical\mint\DicomSeries.py MINTConformance\testdata\DICOM\1.2.392.200036.9116.2.2.2.1762660474.1026398161.357037.dcm
   
To display an entire study:

   > C:\Python26\python.exe MINTConformance\src\org\nema\medical\mint\DicomStudy.py MINTConformance\testdata\DICOM

