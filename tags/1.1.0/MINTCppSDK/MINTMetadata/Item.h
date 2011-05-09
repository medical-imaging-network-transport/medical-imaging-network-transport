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

/// Item.h file
//Author: Gorkem Sevinc

#ifndef __ITEM_H
#define __ITEM_H

#include "../MINTCommon/libraryForwardDeclarations.h"

class Attribute;

/// The definition of the attribute map type, a map of tags to attribute pointers
typedef std::map<std::string, Attribute *> AttributeMapType;

class Item
{
	public:
		/// The default constructor for Item class
		Item();

		/** \brief Overloaded constructor for Item class
		* \param attributeMap The map of tags to attributes to be used
		*/
		Item(AttributeMapType attributeMap);

		/** \brief Function to set the attribute map
		* \param attributeMap The map of tags to attributes to be used
		*/
		void SetMap(AttributeMapType attributeMap);

		/** \brief Function to get the attribute corresponding to the tag
		* \param tag The tag of the attribute to get
		* \return attr The attribute corresponding to the tag
		*/ 
		Attribute * GetAttribute(const std::string & tag);

		/** \brief Function to get the attribute map
		* \return attributeMap The map of tags to attributes being used
		*/
		AttributeMapType GetAttributes();

		/** \brief Function to insert an attribute to the map
		* \param attr The attribute to be inserted into the map
		*/
		void PutAttribute(Attribute * attr);

		/** \brief Function to remove an attribute from the map
		* \param tag The tag of the attribute to be removed
		*/
		void RemoveAttribute(const std::string & tag);
	private:
		/// The attribute map being used.
		AttributeMapType AttributeMap;
};

#endif // ITEM_H