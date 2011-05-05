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

//Instance.h file
//Author: Gorkem Sevinc

#ifndef __INSTANCE_H
#define __INSTANCE_H

#include "../MINTCommon/libraryForwardDeclarations.h"
#include "Attribute.h"

/// The definition of the attribute map type, a map of tags to attribute pointers
typedef std::map<std::string, Attribute *> AttributeMapType;

class Instance
{
	public:
		/// The default constructor for Instance class
		Instance();

		/** \brief Overloaded constructor for Instance class
		* \param transferSyntaxUID The Transfer Syntax UID to be used for this instance
		*/
		Instance(const std::wstring & transferSyntaxUID);

		/** \brief Overloaded constructor for Instance class
		* \param transferSyntaxUID The Transfer Syntax UID to be used for this instance
		* \param attributeMap The map of tags to attributes to be used 
		*/
		Instance(const std::wstring & transferSyntaxUID, AttributeMapType attributeMap);


		/** \brief Function to set the Transfer Syntax UID of the instance
		*  \param transferSyntaxUID The Transfer Syntax UID to be used
		*/
		void SetTransferSyntaxUID(const std::wstring & transferSyntaxUID);

		/** \brief Function to get the Transfer Syntax UID of the instance
		* \return transferSyntaxUID The Transfer Syntax UID being used
		*/
		const std::wstring & GetTransferSyntaxUID(void);

		/** \brief Function to set the SOP Instance UID of the instance
		* \param sopInstanceUID The SOP Instance UID to be used
		*/
		void SetSOPInstanceUID(const std::wstring & sopInstanceUID);

		/** \brief Function to get the SOP Instance UID
		* \return sopInstanceUID The SOP Instance UID being used
		*/
		const std::wstring & GetSOPInstanceUID(void);

		/** \brief Function to set the attribute map
		* \param attributeMap The map of tags to attributes to be used
		*/
		void SetAttributeMap(AttributeMapType attributeMap);

		/** \brief Function to get the attribute map
		* \return attributeMap The map of tags to attributes being used
		*/
		AttributeMapType GetAttributeMap(void);

		/** \brief Function to insert an attribute to the map
		* \param attr The attribute to be inserted into the map
		*/
		void PutAttribute(Attribute * attr);

		/** \brief Function to remove an attribute from the map
		* \param tag The tag of the attribute to be removed
		*/
		void RemoveAttribute(const std::string & tag);

		/** \brief Function to get the attribute corresponding to the tag
		* \param tag The tag of the attribute to get
		* \return attr The attribute corresponding to the tag
		*/
		Attribute * GetAttribute(const std::string & tag);

		/** \brief Function to get the value of the attribute corresponding to the tag
		* \param tag The tag of the attribute to get the value of
		* \return value The value of the attribute
		*/
		const std::wstring & GetValueForAttribute(const std::string & tag);

	private:
		std::wstring TransferSyntaxUID;
		std::wstring SOPInstanceUID;
		AttributeMapType AttributeMap;
};

#endif