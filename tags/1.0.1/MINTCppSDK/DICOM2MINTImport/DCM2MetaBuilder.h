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

//Attribute.h file

#ifndef __DCM2METABUILDER_H
#define __DCM2METABUILDER_H

#include "../MINTCommon/MetaBinaryPair.h"
#include "../MINTCommon/gdcmForwardDeclarations.h"
#include "../MINTCommon/libraryForwardDeclarations.h"
#include "../MINTCommon/CommonUtils.h"
#include "../MINTMetadata/Series.h"
#include "../MINTMetadata/Instance.h"
#include "map"
#include "string.h"
#include "windows.h"

/** \brief NormalizationCounter struct - to keep a count of reoccuring attributes
*/
struct NormalizationCounter
{
	NormalizationCounter()
	{
		attr = new Attribute();
		count = 1;
	}
	Attribute * attr;
	int count;
};

typedef std::map<std::wstring, Instance *> InstanceMapType;
typedef std::map<std::wstring, Series *> SeriesMapType;
typedef std::map<std::string, NormalizationCounter> NormMapType;
typedef std::map<std::wstring, NormMapType> TagNormalizerTableType;
typedef std::map<std::string, std::string> DataDictionaryType;

class DCM2MetaBuilder
{
	public:
		/** \brief Default constructor for the DCM2MetaBuilder class
		* \param studyLevelTags The study level tags
		* \param seriesLevelTags The series level tags
		* \param metaBinaryPair The meta binary pair to be used.
		*/
		DCM2MetaBuilder(std::set<std::string> studyLevelTags, std::set<std::string> seriesLevelTags, MetaBinaryPair & metaBinaryPair);

		/** \brief Function to trigger processing of a dicom file (from a file on disk)
		* \param path The path to the dicom p10 file
		*/
		void ProcessDICOMFile(const char * path);

		/** \brief Function to read dicom data from a gdcm File
		* \param file The GDCM file to be processed
		*/
		void ReadDICOMData(gdcm::File &file);

		/** \brief Function to get the metaBinaryPair being used.
		* \return metaBinaryPair The meta binary pair
		*/
		MetaBinaryPair GetMetadataBuilder(void);

		/** \brief Function to get the series
		* \return seriesMap The Series Map
		*/
		SeriesMapType GetSeries(void);

		/** \brief Function to trigger print of the metadata
		*/
		void PrintMetadata(void);

		/** \brief Function to finish normalization of the attributes
		*/
		void finish(void);

		std::vector<std::string> GetBinaryFilePaths(void);
	private:
		/** \brief Function to convert a string to be all upper case
		* \param s String to be converted
		* \return str Converted string (all uppercase)
		*/
		std::string ToUpper(const std::string & s);

		/** \brief Function to handle an element - checks whether the tag is study, series or instance level, and inserts accordingly
		* \param element The GDCM dicom element
		* \param series The series through the study
		* \param instance The instance through the series
		* \param file The gdcm file in consideration
		* \param normMap The normalization map
		*/
		void HandleElement(gdcm::DataSet::Iterator element, Series * series, Instance * instance, gdcm::File &file, NormMapType & normMap);

		/** \brief Function to generate a new attribute
		* \param element The GDCM dicom element
		* \param tag The tag of the element being created
		* \return attr The generated attribute
		*/
		Attribute * NewAttribute(gdcm::DataSet::Iterator element, const std::string & tag);

		/** \brief Function to generate an unpiped tag given a GDCM piped tag (given: (0002, 0010), return 00020010)
		* \param tag GDCM tag
		* \return strTag The unpiped tag
		*/
		std::string GenerateUnpipedTag(gdcm::Tag tag);

		/** \brief Function to store a binary element
		* \param element The GDCM element
		* \param tag The tag of the element to be inserted
		* \param normalize Boolean to determine if the element is to be considered for normalization or not
		* \param normMap The normalization map
		* \return attr The generated attribute
		*/
		Attribute * StoreBinary(gdcm::DataSet::Iterator element, const std::string & tag, bool normalize, NormMapType & normMap);

		/** \brief Function to store a sequence
		* \param element The GDCM element
		* \param tag The tag of the element to be inserted
		* \param file The GDCM file
		* \param normMap The normalization map
		* \return attr The generated attribute
		*/
		Attribute * StoreSequence(gdcm::DataSet::Iterator element, const std::string & tag, gdcm::File &file, NormMapType & normMap);

		/** \brief Function to store a plain element
		* \param element The GDCM element
		* \param tag The tag of the element to be inserted
		* \param file The GDCM file
		* \param normalize Boolean to determine if the element is to be considered for normalization or not
		* \param normMap The normalization map
		* \return attr The generated attribute
		*/
		Attribute * StorePlain(gdcm::DataSet::Iterator element, const std::string & tag, gdcm::File &file, bool normalize, NormMapType & normMap);

		/** \brief Function to store a plain element that is not normalized - used to store a plain element that is not to be normalized (specified
		* by the normalize boolean) and/or to store a plain element initially.
		* \param element The GDCM element
		* \param tag The tag of the element to be inserted
		* \param file The GDCM file
		* \param normalize Boolean to determine if the element is to be considered for normalization or not
		* \param normMap The normalization map
		* \return attr The generated attribute
		*/
		Attribute * StorePlainNotNorm(gdcm::DataSet::Iterator element, const std::string & tag, gdcm::File &file, bool normalize, NormMapType & normMap);

		/** \brief Function to handle a dicom element - stores the element as binary, sequence or plain depending on the VR of the element
		* \param element The GDCM element
		* \param tag The tag of the element to be inserted
		* \param file The GDCM file
		* \param normalize Boolean to determine if the element is to be considered for normalization or not
		* \param normMap The normalization map
		* \return attr The generated attribute
		*/
		Attribute * HandleDICOMElement(gdcm::DataSet::Iterator element, const std::string & tag, gdcm::File &file, bool normalize, NormMapType & normMap);

		/** \brief Function to get value of an element from the file
		* \param element The GDCM element
		* \param file The GDCM file
		* \return value The string value read from the file
		*/
		std::string GetValFromElement(gdcm::DataSet::Iterator element, gdcm::File &file);
		
		std::set<std::string> StudyLevelTags;
		std::set<std::string> SeriesLevelTags;
		DataDictionaryType dataDictionary;
		std::vector<std::string> BinaryFilePaths;
		MetaBinaryPair BinaryPair;
		TagNormalizerTableType tagNormalizerTable;
		uint32_t DEFAULT_BINARY_INLINE_THRESHOLD;
		CommonUtils comUtils;
		int bid;
};

#endif