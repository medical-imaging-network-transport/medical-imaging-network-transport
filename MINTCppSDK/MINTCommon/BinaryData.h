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

//BinaryData.h file
//Author: Gorkem Sevinc

#ifndef __BINARYDATA_H
#define __BINARYDATA_H

#include "../MINTCommon/libraryForwardDeclarations.h"

class BinaryData
{
	public:
		/** \brief Default constructor for the BinaryData class
		*/
		BinaryData(); 

		/** \brief Overloaded constructor for the BianryData class
		* \param binaryItem The const char pointer to the binary item
		*/
		BinaryData(const char * binaryItem);

		/** \brief Function to get the binary item
		* \return binaryItem The const char pointer to the binary item
		*/
		const char * GetBinaryItem();
		
		/** \brief Function to set the binary item id of the binary item
		* \param bid The binary item id
		*/
		void SetBid(int bid);

		/** \brief Function to get the binary item id of the binary item
		* \return bid The binary item id
		*/
		int GetBid(); 

		/** \brief Function to set the size of the binary item
		* \param bsize The binary item size
		*/
		void SetSize(int bsize);

		/** \brief Function to get the size of the binary item
		* \return bsize The binary item size
		*/
		int GetSize();
	private:
		const char * BinaryItem;
		int Bid;
		int BSize;

};

#endif