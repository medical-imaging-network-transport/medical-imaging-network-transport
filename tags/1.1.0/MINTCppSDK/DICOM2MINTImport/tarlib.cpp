/*---------------------------------------------------------------------
   ___________________________________________________________________

   The information contained in this file is the intellectual property
   of

      DeJarnette Research Systems, Inc.
      401 Washington Avenue
      Towson, MD 21204

   This software and associated documentation cannot be             
   reproduced or distributed in any manner without the              
   expressed written consent of the company.  Any reproduction      
   or distribution of this software and/or documentation,           
   which is consented to by the company, must be accompanied        
   by this notice.                                                  
								    
   Copyright (c) 1997 DeJarnette Research Systems, Inc.             
   All Rights Reserved                                              
								    
   ___________________________________________________________________

   File:       Tarlib.cpp
   Created:    August 29, 2006
   Modified:   August 29, 2006
   Contains:   Creating and reading tar files between memory and disk  

  --------------------------------------------------------------------*/



#include <stdio.h>
#include <string.h>
#include <io.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <share.h>
#include <time.h>
#include <errno.h>
#include <direct.h>
#include <exception>
#include <sstream>
#include <string>
#include <iostream>
#include <assert.h>

using namespace std ;

#include "tarlib.h"

#define TEMPSTR_SIZE   512

#ifndef UINT8
#define UINT8 unsigned char
#endif

/* ____________________________________________________________________________
 *
 *  Input: TarFile - The name of a tar file
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
CTar::CTar(string TarFile)
{
	Init();
	m_TarFile.Name = TarFile;
}

/* ____________________________________________________________________________
 *
 *  Input:
 *      CTarCallback *TarCallback - The pointer to a class that contains a callback
 *		  function.  The calling application will have a class derived from CTarCallback.
 *		  That class will have the following member function: 
 *
 *      int FileReadyCallback(void *File, string ObjectName, unsigned int FileSize)
 *
 *      The callback function will receive 3 parameters:
 *		  void *File – The file in memory 
 *		  string ObjectName – The name of the file returned
 *		  int FileSize – The Size of the file in memory
 *  Output:
 *  Class members affected:
 *  Overview:
 *       This constructor will be used to operate in streaming mode.  All files
 *       passed in memory to the callback function must be freed with a call to
 *       FreeFileMemory.
 _______________________________________________________________________________*/
