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
import sys
import traceback

from org.nema.medical.mint.MintAttribute import MintAttribute
from org.nema.medical.mint.MintSeries    import MintSeries
from org.nema.medical.mint.XmlDocument   import XmlDocument
from org.nema.medical.mint.XmlNode       import XmlNode

# -----------------------------------------------------------------------------
# MintStudy
# -----------------------------------------------------------------------------
class MintStudy(): #XmlDocument):
   
   ROOT_TAG_NAME = "StudyMeta"
   
   def __init__(self, metadataName):
       """
       Parses a MINT Study XML document.
       """
       #XmlDocument.__init__(self, MintStudy.ROOT_TAG_NAME)
       #self.readFromFile(metadataName)
       self.__xml = XmlDocument(MintStudy.ROOT_TAG_NAME)
       self.__attributes = {}
       self.__tags = []
       self.__series = {}
       self.__seriesInstanceUIDs = []
       self.__readFromFile(metadataName)

   def studyInstanceUID(self): 
       return self.__studyInstanceUID;
       
   def numAttributes(self):
       return len(self.__tags)
          
   def attribute(self, n):
       """
       Returns a MintAttribute at index n.
       """
       tag = self.__tags[n]
       return self.__attributes[tag]

   def attributeByTag(self, tag):
       """
       Returns a MintAttribute if tag is found, otherwise None.
       """
       if self.__attributes.has_key(tag):
          return self.__attributes[tag]
       else:
          return None

   def numSeries(self):
       return len(self.__seriesInstanceUIDs)
          
   def series(self, n):
       """
       Returns a MintSeries at index n.
       """
       uid = self.__seriesInstanceUIDs[n]
       return self.__series[uid]

   def seriesByUID(self, uid):
       """
       Returns a MintSeries if UID is found, otherwise None.
       """
       if self.__series.has_key(uid):
          return self.__series[uid]
       else:
          return None

   def __readFromFile(self, metadataName):      
       self.__xml.readFromFile(metadataName)
      
       self.__studyInstanceUID = self.__xml.attributeWithName("studyInstanceUID")
     
       # ---
       # Read Attributes
       # ---
       node = self.__xml.childWithName("Attributes")
       nodes = node.childrenWithName("Attr")
       for node in nodes:
           attb = MintAttribute(node)
           self.__attributes[attb.tag()] = attb
       self.__tags = self.__attributes.keys()
       self.__tags.sort()
   
       # ---
       # Read Series
       # ---
       node = self.__xml.childWithName("SeriesList")
       nodes = node.childrenWithName("Series")
       for node in nodes:
           series = MintSeries(node)
           self.__series[series.seriesInstanceUID()] = series
       self.__seriesInstanceUIDs = self.__series.keys()
       self.__seriesInstanceUIDs.sort()
       
   def __str__(self):
       s =  "- Study Instance UID="+self.__studyInstanceUID+'\n'
       s += " - Attributes\n"
       
       numAttributes = self.numAttributes()
       for n in range(0, numAttributes):
           s += "    "+self.attribute(n).toString()+'\n'
              
       s += " - Series List\n"

       numSeries = self.numSeries()
       for n in range(0, numSeries):
           s += self.series(n).toString()

       return s
       
# -----------------------------------------------------------------------------
# main
# -----------------------------------------------------------------------------
def main():
    progName = sys.argv[0]
    (options, args)=getopt.getopt(sys.argv[1:], "")
    
    try:
       if len(args) != 1:
          print "Usage", progName, "<mint_metadata_xml>"
          sys.exit(1)
          
       # ---
       # Read MINT metadata.
       # ---
       metadataName = sys.argv[1];
       mintStudy = MintStudy(metadataName)
       print mintStudy
       
    except Exception, exception:
       traceback.print_exception(sys.exc_info()[0], 
                                 sys.exc_info()[1],
                                 sys.exc_info()[2])
       sys.exit(1)
       
if __name__ == "__main__":
   main()
