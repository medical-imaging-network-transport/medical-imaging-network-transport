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

//MetaBinaryPair.cpp file
//Author: Gorkem Sevinc

#include "CommonUtils.h"

CommonUtils::CommonUtils()
{}

Study * CommonUtils::LoadMetadataFromGPBFile(const char * path)
{
	Study * study = new Study();
	std::fstream inMetadata(path, std::ios::in | std::ios::binary);
	mint::metadata::StudyData studyData;
	if(!studyData.ParseFromIstream(&inMetadata))
	{
		std::cerr<<"Failed to parse from GPB file."<<std::endl;
	}
	return LoadMetadataFromGPB(studyData);
}

Study * CommonUtils::LoadMetadataFromGPBString(std::string & metadataStringStream)
{
	Study * study = new Study();
	mint::metadata::StudyData studyData;
	if(!studyData.ParseFromString(metadataStringStream))
	{
		std::cerr<<"Failed to parse from GPB string."<<std::endl;
	}
	study = LoadMetadataFromGPB(studyData);
	std::cout<<"ID:: "<<this->utf8_encode(study->GetStudyInstanceUID())<<std::endl;
	return study;
}

Study * CommonUtils::LoadMetadataFromGPB(mint::metadata::StudyData & studyData)
{
	Study * study = new Study();

	study->SetStudyInstanceUID(this->utf8_decode(studyData.study_instance_uid()));
	for(int i = 0; i < studyData.attributes_size(); i++)
	{
		const mint::metadata::AttributeData& attribute = studyData.attributes(i);
		study->PutAttribute(this->GetAttributeFromGPB(attribute));
	}

	for(int j = 0; j < studyData.series_size(); j++)
	{
		Series * series = new Series();
		const mint::metadata::SeriesData& seriesData = studyData.series(j);
		series->SetSeriesInstanceUID(this->utf8_decode(seriesData.series_instance_uid()));
		
		for(int k = 0; k < seriesData.attributes_size(); k++)
		{
			const mint::metadata::AttributeData& attribute = seriesData.attributes(k);
			series->PutAttribute(this->GetAttributeFromGPB(attribute));
		}

		for(int l = 0; l < seriesData.normalized_instance_attributes_size(); l++)
		{
			const mint::metadata::AttributeData& attribute = seriesData.normalized_instance_attributes(l);
			series->PutNormalizedAttribute(this->GetAttributeFromGPB(attribute));
		}

		for(int m = 0; m < seriesData.instances_size(); m++)
		{
			Instance * instance = new Instance();
			const mint::metadata::InstanceData& instanceData = seriesData.instances(m);
			instance->SetSOPInstanceUID(this->utf8_decode(instanceData.sop_instance_uid()));
			instance->SetTransferSyntaxUID(this->utf8_decode(instanceData.transfer_syntax_uid()));
			for(int n = 0; n < instanceData.attributes_size(); n++)
			{
				const mint::metadata::AttributeData& attribute = instanceData.attributes(n);
				instance->PutAttribute(this->GetAttributeFromGPB(attribute));
			}
			series->PutInstance(instance);
		}
		
		study->PutSeries(series);
	}
	
	return study;
}
		
Attribute * CommonUtils::GetAttributeFromGPB(const mint::metadata::AttributeData& attribute)
{
	Attribute * attr = new Attribute();
	if(attribute.has_tag())
	{
		char *str = new char[8];
		sprintf(str, "%lX", (unsigned long)(attribute.tag()));
		std::string s = str;
	
		while(s.length() < 8)
		{
			std::string zero = "0";
			std::string temp = zero.append(s);
			s = temp;
		}
		attr->SetTag(s);
	}
	if(attribute.has_vr())
	{
		attr->SetVR(this->utf8_decode(attribute.vr()));
	}
	if(attribute.has_binary_item_id())
	{
		attr->SetBid(attribute.binary_item_id());
	}
	if(attribute.has_binary_item_size())
	{
		attr->SetBSize(attribute.binary_item_size());
	}
	if(attribute.has_bytes())
	{
		char * myBinData = new char[attribute.ByteSize()];
		
		memcpy(myBinData, attribute.bytes().c_str(), attribute.bytes().length());

		attr->SetBytes(myBinData);
		attr->SetInlineByteSize(attribute.bytes().length());
	}
	if(attribute.has_string_value())
	{
		attr->SetVal(this->utf8_decode(attribute.string_value()));
	}
	if(attribute.items_size() > 0)
	{
		Item item;
		for(int i = 0; i < attribute.items_size(); i++)
		{
			for(int j = 0; j < attribute.items(i).attributes_size(); j++)
			{
				const mint::metadata::AttributeData& itemAttribute = attribute.items(i).attributes(j);
				Attribute * attrItem = GetAttributeFromGPB(itemAttribute);
				item.PutAttribute(attrItem);
				
			}
		}
		attr->AddItem(item);
	}
	return attr;
}


