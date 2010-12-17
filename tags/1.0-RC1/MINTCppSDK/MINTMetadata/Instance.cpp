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

//Instance.cpp file
//Author: Gorkem Sevinc

#include "Instance.h"

Instance::Instance()
{
}

Instance::Instance(const std::wstring & transferSyntaxUID)
{
	this->SetTransferSyntaxUID(transferSyntaxUID);
}

Instance::Instance(const std::wstring & transferSyntaxUID, AttributeMapType attributeMap)
{
	this->SetTransferSyntaxUID(transferSyntaxUID);
	this->SetAttributeMap(attributeMap);
}


void Instance::SetTransferSyntaxUID(const std::wstring & transferSyntaxUID)
{
	this->TransferSyntaxUID = transferSyntaxUID;
}

const std::wstring & Instance::GetTransferSyntaxUID(void)
{
	return this->TransferSyntaxUID;
}

void Instance::SetSOPInstanceUID(const std::wstring & sopInstanceUID)
{
	this->SOPInstanceUID = sopInstanceUID;
}

const std::wstring & Instance::GetSOPInstanceUID(void)
{
	return this->SOPInstanceUID;
}

void Instance::SetAttributeMap(AttributeMapType attributeMap)
{
	this->AttributeMap.clear();
	this->AttributeMap = attributeMap;
	attributeMap.clear();
}

AttributeMapType Instance::GetAttributeMap(void)
{
	return this->AttributeMap;
}

void Instance::PutAttribute(Attribute * attr)
{
	this->AttributeMap.insert(std::make_pair(attr->GetTag(), attr));
}

void Instance::RemoveAttribute(const std::string & tag)
{
	this->AttributeMap.erase(tag);
}

Attribute * Instance::GetAttribute(const std::string & tag)
{
	return this->AttributeMap.find(tag)->second;
}

const std::wstring & Instance::GetValueForAttribute(const std::string & tag)
{
	Attribute * attr = GetAttribute(tag);
	return attr != NULL ? attr->GetVal() : NULL;
}

