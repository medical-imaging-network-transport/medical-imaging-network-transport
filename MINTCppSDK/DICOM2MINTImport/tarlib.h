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

   File:       Tarlib.h
   Created:    August 29, 2006
   Modified:   August 29, 2006
   Contains:   Creating and reading tar files between memory and disk

  --------------------------------------------------------------------*/

#if !defined(TARLIB_H)
#define TARLIB_H

#if _MSC_VER >= 1000
#pragma once
#endif // _MSC_VER >= 1000

#ifndef UINT32
#define UINT32 unsigned int 
#endif

// Tar file field sizes
#define MAX_OBJECT_NAME     100
#define MAX_FILE_MODE       8
#define MAX_OWNER_USER_ID   8
#define MAX_GROUP_USER_ID   8
#define MAX_FILE_SIZE       12
#define MAX_LAST_MOD_TIME   12 
#define MAX_HDR_CHECKSUM    8
#define MAX_MODE_FLAG       1
#define MAX_LINK_FILE_NAME  100
#define TAR_PAD             255
#define TAR_BLOCK_SIZE      512
#define MAX_FILE_CHECKSUM   12

// Bycast requested these strings
#define FILE_MODE_STR        "0000755"
#define OWNER_USERID_STR     "0000766"
#define GROUP_USERID_STR     "0000766"

// Tar files must be rounded up to nearest 512 block
#define  NEXT_BLOCK(_bytes) ((TAR_BLOCK_SIZE - (_bytes % TAR_BLOCK_SIZE)) % TAR_BLOCK_SIZE)
// Macros to parse "objectname.filechecksum" string
#define  GET_FILENAME(_dest, _source) (strncpy(_dest, _source, strlen(_source) - MAX_FILE_CHECKSUM))
#define  GET_FILECHK(_dest, _source) (strncpy(_dest, &_source[strlen(_source) - (MAX_FILE_CHECKSUM - 1)], MAX_FILE_CHECKSUM - 1))

// Misc
#define  FREE_FILE        true
#define  DONT_FREE_FILE   false
#define  FILE_INTO_CHUNK  true
#define  CHUNK_INTO_FILE  false
#define  CREATE_FILE      true
#define  DONT_CREATE      false
#define  DONT_SHARE       false

// Error Types
#define  ERR_MEM          1
#define  ERR_OPEN         2
#define  ERR_READ         3
#define  ERR_WRITE        4
#define  ERR_CHECKSUM     5
#define  ERR_NOFILE       6

// ReadNextFile instructions
#define  NEXT             0
#define  FIRST            1
#define  USE_OBJNAME      2

// Error struct
typedef struct TarException {
	int       Type;
	string   Name;
	__int64   Size;
} TarException_t;

typedef struct Header {
	char    ObjectName[MAX_OBJECT_NAME];
	char    FileMode[MAX_FILE_MODE];
	char    OwnerUserID[MAX_OWNER_USER_ID];
	char    GroupUserID[MAX_GROUP_USER_ID];
	char    FileSize[MAX_FILE_SIZE];
	char    LastModTime[MAX_LAST_MOD_TIME];
	char    Checksum[MAX_HDR_CHECKSUM];
	char    ModeFlag[MAX_MODE_FLAG];
	char    LinkFileName[MAX_LINK_FILE_NAME];
	char    Pad[TAR_PAD];
} Header_t;

typedef struct FileData {
	Header_t  Header;
	void     *File;
	char      ObjectName[MAX_OBJECT_NAME];
	long      FileSize;
	long      HeaderSize;
	__int64   CurrentReadPos;
	long      ChunkOffset;
} FileData_t;

typedef struct Filename {
	struct   Filename *Next;
	struct   Filename *Prev;
	char     ObjectName[MAX_OBJECT_NAME];
	__int64  Offset;
} Filename_t;

typedef struct TarFile {
	string	    Name;       // Keep this 1st - see Init()
	int          Handle;		 // Keep this 2nd - see Init()
	int          State;
	bool         FileCreated;
} TarFile_t;
#define   TAR_CLOSED  0
#define   TAR_OPEN    1

