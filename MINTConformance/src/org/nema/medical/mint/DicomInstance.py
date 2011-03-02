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

from org.nema.medical.mint.DataDictionary import DataDictionary
from org.nema.medical.mint.DicomAttribute import DicomAttribute
from org.nema.medical.mint.DicomTransfer  import DicomTransfer

STUDY_INSTANCE_UID_TAG  = "0020000d"
SERIES_INSTANCE_UID_TAG = "0020000e" 
SOP_INSTANCE_UID_TAG    = "00080018" 

# -----------------------------------------------------------------------------
# DicomInstance
# -----------------------------------------------------------------------------
class DicomInstance():
   def __init__(self, dcmName, dataDictionary):
     
       self.__dcmName = dcmName
       self.__attributes = {}
       self.__tags = []
       self.__transferSyntax = DicomTransfer()
       self.__dataDictionary = dataDictionary
       
       self.__open()

   def tidy(self):
       """
       Removes tempory binary items.
       """
       numAttributes = self.numAttributes()
       for n in range(0, numAttributes): self.attribute(n).tidy()

   def isDicom(filename):
       dcm = open(filename, "rb")
       preamble=dcm.read(128)
       dicm=dcm.read(4)
       return (dicm == "DICM")
   isDicom = staticmethod(isDicom)

   def studyInstanceUID(self):
       attr = self.attributeByTag(STUDY_INSTANCE_UID_TAG)
       return attr.val()
       
   def seriesInstanceUID(self):
       attr = self.attributeByTag(SERIES_INSTANCE_UID_TAG)
       return attr.val()

   def sopInstanceUID(self):
       attr = self.attributeByTag(SOP_INSTANCE_UID_TAG)
       return attr.val()
                        
   def numAttributes(self):
       return len(self.__tags)
       
   def tag(self, n):
       return self.__tags[n]
                        
   def attribute(self, n):
       """
       Returns a MintAttribute at index n.
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
       if output == None:
          print ">> Instance", self.sopInstanceUID()
       else:
          output.write(">> Instance "+self.sopInstanceUID()+"\n")       
       numAttributes = self.numAttributes()
       for n in range(0, numAttributes):
           tag = self.tag(n)
           attr = self.attributeByTag(tag)
           attr.debug(output)

   def __open(self):
       dcm = open(self.__dcmName, "rb")
       
       # ---
       # Read the preamble
       # ---
       preamble=dcm.read(128)
       dicm=dcm.read(4)
       assert(dicm == "DICM")
       
       # ---
       # Read data elements
       # ---
       attr = DicomAttribute(dcm, self.__dataDictionary, self.__transferSyntax)
       while attr.isValid():
          self.__attributes[attr.tag()] = attr
          if attr.isTransferSyntax(): self.__transferSyntax = DicomTransfer(attr.val()) 
          attr = DicomAttribute(dcm, self.__dataDictionary, self.__transferSyntax)

       dcm.close()
       self.__tags = self.__attributes.keys()
       self.__tags.sort()
          
   def __attr(self, tag, index):
       if self.__attrs.has_key(tag):
          attrs = self.__attrs[tag]
          return attrs[index]
       else:
          None
       
# -----------------------------------------------------------------------------
# main
# -----------------------------------------------------------------------------
def main():
    progName = os.path.basename(sys.argv[0])
    (options, args)=getopt.getopt(sys.argv[1:], "h")
    
    # ---
    # Check for help option.
    # ---
    help = False
    for opt in options:
        if opt[0] == "-h":
           help = True
    
    try:
       if len(args) != 2 or help:
          print "Usage", progName, "[options] <dicom_file> <data_dictionary_url>"
          print "  -h: displays usage"
          sys.exit(1)
          
       # ---
       # Read dicom.
       # ---
       dcmName = args[0];
       dataDictionaryUrl = args[1];
       dataDictionary = DataDictionary(dataDictionaryUrl)
       instance = DicomInstance(dcmName, dataDictionary)
       instance.debug()
       instance.tidy()
                        
    except Exception, exception:
       traceback.print_exception(sys.exc_info()[0], 
                                 sys.exc_info()[1],
                                 sys.exc_info()[2])
       sys.exit(1)
       
if __name__ == "__main__":
   main()