AttributeVectorType CommonUtils::GetAttributeFromXML(TiXmlElement * pElemInner)
{
	AttributeVectorType attributes;
	for(pElemInner; pElemInner; pElemInner = pElemInner->NextSiblingElement())
	{
		Attribute * attr = new Attribute();
		const char * tag = pElemInner->Attribute("tag");
		const char * vr = pElemInner->Attribute("vr");
		const char * bytes = pElemInner->Attribute("bytes");
		const char * val = pElemInner->Attribute("val");
		const char * bid = pElemInner->Attribute("bid");
		const char * bsize = pElemInner->Attribute("bsize");
		if(tag)
		{
			attr->SetTag(tag);
		}
		if(vr)
			attr->SetVR(this->utf8_decode(vr));
		if(bytes)
			attr->SetBytes((char *)bytes);
		if(val)
			attr->SetVal(this->utf8_decode(val));
		if(bid)
		{
			attr->SetBid(atoi(bid));
		}
		if(bsize)
			attr->SetBSize(atoi(bsize));

		attributes.push_back(attr);
		if(strcmp(vr, "SQ") == 0)
		{
			AttributeVectorType sequenceAttributes;
			Item item;
			TiXmlElement * pSeq = pElemInner->FirstChildElement();
			TiXmlElement * pSeqChild = pSeq->FirstChildElement();
			sequenceAttributes = GetAttributeFromXML(pSeqChild->FirstChildElement());
			for(AttributeVectorType::iterator it = sequenceAttributes.begin(); it != sequenceAttributes.end(); ++it)
			{
				item.PutAttribute(*it);
			}
			attr->AddItem(item);
		}
	}
	return attributes;
}

