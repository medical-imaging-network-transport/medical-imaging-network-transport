#!/usr/bin/python
# -----------------------------------------------------------------------------
# $Id$
#
# Copyright (C) 2012 MINT Working group. All rights reserved.
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

from org.nema.medical.mint.DicomAttribute import DicomAttribute
from org.nema.medical.mint.DicomTransfer  import DicomTransfer

FILE_META_INFO_GROUP_LENGTH_TAG  = "00020000"

# -----------------------------------------------------------------------------
# DicomHeader
# -----------------------------------------------------------------------------
class DicomHeader():
   def __init__(self, dataDictionary):
       self.__attributes = {}
       self.__tags = []
       self.__dataDictionary = dataDictionary
       self.__transferSyntax = DicomTransfer()

   def isDicom(filename):
       dcm = open(filename, "rb")
       preamble=dcm.read(128)
       dicm=dcm.read(4)
       dcm.close()
       return (dicm == "DICM")
   isDicom = staticmethod(isDicom)
   
   def read(self, dcm):
       
       # ---
       # Read the preamble
       # ---
       preamble=dcm.read(128)
       dicm=dcm.read(4)
       assert(dicm == "DICM")

       # ---
       # Read the File Meta Information Group Length
       # ---
       littleEndian = DicomTransfer()
       attr = DicomAttribute(dcm, self.__dataDictionary, littleEndian)
       self.__attributes[attr.tag()] = attr
       assert(attr.tag() == FILE_META_INFO_GROUP_LENGTH_TAG)
       bytesLeft = int(attr.val())
       
       # ---
       # Read File Meta Information elements
       # ---
       while bytesLeft > 0:
          attr = DicomAttribute(dcm, self.__dataDictionary, littleEndian)
          self.__attributes[attr.tag()] = attr
          bytesLeft -= attr.bytesRead()
          if attr.isTransferSyntax(): self.__transferSyntax = DicomTransfer(attr.val())

       assert(bytesLeft == 0)
       self.__tags = self.__attributes.keys()
       self.__tags.sort()
          
   def transferSyntax(self):
       return self.__transferSyntax

   def numAttributes(self):
       return len(self.__tags)
       
   def tag(self, n):
       return self.__tags[n]
                        
   def attribute(self, n):
       """
       Returns a DicomAttribute at index n.
       """
       tag = self.__tags[n]
       return self.__attributes[tag]
       
   def attributeByTag(self, tag):
       """
       Returns a DicomAttribute if tag is found, otherwise None.
       """
       if self.__attributes.has_key(tag):
          return self.__attributes[tag]
       else:
          return None
   
   def debug(self, output=None):      
       numAttributes = self.numAttributes()
       for n in range(0, numAttributes):
           tag = self.tag(n)
           attr = self.attributeByTag(tag)
           attr.debug(output)
