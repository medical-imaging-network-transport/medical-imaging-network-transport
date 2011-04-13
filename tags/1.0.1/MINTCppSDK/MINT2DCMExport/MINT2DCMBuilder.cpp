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

//MINT2DCMBuilder.cpp file
//Author: Gorkem Sevinc

#include "MINT2DCMBuilder.h"
#include <iostream>
#include <sstream>

MINT2DCMBuilder::MINT2DCMBuilder()
{}

MINT2DCMBuilder::MINT2DCMBuilder(std::string URL)
{
	studyURL = URL;
	std::string metadataURL = URL + "/metadata.gpb";
	Study * metadata = comUtils.ReadStudyFromURL(metadataURL);

	comUtils.GetDataDictionary(dataDictionary, studyLevelTags, seriesLevelTags);

	this->WriteStudy(metadata);
}

template <class T>
bool from_string(T& t, const std::string& s, std::ios_base& (*f)(std::ios_base&)) 
{
	std::istringstream iss(s);
	return !(iss >> f >>t).fail();
}

gdcm::DataElement MINT2DCMBuilder::GetElement(Attribute * attribute)
{	
	gdcm::DataElement element;

	uint32_t tagGroup;
	uint32_t tagElement;
	
	from_string<uint32_t>(tagGroup, attribute->GetTag().substr(0, 4), std::hex);
	from_string<uint32_t>(tagElement, attribute->GetTag().substr(4, 4), std::hex);
	
	gdcm::Tag tag(tagGroup, tagElement);
	element.SetTag(tag);

	std::string VR = comUtils.utf8_encode(attribute->GetVR());
	element.SetVR(gdcm::VR::GetVRType(VR.c_str()));
	
	if(VR == "OW" || VR == "UN" || VR == "OB")
	{
		if(attribute->GetBid() >= 0)
		{
			std::stringstream bidSS;
			bidSS << attribute->GetBid();
			std::string binaryURL = studyURL + "binaryitems/" + bidSS.str() + ".dat";

			//CommonUtils utils;
			std::string * buffer = comUtils.ReadURLToBuffer(binaryURL);

			element.SetByteValue(buffer->c_str(), buffer->length());

			delete buffer;
		}
		else
		{
			if(attribute->GetInlineByteSize() >= 0)
			{
				element.SetByteValue((char *)attribute->GetBytes(), attribute->GetInlineByteSize());
			}
		}
	}
	else
	{
		std::string value = comUtils.utf8_encode(attribute->GetVal());
		//This is just a sanity check, this should never really happen.
		if(value.length() == 0 && attribute->GetInlineByteSize() > 0)
		{
			value = std::string(attribute->GetBytes());
		}

		if(VR == "US")
		{
			char delim = '\\';
			std::vector<std::string> values = comUtils.splitString(value, delim);
			if(values.size() > 1)
			{
				std::vector<std::string>::iterator valueIt = values.begin();
				char * valuesConverted = new char[(2 * values.size())];
				int i = 0;
				for(valueIt; valueIt != values.end(); valueIt++)
				{
					unsigned short shortZero = 0;
					unsigned short shortValue = (unsigned short)atoi(valueIt->c_str());
					valuesConverted[i] = (char)shortValue;
					valuesConverted[i+1] = (char)shortZero;
					i+=2;
				}					
				element.SetByteValue(valuesConverted, values.size() * 2);
			}
			else
			{
				unsigned short shortValue = (unsigned short)atoi(value.c_str());
				element.SetByteValue((const char*)&shortValue, sizeof(shortValue));
			}
		}
		else if(VR == "UL")
		{
			char delim = '\\';
			std::vector<std::string> values = comUtils.splitString(value, delim);
			if(values.size() > 1)
			{
				std::vector<std::string>::iterator valueIt = values.begin();
				char * valuesConverted = new char[values.size()];
				int i = 0;
				for(valueIt; valueIt != values.end(); valueIt++)
				{
					
					unsigned long longValue = (unsigned long)atoi(valueIt->c_str());
					valuesConverted[i] = (char)longValue;
					i++;
				}					
				element.SetByteValue(valuesConverted, values.size());
			}
			else
			{
				unsigned long longValue = (unsigned long)atoi(value.c_str());
				element.SetByteValue((const char*)&longValue, sizeof(longValue));
			}
		}
		else if(VR == "SS")
		{
			char delim = '\\';
			std::vector<std::string> values = comUtils.splitString(value, delim);
			if(values.size() > 1)
			{
				std::vector<std::string>::iterator valueIt = values.begin();
				char * valuesConverted = new char[(2 * values.size())];
				int i = 0;
				for(valueIt; valueIt != values.end(); valueIt++)
				{
					short shortZero = 0;
					short shortValue = (short)atoi(valueIt->c_str());
					valuesConverted[i] = (char)shortValue;
					valuesConverted[i+1] = (char)shortZero;
					i+=2;
				}					
				element.SetByteValue(valuesConverted, values.size() * 2);
			}
			else
			{
				short shortValue = (short)atoi(value.c_str());
				element.SetByteValue((const char*)&shortValue, sizeof(shortValue));
			}
		}
		else if(VR == "SL")
		{
			char delim = '\\';
			std::vector<std::string> values = comUtils.splitString(value, delim);
			if(values.size() > 1)
			{
				std::vector<std::string>::iterator valueIt = values.begin();
				char * valuesConverted = new char[values.size()];
				int i = 0;
				for(valueIt; valueIt != values.end(); valueIt++)
				{
					long longValue = (long)atoi(valueIt->c_str());
					valuesConverted[i] = (char)longValue;
					i++;
				}					
				element.SetByteValue(valuesConverted, values.size());
			}
			else
			{
				long longValue = (long)atoi(value.c_str());
				element.SetByteValue((const char*)&longValue, sizeof(longValue));
			}
		}
		else
		{
			if(attribute->GetInlineByteSize() > 0)
			{
				element.SetByteValue((char *)attribute->GetBytes(), attribute->GetInlineByteSize());
			}
			else
			{
				element.SetByteValue((char *)value.c_str(), value.length()); 
			}
		}
	}
	return element;
}

