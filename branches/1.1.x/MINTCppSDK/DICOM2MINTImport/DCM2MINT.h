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

//DCM2MINT.h file
//Author: Gorkem Sevinc

#ifndef __DCM2MINT_H
#define __DCM2MINT_H

#include "../MINTCommon/MetaBinaryPair.h"
#include "../MINTCommon/gdcmForwardDeclarations.h"
#include "../MINTMetadata/Series.h"
#include "../MINTMetadata/Instance.h"
#include "DCM2MetaBuilder.h"
#include "../MINTCommon/libraryForwardDeclarations.h"

class DCM2MINT
{
	public:
		/** \brief Default constructor for the DCM2MINT class
		*/
		DCM2MINT();
		/** \brief Function to convert a DICOM part 10 file from a file
		* \param Path to the file
		*/
		void ConvertDICOMP10FromFile(const char * path);

		/** \brief Function to convert a DICOM part 10 file from a stream
		* \param The stream to the DICOM part 10 file
		*/

		void ConvertDICOMP10FromStream(std::iostream & stream);

		/** \brief Function to output the metadata
		*/
		std::vector<std::string> OutputMetadata();

	private:
		DCM2MetaBuilder * builder;
		gdcm::Reader * reader;
		std::set<std::string> studyLevelTags;
		std::set<std::string> seriesLevelTags;
		
};

#endif
