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

from org.nema.medical.mint.DicomAttribute import DicomAttribute

STUDY_INSTANCE_UID_TAG  = "0020000d"
SERIES_INSTANCE_UID_TAG = "0020000e" 
SOP_INSTANCE_UID_TAG    = "00080018" 

# -----------------------------------------------------------------------------
# DicomInstance
# -----------------------------------------------------------------------------
class DicomInstance():
   def __init__(self, dcmName, transferSyntax=""):
     
       self.__dcmName = dcmName
       self.__attributes = {}
       self.__tags = []
       self.__explicit = False
       self.__endian = "<" # Default little endian
       self.__transferSyntaxOverride = False
       self.__setTransferSyntax(transferSyntax)
       
       self.__open()
       
   def studyInstanceUID(self):
       attb = self.attributeByTag(STUDY_INSTANCE_UID_TAG)
       return attb.val()
       
   def seriesInstanceUID(self):
       attb = self.attributeByTag(SERIES_INSTANCE_UID_TAG)
       return attb.val()

   def sopInstanceUID(self):
       attb = self.attributeByTag(SOP_INSTANCE_UID_TAG)
       return attb.val()
       
   def isExplicit(self):
       return self.__explicit
                        
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
   
   def _print(self):
       numAttributes = self.numAttributes()
       for n in range(0, numAttributes):
           tag = self.tag(n)
           attb = self.attributeByTag(tag)
           print attb
           
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
       attb = DicomAttribute(dcm, self.__explicit, self.__endian)
       while attb.isValid():
          self.__attributes[attb.tag()] = attb
          
          # ---
          # Check for transfer syntax.
          # ---
          if attb.isTransferSyntax() and not self.__transferSyntaxOverride:
             self.__setTransferSyntax(attb.val())
                
          attb = DicomAttribute(dcm, self.__explicit, self.__endian)

       dcm.close()
       self.__tags = self.__attributes.keys()
       self.__tags.sort()
   
   def __setTransferSyntax(self, transferSyntax):
       if transferSyntax == "":
          return
          
       if transferSyntax == DicomAttribute.IMPLICIT_VR_LITTLE_ENDIAN:
          self.__explicit = False
          self.__endian   = "<"
       elif transferSyntax == DicomAttribute.EXPLICIT_VR_LITTLE_ENDIAN:
          self.__explicit = True    
          self.__endian   = "<" 
       elif transferSyntax == DicomAttribute.EXPLICIT_VR_BIG_ENDIAN:
          self.__explicit = True    
          self.__endian   = ">"
       self.__transferSyntaxOverride = True
   
   def __attb(self, tag, index):
       if self.__attbs.has_key(tag):
          attbs = self.__attbs[tag]
          return attbs[index]
       else:
          None
       
# -----------------------------------------------------------------------------
# main
# -----------------------------------------------------------------------------
def main():
    progName = os.path.basename(sys.argv[0])
    (options, args)=getopt.getopt(sys.argv[1:], "iebh")
    
    # ---
    # Check for transfer syntax.
    # ---
    transferSyntax = ""
    for opt in options:
        if opt[0] == "-i":
           transferSyntax = DicomAttribute.IMPLICIT_VR_LITTLE_ENDIAN
    for opt in options:
        if opt[0] == "-e":
           transferSyntax = DicomAttribute.EXPLICIT_VR_LITTLE_ENDIAN
    for opt in options:
        if opt[0] == "-b":
           transferSyntax = DicomAttribute.EXPLICIT_VR_BIG_ENDIAN
           
    # ---
    # Check for help option.
    # ---
    help = False
    for opt in options:
        if opt[0] == "-h":
           help = True
    
    try:
       if len(args) != 1 or help:
          print "Usage", progName, "[options] <dicom_file>"
          print "  -i: implicit VR little endian"
          print "  -e: explicit VR little endian"
          print "  -b: explicit VR big endian"
          print "  -h: displays usage"
          sys.exit(1)
          
       # ---
       # Read dicom.
       # ---
       dcmName = args[0];
       instance = DicomInstance(dcmName, transferSyntax)
       instance._print()
                        
    except Exception, exception:
       traceback.print_exception(sys.exc_info()[0], 
                                 sys.exc_info()[1],
                                 sys.exc_info()[2])
       sys.exit(1)
       
if __name__ == "__main__":
   main()

