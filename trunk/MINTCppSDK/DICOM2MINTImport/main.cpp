//------------------------------------------------------------------------------
//
//   Copyright 2010 MINT Working Group
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
//------------------------------------------------------------------------------

//main.cpp file
//Author: Gorkem Sevinc

#include <iostream>
#include <cstdlib>
#include <fstream>
#include <string>
#include "DCM2MetaBuilder.h"
#include "DCM2MINT.h"
#include "../MINTCommon/CommonUtils.h"
#include "../MINTCommon/gdcmForwardDeclarations.h"
#include "../MINTMetadata/Study.h"

using namespace std;
#include "tarlib.h"

void main(int argc, char *argv[])
{
	DCM2MINT convertor;

	CTar tarFile("C:/development/studies/TarFile1.tar");
	std::stringbuf streamBuf;
	std::iostream streamIn(&streamBuf);
	std::string objectName;
	while(tarFile.ReadNextFile(streamIn, objectName))
	{
		std::cout<<"Processing: "<<objectName<<std::endl;
		convertor.ConvertDICOMP10FromStream(streamIn);
	}

	//convertor.ConvertDICOMP10FromFile("C:\\development\\testdata\\2.16.840.1.114255.393386351.1568457295.34445.4\\CR.2.16.840.1.114255.393386351.1568457295.48879.7.dcm");
	
	std::vector<std::string> filePaths = convertor.OutputMetadata();

	CommonUtils comUtils;
	comUtils.SendStudy("http://127.0.0.1:8080/MINTServer/jobs/createstudy", "C:/MINTOutput/metadata.gpb", "metadata", filePaths);

}