Study * CommonUtils::LoadMetadataFromXML(const char * path)
{
	Study * metadata = new Study();

	TiXmlDocument doc(path);
	bool openOkay = doc.LoadFile(TIXML_ENCODING_UTF8);
	if(openOkay)
	{
		printf("\n%s:\n", path);
	}

	const char * studyInstanceUID;

	TiXmlHandle hDoc(&doc);
	TiXmlElement* pElem;
	TiXmlHandle hRoot(0);
	pElem = hDoc.FirstChildElement().Element();
	
	const char * pKey = pElem->Value();
	studyInstanceUID = pElem->Attribute("studyInstanceUID");
	
	metadata->SetStudyInstanceUID(this->utf8_decode(studyInstanceUID));

	pElem = hDoc.FirstChildElement().FirstChildElement().Element();

	const char * attribute = "attributes";
	const char * seriesList = "seriesList";
	const char * normalizedInstanceAttributes = "normalizedInstanceAttributes";
	const char * instances = "instances";

	for(pElem; pElem; pElem = pElem->NextSiblingElement())
	{
		pKey = pElem->Value();
		if(strcmp(pKey, attribute) == 0)
		{
			//study level attributes
			AttributeVectorType studyAttributes = GetAttributeFromXML(pElem->FirstChildElement());
			for(AttributeVectorType::iterator it = studyAttributes.begin(); it != studyAttributes.end(); ++it)
			{
				metadata->PutAttribute(*it);
			}
		}
		else if(strcmp(pKey, seriesList) == 0)
		{
			TiXmlElement * pElemSeries = pElem->FirstChildElement();
			for(pElemSeries; pElemSeries; pElemSeries = pElemSeries->NextSiblingElement()) 
			{
				const char * seriesInstanceUID = pElemSeries->Attribute("seriesInstanceUID");
				if(seriesInstanceUID)
				{
					TiXmlElement * pElemInner = pElemSeries->FirstChildElement();
					Series * series = new Series();
					series->SetSeriesInstanceUID(this->utf8_decode(seriesInstanceUID));
					for(pElemInner; pElemInner; pElemInner = pElemInner->NextSiblingElement()) 
					{
						if(strcmp(pElemInner->Value(), attribute) == 0)
						{
							//series level attributes
							AttributeVectorType seriesAttributes = GetAttributeFromXML(pElemInner->FirstChildElement());
							for(AttributeVectorType::iterator it = seriesAttributes.begin(); it != seriesAttributes.end(); ++it)
							{
								series->PutAttribute(*it);
							}
							
						}
						else if(strcmp(pElemInner->Value(), normalizedInstanceAttributes) == 0)
						{
							//normalized instance attributes
							AttributeVectorType seriesNormalizedAttributes = GetAttributeFromXML(pElemInner->FirstChildElement());
							for(AttributeVectorType::iterator it = seriesNormalizedAttributes.begin(); it != seriesNormalizedAttributes.end(); ++it)
							{
								series->PutNormalizedAttribute(*it);
							}
						}
						else if(strcmp(pElemInner->Value(), instances) == 0)
						{
							//instances
							TiXmlElement * pElemInstance = pElemInner->FirstChildElement();
							for(pElemInstance; pElemInstance; pElemInstance = pElemInstance->NextSiblingElement())
							{
								const char * sopInstanceUID = pElemInstance->Attribute("sopInstanceUID");
								const char * transferSyntaxUID = pElemInstance->Attribute("transferSyntaxUID");
								Instance * instance = new Instance();
								instance->SetSOPInstanceUID(this->utf8_decode(sopInstanceUID));
								instance->SetTransferSyntaxUID(this->utf8_decode(transferSyntaxUID));
								
								TiXmlElement * pElemAttr = pElemInstance->FirstChildElement();
								if(strcmp(pElemAttr->Value(), attribute) == 0)
								{
									//instance level attributes
									AttributeVectorType instanceAttributes = GetAttributeFromXML(pElemAttr->FirstChildElement());
									for(AttributeVectorType::iterator it = instanceAttributes.begin(); it != instanceAttributes.end(); ++it)
									{
										instance->PutAttribute(*it);
									}
								}
								series->PutInstance(instance);
							}
						}
					}
					metadata->PutSeries(series);
				}
			}
		}
	}
	return metadata;
}

template <class T>
bool from_string(T& t, const std::string& s, std::ios_base& (*f)(std::ios_base&)) 
{
	std::istringstream iss(s);
	return !(iss >> f >>t).fail();
}

void CommonUtils::PrintAttributeGPB(Attribute * attr, mint::metadata::AttributeData * attributeData)
{
	if(attr->GetTag().length() != 0)
	{
		uint32_t tagGroup;
		from_string<uint32_t>(tagGroup, attr->GetTag(), std::hex);
		attributeData->set_tag(tagGroup);
		attributeData->set_vr(this->utf8_encode(attr->GetVR()));
		if(this->utf8_encode(attr->GetVR()) == "SQ")
		{
			std::vector<Item> items = attr->GetItems();
			if(!items.empty())
			{
				mint::metadata::ItemData * itemData = attributeData->add_items();
				
				std::vector<Item>::iterator itemIt = items.begin();
				for(itemIt; itemIt != items.end(); ++itemIt)
				{
					
					
					AttributeMapType attrItem = itemIt->GetAttributes();
					AttributeMapType::iterator attrIt = attrItem.begin();
					for(attrIt; attrIt != attrItem.end(); attrIt++)
					{
						mint::metadata::AttributeData * itemAttributeData = itemData->add_attributes();
						PrintAttributeGPB(attrIt->second, itemAttributeData);
					}
				}
			}
		}
		else
		{
			if(attr->GetBid() != -1)
			{
				attributeData->set_binary_item_id(attr->GetBid());
				attributeData->set_binary_item_size(attr->GetBSize());
			}
			else if(attr->GetInlineByteSize() > 0)
			{
				std::string& dstBuf = *attributeData->mutable_bytes();
				dstBuf.resize(attr->GetInlineByteSize());
				memcpy(&dstBuf[0], attr->GetBytes(), dstBuf.size());
			}
			else
			{
				if(this->utf8_encode(attr->GetVal()).length() != 0)
				{
					attributeData->set_string_value(this->utf8_encode(attr->GetVal()).c_str());
				}
			}
		}
	}
}

