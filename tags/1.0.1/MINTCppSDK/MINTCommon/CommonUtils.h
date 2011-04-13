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

//MetaBinaryPair.h file
//Author: Gorkem Sevinc

#ifndef __COMMONUTILS_H
#define __COMMONUTILS_H

#include "../TinyXML/tinyxml.h"
#include "../MINTMetadata/config/mint-metadata.pb.h"
#include "../MINTMetadata/Study.h"
#include "gdcmForwardDeclarations.h"
#include "libraryForwardDeclarations.h"
#include "windows.h"
#include "BinaryData.h"
#include "curl.h"

typedef std::map<std::string, Attribute *> AttributeMapType;
typedef std::map<std::wstring, Series *> SeriesMapType;
typedef std::map<std::wstring, Instance *> InstanceMapType;
typedef std::vector<Attribute *> AttributeVectorType;

class CommonUtils
{
	public:
		/** \brief Default constructor for the MetaBinaryPair class
		*/
		CommonUtils();

		/** \brief Function to print the metadata to GPB
		*/
		void PrintMetadataGPB(Study * Metadata);

		/** \brief Function to print the attribute from the Attribute data structure to GPB
		* \param attr The Attribute data structure
		* \param attributeData The GPB attribute data to be written to
		*/
		void PrintAttributeGPB(Attribute * attr, mint::metadata::AttributeData * attributeData);

		/** \brief Function to print the metadata to XML
		*/
		void PrintMetadataXML(Study * Metadata);

		/** \brief Function to print teh attribute from the Attribute data structure to XML
		* \param attr The attribute data structure
		* \return element TiXmlElement where the data is written to
		*/
		TiXmlElement * PrintAttributeXML(Attribute * attr);

		/** \brief Function to load metadata from GPB string
		* \param metadataString The metadata read into a string
		* \return metadata The metadata read in from GPB
		*/
		Study * LoadMetadataFromGPBString(std::string & metadataStringStream);

		/** \brief Function to load metadata from GPB file
		* \param path The path to the GPB file
		* \return metadata The metadata read in from GPB
		*/
		Study * LoadMetadataFromGPBFile(const char * path);

		/** \brief Function to load metadata from GPB
		* \param path The path to the GPB file
		* \return metadata The metadata read in from GPB
		*/
		Study * LoadMetadataFromGPB(mint::metadata::StudyData & studyData);

		/** \brief Function to load an attribute from GPB
		* \param attribute The GPB attributeData to be read in
		* \return attr The Attribute data structure instance that data is read into
		*/
		Attribute * GetAttributeFromGPB(const mint::metadata::AttributeData& attribute);

		/** \brief Function to load metadata from XML
		* \param path The path to the XML file
		* \return metadata The metadata read in from XML
		*/
		Study * LoadMetadataFromXML(const char * path);

		/** \brief Function to load an attribute from XML
		* \param pElemInner The inner XML element 
		* \return attrVector The vector of attributes read in from XML
		*/
		AttributeVectorType GetAttributeFromXML(TiXmlElement * pElemInner);

		/** \brief Function to send a study (file on disk) to the server
		* \param URL The URL path of the server
		* \param filepath The path to the file to be sent
		* \param filename The name of the file
		*/
		void SendStudy(std::string URL, const char * filepath, const char * filename, std::vector<std::string> binaryFilePaths);


		//int writer(char *data, size_t size, size_t nmemb, std::string *buffer);

		/** \brief Function to read a URL stream to a buffer
		* \param URL The URL path of the server
		* \return buffer The buffer
		*/
		std::string * ReadURLToBuffer(std::string URL);
		
		/** \brief Function to read a study from the server
		* \param URL The URL path of the study on  the server
		* \return metadata The metadata from the server
		*/
		Study * ReadStudyFromURL(std::string URL);

		/** \brief Function to split a string
		* \param s String to be split
		* \param delim The delimiter character
		* \param elems The vector of strings to be returned
		* \return elems The vector of strings split by the delimiter
		*/
		std::vector<std::string> &split(const std::string &s, char delim, std::vector<std::string> &elems);

		/** \brief Helper function for split
		* \param s String to be split
		* \param delim The delimiter character
		* \return elems The vector of strings split by the delimiter
		*/
		std::vector<std::string> splitString(const std::string &s, char delim);
		
		/** \brief Function to read the data dictionary
		* \param dataDictionary Map of strings to strings for tag - VR pairs
		* \param studyLevelTags The tags that are at the study level
		* \param seriesLevelTags The tags that are at the series level
		*/
		void GetDataDictionary(std::map<std::string, std::string> & dataDictionary, 
							   std::set<std::string> & studyLevelTags, std::set<std::string> & seriesLevelTags);

		/** \brief Function to convert a wide Unicode string to an UTF8 string
		* \param wstr The wide unicode string
		*/
		std::string utf8_encode(const std::wstring &wstr);

		/** \brief Function to convert an UTF8 string to a wide Unicode String
		* \param str The UTF8 string
		*/
		std::wstring utf8_decode(const std::string &str);


};

#endif