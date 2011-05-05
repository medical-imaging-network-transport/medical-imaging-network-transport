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

//Attribute.h file
//Author: Gorkem Sevinc

#include "../MINTCommon/libraryForwardDeclarations.h"
#include "Item.h"

class Attribute
{
	public:
		/// The default constructor for the Attribute class
		Attribute();

		/** \brief Overloaded constructor for the Attribute class
		* \param tag The tag of the attribute
		* \param vr The VR of the attribute
		*/
		Attribute(const std::string & tag, const std::wstring & vr);

		/** \brief Overloaded constructor for the Attribute class
		* \param tag The tag of the attribute
		* \param vr The VR of the attribute
		* \param val The val of the attribute
		*/
		Attribute(const std::string & tag, const std::wstring & vr, const std::wstring & val);


		/** \brief Function to set the tag of the attribute
		* \param tag The tag of the attribute
		*/
		void SetTag(const std::string & tag);

		/** \brief Function to get the tag of the attribute
		* \return tag The tag of the attribute
		*/
		const std::string & GetTag(void);
		

		/** \brief Function to set the bid of the attribute
		* \param bid The bid of the attribute
		*/
		void SetBid(const int & bid);

		/** \brief Function to get the bid of the attribute
		* \return bid The bid of the attribute
		*/
		const int & GetBid(void);


		/** \brief Function to set the binary item size of the attribute
		* \param bSize The binary item size of the attribute
		*/
		void SetBSize(const int & bSize);

		/** \brief Function to get the binary item size of the attribute
		* \param bSize The binary item size of the attribute
		*/
		const int & GetBSize(void);
		

		/** \brief Function to set the VR of the attribute
		* \param vr The VR of the attribute
		*/
		void SetVR(const std::wstring & vr);

		/** \brief Function to get the VR of the attribute
		* \return vr The VR of the attribute
		*/
		const std::wstring & GetVR(void);
		

		/** \brief Function to set the value of the attribute
		* \param val The value of the attribute
		*/
		void SetVal(const std::wstring & val);

		/** \brief Function to get the value of the attribute
		* \return val The value of the attribute
		*/
		const std::wstring & GetVal(void);
		

		/** \brief Function to set the inline bytes of the attribute
		* \param bytes The inline bytes of the attribute
		*/
		void SetBytes(const char * bytes);

		/** \brief Function to get the inline bytes of the attribute
		* \return bytes The inline bytes of the attribute 
		*/
		const char * GetBytes(void);


		/** \brief Function to get the items of the attribute (used in sequences)
		* \return items The items of the attribute
		*/
		std::vector<Item> & GetItems(void);
		
		/** \brief Function to add an item to the item list
		* \param item The item to be added
		*/
		void AddItem(Item item);
		
		/** \brief Function to remove an item from the item list
		* \param item The item to be removed
		*/
		void RemoveItem(Item item);

		/** \brief Function to check if the attribute has sequence items
		* \return hasSequenceItems Boolean indicating whether the attribute has sequence items
		*/
		bool HasSequenceItems();


		int GetInlineByteSize(void);

		void SetInlineByteSize(int inlineByteSize);

	private:
		/// The tag string
		std::string Tag;
		/// The binary item id integer
		unsigned int Bid;
		/// The binary item size integer
		unsigned int BSize;
		
		/// The VR string
		std::wstring VR;
		/// The Value string
		std::wstring Val;

		/// The inline byte value pointer
		const char * Bytes;
		/// The sequence items vector
		std::vector<Item> Items;

		unsigned int InlineByteSize;

};