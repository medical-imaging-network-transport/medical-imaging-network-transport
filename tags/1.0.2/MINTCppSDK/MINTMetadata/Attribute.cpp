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

//Attribute.cpp file
//Author: Gorkem Sevinc

#include "Attribute.h"
#include <iostream>

Attribute::Attribute()
{
	//Tag = "";
	Bid = -1;
	BSize = -1;
	InlineByteSize = -1;
	
	Items.empty();
}

Attribute::Attribute(const std::string & tag, const std::wstring & vr)
{
	this->SetTag(tag);
	this->SetVR(VR);
	Bid = -1;
	BSize = -1;
	InlineByteSize = -1;
}

Attribute::Attribute(const std::string & tag, const std::wstring & vr, const std::wstring & val)
{
	this->SetTag(tag);
	this->SetVR(VR);
	this->SetVal(val);
	Bid = -1;
	BSize = -1;
	InlineByteSize = -1;
}

void Attribute::SetTag(const std::string & tag)
{
	this->Tag = tag;
}

const std::string & Attribute::GetTag(void)
{
	return this->Tag;
}

void Attribute::SetBid(const int & bid)
{
	this->Bid = bid;
}

const int & Attribute::GetBid(void)
{
	return this->Bid;
}

void Attribute::SetBSize(const int & bSize)
{
	this->BSize = bSize;
}

const int & Attribute::GetBSize(void)
{
	return this->BSize;
}

void Attribute::SetVR(const std::wstring & vr)
{
	this->VR = vr;
}

const std::wstring & Attribute::GetVR(void)
{
	return this->VR;
}

void Attribute::SetVal(const std::wstring & val)
{
	this->Val = val;
}

const std::wstring & Attribute::GetVal(void)
{
	return this->Val;
}

void Attribute::SetBytes(const char * bytes)
{
	this->Bytes = bytes;
}

const char * Attribute::GetBytes(void)
{
	return this->Bytes;
}

std::vector<Item> & Attribute::GetItems(void)
{
	return Items;
}

void Attribute::AddItem(Item item)
{
	Items.push_back(item);
}

void Attribute::RemoveItem(Item item)
{
	std::vector<Item>::iterator it = Items.begin();
	for(it; it != Items.end(); it++)
	{
		if(it->GetAttributes() == item.GetAttributes()) // Hacky solution by comparing attributes of the item
		{
			it = Items.erase(it);
			break;
		}
	}
}

bool Attribute::HasSequenceItems()
{
	return !Items.empty();
}

int Attribute::GetInlineByteSize(void)
{
	return InlineByteSize;
}

void Attribute::SetInlineByteSize(int inlineByteSize)
{
	InlineByteSize = inlineByteSize;
}

