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
import urllib

from struct import unpack

from org.nema.medical.mint.MintAttribute import MintAttribute
from org.nema.medical.mint.MintSeries    import MintSeries
from org.nema.medical.mint.XmlDocument   import XmlDocument
from org.nema.medical.mint.XmlNode       import XmlNode

# -----------------------------------------------------------------------------
# MintStudy
# -----------------------------------------------------------------------------
class MintStudy():
   
   ROOT_TAG_NAME = "StudyMeta"
   
   def __init__(self, hostname, port, uuid):
       """
       Parses a MINT Study XML document.
       """
       self.__xml = XmlDocument(MintStudy.ROOT_TAG_NAME)
       self.__xmlns = ""
       self.__studyInstanceUID = ""
       self.__type = ""
       self.__version = ""
       self.__instanceCount = ""       
       self.__attributes = {}
       self.__tags = []
       self.__series = {}
       self.__seriesInstanceUIDs = []
       self.__url = "http://"+hostname+":"+port+"/MINTServer/studies/"+uuid+"/DICOM"
              
       self.__readFromURL(self.__url+"/metadata")

   def xmlns(self): 
       return self.__xmlns
       
   def studyInstanceUID(self): 
       return self.__studyInstanceUID
       
   def type(self): 
       return self.__type
       
   def version(self): 
       return self.__version
       
   def instanceCount(self): 
       return self.__instanceCount
       
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
          
   def numInstances(self):
       numInstances = 0
       numSeries = self.numSeries()
       for n in range(0, numSeries):
           series = self.series(n)
           numInstances += series.numInstances()           
       return numInstances

   def instanceByUID(self, uid):
       """
       Returns a MintInstance if UID is found, otherwise None.
       """
       instance = None
       numSeries = self.numSeries()
       for n in range(0, numSeries):
           series = self.series(n)
           instance = series.instanceByUID(uid)
           if instance != None:
              return instance
       return instance
          
   def find(self, tag, seriesInstanceUID="", sopInstanceUID=""):
       attr = self.attributeByTag(tag)
       if attr == None:
          series = self.seriesByUID(seriesInstanceUID)
          if series != None:
             attr = series.find(tag, sopInstanceUID)
       return attr
       
   def open(self, bid):
       assert bid != ""
       return urllib.urlopen(self.__url+"/binaryitems/"+str(bid))
       
   def bytes(self, bid, length):
       f = self.open(bid)
       buf = f.read(length)
       f.close()
       return unpack('B'*len(buf), buf)
       
   def debug(self, indent=""):
       print "- studyMeta xmlns", self.xmlns(), "studyInstanceUID", self.__studyInstanceUID, "type", self.__type, "version", self.__version, "instanceCount", self.__instanceCount
       indent += " "
       print indent+"- attributes"
       indent += " "
       numAttributes = self.numAttributes()
       for n in range(0, numAttributes):
           self.attribute(n).debug(indent)
       indent = indent[0:-1]
       print indent+"- attributes"
             
       print indent+"- seriesList"
       indent += " "
       numSeries = self.numSeries()
       for n in range(0, numSeries):
           self.series(n).debug(indent)
       indent = indent[0:-1]
       print indent+"- seriesList"

       indent = indent[0:-1]
       print indent+"- studyMeta"
       
   def __readFromURL(self, metadataURL):
       self.__xml.readFromURL(metadataURL)
      
       self.__xmlns = self.__xml.attributeWithName("xmlns")
       self.__studyInstanceUID = self.__xml.attributeWithName("studyInstanceUID")
       self.__type = self.__xml.attributeWithName("type")
       self.__version = self.__xml.attributeWithName("version")
       self.__instanceCount = self.__xml.attributeWithName("instanceCount")
     
       # ---
       # Read Attributes
       # ---
       node = self.__xml.childWithName("attributes")
       if node != None:
          nodes = node.childrenWithName("attr")
          for node in nodes:
              attb = MintAttribute(node)
              self.__attributes[attb.tag()] = attb
          self.__tags = self.__attributes.keys()
          self.__tags.sort()
   
       # ---
       # Read Series
       # ---
       node = self.__xml.childWithName("seriesList")
       if node != None:
          nodes = node.childrenWithName("series")
          for node in nodes:
              series = MintSeries(node)
              self.__series[series.seriesInstanceUID()] = series
          self.__seriesInstanceUIDs = self.__series.keys()
          self.__seriesInstanceUIDs.sort()

# -----------------------------------------------------------------------------
# main
# -----------------------------------------------------------------------------
def main():
    progName = sys.argv[0]
    (options, args)=getopt.getopt(sys.argv[1:], "p:h")
    
    # ---
    # Check for port option.
    # ---
    port = "8080"
    for opt in options:
        if opt[0] == "-p":
           port = opt[1]
           
    # ---
    # Check for help option.
    # ---
    help = False
    for opt in options:
	if opt[0] == "-h":
	   help = True
	              
    try:
       # ---
       # Check usage.
       # ---
       argc = len(args)
       if help or argc < 2:
	  print "Usage:", progName, "[options] <hostname> <mint_study_uuid>"
	  print "  -p <port>: defaults to 8080"
	  print "  -h:        displays usage"
	  sys.exit(1)

       hostname = args[0]
       uuid     = args[1]

       # ---
       # Read MINT metadata.
       # ---
       mintStudy = MintStudy(hostname, port, uuid)
       mintStudy.debug()
       
    except Exception, exception:
       traceback.print_exception(sys.exc_info()[0], 
                                 sys.exc_info()[1],
                                 sys.exc_info()[2])
       sys.exit(1)
       
if __name__ == "__main__":
   main()