void CommonUtils::PrintMetadataGPB(Study * Metadata)
{
	mint::metadata::StudyData studyData;
	studyData.set_study_instance_uid(this->utf8_encode(Metadata->GetStudyInstanceUID()));

	AttributeMapType studyLevelAttributes = Metadata->GetAttributes();
	AttributeMapType::iterator it = studyLevelAttributes.begin();

	for(it; it != studyLevelAttributes.end(); it++)
	{
		if(it->second != NULL)
			PrintAttributeGPB(it->second, studyData.add_attributes());
	}
	
	SeriesMapType seriesLevel = Metadata->GetSeries();
	SeriesMapType::iterator seriesIt = seriesLevel.begin();

	int totalInstanceCount = 0;

	for(seriesIt; seriesIt != seriesLevel.end(); seriesIt++)
	{
		totalInstanceCount += seriesIt->second->GetInstances().size();

		mint::metadata::SeriesData * seriesData = studyData.add_series();
		seriesData->set_series_instance_uid(this->utf8_encode(seriesIt->second->GetSeriesInstanceUID()));
		seriesData->set_instance_count(seriesIt->second->GetInstances().size());
		
		AttributeMapType seriesLevelAttributes = seriesIt->second->GetAttributes();
		AttributeMapType::iterator seriesAttrIt = seriesLevelAttributes.begin();
		for(seriesAttrIt; seriesAttrIt != seriesLevelAttributes.end(); seriesAttrIt++)
		{
			if(seriesAttrIt->second != NULL)
				PrintAttributeGPB(seriesAttrIt->second, seriesData->add_attributes());
		}

		AttributeMapType normalizedInstanceAttributes = seriesIt->second->GetNormalizedInstanceAttributes();
		if(!normalizedInstanceAttributes.empty())
		{
			AttributeMapType::iterator normalizedAttrIt = normalizedInstanceAttributes.begin();
			for(normalizedAttrIt; normalizedAttrIt != normalizedInstanceAttributes.end(); normalizedAttrIt++)
			{
				if(normalizedAttrIt->second != NULL)
					PrintAttributeGPB(normalizedAttrIt->second, seriesData->add_normalized_instance_attributes());
			}	
		}


		InstanceMapType instanceLevelAttributes = seriesIt->second->GetInstances();
		InstanceMapType::iterator instanceIt = instanceLevelAttributes.begin();
		for(instanceIt; instanceIt != instanceLevelAttributes.end(); instanceIt++)
		{
			mint::metadata::InstanceData * instanceData = seriesData->add_instances();
			instanceData->set_sop_instance_uid(this->utf8_encode(instanceIt->second->GetSOPInstanceUID()));
			instanceData->set_transfer_syntax_uid(this->utf8_encode(instanceIt->second->GetTransferSyntaxUID()));
	
			AttributeMapType instanceLevelAttributes = instanceIt->second->GetAttributeMap();
			AttributeMapType::iterator instanceAttrIt = instanceLevelAttributes.begin();
			for(instanceAttrIt; instanceAttrIt != instanceLevelAttributes.end(); ++instanceAttrIt)
			{
				if(instanceAttrIt->second != NULL)
					PrintAttributeGPB(instanceAttrIt->second, instanceData->add_attributes());
			}
		}
	}
	studyData.set_instance_count(totalInstanceCount);

	std::ofstream outDebug("C:/MINTOutput/debuglog.txt", std::ios::out);
	outDebug.write(studyData.DebugString().c_str(), studyData.DebugString().length());
	
	std::fstream outMetadata("C:/MINTOutput/metadata.gpb", std::ios::out | std::ios::binary);
	if(!studyData.SerializeToOstream(&outMetadata))
	{
		std::cerr << "Failed to write to GPB file." << std::endl;
	}

	//Below is commented out code that can serialize the metadata to a string rather than an outputstream
	/*std::string * output = new std::string();
	if(!studyData.SerializeToString(output))
	{
		std::cerr << "Failed to write to GPB file." << std::endl;
	}
	return output;*/
}


