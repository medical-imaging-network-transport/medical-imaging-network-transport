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

//Series.cpp file
//Author: Gorkem Sevinc

#include "Series.h"

Series::Series()
{
}

Series::Series(const std::wstring & seriesInstanceUID)
{
	this->SetSeriesInstanceUID(seriesInstanceUID);
}

Series::Series(const std::wstring & seriesInstanceUID, AttributeMapType attributes)
{
	this->SetSeriesInstanceUID(seriesInstanceUID);
	this->SetAttributes(attributes);
}

Series::Series(const std::wstring & seriesInstanceUID, AttributeMapType attributes, InstanceMapType instances)
{
	this->SetSeriesInstanceUID(seriesInstanceUID);
	this->SetAttributes(attributes);
	this->SetInstances(instances);
}

void Series::SetSeriesInstanceUID(const std::wstring & seriesInstanceUID)
{
	this->SeriesInstanceUID = seriesInstanceUID;
}

const std::wstring & Series::GetSeriesInstanceUID(void)
{
	return this->SeriesInstanceUID;
}

void Series::SetAttributes(AttributeMapType attributes)
{
	this->Attributes.clear();
	this->Attributes = attributes;
	attributes.clear();
}

AttributeMapType Series::GetAttributes(void)
{
	return this->Attributes;
}

void Series::SetNormalizedInstanceAttributes(AttributeMapType normalizedAttributes)
{
	this->NormalizedInstanceAttributes.clear();
	this->NormalizedInstanceAttributes = normalizedAttributes;
	normalizedAttributes.clear();
}

AttributeMapType Series::GetNormalizedInstanceAttributes(void)
{
	return this->NormalizedInstanceAttributes;
}

void Series::SetInstances(InstanceMapType instances)
{
	this->Instances.clear();
	this->Instances = instances;
	instances.clear();
}

InstanceMapType Series::GetInstances(void)
{
	return this->Instances;
}

void Series::PutAttribute(Attribute * attr)
{
	this->Attributes.insert(std::make_pair(attr->GetTag(), attr));
}

void Series::RemoveAttribute(const std::string & tag)
{
	this->Attributes.erase(tag);
}

Attribute * Series::GetAttribute(const std::string & tag)
{
	return this->Attributes.find(tag)->second;
}

void Series::PutNormalizedAttribute(Attribute * attr)
{
	this->NormalizedInstanceAttributes.insert(std::make_pair(attr->GetTag(), attr));
}

void Series::RemoveNormalizedAttribute(const std::string & tag)
{
	this->NormalizedInstanceAttributes.erase(tag);
}

Attribute * Series::GetNormalizedAttribute(const std::string & tag)
{
	return this->NormalizedInstanceAttributes.find(tag)->second;
}

void Series::PutInstance(Instance * inst)
{
	this->Instances.insert(std::make_pair(inst->GetSOPInstanceUID(), inst));
}

void Series::RemoveInstance(const std::wstring & sopInstanceUID)
{
	this->Instances.erase(sopInstanceUID);
}

Instance * Series::GetInstance(const std::wstring & sopInstanceUID)
{
	return this->Instances.find(sopInstanceUID)->second;
}
