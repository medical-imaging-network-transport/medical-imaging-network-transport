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

//Series.h file
//Author: Gorkem Sevinc

#ifndef __SERIES_H
#define __SERIES_H

#include "../MINTCommon/libraryForwardDeclarations.h"
#include "Instance.h"

/// The definition of the Attribute map type
typedef std::map<std::string, Attribute *> AttributeMapType;
/// The definition of the Instance map type
typedef std::map<std::wstring, Instance *> InstanceMapType;

class Series
{
	public:
		/// The default constructor for the Series class
		Series();
		
		/** \brief Overloaded constructor for the Series class
		* \param seriesInstanceUID The Series Instance UID
		*/
		Series(const std::wstring & seriesInstanceUID);

		/** \brief Overloaded constructor for the Series class
		* \param seriesInstanceUID The Series Instance UID
		* \param attribute The attributes map
		*/
		Series(const std::wstring & seriesInstanceUID, AttributeMapType attributes);
		
		/** \brief Overloaded constructor for the Series class
		* \param seriesInstanceUID The Series Instance UID
		* \param attribute The attributes map
		* \param instances The instances map
		*/
		Series(const std::wstring & seriesInstanceUID, AttributeMapType attributes, InstanceMapType instances);


		/** \brief Function to set the series instance UID
		* \param seriesInstanceUID Series Instance UID
		*/
		void SetSeriesInstanceUID(const std::wstring & seriesInstanceUID);

		/** \brief Function to get the series instance UID
		* \return seriesInstanceUID Series Instance UID
		*/
		const std::wstring & GetSeriesInstanceUID(void);

		/** \brief Function to set the attributes
		* \param attributes The attributes map
		*/
		void SetAttributes(AttributeMapType attributes);

		/** \brief Function to get the attributes
		* \return attributes The attributes map
		*/
		AttributeMapType GetAttributes(void);

		/** \brief Function to set the normalized instance attributes
		* \param normalizedAttributes The normalized attributes map
		*/
		void SetNormalizedInstanceAttributes(AttributeMapType normalizedAttributes);

		/** \brief Function to get the normalized instance attributes
		* \return normalizedAttributes The normalized attributes map
		*/
		AttributeMapType GetNormalizedInstanceAttributes(void);
		
		/** \brief Function to set the instances map
		* \param instances The instances map
		*/
		void SetInstances(InstanceMapType instances);
		
		/** \brief Function to get the instances map
		* \return instances The instances map
		*/
		InstanceMapType GetInstances(void);


		/** \brief Function to insert an attribute to the attribute map
		* \param attr The attribute to insert
		*/
		void PutAttribute(Attribute * attr);
		
		/** \brief Function to remove an attribute from the attribute map
		* \param tag The tag of the attribute to remove
		*/
		void RemoveAttribute(const std::string & tag);
		
		/** \brief Function to get an attribute given a tag
		* \param tag The tag of the attribute to get
		* \return attribute The attribute
		*/
		Attribute * GetAttribute(const std::string & tag);


		/** \brief Function to insert a normalized attribute
		* \param attr The attribute to insert
		*/
		void PutNormalizedAttribute(Attribute * attr);

		/** \brief Function to remove an attribute from the normalized attribute map
		* \param tag The tag of the attribute to remove
		*/
		void RemoveNormalizedAttribute(const std::string & tag);

		/** \brief Function to get a normalized attribute given a tag
		* \param tag The tag of the normalized attribute to get
		* \return attribute The normalized attribute
		*/
		Attribute * GetNormalizedAttribute(const std::string & tag);


		/** \brief Function to insert an instance into the instances map
		* \param inst The instance to insert
		*/
		void PutInstance(Instance * inst);

		/** \brief Function to remove an instance given a sopInstanceUID
		* \param sopInstanceUID SOP Instance UID
		*/
		void RemoveInstance(const std::wstring & sopInstanceUID);

		/** \brief Function to get an instance given a sopInstanceUID
		* \param sopInstanceUID SOP Instance UID
		* \return Instance The instance that corresponds to the give sopInstanceUID
		*/
		Instance * GetInstance(const std::wstring & sopInstanceUID);

	private:
		/// Series Instance UID
		std::wstring SeriesInstanceUID;
		/// The map of Attributes
		AttributeMapType Attributes;
		/// The map of Normalized Instance Attributes
		AttributeMapType NormalizedInstanceAttributes;
		/// The map of instances
		InstanceMapType Instances;

};

#endif //SERIES_H