TiXmlElement * CommonUtils::PrintAttributeXML(Attribute * attr)
{
	TiXmlElement * attribute = new TiXmlElement("attr");
	attribute->SetAttribute("tag", attr->GetTag().c_str());
	attribute->SetAttribute("vr", this->utf8_encode(attr->GetVR()).c_str());
	if(attr->GetTag().length() != 0)
	{
		if(this->utf8_encode(attr->GetVR()) == "SQ")
		{
			std::vector<Item> items = attr->GetItems();
			if(!items.empty())
			{
				TiXmlElement * item = new TiXmlElement("item");

				std::vector<Item>::iterator itemIt = items.begin();
				for(itemIt; itemIt != items.end(); ++itemIt)
				{
					TiXmlElement * itemAttr = new TiXmlElement("attributes");
					AttributeMapType attrItem = itemIt->GetAttributes();
					AttributeMapType::iterator attrIt = attrItem.begin();
					for(attrIt; attrIt != attrItem.end(); attrIt++)
					{
						itemAttr->LinkEndChild(PrintAttributeXML(attrIt->second));
					}
					item->LinkEndChild(itemAttr);
				}
				attribute->LinkEndChild(item);
			}
		}
		else
		{
			if(attr->GetBid() != -1)
			{
				attribute->SetAttribute("bid", attr->GetBid());
				attribute->SetAttribute("bsize", attr->GetBSize());
			}
			else if(attr->GetInlineByteSize() > 0)
			{
				attribute->SetAttribute("bytes", attr->GetBytes());
			}
			else
			{
				if(attr->GetVal().length() != 0)
				{
					attribute->SetAttribute("val", this->utf8_encode(attr->GetVal()).c_str());
				}
			}
		}
	}
	return attribute;
}

