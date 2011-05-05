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

//MINT2DCMBuilder.h file
//Author: Gorkem Sevinc

#ifndef __MINT2DCMBUILDER_H
#define __MINT2DCMBUILDER_H

//#include "../MINTCommon/MetaBinaryPair.h"
#include "../MINTCommon/gdcmForwardDeclarations.h"
#include "../MINTCommon/libraryForwardDeclarations.h"
#include "../MINTCommon/CommonUtils.h"
#include "../MINTMetadata/Series.h"
#include "../MINTMetadata/Instance.h"
#include "../MINTMetadata/Study.h"
#include "../MINTMetadata/config/mint-metadata.pb.h"
#include "../TinyXML/tinyxml.h"
#include "windows.h"

typedef std::vector<Attribute *> AttributeVectorType;

class MINT2DCMBuilder
{
	public:
		/** \brief Default constructor for the MINT2DCMBuilder class
		*/
		MINT2DCMBuilder();

		/** \brief Overloaded constructor for the MINT2DCMBuilder class
		* \param path The URL of the MINT metadata file
		*/
		MINT2DCMBuilder(std::string URL);

		/** \brief Function to convert a wide Unicode string to an UTF8 string
		* \param wstr The wide unicode string
		*/
		std::string utf8_encode(const std::wstring &wstr);

		/** \brief Function to convert an UTF8 string to a wide Unicode String
		* \param str The UTF8 string
		*/
		std::wstring utf8_decode(const std::string &str);
	private:
		
		gdcm::TransferSyntax GetTransferSyntaxFromString(std::string transferSyntaxUID);
		gdcm::DataElement GetElement(Attribute * attribute);
		void WriteStudy(Study * metadata);
		void InsertAttribute(gdcm::DataSet & dataSet, gdcm::FileMetaInformation & fmi, Attribute * attribute);
		std::map<std::string, std::string> dataDictionary;
		std::set<std::string> studyLevelTags;
		std::set<std::string> seriesLevelTags;
		std::string studyURL;
		CommonUtils comUtils;
};

#endif