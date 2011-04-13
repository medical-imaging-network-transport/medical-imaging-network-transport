# -----------------------------------------------------------------------------
# $Id$
#
# Copyright (C) 2010 MINT Working group. All rights reserved.
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#
# Contact mint-user@googlegroups.com if any conditions of this
# licensing are not clear to you.
# -----------------------------------------------------------------------------

from struct import unpack

# -----------------------------------------------------------------------------
# DicomTransfer
# -----------------------------------------------------------------------------
class DicomTransfer():
      
   IMPLICIT_VR_LITTLE_ENDIAN    = "1.2.840.10008.1.2"
   EXPLICIT_VR_LITTLE_ENDIAN    = "1.2.840.10008.1.2.1"
   EXPLICIT_VR_BIG_ENDIAN       = "1.2.840.10008.1.2.2" 
   JPEG_COMPRESSION             = "1.2.840.10008.1.2.4."
   LOSSLESS_RUN_LENGTH_ENCODING = "1.2.840.10008.1.2.5"
   
   def __init__(self, transferSyntaxUI="1.2.840.10008.1.2"):
   
       self.__implicit = True
       self.__littleEndian = True

       if transferSyntaxUI == self.IMPLICIT_VR_LITTLE_ENDIAN:
          pass
       elif transferSyntaxUI == self.EXPLICIT_VR_LITTLE_ENDIAN:
          self.__implicit = False
          self.__littleEndian = True
       elif transferSyntaxUI == self.EXPLICIT_VR_BIG_ENDIAN:
          self.__implicit = False
          self.__littleEndian = False
       elif transferSyntaxUI[0:20] == self.JPEG_COMPRESSION:
          # xx = 50-64: Lossy JPEG
          # xx = 65-70: Lossless JPEG
          pass
       elif transferSyntaxUI == self.LOSSLESS_RUN_LENGTH_ENCODING:
          pass
       else:
          raise IOError("Unknown transfer syntax UI "+transferSyntaxUI)
       
   def isImplicit(self)     : return     self.__implicit
   def isExplicit(self)     : return not self.__implicit
   def isLittleEndian(self) : return     self.__littleEndian
   def isBigEndian(self)    : return not self.__littleEndian 
       
   def unpack(self, type, bytes):
       unpacked = []
       if self.__littleEndian:
          unpacked = unpack("<"+type, bytes)
       else:
          unpacked = unpack(">"+type, bytes)
       return unpacked[0]
       