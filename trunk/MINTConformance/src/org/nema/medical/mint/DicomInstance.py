#!/usr/bin/python
# -----------------------------------------------------------------------------
# $Id$
#
# Copyright (C) 2010-2012 MINT Working group. All rights reserved.
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

from org.nema.medical.mint.DCM4CHE_Dictionary import DCM4CHE_Dictionary
from org.nema.medical.mint.DicomHeader        import DicomHeader
from org.nema.medical.mint.DicomAttribute     import DicomAttribute
from org.nema.medical.mint.DicomTransfer      import DicomTransfer

STUDY_INSTANCE_UID_TAG  = "0020000d"
SERIES_INSTANCE_UID_TAG = "0020000e" 
SOP_INSTANCE_UID_TAG    = "00080018" 

# -----------------------------------------------------------------------------
# DicomInstance
# -----------------------------------------------------------------------------
class DicomInstance():
   def __init__(self, dcmName, dataDictionary, skipPrivate=False):

       DicomAttribute.setSkipPrivate(skipPrivate)
       
       self.__dcmName = dcmName
       self.__attributes = {}
       self.__tags = []
       self.__dataDictionary = dataDictionary
       self.__header = DicomHeader(dataDictionary)

       self.__open()

   def studyInstanceUID(self):
       attr = self.attributeByTag(STUDY_INSTANCE_UID_TAG)
       if attr != None:
          return attr.val()
       else:
          return ""
       
   def seriesInstanceUID(self):
       attr = self.attributeByTag(SERIES_INSTANCE_UID_TAG)
       if attr != None:
          return attr.val()
       else:
          return ""

   def sopInstanceUID(self):
       attr = self.attributeByTag(SOP_INSTANCE_UID_TAG)
       if attr != None:
          return attr.val()
       else:
          return ""

   def header(self):
       return self.__header
                        
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
       if output == None:
          print ">>> Instance", self.sopInstanceUID()
       else:
          output.write(">>> Instance "+self.sopInstanceUID()+"\n")
       self.__header.debug(output)
       numAttributes = self.numAttributes()
       for n in range(0, numAttributes):
           tag = self.tag(n)
           attr = self.attributeByTag(tag)
           attr.debug(output)

   def __open(self):
       dcm = open(self.__dcmName, "rb")

       self.__header.read(dcm)
       transferSyntax = self.__header.transferSyntax()
       
       # ---
       # Read data elements
       # ---
       attr = DicomAttribute(dcm, self.__dataDictionary, transferSyntax)
       while attr.isValid():
          self.__attributes[attr.tag()] = attr
          attr = DicomAttribute(dcm, self.__dataDictionary, transferSyntax)

       dcm.close()
       self.__tags = self.__attributes.keys()
       self.__tags.sort()
       
# -----------------------------------------------------------------------------
# main
# -----------------------------------------------------------------------------
def main():
    progName = os.path.basename(sys.argv[0])
    (options, args)=getopt.getopt(sys.argv[1:], "hp")
    
    # ---
    # Check for help option.
    # ---
    help = False
    skipPrivate = False
    for opt in options:
        if opt[0] == "-h":
           help = True
        if opt[0] == "-p":
           skipPrivate = True
    
    try:
       if len(args) < 1 or help:
          print "Usage", progName, "[options] <dicom_file>"
          print "  -h: displays usage"
          print "  -p: skip private tags"
          sys.exit(1)
          
       # ---
       # Read dicom.
       # ---
       dcmName = args[0];
       dataDictionary = DCM4CHE_Dictionary()
       instance = DicomInstance(dcmName, dataDictionary, skipPrivate)
       instance.debug()
                        
    except Exception, exception:
       traceback.print_exception(sys.exc_info()[0], 
                                 sys.exc_info()[1],
                                 sys.exc_info()[2])
       sys.exit(1)
       
if __name__ == "__main__":
   main()