void CommonUtils::PrintMetadataXML(Study * Metadata)
{
	TiXmlDocument doc;
	TiXmlDeclaration *decl = new TiXmlDeclaration("1.0", "UTF-8", "");
	doc.LinkEndChild(decl);
	
	TiXmlElement * root = new TiXmlElement("studyMeta");
	root->SetAttribute("studyInstanceUID", this->utf8_encode(Metadata->GetStudyInstanceUID()).c_str());
	root->SetAttribute("type", "DICOM");
	root->SetAttribute("version", "0");
	root->SetAttribute("instanceCount", "1"); //instance count not real here.
	doc.LinkEndChild(root);

	AttributeMapType studyLevelAttributes = Metadata->GetAttributes();
	AttributeMapType::iterator it = studyLevelAttributes.begin();

	TiXmlElement * attributes = new TiXmlElement("attributes");
	root->LinkEndChild(attributes);

	for(it; it != studyLevelAttributes.end(); it++)
	{
		if(it->second != NULL)
			attributes->LinkEndChild(PrintAttributeXML(it->second));
	}
	
	
	SeriesMapType seriesLevel = Metadata->GetSeries();
	SeriesMapType::iterator seriesIt = seriesLevel.begin();

	TiXmlElement * seriesList = new TiXmlElement("seriesList");
	root->LinkEndChild(seriesList);
	for(seriesIt; seriesIt != seriesLevel.end(); seriesIt++)
	{
		TiXmlElement * series = new TiXmlElement("series");
		series->SetAttribute("seriesInstanceUID", this->utf8_encode(seriesIt->second->GetSeriesInstanceUID()).c_str());
		series->SetAttribute("instanceCount", seriesIt->second->GetInstances().size());
		seriesList->LinkEndChild(series);

		TiXmlElement * seriesAttributes = new TiXmlElement("attributes");
		series->LinkEndChild(seriesAttributes);
		
		AttributeMapType seriesLevelAttributes = seriesIt->second->GetAttributes();
		AttributeMapType::iterator seriesAttrIt = seriesLevelAttributes.begin();
		for(seriesAttrIt; seriesAttrIt != seriesLevelAttributes.end(); seriesAttrIt++)
		{
			if(seriesAttrIt->second != NULL)
				seriesAttributes->LinkEndChild(PrintAttributeXML(seriesAttrIt->second));
		}
		

		AttributeMapType normalizedInstanceAttributes = seriesIt->second->GetNormalizedInstanceAttributes();
		if(!normalizedInstanceAttributes.empty())
		{
			TiXmlElement * normalizedAttributes = new TiXmlElement("normalizedInstanceAttributes");
			series->LinkEndChild(normalizedAttributes);
			
			AttributeMapType::iterator normalizedAttrIt = normalizedInstanceAttributes.begin();
			for(normalizedAttrIt; normalizedAttrIt != normalizedInstanceAttributes.end(); normalizedAttrIt++)
			{
				if(normalizedAttrIt->second != NULL)
					normalizedAttributes->LinkEndChild(PrintAttributeXML(normalizedAttrIt->second));
			}
			
		}

		TiXmlElement * instances = new TiXmlElement("instances");
		series->LinkEndChild(instances);
		InstanceMapType instanceLevelAttributes = seriesIt->second->GetInstances();
		InstanceMapType::iterator instanceIt = instanceLevelAttributes.begin();
		for(instanceIt; instanceIt != instanceLevelAttributes.end(); instanceIt++)
		{
			TiXmlElement * instance = new TiXmlElement("instance");
			instance->SetAttribute("sopInstanceUID", this->utf8_encode(instanceIt->second->GetSOPInstanceUID()).c_str());
			instance->SetAttribute("transferSyntaxUID", this->utf8_encode(instanceIt->second->GetTransferSyntaxUID()).c_str());
			instances->LinkEndChild(instance);

			TiXmlElement * instanceAttr = new TiXmlElement("attributes");
			instance->LinkEndChild(instanceAttr);
			AttributeMapType instanceLevelAttributes = instanceIt->second->GetAttributeMap();
			AttributeMapType::iterator instanceAttrIt = instanceLevelAttributes.begin();
			for(instanceAttrIt; instanceAttrIt != instanceLevelAttributes.end(); ++instanceAttrIt)
			{
				if(instanceAttrIt->second != NULL)
					instanceAttr->LinkEndChild(PrintAttributeXML(instanceAttrIt->second));
			}
		}
	}
	doc.SaveFile("C:/MINTOutput/metadata.xml");
}

//Below is commented out libcurl code that can stream the metadata rather than opening a file

/*
int sendMINTMessage(std::string URL, std::iostream &mintdata)
{
  CURL *curl;
  CURLcode res;

  struct curl_httppost *formpost=NULL;
  struct curl_httppost *lastptr=NULL;
  struct curl_slist *headerlist=NULL;
  static const char buf[] = "Expect:";
 
  curl_global_init(CURL_GLOBAL_ALL);
 


  int filesize= mintdata.tellp()-mintdata.tellg();
  char *databuf = new char[filesize];
  mintdata.read(databuf, filesize);

  curl_formadd(&formpost,
               &lastptr,
               CURLFORM_COPYNAME, "mintdata",
               CURLFORM_PTRCONTENTS, databuf,
               CURLFORM_CONTENTSLENGTH, filesize,
               CURLFORM_END);
 

  curl = curl_easy_init();
  headerlist = curl_slist_append(headerlist, buf);
  if(curl) {
    curl_easy_setopt(curl, CURLOPT_URL, URL.c_str());
    curl_easy_setopt(curl, CURLOPT_HTTPPOST, formpost);
    res = curl_easy_perform(curl);
 
    curl_easy_cleanup(curl);
 
    curl_formfree(formpost);
    curl_slist_free_all (headerlist); 

    delete databuf;
    if (res != CURLE_OK) {
       throw(res);
    }
  }
  return(0);
}
*/

