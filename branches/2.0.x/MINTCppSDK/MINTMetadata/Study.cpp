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

//Study.cpp file
//Author: Gorkem Sevinc

#include "Study.h"

Study::Study()
{
}

Study::Study(const std::wstring & studyInstanceUID)
{
	this->SetStudyInstanceUID(studyInstanceUID);
}

Study::Study(const std::wstring & studyInstanceUID, AttributeMapType attributes)
{
	this->SetStudyInstanceUID(studyInstanceUID);
	this->SetAttributes(attributes);
}

Study::Study(const std::wstring & studyInstanceUID, AttributeMapType attributes, SeriesMapType series)
{
	this->SetStudyInstanceUID(studyInstanceUID);
	this->SetAttributes(attributes);
	this->SetSeries(series);
}

void Study::SetStudyInstanceUID(const std::wstring & studyInstanceUID)
{
	this->StudyInstanceUID = studyInstanceUID;
}

const std::wstring & Study::GetStudyInstanceUID(void)
{
	return this->StudyInstanceUID;
}

void Study::SetVersion(const std::wstring & version)
{
	this->Version = version;
}

const std::wstring & Study::GetVersion(void)
{
	return this->Version;
}

void Study::SetAttributes(AttributeMapType attributes)
{
	this->Attributes.clear();
	this->Attributes = attributes;
	attributes.clear();
}

AttributeMapType Study::GetAttributes(void)
{
	return this->Attributes;
}

void Study::SetSeries(SeriesMapType series)
{
	this->SeriesMap.clear();
	this->SeriesMap = series;
	series.clear();
}

SeriesMapType Study::GetSeries(void)
{
	return this->SeriesMap;
}

Attribute * Study::GetAttribute(const std::string & tag)
{
	return this->Attributes.find(tag)->second;
}

const std::wstring & Study::GetValueForAttribute(const std::string & tag)
{
	Attribute * attr = GetAttribute(tag);
	return attr != NULL ? attr->GetVal() : NULL;
}

void Study::PutAttribute(Attribute * attr)
{
	this->Attributes.insert(std::make_pair(attr->GetTag(), attr));
}

void Study::RemoveAttribute(const std::string & tag)
{
	this->Attributes.erase(tag);
}

Series * Study::GetSeriesFromUID(const std::wstring & uid)
{
	SeriesMapType::iterator seriesIt = this->SeriesMap.find(uid);
	if(seriesIt == this->SeriesMap.end())
	{
		return NULL;
	}
	else
	{
		return this->SeriesMap.find(uid)->second;
	}
}


void Study::PutSeries(Series * series)
{
	this->SeriesMap.insert(std::make_pair(series->GetSeriesInstanceUID(), series));
}

void Study::RemoveSeries(const std::wstring & uid)
{
	this->SeriesMap.erase(uid);
}
