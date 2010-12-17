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

//Study.h file
//Author: Gorkem Sevinc

#ifndef __STUDY_H
#define __STUDY_H

#include "../MINTCommon/libraryForwardDeclarations.h"
#include "Series.h"

/// The definition of the Attribute map type
typedef std::map<std::string, Attribute *> AttributeMapType;
/// The definition of the Series map type
typedef std::map<std::wstring, Series *> SeriesMapType;

class Study
{
	public:
		/// The default constructor for the Study class 
		Study();
		/** \brief Overloaded constructor for the Study class
		* \param studyInstanceUID Study Instance UID
		*/
		Study(const std::wstring & studyInstanceUID);
		
		/** \brief Overloaded constructor for the Study class
		* \param studyInstanceUID Study Instance UID
		* \param attributes The map of attributes
		*/
		Study(const std::wstring & studyInstanceUID, AttributeMapType attributes);
		
		/** \brief Overloaded constructor for the Study class
		* \param studyInstanceUID Study Instance UID
		* \param attributes The map of attributes
		* \param series The map of series
		*/
		Study(const std::wstring & studyInstanceUID, AttributeMapType attributes, SeriesMapType series);


		/** \brief Function to set the study instance UID
		* \param studyInstanceUID Study Instance UID
		*/
		void SetStudyInstanceUID(const std::wstring & studyInstanceUID);

		/** \brief Function to get the study instance UID
		* \return studyInstanceUID Study Instance UID
		*/
		const std::wstring & GetStudyInstanceUID(void);

		/** \brief Function to set the version of the study
		* \param version The version of the study
		*/
		void SetVersion(const std::wstring & version);

		/** \brief Function to get the version of the study
		* \return version The version of the study
		*/
		const std::wstring & GetVersion(void);

		/** \brief Function to set the attributes to a given map
		* \param attributes The map of attributes to set to
		*/
		void SetAttributes(AttributeMapType attributes);

		/** \brief Function to get the attributes map
		* \return attributes The map of attributes
		*/
		AttributeMapType GetAttributes(void);

		/** \brief Function to set the series map
		* \param series The map of series to set to
		*/
		void SetSeries(SeriesMapType series);
		
		/** \brief Function to get the series map
		* \param series The map of series
		*/
		SeriesMapType GetSeries(void);


		/** \brief Function to get an attribute given a tag
		* \param tag The tag of the desired Attribute
		* \return Attribute The desired Attribute
		*/
		Attribute * GetAttribute(const std::string & tag);

		/** \brief Function to get the value of an attribute given a tag
		* \param tag The tag of the desired Attribute
		* \return value The desired Attribute's value
		*/
		const std::wstring & GetValueForAttribute(const std::string & tag);

		/** \brief Function to insert an attribute into the attribute map
		* \param attr The attribute to insert
		*/
		void PutAttribute(Attribute * attr);

		/** \brief Function to remove an attribute from the map given a tag
		* \param tag The tag of the attribute to remove from the map
		*/
		void RemoveAttribute(const std::string & tag);

		/** \brief Function to get a series given a series instance UID
		* \param uid Series Instance UID
		* \return Series The series that corresponds to the given series instance UID
		*/
		Series * GetSeriesFromUID(const std::wstring & uid);

		/** \brief Function to insert a series into the series map
		* \param series The series to insert
		*/
		void PutSeries(Series * series);

		/** \brief Function to remove a series from the map given a UID
		* \param uid Series Instance UID
		*/
		void RemoveSeries(const std::wstring & uid);

	private:
		/// Study Instance UID
		std::wstring StudyInstanceUID;
		/// Version of the study
		std::wstring Version;
		/// Attribute map
		AttributeMapType Attributes;
		/// Series map
		SeriesMapType SeriesMap;

};

#endif //STUDY_H