void CommonUtils::SendStudy(std::string URL, const char * filepath, const char * filename, std::vector<std::string> binaryFilePaths)
{
	CURL *curl;
	CURLcode res;

	struct curl_httppost *formpost=NULL;
	struct curl_httppost *lastptr=NULL;
	struct curl_slist *headerlist=NULL;

	curl_global_init(CURL_GLOBAL_ALL);

	/* Fill in the file upload field  */
	curl_formadd(&formpost,
			     &lastptr,
			     CURLFORM_COPYNAME, "sendfile",
			     CURLFORM_FILE, filepath,
			     CURLFORM_END); 

	/* Fill in the filename field */
	curl_formadd(&formpost,
			     &lastptr,
			     CURLFORM_COPYNAME, "filedata",
			     CURLFORM_COPYCONTENTS, filename,
			     CURLFORM_END); 

	std::vector<std::string>::iterator it = binaryFilePaths.begin();
	int i = 0;
	for(it; it != binaryFilePaths.end(); it++)
	{
		curl_formadd(&formpost,
					 &lastptr,
					 CURLFORM_COPYNAME, "sendfile",
					 CURLFORM_FILE, it->data(),
					 CURLFORM_END); 

		std::stringstream fileNameStr;
		fileNameStr << i;
		/* Fill in the filename field */
		curl_formadd(&formpost,
					 &lastptr,
					 CURLFORM_COPYNAME, "filedata",
					 CURLFORM_COPYCONTENTS, fileNameStr.str(),
					 CURLFORM_END); 
		i++;
	}
	
	/* Fill in the submit field too, even if this is rarely needed */
	curl_formadd(&formpost,
			     &lastptr,
			     CURLFORM_COPYNAME, "type",
			     CURLFORM_COPYCONTENTS, "DICOM",
			     CURLFORM_END);
	           

	curl = curl_easy_init();
	/* initalize custom header list (stating that Expect: 100-continue is not
	 wanted */ 
	//headerlist = curl_slist_append(headerlist, buf);
	if(curl) 
	{
		/* what URL that receives this POST */ 
		curl_easy_setopt(curl, CURLOPT_URL, URL.c_str());
		//curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headerlist);
		curl_easy_setopt(curl, CURLOPT_HTTPPOST, formpost);
		res = curl_easy_perform(curl);

		/* always cleanup */  
		curl_easy_cleanup(curl);

		/* then cleanup the formpost chain */ 
		curl_formfree(formpost);
		/* free slist */ 
		curl_slist_free_all (headerlist); 

		//delete databuf;
		if (res != CURLE_OK) {
		   throw(res);
		}
	}
}

static int writer(char *data, size_t size, size_t nmemb, std::string *buffer)  
{   
	int result = 0;  

	if (buffer != NULL)  
	{  
		buffer->append(data, size * nmemb); 
		result = size * nmemb;  
	}  

	return result;  
}  

std::string * CommonUtils::ReadURLToBuffer(std::string URL)
{
	std::string * buffer = new std::string();
	CURL *curl;
	CURLcode res;

	curl = curl_easy_init();
	if(curl)
	{
		curl_easy_setopt(curl, CURLOPT_URL, URL.c_str());
        curl_easy_setopt(curl, CURLOPT_HEADER, 0);  
        curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 1);  
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, writer);  
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, buffer);  
		res = curl_easy_perform(curl);

		curl_easy_cleanup(curl);
	}

	return buffer;
}

Study * CommonUtils::ReadStudyFromURL(std::string URL)
{
	std::string * buffer = ReadURLToBuffer(URL);

	Study * study = new Study();
	study = LoadMetadataFromGPBString(*buffer);
	delete buffer;
	return study;
}