void MINT2DCMBuilder::InsertAttribute(gdcm::DataSet & dataSet, gdcm::FileMetaInformation & fmi, Attribute * attribute)
{
	std::vector<Item> items = attribute->GetItems();
	if(items.size() != 0)
	{
		gdcm::SmartPointer<gdcm::SequenceOfItems> sq = new gdcm::SequenceOfItems();
		uint32_t tagGroup;
		uint32_t tagElement;
		
		from_string<uint32_t>(tagGroup, attribute->GetTag().substr(0, 4), std::hex);
		from_string<uint32_t>(tagElement, attribute->GetTag().substr(4, 4), std::hex);
		
		gdcm::Tag tag(tagGroup, tagElement);
		gdcm::DataElement des(tag);
		des.SetVR(gdcm::VR::SQ);

		//items found
		std::vector<Item>::iterator itemIter = items.begin();
		for(itemIter; itemIter != items.end(); itemIter++)
		{
			gdcm::Item item;
			AttributeMapType itemAttributes = itemIter->GetAttributes();
			AttributeMapType::iterator itemAttrIter = itemAttributes.begin();
			for(itemAttrIter; itemAttrIter != itemAttributes.end(); itemAttrIter++)
			{
				item.InsertDataElement(GetElement(itemAttrIter->second));	
			}
			sq->AddItem(item);
			
		}
		des.SetValue(*sq);
		dataSet.Insert(des);
	}
	else
	{
		gdcm::DataElement element = GetElement(attribute);
		if(element.GetTag().GetGroup() == 0x0002)
		{
			fmi.Insert(element);
		}
		else
		{
			dataSet.Insert(element);
		}
	}
}

gdcm::TransferSyntax MINT2DCMBuilder::GetTransferSyntaxFromString(std::string transferSyntaxUID)
{
	if(transferSyntaxUID == "1.2.840.10008.1.2")
		return gdcm::TransferSyntax::ImplicitVRLittleEndian;
	else if(transferSyntaxUID == "1.2.840.10008.1.2.1")
		return gdcm::TransferSyntax::ExplicitVRLittleEndian;
	else if(transferSyntaxUID == "1.2.840.10008.1.2.2")
		return gdcm::TransferSyntax::ExplicitVRBigEndian;
	else if(transferSyntaxUID == "1.2.840.10008.1.2.5")
		return gdcm::TransferSyntax::RLELossless;
	else if(transferSyntaxUID == "1.2.840.10008.1.2.4.50")
		return gdcm::TransferSyntax::JPEGBaselineProcess1;
	else if(transferSyntaxUID == "1.2.840.10008.1.2.4.51")
		return gdcm::TransferSyntax::JPEGExtendedProcess2_4;
	else if(transferSyntaxUID == "1.2.840.10008.1.2.4.55")
		return gdcm::TransferSyntax::JPEGFullProgressionProcess10_12;
	else if(transferSyntaxUID == "1.2.840.10008.1.2.4.57")
		return gdcm::TransferSyntax::JPEGLosslessProcess14;
	else if(transferSyntaxUID == "1.2.840.10008.1.2.4.70")
		return gdcm::TransferSyntax::JPEGLosslessProcess14_1;
	else if(transferSyntaxUID == "1.2.840.10008.1.2.4.80")
		return gdcm::TransferSyntax::JPEGLSLossless;
	else if(transferSyntaxUID == "1.2.840.10008.1.2.4.81")
		return gdcm::TransferSyntax::JPEGLSNearLossless;
	else if(transferSyntaxUID == "1.2.840.10008.1.2.4.90")
		return gdcm::TransferSyntax::JPEG2000Lossless;
	else if(transferSyntaxUID == "1.2.840.10008.1.2.4.91")
		return gdcm::TransferSyntax::JPEG2000;
	else if(transferSyntaxUID == "1.2.840.113619.5.2")
		return gdcm::TransferSyntax::ImplicitVRBigEndianPrivateGE;
	else if(transferSyntaxUID == "1.2.840.10008.1.2.1.99")
		return gdcm::TransferSyntax::DeflatedExplicitVRLittleEndian;
	else if(transferSyntaxUID == "1.2.840.10008.1.2.4.52")
		return gdcm::TransferSyntax::JPEGExtendedProcess3_5;
	else if(transferSyntaxUID == "1.2.840.10008.1.2.4.53")
		return gdcm::TransferSyntax::JPEGSpectralSelectionProcess6_8;
	else if(transferSyntaxUID == "1.2.840.10008.1.2.4.100")
		return gdcm::TransferSyntax::MPEG2MainProfile;
	else if(transferSyntaxUID == "ImplicitVRBigEndianACRNEMA")
		return gdcm::TransferSyntax::ImplicitVRBigEndianACRNEMA;
	else
		return gdcm::TransferSyntax::ImplicitVRLittleEndian;
}