CTar::CTar(CTarCallback *TarCallback)
{
	Init();
	m_TarCallback = TarCallback;
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
void CTar::Init(void)
{
	m_LastError = "";
	// string doesn't like to be set to 0
	m_TarFile.Name = "";
	memset(&(m_TarFile.Handle), 0, sizeof(TarFile_t) - sizeof(m_TarFile.Name));
	m_FilenameList = NULL;
	memset(m_NULLBLOCK, 0, sizeof(m_NULLBLOCK));
	memset(&m_ReadFile, 0, sizeof(FileData_t));
	memset(&m_ReadChunk, 0, sizeof(FileData_t));
	memset(&m_ChunkStream, 0, sizeof(FileData_t));
	ResetFile(m_ChunkStream, DONT_FREE_FILE);
	m_TarCallback = NULL;
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
CTar::~CTar()
{
	Filename_t *file, *next = NULL;

	FreeFileMemory(m_ReadChunk.File);
	if (m_FilenameList) {
		for (file = m_FilenameList; file; file = next) {
			next = file->Next;
			DeleteFilenameFromList(file->ObjectName);
		}
	}
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
int CTar::OpenFile(TarFile_t& File, bool Create, bool Share)
{
	TarException_t  exception;
   int ShareMode;

	if (File.State == TAR_OPEN)
		return(0);

   if(!Share)
      ShareMode= _SH_DENYRW;
   else
      ShareMode= _SH_DENYNO;

   File.Handle = _sopen(File.Name.c_str(), O_RDWR|O_BINARY, ShareMode, _S_IREAD|_S_IWRITE);

	if (File.Handle != -1) {
		File.State = TAR_OPEN;
		return(0);
	}

	if (Create == DONT_CREATE) {
		exception.Name = File.Name;
		exception.Type = ERR_OPEN;
		throw exception;
	}

	if (errno == ENOENT) // if file doesn't exist, create the file
		File.Handle = _sopen(File.Name.c_str(), O_RDWR|O_CREAT|O_BINARY, ShareMode, _S_IREAD|_S_IWRITE);
	if (File.Handle == -1) {
		exception.Name = File.Name;
		exception.Type = ERR_OPEN;
		throw exception;
	}

	File.FileCreated = true;
	File.State = TAR_OPEN;
	return(0);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
int CTar::OpenTar(bool Create)
{
	return(OpenFile(m_TarFile, Create));
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
void CTar::CloseFile(TarFile_t& File)
{
	if (File.State == TAR_OPEN) {
		_close(File.Handle);
		File.State = TAR_CLOSED;
	}
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
void CTar::CloseTar(void)
{
	CloseFile(m_TarFile);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
void CTar::ProcessException(TarException_t Error)
{

   ostringstream str;
   if (Error.Type == ERR_MEM)
      str << "[TARLIB] ERROR: Unable to allocate memory for " << Error.Name << " size =" << Error.Size;
	else if (Error.Type == ERR_OPEN)
      str << "[TARLIB] ERROR: Unable to open file " << Error.Name << " size =" << errno;
	else if (Error.Type == ERR_READ)
      str << "[TARLIB] ERROR: Unable to read file " << Error.Name << " size =" << errno;
	else if (Error.Type == ERR_WRITE)
      str << "[TARLIB] ERROR: Unable to write to file " << m_TarFile.Name << " size =" << errno;
	else if (Error.Type == ERR_CHECKSUM)
      str << "[TARLIB] ERROR: Invalid checksum in file " << m_TarFile.Name;
	else if (Error.Type == ERR_NOFILE)
      str << "[TARLIB] ERROR: Filename " << Error.Name << " does not exist in file " << m_TarFile.Name;
   
   m_LastError = str.str();
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output: string – A string describing the last error encountered.
 *  Class members affected:
 *  Overview:
 *		The contents of the returned string could change with the next call
 *		to the CTar instance, so a copy should be made if the calling function
 *		needs to save the contents for later use.
 _______________________________________________________________________________*/
string CTar::GetLastError(void)
{
	return(m_LastError);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
Filename_t *CTar::AddFilenameToList(string ObjectName, __int64 Offset)
{
	Filename_t     *newFile = NULL;
	TarException_t  exception;

	if ((newFile = (Filename_t *)malloc(sizeof(Filename_t))) == NULL) {
		exception.Type = ERR_MEM; exception.Name = "Filename_t";
		exception.Size = sizeof(Filename_t);
		throw exception;
	}
	memset(newFile, 0, sizeof(Filename_t));
	strcpy(newFile->ObjectName, ObjectName.c_str());
	newFile->Offset = Offset;

	if (!m_FilenameList) {
		m_FilenameList = newFile;
		return(newFile);
	}
	m_FilenameList->Prev = newFile;
	newFile->Next = m_FilenameList;
	m_FilenameList = newFile;

	return(newFile);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
Filename_t *CTar::UpdateFilenameOffset(string ObjectName, __int64 Offset, bool StripChecksum)
{
	Filename_t *file;
	char        origObjectName[MAX_OBJECT_NAME];

	// strip the checksum off the object name
	if (StripChecksum)
	{
		memset(origObjectName, 0, sizeof(origObjectName));
		GET_FILENAME(origObjectName, ObjectName.c_str());
	}
	else
	{
		strcpy(origObjectName, ObjectName.c_str());
	}
	for (file = m_FilenameList; file; file = file->Next) {
		if (strcmp(origObjectName, file->ObjectName) == 0) {
			file->Offset = Offset;
			return(file);
		}
	}

	return(NULL);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
void CTar::DeleteFilenameFromList(string ObjectName)
{
	Filename_t *file, *next;

	for (file = m_FilenameList; file; file = next) {
		next = file->Next;
		if (strcmp(ObjectName.c_str(), file->ObjectName) == 0) {
			if (m_FilenameList == file)
				m_FilenameList = file->Next;
			if (file->Prev)
				file->Prev->Next = file->Next;
			if (file->Next)
				file->Next->Prev = file->Prev;
			free(file);
		}
	}
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
int CTar::FillFilenameList()
{
	__int64      offset;
	char         origObjectName[MAX_OBJECT_NAME];
	Header_t     header;
	int          len;

	assert(m_TarFile.State == TAR_OPEN);

	_lseeki64(m_TarFile.Handle, 0, SEEK_SET);
	offset = _telli64(m_TarFile.Handle);
	while ((len = ReadVerifyHeader(header)) > 0) {
      memset(origObjectName, 0, sizeof(origObjectName));
		GET_FILENAME(origObjectName, header.ObjectName);
		AddFilenameToList(origObjectName, offset);
		SkipFileToNextHeader(header.FileSize);
		offset = _telli64(m_TarFile.Handle);
	}

	_lseeki64(m_TarFile.Handle, 0, SEEK_END);
	return(0);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
__int64 CTar::GetOffsetFromFilenameList(string ObjectName)
{
	Filename_t *file;

	for (file = m_FilenameList; file; file = file->Next) {
		if (ObjectName.compare(file->ObjectName) == 0)
			return(file->Offset);
	}

	return(-1);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *		void * File – A file in memory to be added to the current TarFile
 * 	string ObjectName – The name of the file being added to the tar
 * 	unsigned int FileSize – The size of file
 *  Output: int - 0 success, non-zero returned on error
 *  Class members affected:
 *  Overview:
 *		AddFile will add the file passed in memory to the tar file.  
 *    AddFile will maintain a 1 – 1 relationship between ObjectName
 *		and files in the tar.  If an ObjectName that already exists in
 *		the tar is passed to AddFile, it will be replaced in the tar.  
 *		If the new FileSize rounded to the next 512 block is the same as
 *		the existing FileSize of the object in the tar, the file will be
 *		written in the same area of the disk.  Otherwise, the remaining 
 *		objects of the tar will have to be read and rewritten, with the new
 *		object attached to the end of the tar.  This method could result in
 *		slow performance.
 *		If the FileSize passed is 0 the ObjectName will be deleted.
 _______________________________________________________________________________*/
int CTar::AddFile(void *File, string ObjectName, unsigned int FileSize)
{
	Header_t   header;
	__int64    offset;
	int        len;

	assert(m_TarFile.Name[0] != NULL);
	assert(ObjectName[0] != NULL);
	assert(ObjectName.length() <= (MAX_OBJECT_NAME - MAX_FILE_CHECKSUM - 1));
	memset(&header, 0, sizeof(header));

	try
	{
		// Open the file - 1st try to append to an existing file
		// If no file exists create a new one.
		OpenTar(CREATE_FILE);
		// If the file existed, but FilenameList doesn't, initialize the filename list
		if ((!m_FilenameList) && (!m_TarFile.FileCreated))
			FillFilenameList();

		// If the file is already in this tar file - Replace the old file
		if ((offset = GetOffsetFromFilenameList(ObjectName)) != -1) {
			_lseeki64(m_TarFile.Handle, offset, SEEK_SET);
			if ((len = ReadVerifyHeader(header)) > 0) {
				// If the file will NOT fit in the same area on disk
				//	The old file must be overwritten,
				// then the new file tacked onto the end of the file.
				// This very inefficent, but it must be done.
				len = strtol(header.FileSize, (char **)NULL, 8);
				if ((len + NEXT_BLOCK(len)) != (FileSize + NEXT_BLOCK(FileSize))) {
					OverwriteCurrentFile(header);
					UpdateFilenameOffset(ObjectName, _telli64(m_TarFile.Handle));
				}
				// If the file will fit in the same area on disk
				//	Overwrite the previous file
				// Set the current file pointer to point to the old position, then write the header/file
				else
					_lseeki64(m_TarFile.Handle, offset, SEEK_SET);
			}
		}
		else {
			_lseeki64(m_TarFile.Handle, 0, SEEK_END);
			AddFilenameToList(ObjectName, _telli64(m_TarFile.Handle));
		}

		// FileSize == 0 will delete the file, just close the file and return
		if (FileSize == 0) {
			DeleteFilenameFromList(ObjectName);
			CloseTar();
			return(0);
		}

		try
		{
			FillHeader(header, ObjectName, FileSize, GetChecksum(File, FileSize));
		}
		catch(std::exception& e)
		{
         ostringstream str;
         str << "[TARLIB] ERROR: Exception " << e.what() << " occurred in GetChecksum";
         this->m_LastError= str.str();
			DeleteFilenameFromList(ObjectName);
			CloseTar();
			return(-1);

		}

		WriteTarFile(header, File, FileSize);
		CloseTar();
	}
	catch (TarException_t Error)
	{
		ProcessException(Error);
		CloseTar();
		return(-1);
	}

	return(0);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
int CTar::AddFile(string Filename, string ObjectName)
{
	void           *pFile = NULL;
	__int64         filesize;
	TarFile_t       fileInfo;
	TarException_t  exception;
	int             n;

	// string doesn't like to be set to 0
	fileInfo.Name = "";
	memset(&(fileInfo.Handle), 0, sizeof(TarFile_t) - sizeof(fileInfo.Name));
	fileInfo.Name = Filename;

	try
	{
		OpenFile(fileInfo, false, true);
		// Find the filesize
		_lseeki64(fileInfo.Handle, 0, SEEK_END);
		filesize = _telli64(fileInfo.Handle);
		_lseeki64(fileInfo.Handle, 0, SEEK_SET);
		// Allocate the memory
		if ((pFile = malloc((size_t)filesize)) == NULL) {
			exception.Type = ERR_MEM; exception.Name = "MemoryFile";
			exception.Size = filesize;
			throw exception;
		}
		// Read the file into memory
		if ((n = _read(fileInfo.Handle, pFile, (unsigned int)filesize)) != filesize) {
			exception.Name = fileInfo.Name;
			exception.Type = ERR_READ;
			throw exception;
		}
		CloseFile(fileInfo);
	}
	catch (TarException_t Error)
	{
		ProcessException(Error);
		CloseFile(fileInfo);
		if (pFile) free(pFile); pFile = NULL;
		return(-1);
	}

	// Add the file to the tar
	return(AddFile(pFile, ObjectName, (unsigned int)filesize));
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
void CTar::WriteTarFile(Header_t Header, void *File, unsigned int FileSize)
{
	TarException_t  exception;

	assert(m_TarFile.State == TAR_OPEN);

	// Write the Header
	if (_write(m_TarFile.Handle, &Header, TAR_BLOCK_SIZE) < TAR_BLOCK_SIZE) {
		exception.Type = ERR_WRITE;
		throw exception;
	}
	// Write the File
	if (_write(m_TarFile.Handle, File, FileSize) < (int)FileSize) {
		exception.Type = ERR_WRITE;
		throw exception;
	}
	// Pad to the next Block
	if (_write(m_TarFile.Handle, m_NULLBLOCK, NEXT_BLOCK(FileSize)) < (int)NEXT_BLOCK(FileSize)) {
		exception.Type = ERR_WRITE;
		throw exception;
	}
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
int CTar::OverwriteCurrentFile(Header_t Header)
{
	__int64  writePos, readPos;
	long     len;
	void    *file = NULL;
	int      fileSize;

	assert(m_TarFile.State == TAR_OPEN);

	readPos = 0;
	writePos = _telli64(m_TarFile.Handle) - TAR_BLOCK_SIZE;
	if (m_ReadFile.CurrentReadPos > writePos)
		m_ReadFile.CurrentReadPos = writePos; // If replacing a file before the current read position; reset read pos
	if (m_ReadChunk.CurrentReadPos > writePos)
		m_ReadChunk.CurrentReadPos = writePos; // If replacing a file before the current read position; reset read pos
	SkipFileToNextHeader(Header.FileSize);
	while ((len = ReadVerifyHeader(Header)) > 0) {
		fileSize = ReadVerifyFile(file, Header);
		// Save the read position and go back to the write position
		readPos = _telli64(m_TarFile.Handle);
		_lseeki64(m_TarFile.Handle, writePos, SEEK_SET);
		UpdateFilenameOffset(Header.ObjectName, writePos, true);
		WriteTarFile(Header, file, fileSize);
		if (file) free(file); file = NULL;
		// Save the write position and go to the read position
		writePos = _telli64(m_TarFile.Handle);
		_lseeki64(m_TarFile.Handle, readPos, SEEK_SET);
	}
	_lseeki64(m_TarFile.Handle, writePos, SEEK_SET);
	// chop off the end of the file
	_chsize(m_TarFile.Handle, (long)writePos);

	return(0);
}

/* ____________________________________________________________________________
 *
 *  Input: string ObjectName – The name of the file being deleted from the tar.
 *  Output: int - 0 success, non-zero returned on error.
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
int CTar::DeleteFile(string ObjectName)
{
	return(AddFile(NULL, ObjectName, 0));
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
UINT32 CTar::GetChecksum(void *Memory, unsigned int FileSize)
{
   unsigned int i;
   UINT32   total = 0;

   for (i = 0; i < FileSize; i++)
   {
	   total += ((UINT8 *)Memory)[i];
   }

   return(total);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
bool CTar::VerifyChecksum(void *Memory, unsigned int FileSize, UINT32 Checksum)
{
	TarException_t  exception;

	if (GetChecksum(Memory, FileSize) != Checksum) {
		exception.Type = ERR_CHECKSUM;
		throw exception;
	}

	return(true);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
bool CTar::VerifyHeader(Header_t Header)
{
	char    checksumStr[MAX_HDR_CHECKSUM];
	UINT32  checksum;
	bool    ok;

	strncpy(checksumStr, Header.Checksum, MAX_HDR_CHECKSUM - 1);
	checksum = (UINT32)_strtoui64(checksumStr, (char **)NULL, 8);
	memcpy(checksumStr, &Header.Checksum, MAX_HDR_CHECKSUM);
	memset(&Header.Checksum, ' ', MAX_HDR_CHECKSUM);
	ok = VerifyChecksum(&Header, sizeof(Header_t), checksum);
	// Leave the checksum in tact in case caller wants to reuse
	memcpy(&Header.Checksum, checksumStr, MAX_HDR_CHECKSUM);
	return(ok);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
int CTar::ReadVerifyHeader(Header_t &Header)
{
	int             n;
	TarException_t  exception;

	assert(m_TarFile.State == TAR_OPEN);

	if ((n = _read(m_TarFile.Handle, &Header, sizeof(Header_t))) < 0) {
		exception.Name = m_TarFile.Name;
		exception.Type = ERR_READ;
		throw exception;
	}
	if (!n)
		return(0);
	if (!VerifyHeader(Header))
		return(-1);

	return(n);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
void CTar::SkipFileToNextHeader(char *FileSize)
{
	long	 nfilesize;

	assert(m_TarFile.State == TAR_OPEN);

	nfilesize = strtol(FileSize, (char **)NULL, 8);
	_lseeki64(m_TarFile.Handle, nfilesize + NEXT_BLOCK(nfilesize), SEEK_CUR);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
bool CTar::VerifyFile(void *& File, long Filesize, char *ObjectName)
{
	char    checksumStr[MAX_FILE_CHECKSUM];
	UINT32  checksum;

	memset(checksumStr, 0, sizeof(char) * (MAX_FILE_CHECKSUM));
	GET_FILECHK(checksumStr, ObjectName);
	checksum = (UINT32)_strtoui64(checksumStr, (char **)NULL, 8);
	return(VerifyChecksum(File, Filesize, checksum));
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
int CTar::ReadVerifyFile(void *& File, Header_t Header)
{
	long	          filesize;
	int             n;
	TarException_t  exception;

	assert(m_TarFile.State == TAR_OPEN);

	filesize = strtol(Header.FileSize, (char **)NULL, 8);
	if ((File = malloc(sizeof(UINT8) * filesize)) == NULL) {
		exception.Type = ERR_MEM; exception.Name = "File";
		exception.Size = sizeof(filesize);
		throw exception;
	}
	if ((n = _read(m_TarFile.Handle, File, filesize)) != filesize) {
		exception.Name = m_TarFile.Name;
		exception.Type = ERR_READ;
		//SRD - If we throw here the allocated File pointer in the block above is not
		//taken care of in the event that we throw back up through the path
		//ReadVerifyFile() >> OverwriteCurrentFile() >> AddFile()
		//So if we throw here we should free the file here
		if( File != NULL )
		{
			free(File);
			File = NULL;
		}
		throw exception;
	}
	if (!n)
		return(0);
	if (!VerifyFile(File, filesize, Header.ObjectName))
		return(-1);

	_lseeki64(m_TarFile.Handle, NEXT_BLOCK(filesize), SEEK_CUR);
	return(n);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
void CTar::FillHeader(Header_t &Header, string ObjectName, unsigned int FileSize, UINT32 FileChecksum)
{
	char       formatStr[TEMPSTR_SIZE];
	time_t     lastModTime;
	int        len;

	strncpy(Header.ObjectName, ObjectName.c_str(), MAX_OBJECT_NAME - 1);
	sprintf(formatStr, ".%%0%do", (MAX_FILE_CHECKSUM - 1));
	sprintf(&Header.ObjectName[strlen(Header.ObjectName)],formatStr, FileChecksum);
	for (len = strlen(Header.ObjectName); len < MAX_OBJECT_NAME; len++)
		Header.ObjectName[len] = 0;
	strncpy(Header.FileMode, FILE_MODE_STR, MAX_FILE_MODE - 1);
	strncpy(Header.OwnerUserID, OWNER_USERID_STR, MAX_OWNER_USER_ID - 1);
	strncpy(Header.GroupUserID, GROUP_USERID_STR, MAX_GROUP_USER_ID - 1);
	sprintf(formatStr, "%%0%do", (MAX_FILE_SIZE - 1));
	sprintf(Header.FileSize, formatStr, FileSize);
	sprintf(formatStr, "%%0%do", (MAX_LAST_MOD_TIME - 1));
	sprintf(Header.LastModTime, formatStr, time(&lastModTime));
	// Header Checksum
	// The checksum must be all spaces when calculating the header checksum
	memset(&Header.Checksum, ' ', MAX_HDR_CHECKSUM);
	sprintf(formatStr, "%%0%do", (MAX_HDR_CHECKSUM - 2));
	sprintf(Header.Checksum, formatStr, GetChecksum((void *)&Header, sizeof(Header_t)));
	Header.Checksum[MAX_HDR_CHECKSUM - 1] = ' ';
}

/* ____________________________________________________________________________
 *
 *  Input: void
 *  Output: __int64 – Size of the tar file
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
__int64 CTar::GetTarSize(void)
{
	__int64         offset;

	assert(m_TarFile.Name[0] != NULL);

	try
	{
		OpenTar();
		_lseeki64(m_TarFile.Handle, 0, SEEK_END);
		offset = _telli64(m_TarFile.Handle);
		CloseTar();
	}
	catch (TarException_t Error)
	{
		ProcessException(Error);
		CloseTar();
		return(0);
	}

	return(offset);
}

/* ____________________________________________________________________________
 *
 *  Input: void
 *  Output: int – The number of files in the tar file
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
int CTar::GetNumFiles(void)
{
	int         nFiles = 0;
	Filename_t *file;

	assert(m_TarFile.Name[0] != NULL);

	try
	{
		// Open the file - 1st try to append to an existing file
		// If no file exists create a new one.
		OpenTar(CREATE_FILE);
		// If the file existed, but FilenameList doesn't, initialize the filename list
		if ((!m_FilenameList) && (!m_TarFile.FileCreated))
			FillFilenameList();

		// Count the number of files in the list
		for (file = m_FilenameList; file; file = file->Next)
			nFiles++;

		CloseTar();
	}
	catch (TarException_t Error)
	{
		ProcessException(Error);
		CloseTar();
		return(-1);
	}

	return(nFiles);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *     string ObjectName – The name of the file being verified in the tar.
 *     If no Object name is passed, the entire tar is verified
 *  Output: bool – true if valid, false if not valid
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
bool CTar::Verify(string ObjectName)
{
	Header_t        header;
	long            len;
	void           *file = NULL;
	int             fileSize;
	__int64         offset;
	TarException_t  exception;

	assert(m_TarFile.Name[0] != NULL);
	assert(ObjectName.length() <= (MAX_OBJECT_NAME - MAX_FILE_CHECKSUM - 1));
	memset(&header, 0, sizeof(header));

	try
	{
		// Open the file
		OpenTar();

		// Check the entire file if ObjectName is NULL
		if (ObjectName[0] == NULL) {
			while ((len = ReadVerifyHeader(header)) > 0) {
				fileSize = ReadVerifyFile(file, header);
				if (file) free(file); file = NULL;
			}
			CloseTar();
			return(true);
		}

		// If an ObjectName is specified find it's offset
		if ((offset = GetOffsetFromFilenameList(ObjectName)) == -1) {
			exception.Type = ERR_NOFILE; exception.Name = ObjectName;
			throw exception;
		}
		_lseeki64(m_TarFile.Handle, offset, SEEK_SET);
		if ((len = ReadVerifyHeader(header)) > 0)
			ReadVerifyFile(file, header);
		if (file) free(file); file = NULL;
		CloseTar();
	}
	catch (TarException_t Error)
	{
		ProcessException(Error);
		CloseTar();
		if (file) free(file); file = NULL;
		return(false);
	}

	return(true);
}

/* ____________________________________________________________________________
 *
 *  Input: void *& File – The pointer to a file previously allocated by a call to CTar.
 *  Output:
 *  Class members affected:
 *  Overview:
		FreeFileMemory will free the memory of a file allocated with a previous call to CTar.
 _______________________________________________________________________________*/
void CTar::FreeFileMemory(void *& File)
{
	if (File)
		free(File);
	File = NULL;
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *		void *File – The file in memory (must be freed with a call to FreeFileMemory)
 *		string ObjectName – The name of the file returned
 *		int FileSize – The Size of the file in memory
 *		int – The number of bytes read, 0 = end of file, -1 = error
 *  Class members affected:
 *  Overview:
 *       ReadNextFile will read one file at a time starting with the first file in
 *       the tar. It will keep track of the last file read.  Call ReadFirstFile to
 *       reset the internal pointer.  ReadNextFile will return 0 when all of the files
 *			have been read.  If any file in the tar that is positioned ahead of the
 *			current internal pointer is replaced by AddFile, then the current read
 *			position of ReadNextFile will be reset to point to the position of the
 *			new file. The File returned must be freed with a call to FreeFileMemory.
 _______________________________________________________________________________*/
int CTar::ReadNextFile(void *& File, string& ObjectName, int& FileSize)
{
	int status;

	ResetFile(m_ReadFile, DONT_FREE_FILE);
	status = ReadFileData(m_ReadFile);
	File = m_ReadFile.File;  // Caller will have to free this memory
	FileSize = m_ReadFile.FileSize;
	ObjectName = m_ReadFile.ObjectName;
	return(status);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *		iostream *File – The file in memory (must be freed with a call to FreeFileMemory)
 *		string ObjectName – The name of the file returned
 *		int FileSize – The Size of the file in memory
 *		int – The number of bytes read, 0 = end of file, -1 = error
 *  Class members affected:
 *  Overview:
 *       ReadNextFile will read one file at a time starting with the first file in
 *       the tar. It will keep track of the last file read.  Call ReadFirstFile to
 *       reset the internal pointer.  ReadNextFile will return 0 when all of the files
 *			have been read.  If any file in the tar that is positioned ahead of the
 *			current internal pointer is replaced by AddFile, then the current read
 *			position of ReadNextFile will be reset to point to the position of the
 *			new file. The File returned must be freed with a call to FreeFileMemory.
 _______________________________________________________________________________*/
int CTar::ReadNextFile(iostream & File, string& ObjectName)
{
	int status;

	ResetFile(m_ReadFile, DONT_FREE_FILE);
	status = ReadFileData(m_ReadFile);
   if(status) {
	   File.write((const char*)m_ReadFile.File, m_ReadFile.FileSize);  // Caller will have to free this memory
	   ObjectName = m_ReadFile.ObjectName;
      FreeFileMemory(m_ReadFile.File);
   }
	return(status);
}



/* ____________________________________________________________________________
 *
 *  Input:
 *    string Path - The path to save the extracted file to.
 *  Output:
 *		string ObjectName – The name of the file returned
 *		int – The number of bytes read, 0 = end of file, -1 = error
 *  Class members affected:
 *  Overview:
 *       ReadNextFile will extract one file at a time starting with the first file in
 *       the tar. It will keep track of the last file read.  Call ReadFirstFile to
 *       reset the internal pointer.  ReadNextFile will return 0 when all of the files
 *			have been read.  If any file in the tar that is positioned ahead of the
 *			current internal pointer is replaced by AddFile, then the current read
 *			position of ReadNextFile will be reset to point to the position of the
 *			new file.
 _______________________________________________________________________________*/
int CTar::ReadNextFile(string& ObjectName, string Path, int WhatFile)
{
	TarFile_t       extractedFile;
	TarException_t  exception;
	int             status = 0;

	try
	{
		ResetFile(m_ReadFile, DONT_FREE_FILE);
		if (WhatFile == FIRST)
			status = ReadFileData(m_ReadFile, true);
		if (WhatFile == NEXT)
			status = ReadFileData(m_ReadFile);
		if (WhatFile == USE_OBJNAME) {
			strcpy(m_ReadFile.ObjectName, ObjectName.c_str());
			status = ReadFileData(m_ReadFile);
		}
		if (status == 0)
			return(status);
		if (Path.substr(0,1).compare("\\") == 0)
			Path += "\\";
		CreateSubDir(Path, m_ReadFile.ObjectName);  // Will create a sub dir 1 level underneath path
		extractedFile.Name = Path + m_ReadFile.ObjectName;
		OpenFile(extractedFile, CREATE_FILE);
		if (_write(extractedFile.Handle, m_ReadFile.File, m_ReadFile.FileSize) < m_ReadFile.FileSize) {
			exception.Type = ERR_WRITE;
			throw exception;
		}
		FreeFileMemory(m_ReadFile.File);
		ObjectName = m_ReadFile.ObjectName;
		CloseFile(extractedFile);
	}
	catch (TarException_t Error)
	{
		ProcessException(Error);
		CloseFile(extractedFile);
		ResetFile(m_ReadFile, FREE_FILE);
		return(-1);
	}

	return(status);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *    void *File – The file in memory (must be freed with a call to FreeFileMemory)
 *		string ObjectName – The name of the file returned
 *		int FileSize – The Size of the file in memory
 *		int – The number of bytes read, 0 = end of file, -1 = error
 *  Class members affected:
 *  Overview:
 *     ReadFirstFile will return the first file in the tar and after successful
 *     completion reset the internal pointer used by ReadNextFile to point to the
 *     second file in the tar. ReadFirstFile will return 0 if all of the files
 *     have been read. The File returned must be freed with a call to FreeFileMemory.
 _______________________________________________________________________________*/
int CTar::ReadFirstFile(void *& File, string& ObjectName, int& FileSize)
{
	int status;

	ResetFile(m_ReadFile, DONT_FREE_FILE);
	status = ReadFileData(m_ReadFile, true);
	File = m_ReadFile.File;  // Caller will have to free this memory
	FileSize = m_ReadFile.FileSize;
	ObjectName = m_ReadFile.ObjectName;
	return(status);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *    string Path - The path to save the extracted file to.
 *  Output:
 *		string ObjectName – The name of the file returned
 *		int – The number of bytes read, 0 = end of file, -1 = error
 *  Class members affected:
 *  Overview:
 *     ReadFirstFile will extract the first file in the tar and after successful
 *     completion reset the internal pointer used by ReadNextFile to point to the
 *     second file in the tar. ReadFirstFile will return 0 if all of the files
 *     have been read.
 _______________________________________________________________________________*/
int CTar::ReadFirstFile(string& ObjectName, string Path)
{
	return(ReadNextFile(ObjectName, Path, FIRST));
}

/* ____________________________________________________________________________
 *
 *  Input: string ObjectName – The name of the file to retrieve
 *  Output:
 *    void *File – The file in memory (must be freed with a call to FreeFileMemory)
 *		int FileSize – The Size of the file in memory
 *		int – The number of bytes read, 0 = end of file, -1 = error
 *  Class members affected:
 *  Overview:
 *    ReadFile will return the file specified by ObjectName and after successful
 *    completion reset the internal pointer used by ReadNextFile to point to the
 *		next file in the tar. ReadFile will return -1 if the file specified doesn’t
 *		exist in the tar.  The File returned must be freed with a call to
 *		FreeFileMemory.
 _______________________________________________________________________________*/
int CTar::ReadFile(void *& File, string ObjectName, int& FileSize)
{
	int status;

	assert(ObjectName.length() <= (MAX_OBJECT_NAME - MAX_FILE_CHECKSUM - 1));

	ResetFile(m_ReadFile, DONT_FREE_FILE);
	strcpy(m_ReadFile.ObjectName, ObjectName.c_str());
	status = ReadFileData(m_ReadFile);
	File = m_ReadFile.File;  // Caller will have to free this memory
	FileSize = m_ReadFile.FileSize;
	ObjectName = m_ReadFile.ObjectName;
	return(status);
}

/* ____________________________________________________________________________
 *
 *  Input: string ObjectName – The name of the file to retrieve
 *  Output:
 *    void *File – The file in memory (must be freed with a call to FreeFileMemory)
 *		int FileSize – The Size of the file in memory
 *		int – The number of bytes read, 0 = end of file, -1 = error
 *  Class members affected:
 *  Overview:
 *    ReadFile will return the file specified by ObjectName and after successful
 *    completion reset the internal pointer used by ReadNextFile to point to the
 *		next file in the tar. ReadFile will return -1 if the file specified doesn’t
 *		exist in the tar.  The File returned must be freed with a call to
 *		FreeFileMemory.
 _______________________________________________________________________________*/
int CTar::ReadFile(string& ObjectName, string Path)
{
	return(ReadNextFile(ObjectName, Path, USE_OBJNAME));
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
int CTar::ReadFileData(FileData_t &File, bool First)
{
	long           len;
	__int64        offset;
	TarException_t exception;

	assert(m_TarFile.Name[0] != NULL);

	try
	{
		// Open the file
		OpenTar();

		// Set the file position based on input parameters
		if (First)
			File.CurrentReadPos = 0;
		if (File.ObjectName[0]) {
			if (!m_FilenameList)
				FillFilenameList();
			if ((offset = GetOffsetFromFilenameList(File.ObjectName)) == -1) {
				exception.Type = ERR_NOFILE; exception.Name = File.ObjectName;
				throw exception;
			}
			File.CurrentReadPos = offset;
		}
		_lseeki64(m_TarFile.Handle, File.CurrentReadPos, SEEK_SET);

		// Read the header then the file
		if ((len = ReadVerifyHeader(File.Header)) > 0) {
			File.FileSize = ReadVerifyFile(File.File, File.Header);
			memset(File.ObjectName, 0, sizeof(File.ObjectName));
			GET_FILENAME(File.ObjectName, File.Header.ObjectName);
		}
		File.CurrentReadPos = _telli64(m_TarFile.Handle);
		CloseTar();
	}
	catch (TarException_t Error)
	{
		ProcessException(Error);
		CloseTar();
		ResetFile(File, FREE_FILE);
		return(-1);
	}

	return(File.FileSize);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
unsigned int CTar::TarMemCpy(long &Offset,
									  void *& Chunk,
									  unsigned int &Remaining,
									  void *FilePtr,
									  unsigned int Len,
									  bool StreamFlow)
{
	Len = min(Len, Remaining);
	if (StreamFlow == FILE_INTO_CHUNK)
		memcpy(Chunk, FilePtr, Len);
	else
		memcpy(FilePtr, Chunk, Len);
	Offset += Len;
	Remaining -= Len;
	Chunk = &((UINT8 *)Chunk)[Len];
	return(Remaining);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
unsigned int CTar::CopyChunk(FileData_t& FileData, void *& Chunk, unsigned int Remaining, bool StreamFlow)
{
	void         *filePtr;
	unsigned int  len;
	char          junk[TAR_BLOCK_SIZE];

	// There's nothing to copy
	if (!FileData.FileSize && (StreamFlow == FILE_INTO_CHUNK))
		return(Remaining);

	// Copy Header?
	if (FileData.ChunkOffset < FileData.HeaderSize) {
		filePtr = &FileData.Header;
		filePtr = &((UINT8 *)filePtr)[FileData.ChunkOffset];
		len = FileData.HeaderSize - FileData.ChunkOffset;
		if (TarMemCpy(FileData.ChunkOffset, Chunk, Remaining, filePtr, len, StreamFlow) == 0)
			return(0);
	}
	// Copy File?
	if ((StreamFlow == CHUNK_INTO_FILE) && !FileData.File && (FileData.ChunkOffset >= FileData.HeaderSize))
		return(Remaining);
	if ((FileData.ChunkOffset >= FileData.HeaderSize) &&
		 (FileData.ChunkOffset < (FileData.HeaderSize + FileData.FileSize))) {
		filePtr = FileData.File;
		filePtr = &((UINT8 *)filePtr)[FileData.ChunkOffset - FileData.HeaderSize];
		len = FileData.FileSize - (FileData.ChunkOffset - FileData.HeaderSize);
		if (TarMemCpy(FileData.ChunkOffset, Chunk, Remaining, filePtr, len, StreamFlow) == 0)
			return(0);
	}
	// Copy File Padding?
	if ((FileData.ChunkOffset >= (FileData.HeaderSize + FileData.FileSize)) &&
		 (FileData.ChunkOffset < (FileData.HeaderSize + FileData.FileSize + NEXT_BLOCK(FileData.FileSize)))) {
		if (StreamFlow == FILE_INTO_CHUNK)
			filePtr = m_NULLBLOCK;
		else
			filePtr = junk;
		len = NEXT_BLOCK(FileData.FileSize) - (FileData.ChunkOffset - (FileData.HeaderSize + FileData.FileSize));
		if (TarMemCpy(FileData.ChunkOffset, Chunk, Remaining, filePtr, len, StreamFlow) == 0)
			return(0);
	}

	return(Remaining);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
void CTar::ResetFile(FileData_t &FileData, bool FreeFile)
{
	__int64  currentPos;

	// Save the CurrentReadPos
	currentPos = FileData.CurrentReadPos;
	if (FreeFile == FREE_FILE)
		FreeFileMemory(FileData.File);
	memset(&FileData, 0, sizeof(FileData_t));
	// Reset the CurrentReadPos
	FileData.CurrentReadPos = currentPos;
	FileData.HeaderSize = sizeof(Header_t);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *     void *Memory – A buffer to hold a chunk of memory.  It must be at least
 *		 FileSize bytes large.
 *     unsigned int FileSize – The maximum number bytes to be returned in the
 *		 Memory buffer.
 *     bool First – Option to read the first chunk of memory from the tar.
 *     The default value is false.
 *  Output:
 *     void *Memory – A chunk of memory that is at most FileSize
 *		 int – The number of bytes read, 0 = end of file, -1 = error
 *  Class members affected:
 *  Overview:
 *     ReadVerifiedChunk will return the next chunk of memory in the tar,
 *		 starting with the first chunk the first time ReadVerifiedChunk is
 *		 called.  Call ReadFirstVerifiedChunk to reset the internal pointer
 *		 to the first chunk in the tar.  ReadVerifiedChunk will only return
 *		 chunks of memory that have been verified using the checksums save in
 *		 the tar.  Repeatedly calling ReadVerifiedChunk until 0 is returned
 *		 will return a tar file in its entirety including the header blocks.
 _______________________________________________________________________________*/
int CTar::ReadVerifiedChunk(void *Memory, unsigned int FileSize, bool First)
{
	void *dest;
	int   status;
	unsigned int leftToCopy;

	assert(m_TarFile.Name[0] != NULL);

	dest = Memory;
	leftToCopy = FileSize;
	leftToCopy = CopyChunk(m_ReadChunk, dest, leftToCopy);

	while (leftToCopy) {
		ResetFile(m_ReadChunk);
		// Read another file, the File buffer is empty
		if ((status = ReadFileData(m_ReadChunk, First)) > 0)
			leftToCopy = CopyChunk(m_ReadChunk, dest, leftToCopy);
		else if (status == 0)
			return(FileSize - leftToCopy);
		else if (status == -1)
			return(status);
		First = false;
	}

	return(FileSize - leftToCopy);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *     void *Memory – A buffer to hold a chunk of memory.  It must be at least
 *		 FileSize bytes large.
 *     unsigned int FileSize – The maximum number bytes to be returned in the
 *		 Memory buffer.
 *  Output:
 *     void *Memory – A chunk of memory that is at most FileSize
 *		 int – The number of bytes read, 0 = end of file, -1 = error
 *  Class members affected:
 *  Overview:
 *     ReadFirstVerifiedChunk will return the first chunk of memory in the tar
 *     and after successful completion reset the internal pointer used by
 *		 ReadVerifiedChunk to point to the second chunk of memory in the tar.
 *		 ReadFirstVerifiedChunk will return the number of bytes read.
 _______________________________________________________________________________*/
int CTar::ReadFirstVerifiedChunk(void *Memory, unsigned int FileSize)
{
	return(ReadVerifiedChunk(Memory, FileSize, true));
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  Overview:
 *     StartStream will indicate that a new tar file memory stream is about
 *     to start.  The next call to AddChunk will be the start of a new file.
 _______________________________________________________________________________*/
void CTar::StartStream(void)
{
	ResetFile(m_ChunkStream, DONT_FREE_FILE);
	m_ChunkStream.ChunkOffset = 0;
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
bool CTar::FileReady(unsigned int Remaining)
{
	int             totalFileSize;
	TarException_t  exception;

	// See if memory needs to be allocated
	if (Remaining && !m_ChunkStream.File && (m_ChunkStream.ChunkOffset >= m_ChunkStream.HeaderSize)) {
		if (VerifyHeader(m_ChunkStream.Header)) {
			m_ChunkStream.FileSize = strtol(m_ChunkStream.Header.FileSize, (char **)NULL, 8);
			if ((m_ChunkStream.File = malloc(sizeof(UINT8) * m_ChunkStream.FileSize)) == NULL) {
				exception.Type = ERR_MEM; exception.Name = "File";
				exception.Size = m_ChunkStream.FileSize;
				throw exception;
			}
			memset(m_ChunkStream.ObjectName, 0, sizeof(m_ChunkStream.ObjectName));
			GET_FILENAME(m_ChunkStream.ObjectName, m_ChunkStream.Header.ObjectName);
			return(false);
		}
	}
	// See is a file is ready
	totalFileSize = m_ChunkStream.HeaderSize + m_ChunkStream.FileSize + NEXT_BLOCK(m_ChunkStream.FileSize);
	if (m_ChunkStream.FileSize && (m_ChunkStream.ChunkOffset >= totalFileSize)) {
		if (VerifyFile(m_ChunkStream.File, m_ChunkStream.FileSize, m_ChunkStream.Header.ObjectName))
			return(true);
	}

	return(false);
}

/* ____________________________________________________________________________
 *
 *  Input: void *Memory – A new chunk of memory to be added to the running tar
 *  Output: int – 0 = success, -1 = error
 *  Class members affected:
 *  Overview:
 *     AddChunk will add a chunk of memory to the current streaming tar.
 *     It will call the FileReadyCallback for each complete, verified file
 *		 in the stream.  The header information from the tar stream will be
 *		 discarded.
_______________________________________________________________________________*/
int CTar::AddChunk(void *Memory, unsigned int Size)
{
	void     *chunk;
	unsigned  int leftToCopy;

	assert(m_TarCallback != NULL);

	chunk = Memory;
	leftToCopy = Size;

	try
	{
		while (leftToCopy) {
			leftToCopy = CopyChunk(m_ChunkStream, chunk, leftToCopy, CHUNK_INTO_FILE);
			if (FileReady(leftToCopy)) {
				m_TarCallback->FileReadyCallback(m_ChunkStream.File, m_ChunkStream.ObjectName, m_ChunkStream.FileSize);
				ResetFile(m_ChunkStream, DONT_FREE_FILE);
			}
		}
	}
	catch (TarException_t Error)
	{
		ProcessException(Error);
		ResetFile(m_ChunkStream, FREE_FILE);
		return(-1);
	}

	return(0);
}

/* ____________________________________________________________________________
 *
 *  Input:
 *  Output:
 *  Class members affected:
 *  PDL:
 _______________________________________________________________________________*/
void CTar::CreateSubDir(string Path, string ObjectName)
{
   string  subdir;
	int      index = 0;

	if ((index = ObjectName.find(('\\'))) == -1)
		return;
	subdir = Path + ObjectName.substr(0,index);
	_mkdir(subdir.c_str());
}

char* CTar::GetFirstFileName()
{
	if( m_FilenameList == NULL )
	{
		OpenTar();
		FillFilenameList();
		CloseTar();
	}

	if( m_FilenameList != NULL )
	{
		char* retVal = new char[MAX_OBJECT_NAME];
		strcpy_s(retVal, MAX_OBJECT_NAME - 1, m_FilenameList->ObjectName);
		return retVal;
	}
	else
	{
		return NULL;
	}

}

/*---------------------------- End of File --------------------------------*/
