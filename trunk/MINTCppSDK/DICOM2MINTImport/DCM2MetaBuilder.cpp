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

//DCM2MetaBuilder.cpp file
//Author: Gorkem Sevinc

#include "DCM2MetaBuilder.h"
#include <iostream>

DCM2MetaBuilder::DCM2MetaBuilder(std::set<std::string> studyLevelTags, std::set<std::string> seriesLevelTags, MetaBinaryPair & metaBinaryPair)
{
	this->BinaryPair = metaBinaryPair;
	bid = 0;
	BinaryFilePaths.clear();
	DEFAULT_BINARY_INLINE_THRESHOLD = 256;
	comUtils.GetDataDictionary(dataDictionary, this->StudyLevelTags, this->SeriesLevelTags);
}

Attribute * DCM2MetaBuilder::NewAttribute(gdcm::DataSet::Iterator element, const std::string & tag)
{
	Attribute * attr = new Attribute();
	attr->SetTag(tag);
	const char * VRFromFile = gdcm::VR::GetVRStringFromFile(element->GetVR());
	if(!(gdcm::VR::CanDisplay(element->GetVR())) && (strcmp(VRFromFile, "SQ") != 0))
	{
		DataDictionaryType::iterator dataIt = dataDictionary.find(tag);
		if(dataIt != dataDictionary.end())
		{
			attr->SetVR(comUtils.utf8_decode(dataIt->second));
		}
		else
		{
			attr->SetVR(comUtils.utf8_decode("UN"));
		}	
	}
	else
	{
		attr->SetVR(comUtils.utf8_decode(VRFromFile));
	}
	return attr;
}

std::string DCM2MetaBuilder::ToUpper(const std::string & s)
{
    std::string ret(s.size(), char());
    for(unsigned int i = 0; i < s.size(); ++i)
        ret[i] = (s[i] <= 'z' && s[i] >= 'a') ? s[i]-('a'-'A') : s[i];
    return ret;
}


std::string DCM2MetaBuilder::GenerateUnpipedTag(gdcm::Tag tag)
{
	std::string tagPipeSeparated = tag.PrintAsPipeSeparatedString();
	std::string groupTag = tagPipeSeparated.substr(1,4);
	std::string elementTag = tagPipeSeparated.substr(6,4);
	
	std::string tagUnpiped = groupTag;
	tagUnpiped.append(elementTag);	
	return ToUpper(tagUnpiped);
}

Attribute * DCM2MetaBuilder::StoreBinary(gdcm::DataSet::Iterator element, const std::string & tag, bool normalize, NormMapType & normMap)
{

	Attribute * attr = new Attribute();
	const gdcm::ByteValue *bytePointer = element->GetByteValue();
	
	if(bytePointer != NULL)
	{
		gdcm::VL len = bytePointer->GetLength();
		attr = NewAttribute(element, tag);

		if(len.GetValueLength() <= DEFAULT_BINARY_INLINE_THRESHOLD)
		{
			char * readByte = new char[len.GetValueLength()];
			bytePointer->GetBuffer(readByte, len.GetValueLength());
			NormMapType::iterator normIt = normMap.find(tag);
			if(normIt != normMap.end() && normalize == true)
			{
				Attribute * ncAttr = normIt->second.attr;
				const char * mappedByte = ncAttr->GetBytes();
				if(sizeof(mappedByte) == sizeof(readByte) && strcmp(mappedByte,readByte))
				{
					attr = ncAttr;
					normIt->second.count++;
				}
				else
				{
					attr->SetBytes(readByte);
					attr->SetInlineByteSize(len.GetValueLength());
				}
			}
			else
			{
				attr->SetBytes(readByte);
				attr->SetInlineByteSize(len.GetValueLength());
				NormalizationCounter normCounter;
				normCounter.attr = attr;
				normMap.insert(std::make_pair(tag, normCounter));
			}
		}
		else
		{
			attr->SetBid(bid);
			attr->SetBSize(len.GetValueLength());
				
			std::string baseDir("C:/MINTOutput/binaryitems/");
			std::string bidStr;
			std::stringstream bidOut;
			bidOut << bid;
			bidStr = bidOut.str();
			std::string dataExtension(".dat");
			std::string dir = baseDir;
			dir.append(bidStr);
			dir.append(dataExtension);
			
			BinaryFilePaths.push_back(dir);

			std::ofstream out(dir.c_str(), std::ios::out | std::ios::binary);

			bytePointer->WriteBuffer(out);
		    
			out.close();

			bid++;
		}
	}
	else
	{
		attr = NewAttribute(element, tag);
	}
    return attr;
}