void MINT2DCMBuilder::WriteStudy(Study * metadata)
{
	gdcm::Writer w;
	SeriesMapType series = metadata->GetSeries();
	SeriesMapType::iterator seriesIter = series.begin();
	int i = 0;
	for(seriesIter; seriesIter != series.end(); seriesIter++)
	{	
		gdcm::FileMetaInformation header;
		gdcm::DataSet dataSet;

		AttributeMapType studyAttributes = metadata->GetAttributes();
		AttributeMapType::iterator studyAttributeIter = studyAttributes.begin();
		for(studyAttributeIter; studyAttributeIter != studyAttributes.end(); studyAttributeIter++)
		{
			//insert attribute
			InsertAttribute(dataSet, header, studyAttributeIter->second);
		}

		AttributeMapType seriesAttributes = seriesIter->second->GetAttributes();
		AttributeMapType::iterator seriesAttributeIter = seriesAttributes.begin();
		for(seriesAttributeIter; seriesAttributeIter != seriesAttributes.end(); seriesAttributeIter++)
		{
			//insert attribute
			InsertAttribute(dataSet, header, seriesAttributeIter->second);
		}
		
		AttributeMapType normalizedAttributes = seriesIter->second->GetNormalizedInstanceAttributes();
		AttributeMapType::iterator normalizedAttributeIter = normalizedAttributes.begin();
		for(normalizedAttributeIter; normalizedAttributeIter != normalizedAttributes.end(); normalizedAttributeIter++)
		{
			//insert attribute
			InsertAttribute(dataSet, header, normalizedAttributeIter->second);
		}

		InstanceMapType instances = seriesIter->second->GetInstances();
		InstanceMapType::iterator seriesInstanceIter = instances.begin();
		for(seriesInstanceIter; seriesInstanceIter != instances.end(); seriesInstanceIter++)
		{
			gdcm::SmartPointer<gdcm::File> file = new gdcm::File;
			file.GetPointer()->SetHeader(header);
			gdcm::DataSet instanceDataSet = dataSet;
			//put transfer syntax uid
			gdcm::FileMetaInformation &fmi = file.GetPointer()->GetHeader();
			// const char * is ok since padding is \0 anyway...
			std::string transferSyntaxUID = comUtils.utf8_encode(seriesInstanceIter->second->GetTransferSyntaxUID());
			gdcm::TransferSyntax ts = GetTransferSyntaxFromString(transferSyntaxUID);
			gdcm::DataElement de( gdcm::Tag(0x0002,0x0010) );
			de.SetByteValue( transferSyntaxUID.c_str(), transferSyntaxUID.length() );
			de.SetVR( gdcm::Attribute<0x0002, 0x0010>::GetVR() );
			fmi.Replace( de );
			fmi.SetDataSetTransferSyntax(ts);

			AttributeMapType seriesInstanceAttributes = seriesInstanceIter->second->GetAttributeMap();
			AttributeMapType::iterator seriesInstanceAttributeIter = seriesInstanceAttributes.begin();
			for(seriesInstanceAttributeIter; seriesInstanceAttributeIter != seriesInstanceAttributes.end(); seriesInstanceAttributeIter++)
			{
				//insert attribute
				InsertAttribute(instanceDataSet, file.GetPointer()->GetHeader(), seriesInstanceAttributeIter->second);
			}

			//create dicom file
			std::string filePath = "C:/MINTOutput/out/";
			filePath.append(comUtils.utf8_encode(seriesInstanceIter->second->GetSOPInstanceUID()));
			filePath.append(".dcm");

			file.GetPointer()->SetDataSet(instanceDataSet);

		    w.SetFile( *(file.GetPointer()) );
		    w.SetFileName( filePath.c_str() );
			w.CheckFileMetaInformationOff();
		    if (!w.Write() )
			{
				std::cerr<<"Error writing."<<std::endl;
			}
			
			i++;
		}
	}
}

