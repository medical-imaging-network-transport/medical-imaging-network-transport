cmake_minimum_required(VERSION 2.8)
project(MINT)

SET(GDCM_DIR C:\\GDCM2.1)
SET(PROTO_DIR C:\\development\\googleprotobuf)
SET(CURL_DIR C:\\development\\curl-7.21.2)

include_directories(${CMAKE_CURRENT_BINARY_DIR})
include_directories(${GDCM_DIR}/include/gdcm-2.1)
include_directories(${PROTO_DIR}/include)
include_directories(${CURL_DIR}/include/curl)

link_directories(${GDCM_DIR}/lib)
link_directories(${PROTO_DIR}/libraries)
link_directories(${CURL_DIR}/build/lib)


add_library(
    MINTMetadata
    MINTMetadata/Attribute.h
    MINTMetadata/Attribute.cpp
    MINTMetadata/Instance.h
    MINTMetadata/Instance.cpp
    MINTMetadata/Item.h
    MINTMetadata/Item.cpp
    MINTMetadata/Series.h
    MINTMetadata/Series.cpp
    MINTMetadata/Study.h
    MINTMetadata/Study.cpp
    MINTMetadata/config/mint-metadata.pb.h
    MINTMetadata/config/mint-metadata.pb.cc
    )
	
add_library(
	MINTCommon
	MINTCommon/BinaryData.h
	MINTCommon/BinaryData.cpp
	MINTCommon/MetaBinaryPair.h
	MINTCommon/MetaBinaryPair.cpp
	MINTCommon/CommonUtils.h
	MINTCommon/CommonUtils.cpp
	MINTCommon/gdcmForwardDeclarations.h
	MINTCommon/libraryForwardDeclarations.h
	)
	
add_library(
	TinyXML
	TinyXML/tinystr.h
	TinyXML/tinystr.cpp
	TinyXML/tinyxml.h
	TinyXML/tinyxml.cpp
	TinyXML/tinyxmlerror.cpp
	TinyXML/tinyxmlparser.cpp	
	)
	
add_executable(
	DCM2MINTImport
	DICOM2MINTImport/main.cpp
	DICOM2MINTImport/DCM2MetaBuilder.h
	DICOM2MINTImport/DCM2MetaBuilder.cpp
	DICOM2MINTImport/DCM2MINT.h
	DICOM2MINTImport/DCM2MINT.cpp
	DICOM2MINTImport/tarlib.h
	DICOM2MINTImport/tarlib.cpp
	)
	
add_executable(
	MINT2DCMExport
	MINT2DCMExport/main.cpp
	MINT2DCMExport/MINT2DCMBuilder.h
	MINT2DCMExport/MINT2DCMBuilder.cpp
	)

add_definitions(-DCURL_STATICLIB)	

target_link_libraries(DCM2MINTImport libcurl ws2_32 TinyXML MINTMetadata MINTCommon gdcmcharls gdcmCommon gdcmDICT gdcmDSED gdcmexpat gdcmgetopt gdcmIOD gdcmjpeg8 gdcmjpeg12 gdcmjpeg16 gdcmMSFF gdcmopenjpeg gdcmzlib libprotobuf libprotobuf-lite libprotoc)
target_link_libraries(MINT2DCMExport libcurl ws2_32 TinyXML MINTMetadata MINTCommon gdcmcharls gdcmCommon gdcmDICT gdcmDSED gdcmexpat gdcmgetopt gdcmIOD gdcmjpeg8 gdcmjpeg12 gdcmjpeg16 gdcmMSFF gdcmopenjpeg gdcmzlib libprotobuf libprotobuf-lite libprotoc)    