class CTarCallback
{
public:
	CTarCallback(void) {};
	~CTarCallback(void) {};
	virtual int FileReadyCallback(void *File, string ObjectName, unsigned int FileSize) = 0;
};

class CTar
{

public:
	         CTar(string TarFile);             // File mode
	         CTar(CTarCallback *TarCallback);   // Streaming mode
	virtual ~CTar();
	string   GetLastError(void);
	int      AddFile(void *File, string ObjectName, unsigned int FileSize);
	int      AddFile(string Filename, string ObjectName);
	int      DeleteFile(string ObjectName);
	__int64  GetTarSize(void);
	int      GetNumFiles(void);
	bool     Verify(string ObjectName = "");
	void		FreeFileMemory(void *& File);
	int		ReadNextFile(void *& File, string& ObjectName, int& FileSize);
	int		ReadNextFile(string& ObjectName, string Path, int WhatFile = NEXT);
   int      ReadNextFile(iostream & File, string& ObjectName);
	int		ReadFirstFile(void *& File, string& ObjectName, int& FileSize);
	int		ReadFirstFile(string& ObjectName, string Path);
	int		ReadFile(void *& File, string ObjectName, int& FileSize);
	int      ReadFile(string& ObjectName, string Path);
	int		ReadVerifiedChunk(void *Memory, unsigned int FileSize, bool First = false);
	int		ReadFirstVerifiedChunk(void *Memory, unsigned int FileSize);
	void     StartStream(void);
	int		AddChunk(void *Memory, unsigned int Size);
	char* GetFirstFileName();

protected:
	void     Init(void);
	void     ProcessException(TarException_t Error);
	int      OpenFile(TarFile_t& File, bool Create = DONT_CREATE, bool Share = DONT_SHARE);
	int      OpenTar(bool Create = DONT_CREATE);
	void     CloseFile(TarFile_t& File);
	void     CloseTar(void);
	Filename_t  *AddFilenameToList(string ObjectName, __int64 Offset);
	Filename_t  *UpdateFilenameOffset(string ObjectName, __int64 Offset, bool StripChecksum = false);
	void     DeleteFilenameFromList(string ObjectName);
	int      FillFilenameList();
	__int64  GetOffsetFromFilenameList(string ObjectName);
	void     WriteTarFile(Header_t Header, void *File, unsigned int FileSize);
	int      OverwriteCurrentFile(Header_t Header);
	UINT32   GetChecksum(void *Memory, unsigned int FileSize);
	bool     VerifyChecksum(void *Memory, unsigned int FileSize, UINT32 Checksum);
	bool     VerifyHeader(Header_t Header);
	int      ReadVerifyHeader(Header_t &Header);
	void		SkipFileToNextHeader(char *FileSize);
	bool     VerifyFile(void *& File, long Filesize, char *ObjectName);
	int      ReadVerifyFile(void *& File, Header_t Header);
	void     FillHeader(Header_t &Header, string ObjectName, unsigned int FileSize, UINT32 FileChecksum);
	int		ReadFileData(FileData_t &File, bool First = false);
	unsigned int TarMemCpy(long &Offset, void *& Dest, unsigned int &Remaining,
		                    void *Source, unsigned int Len, bool StreamFlow);
	unsigned int CopyChunk(FileData_t& FileData, void *& Chunk,
		                    unsigned int Remaining, bool StreamFlow = FILE_INTO_CHUNK);
	void     ResetFile(FileData_t &FileData, bool FreeFile = FREE_FILE);
	bool     FileReady(unsigned int Remaining);
	void     CreateSubDir(string Path, string ObjectName);


	string          m_LastError;
	TarFile_t       m_TarFile;
	Filename_t     *m_FilenameList;
	char            m_NULLBLOCK[TAR_BLOCK_SIZE];
	FileData_t      m_ReadFile;
	FileData_t      m_ReadChunk;
	FileData_t      m_ChunkStream;
	CTarCallback   *m_TarCallback;
};

#endif