Attribute * DCM2MetaBuilder::StoreSequence(gdcm::DataSet::Iterator element, const std::string & tag, gdcm::File &file, NormMapType & normMap)
{		
	Attribute * attr = NewAttribute(element, tag);
	gdcm::SmartPointer<gdcm::SequenceOfItems> seqOfItems = element->GetValueAsSQ();
	gdcm::SequenceOfItems::Iterator it = seqOfItems.GetPointer()->Begin();
	for(it; it != seqOfItems.GetPointer()->End(); it++)
	{
		Item item;
		gdcm::DataSet::Iterator nestedIt = it->GetNestedDataSet().Begin();
		if(!nestedIt->IsEmpty())
		{
			for(nestedIt; nestedIt != it->GetNestedDataSet().End(); nestedIt++)
			{
				std::string unpipedTag = GenerateUnpipedTag(nestedIt->GetTag());
				Attribute * seqAttr = this->HandleDICOMElement(nestedIt, unpipedTag, file, false, normMap);
				item.PutAttribute(seqAttr);
			}
		}
		attr->AddItem(item);
	}
    return attr;
}

std::string DCM2MetaBuilder::GetValFromElement(gdcm::DataSet::Iterator element, gdcm::File &file)
{
	gdcm::StringFilter sf;
	sf.SetFile(file);
	gdcm::VL val;
	std::string value;
	if(!element->IsEmpty())
	{
		value = sf.ToString(element->GetTag());
		if(value == "") // This is a gdcm issue, when in a sequence can not get the value as string from the file
		{
			std::stringstream seqvalue;
			element->GetValue().Print(seqvalue);
			value = seqvalue.str();
		}
	}
	return value;
}

Attribute * DCM2MetaBuilder::StorePlainNotNorm(gdcm::DataSet::Iterator element, const std::string & tag, gdcm::File &file, bool normalize, NormMapType & normMap)
{
	Attribute * attr = NewAttribute(element, tag);

	std::string value = GetValFromElement(element, file);
	attr->SetVal(comUtils.utf8_decode(value));
	if(normalize)
	{
		NormalizationCounter normCounter;
		normCounter.attr = attr;
		NormMapType::iterator normIt = normMap.find(tag);
		if(normIt == normMap.end())
		{
			normMap.insert(std::make_pair(tag, normCounter));
		}
	}
	return attr;
}

Attribute * DCM2MetaBuilder::StorePlain(gdcm::DataSet::Iterator element, const std::string & tag, gdcm::File &file, bool normalize, NormMapType & normMap)
{
	Attribute * attr = new Attribute();
	NormMapType::iterator normIt = normMap.find(tag);
	NormalizationCounter normCounter;
	if(normIt != normMap.end() && normalize == true)
	{
		normCounter = normIt->second;
		Attribute * ncAttr = normCounter.attr;
		std::string value = GetValFromElement(element, file);
		if(strcmp(value.c_str(), comUtils.utf8_encode(ncAttr->GetVal()).c_str()) == 0)
		{
			attr = ncAttr;
			normIt->second.count++;
		}
		else
		{
			NormMapType::iterator it = normMap.find(tag);
			if(it != normMap.end())
			{
				normMap.erase(it);
			}
			else
			{
				std::cerr<<"Error: could not find the tag to remove from normalization table."<<std::endl;
			}
			attr = StorePlainNotNorm(element, tag, file, false, normMap);
		}
	}
	else
	{
		attr = StorePlainNotNorm(element, tag, file, normalize, normMap);
	}
	return attr;
}

Attribute * DCM2MetaBuilder::HandleDICOMElement(gdcm::DataSet::Iterator element, const std::string & tag, gdcm::File &file, bool normalize, NormMapType & normMap)
{
	std::string vrName = gdcm::VR::GetVRStringFromFile(element->GetVR());

	if(!gdcm::VR::CanDisplay(element->GetVR()) && vrName != "SQ") 
	{	
		DataDictionaryType::iterator dataIt = dataDictionary.find(tag);
		if(dataIt != dataDictionary.end())
		{
			vrName = dataIt->second;
		}
		else
		{
			vrName = "UN";
		}	
	}

	Attribute * attr;
	if(vrName == "OW" || vrName == "OB" || vrName == "OF" || vrName == "UN" || vrName == "UN_SIEMENS")
	{
		attr = StoreBinary(element, tag, normalize, normMap);
	}
	else if(vrName == "SQ")
	{
		attr = StoreSequence(element, tag, file, normMap);
	}
	else
	{
		attr = StorePlain(element, tag, file, normalize, normMap);
	}
	return attr;
}

void DCM2MetaBuilder::HandleElement(gdcm::DataSet::Iterator element, Series * series, Instance * instance, gdcm::File &file, NormMapType & normMap)
{
	std::string tag = GenerateUnpipedTag(element->GetTag());
	
	std::set<std::string>::const_iterator it;
	it = this->StudyLevelTags.find(tag);
	if(it!=this->StudyLevelTags.end())
	{
		Attribute * attr = HandleDICOMElement(element, tag, file, false, normMap);
		if(!attr->GetTag().empty())
			this->BinaryPair.GetMetadata()->PutAttribute(attr);
	}
	else
	{
		it = this->SeriesLevelTags.find(tag);
		if(it!=this->SeriesLevelTags.end())
		{
			Attribute * attr = HandleDICOMElement(element, tag, file, false, normMap);
			if(!attr->GetTag().empty())
				series->PutAttribute(attr); 
		}
		else
		{
			Attribute * attr = HandleDICOMElement(element, tag, file, true, normMap);
			if(!attr->GetTag().empty())
				instance->PutAttribute(attr);
		}
	}
	
}

