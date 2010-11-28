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

//BinaryData.cpp file
//Author: Gorkem Sevinc

#include "BinaryData.h"

BinaryData::BinaryData()
{}

BinaryData::BinaryData(const char * binaryItem)
{
	this->BinaryItem = binaryItem;
}

const char * BinaryData::GetBinaryItem()
{
	return this->BinaryItem;
}

void BinaryData::SetBid(int bid)
{
	this->Bid = bid;
}

int BinaryData::GetBid()
{
	return this->Bid;
}

void BinaryData::SetSize(int bsize)
{
	this->BSize = bsize;
}

int BinaryData::GetSize()
{
	return this->BSize;
}
