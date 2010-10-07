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

import getopt
import os
import string
import sys
import traceback

from org.nema.medical.mint.DataDictionaryElement import DataDictionaryElement
from org.nema.medical.mint.XmlDocument           import XmlDocument
from org.nema.medical.mint.XmlNode               import XmlNode

# -----------------------------------------------------------------------------
# DataDictionary
# -----------------------------------------------------------------------------
class DataDictionary():
   
   ROOT_TAG_NAME = "dictionary"
   
   def __init__(self, dataDictionaryUrl):
       """
       Parses a Data Dictionary XML document.
       """
       self.__xml = XmlDocument(DataDictionary.ROOT_TAG_NAME)
       self.__elements = {}
       self.__tags = []
       
       # TODO: Replace with URL
       self.__readFromFile(dataDictionaryUrl)
       
   def numElements(self):
       return len(self.__tags)
          
   def tag(self, n):
       return self.__tags[n]
          
   def element(self, n):
       """
       Returns a Dictionary element at index n.
       """
       tag = self.__tags[n]
       return self.__elements[tag]
          
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
       if self.__elements.has_key(utag):
          return self.__elements[utag]
       
       maskedTag = utag[0:6]+"xx"
       if self.__elements.has_key(maskedTag):
          return self.__elements[maskedTag]
       
       maskedTag = utag[0:2]+"xx"+utag[4:8]
       if self.__elements.has_key(maskedTag):
          return self.__elements[maskedTag]
          
       maskedTag = utag[0:4]+"xxx"+utag[7]
       if self.__elements.has_key(maskedTag):
          return self.__elements[maskedTag]
          
       return None

   def __readFromFile(self, dataDictionaryName):
       self.__xml.readFromFile(dataDictionaryName)
       nodes = self.__xml.childrenWithName("element")
       for node in nodes:
           element = DataDictionaryElement(node)
           self.__elements[element.tag()] = element
       self.__tags = self.__elements.keys()
       self.__tags.sort()
          
   def debug(self):
       numElements = self.numElements()
       for n in range(0, numElements):
           tag = self.tag(n)  
           element = self.elementByTag(tag)
           if element != None: element.debug()
                      
# -----------------------------------------------------------------------------
# main
# -----------------------------------------------------------------------------
def main():
    progName = sys.argv[0]
    (options, args)=getopt.getopt(sys.argv[1:], "")
    
    try:
       if len(args) != 1:
          print "Usage", progName, "<data_dictionary_xml>"
          sys.exit(1)
          
       # ---
       # Read data dictionary.
       # ---
       dataDictionaryName = sys.argv[1];
       dataDictionary = DataDictionary(dataDictionaryName)
       dataDictionary.debug()

    except Exception, exception:
       traceback.print_exception(sys.exc_info()[0], 
                                 sys.exc_info()[1],
                                 sys.exc_info()[2])
       sys.exit(1)
       
if __name__ == "__main__":
   main()
