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

//DCM2MINT.cpp file
//Author: Gorkem Sevinc

#include "DCM2MINT.h"
#include <iostream>

DCM2MINT::DCM2MINT()
{
	Study * Metadata = new Study();
	MetaBinaryPair binaryPair;
	binaryPair.SetMetadata(Metadata);

	std::ifstream studyLevelFile;
	studyLevelFile.open("../DICOM2MINTImport/StudyTags.txt");
	
	std::string line;
	while(!studyLevelFile.eof())
	{
		getline(studyLevelFile, line);
		std::string temp = line.substr(0,8).c_str();
		studyLevelTags.insert(temp);
	}
	std::ifstream seriesLevelFile;
	seriesLevelFile.open("../DICOM2MINTImport/SeriesTags.txt");
	
	while(!seriesLevelFile.eof())
	{
		getline(seriesLevelFile, line);
		seriesLevelTags.insert(line.substr(0,8).c_str());
	}

	builder = new DCM2MetaBuilder(studyLevelTags, seriesLevelTags, binaryPair);
}

std::vector<std::string> DCM2MINT::OutputMetadata()
{
	builder->finish();
	builder->PrintMetadata();
	std::vector<std::string> filePaths = builder->GetBinaryFilePaths();
	delete builder;
	return filePaths;
}

void DCM2MINT::ConvertDICOMP10FromFile(const char * path)
{
	reader = new gdcm::Reader();
	reader->SetFileName(path);
	bool success = reader->Read();
	if( !success)
	{
		std::cerr << "Failed to read: " << std::endl;
	}

	builder->ReadDICOMData(reader->GetFile());
}

void DCM2MINT::ConvertDICOMP10FromStream(std::iostream & stream)
{
	reader = new gdcm::Reader();
	reader->SetStream(stream);
	bool success = reader->Read();
	if( !success)
	{
		std::cerr << "Failed to read: " << std::endl;
	}

	builder->ReadDICOMData(reader->GetFile());
	stream.clear();
}