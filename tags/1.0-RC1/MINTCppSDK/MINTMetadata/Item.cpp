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

//Item.cpp file
//Author: Gorkem Sevinc

#include "Item.h"
#include "Attribute.h"

Item::Item()
{
}

Item::Item(AttributeMapType attributeMap)
{
	SetMap(attributeMap);
}

void Item::SetMap(AttributeMapType attributeMap)
{
	this->AttributeMap = attributeMap;
}

Attribute * Item::GetAttribute(const std::string & tag)
{
	return this->AttributeMap.find(tag)->second;
}

AttributeMapType Item::GetAttributes()
{
	return this->AttributeMap;
}

void Item::PutAttribute(Attribute * attr)
{
	this->AttributeMap.insert(std::make_pair(attr->GetTag(), attr));
}

void Item::RemoveAttribute(const std::string & tag)
{
	this->AttributeMap.erase(tag);
}
