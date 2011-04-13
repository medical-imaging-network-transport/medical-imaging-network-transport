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

//MetaBinaryPair.h file
//Author: Gorkem Sevinc

#ifndef __METABINARYPAIR_H
#define __METABINARYPAIR_H

#include "../MINTMetadata/Study.h"
#include "gdcmForwardDeclarations.h"
#include "BinaryData.h"

class MetaBinaryPair
{
	public:
		/** \brief Default constructor for the MetaBinaryPair class
		*/
		MetaBinaryPair();

		/** \brief Function to set the metadata of the metabinarypair
		* \param metadata The metadata (study)
		*/
		void SetMetadata(Study * metadata);

		/** \brief Function to get the metadata of the metabinarypair
		* \return metadata The metadata (study)
		*/
		Study * GetMetadata(void);

		/** \brief Function to set the binary data of the metabinarypair
		* \param binaryData The binary data
		*/
		void SetBinarydata(BinaryData binaryData);

		/** \brief Function to get the binary data of the metabinarypair
		* \return binaryData The binary data
		*/
		BinaryData GetBinaryData(void);
		
	private:
		BinaryData BinData;
		Study * Metadata;
};

#endif