MetaBinaryPair DCM2MetaBuilder::GetMetadataBuilder(void)
{
	return this->BinaryPair;
}

SeriesMapType DCM2MetaBuilder::GetSeries(void)
{
	return this->BinaryPair.GetMetadata()->GetSeries();
}

void DCM2MetaBuilder::ReadDICOMData(gdcm::File &file)
{
	gdcm::FileMetaInformation metaDICOM = file.GetHeader();

	gdcm::StringFilter sf;
	sf.SetFile(file);

	std::string studyInstanceUIDStr = sf.ToString(gdcm::Tag(0x0020, 0x000d));
	std::string seriesInstanceUIDStr = sf.ToString(gdcm::Tag(0x0020, 0x000e));
	std::string sopInstanceUIDStr = sf.ToString(gdcm::Tag(0x0008, 0x0018));
	std::string transferSyntaxUIDStr = sf.ToString(gdcm::Tag(0x0002, 0x0010));
	std::string characterSetStr = sf.ToString(gdcm::Tag(0x0008, 0x0005));

	std::wstring studyInstanceUID = comUtils.utf8_decode(studyInstanceUIDStr);
	std::wstring seriesInstanceUID = comUtils.utf8_decode(seriesInstanceUIDStr);
	std::wstring sopInstanceUID = comUtils.utf8_decode(sopInstanceUIDStr);
	std::wstring transferSyntaxUID = comUtils.utf8_decode(transferSyntaxUIDStr);
	std::wstring characterSet = comUtils.utf8_decode(characterSetStr);
	

	this->BinaryPair.GetMetadata()->SetStudyInstanceUID(std::wstring(studyInstanceUID));

	Series * series = this->BinaryPair.GetMetadata()->GetSeriesFromUID(seriesInstanceUID);
	if(series == NULL)
	{
		series = new Series();
		series->SetSeriesInstanceUID(seriesInstanceUID);
	}

	NormMapType normMap;
	TagNormalizerTableType::iterator normIt = tagNormalizerTable.find(seriesInstanceUID);
	if(normIt != tagNormalizerTable.end())
	{
		normMap = normIt->second;
	}
	this->tagNormalizerTable.insert(std::make_pair(seriesInstanceUID, normMap));

	Instance * instance = new Instance();
	instance->SetTransferSyntaxUID(transferSyntaxUID);
	instance->SetSOPInstanceUID(sopInstanceUID);

	gdcm::FileMetaInformation fmi = file.GetHeader();
	gdcm::DataSet::Iterator it = fmi.Begin();
	for(it; it != fmi.End(); ++it)
	{
		HandleElement(it, series, instance, file, this->tagNormalizerTable.find(seriesInstanceUID)->second);
	}
	
	gdcm::DataSet::Iterator dataIt = file.GetDataSet().Begin();
	for(dataIt; dataIt != file.GetDataSet().End(); ++dataIt)
	{
		HandleElement(dataIt, series, instance, file, this->tagNormalizerTable.find(seriesInstanceUID)->second);
	}

	series->PutInstance(instance);
	this->BinaryPair.GetMetadata()->PutSeries(series);
}

void DCM2MetaBuilder::finish(void)
{
	TagNormalizerTableType::iterator tagIt = tagNormalizerTable.begin();
	for(tagIt; tagIt != tagNormalizerTable.end(); tagIt++)
	{
		Series * seriesTag = this->BinaryPair.GetMetadata()->GetSeriesFromUID(tagIt->first);
		if(seriesTag == NULL)
		{
			std::cerr<<"Normalization: cannot find series in study data"<<std::endl;
			return;
		}
		int nInstances = seriesTag->GetInstances().size();
		if(nInstances > 1)
		{
			NormMapType::iterator normIt = tagIt->second.begin();
			int i = 0;
			for(normIt; normIt != tagIt->second.end(); ++normIt)
			{
				if(normIt->second.count == nInstances)
				{
					seriesTag->PutNormalizedAttribute(normIt->second.attr);
					InstanceMapType seriesInstance = seriesTag->GetInstances();
					InstanceMapType::iterator instanceIt = seriesInstance.begin();
					for(instanceIt; instanceIt != seriesInstance.end(); instanceIt++)
					{
						instanceIt->second->RemoveAttribute(normIt->second.attr->GetTag());
					}
				}
			}
		}
	}
}

void DCM2MetaBuilder::ProcessDICOMFile(const char * path)
{
	gdcm::Reader reader;
	reader.SetFileName(path);
	bool success = reader.Read();
	if( !success)
	{
		std::cerr << "Failed to read: " << std::endl;
	}
	gdcm::File &file = reader.GetFile();

	this->ReadDICOMData(file);
}

void DCM2MetaBuilder::PrintMetadata(void)
{
	CommonUtils comUtils;
	comUtils.PrintMetadataGPB(this->BinaryPair.GetMetadata());
}


std::vector<std::string> DCM2MetaBuilder::GetBinaryFilePaths(void)
{
	return BinaryFilePaths;
}
