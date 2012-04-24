#!/usr/bin/python
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

import os
import string

from org.nema.medical.mint.XmlDocument import XmlDocument

# -----------------------------------------------------------------------------
# DataDictionary
# -----------------------------------------------------------------------------
class DataDictionary():
   
   def __init__(self, rootTag, dataDictionaryURL):

       self._xml = XmlDocument(rootTag)
       self._elements = {}
       self._url = dataDictionaryURL

       self.__tags = []
       self.__output = None
       self.__verbose = False
       self.__warnings = False
       self.__count = 0

       self.__read()

   def tidy(self):
       if self.__output != None: self.__output.close()
       
   def setOutput(self, output):
       if output == "": return
       if self.__output != None: self.__output.close()
       if os.access(output, os.F_OK):
          raise IOError("File already exists - "+output)
       self.__output = open(output, "w")

   def setVerbose(self, verbose):
       self.__verbose = verbose

   def setWarnings(self, warnings):
       self.__warnings = warnings

   def url(self):
       return self._url
              
   def numElements(self):
       return len(self.__tags)
          
   def tag(self, n):
       return self.__tags[n]
          
   def element(self, n):
       """
       Returns a Dictionary element at index n.
       """
       tag = self.__tags[n]
       return self._elements[tag]
          
   def vrs(self, tag):
       unknown=[u'UN']
       element = self.elementByTag(tag)
       if element == None: return unknown
       if element.numVRs() == 0 : return unknown
       if element.vrs()[0] == "" : return unknown
       return element.vrs()
          
   def elementByTag(self, tag):
       """
       Returns a Dictionary element if tag is found, otherwise None.
       """
       utag = string.upper(tag)
       if self._elements.has_key(utag):
          return self._elements[utag]
       
       maskedTag = utag[0:6]+"xx"
       if self._elements.has_key(maskedTag):
          return self._elements[maskedTag]
       
       maskedTag = utag[0:2]+"xx"+utag[4:8]
       if self._elements.has_key(maskedTag):
          return self._elements[maskedTag]
          
       maskedTag = utag[0:4]+"xxx"+utag[7]
       if self._elements.has_key(maskedTag):
          return self._elements[maskedTag]
          
       return None


   def compare(self, refDictionary):
       numElements = min(refDictionary.numElements(), self.numElements())
       tagsCompared = 0
       self.__count = 0
       self.__warningCount = 0
       for n in range(0, numElements):
           tag = refDictionary.tag(n)
           tagsCompared += 1
           element1 = refDictionary.elementByTag(tag)           
           element2 = self.elementByTag(tag)
           if element2 != None:
              self.__check("Keywords differ", element1.keyword(), element2.keyword(), tag)
              self.__check("VRs differ", element1.vrs(), element2.vrs(), tag)
              self.__check("VMs differ", element1.vm(), element2.vm(), tag)
              self.__warn ("Names differ", element1.name().encode('ascii', 'replace'), element2.name().encode('ascii', 'replace'), tag)
           else:
              self.__check("Missing tag", element1.keyword(), "None", tag)

       # ---
       # Print out stats if verbose.
       # ---       
       if self.__verbose:
          if self.__output == None:
             print "%10d data dictionary element(s) compared." % (tagsCompared)
          else:
             self.__output.write("%10d data dictionary element(s) compared.\n" % (tagsCompared))
       
       # ---
       # Print warnings if they want.
       # ---
       if self.__warnings:
          if self.__output == None:
             print "%10d warning(s) found." % (self.__warningCount)
          else:
             self.__output.write("%10d warning(s) found.\n" % (self.__warningCount))

       # ---
       # Always print differences.
       # ---
       if self.__count != 0:
          if self.__output == None:
             print "%10d difference(s) found." % (self.__count)
          else:
             self.__output.write("%10d difference(s) found.\n" % (self.__count))

       return self.__count

   def __check(self, msg, obj1, obj2, tag):
       if obj1 != obj2:
          self.__count += 1
          print "ERROR:", tag, msg, ":", obj1, "!=", obj2
       
   def __warn(self, msg, obj1, obj2, tag):
       if obj1 != obj2 and self.__warnings:
          self.__warningCount += 1
          print "WARNING:", tag, msg, ":", obj1, "!=", obj2

   def __read(self):
       self._xml.readFromURL(self._url)
       self._parseXml()
       self.__tags = self._elements.keys()
       self.__tags.sort()

   def _parseXml(self):
       pass
   
   def debug(self):
       numElements = self.numElements()
       for n in range(0, numElements):
           tag = self.tag(n)  
           element = self.elementByTag(tag)
           if element != None: element.debug(self.__output)
