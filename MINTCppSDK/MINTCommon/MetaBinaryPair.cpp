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

#include "MetaBinaryPair.h"


MetaBinaryPair::MetaBinaryPair()
{
}

void MetaBinaryPair::SetMetadata(Study * metadata)
{
	this->Metadata = metadata;
}

Study * MetaBinaryPair::GetMetadata(void)
{
	return this->Metadata;
}

void MetaBinaryPair::SetBinarydata(BinaryData binaryData)
{
	this->BinData = binaryData;
}

BinaryData MetaBinaryPair::GetBinaryData(void)
{
	return this->BinData;
}
