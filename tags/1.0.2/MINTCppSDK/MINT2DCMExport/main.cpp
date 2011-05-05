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

//main.cpp file for MINT2DCM conversion file
//Author: Gorkem Sevinc

#include <iostream>
#include <cstdlib>
#include <fstream>
#include <string>
#include "MINT2DCMBuilder.h"

void main(int argc, char *argv[])
{
	MINT2DCMBuilder builder("http://127.0.0.1:8080/MINTServer/studies/a71dd28d-b23b-401c-8edf-d8a206993d43/DICOM/");
	//MINT2DCMBuilder builder("http://10.173.13.52/MINTServer/studies/43eb6591-1d18-4bba-9719-4b47407b485a/DICOM/");
}