std::vector<std::string> &CommonUtils::split(const std::string &s, char delim, std::vector<std::string> &elems) 
{
    std::stringstream ss(s);
    std::string item;
    while(std::getline(ss, item, delim)) {
        elems.push_back(item);
    }
    return elems;
}


std::vector<std::string> CommonUtils::splitString(const std::string &s, char delim) 
{
    std::vector<std::string> elems;
    return split(s, delim, elems);
}


void CommonUtils::GetDataDictionary(std::map<std::string, std::string> & dataDictionary, 
					   std::set<std::string> & studyLevelTags, std::set<std::string> & seriesLevelTags)
{
	TiXmlDocument doc("C:\\development\\mintcpp\\branches\\development\\config\\DICOM.xml");
	bool openOkay = doc.LoadFile(TIXML_ENCODING_UTF8);
	if(openOkay)
	{
		std::cerr<<"Problem with data dictionary path: "<<"C:\\development\\mintcpp\\branches\\development\\config\\DICOM.xml"<<std::endl;
	}
	
	TiXmlHandle hDoc(&doc);
	TiXmlElement* pElem;
	TiXmlHandle hRoot(0);
	pElem = hDoc.FirstChildElement().FirstChildElement().Element();

	const char * attributes = "attributes";
	const char * studyAttributes = "study-attributes";
	const char * seriesAttributes = "series-attributes";
	//const char * study
	for(pElem; pElem; pElem = pElem->NextSiblingElement())
	{
		std::cout<<"value: "<<pElem->Value()<<std::endl;
		if(strcmp(pElem->Value(), attributes) == 0)
		{
			TiXmlElement* pElemInner = pElem->FirstChildElement();
			for(pElemInner; pElemInner; pElemInner = pElemInner->NextSiblingElement())
			{
				const char * tag = pElemInner->Attribute("tag");
				const char * VR = pElemInner->Attribute("vr");
				std::string tagStr = tag;
				std::string vrStr = VR;
				std::vector<std::string> strVec = splitString(vrStr, '|');
				if(strVec.size() > 1)
				{
					vrStr = strVec.at(0);
				}
				dataDictionary.insert(std::make_pair(tagStr, vrStr));
			}
		}
		else if(strcmp(pElem->Value(), studyAttributes) == 0)
		{
			TiXmlElement* pElemInner = pElem->FirstChildElement();
			for(pElemInner; pElemInner; pElemInner = pElemInner->NextSiblingElement())
			{
				const char * tag = pElemInner->Attribute("tag");
				std::string tagStr = tag;
				studyLevelTags.insert(tag);
			}
		}
		else if(strcmp(pElem->Value(), seriesAttributes) == 0)
		{
			TiXmlElement* pElemInner = pElem->FirstChildElement();
			for(pElemInner; pElemInner; pElemInner = pElemInner->NextSiblingElement())
			{
				const char * tag = pElemInner->Attribute("tag");
				std::string tagStr = tag;
				seriesLevelTags.insert(tag);
			}
		}
	}
}

// Convert a wide Unicode string to an UTF8 string
std::string CommonUtils::utf8_encode(const std::wstring &wstr)
{
    int size_needed = WideCharToMultiByte(CP_UTF8, 0, &wstr[0], (int)wstr.size(), NULL, 0, NULL, NULL);
    std::string strTo( size_needed, 0 );
    WideCharToMultiByte                  (CP_UTF8, 0, &wstr[0], (int)wstr.size(), &strTo[0], size_needed, NULL, NULL);
    return strTo;
}

// Convert an UTF8 string to a wide Unicode String
std::wstring CommonUtils::utf8_decode(const std::string &str)
{
    int size_needed = MultiByteToWideChar(CP_UTF8, 0, &str[0], (int)str.size(), NULL, 0);
    std::wstring wstrTo( size_needed, 0 );
    MultiByteToWideChar                  (CP_UTF8, 0, &str[0], (int)str.size(), &wstrTo[0], size_needed);
    return wstrTo;
}
