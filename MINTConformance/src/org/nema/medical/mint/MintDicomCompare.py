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

from os.path import join
from struct import unpack

from org.nema.medical.mint.DicomStudy    import DicomStudy
from org.nema.medical.mint.MintAttribute import MintAttribute
from org.nema.medical.mint.MintStudy     import MintStudy

# -----------------------------------------------------------------------------
# MintDicomCompare
# -----------------------------------------------------------------------------
class MintDicomCompare():
   
   def __init__(self, dicomStudyDir, mintStudyXml):
       if not os.path.isdir(dicomStudyDir):
          print "Directory not found -", dicomStudyDir
          sys.exit(1)

       if not os.path.isfile(mintStudyXml):
          print "File not found -", mintStudyXml
          sys.exit(1)
       
       self.__dicom = DicomStudy(dicomStudyDir)
       self.__studyInstanceUID = self.__dicom.studyInstanceUID()
       self.__mint = MintStudy(mintStudyXml)
       self.__binary = os.path.join(os.path.dirname(mintStudyXml), "binaryitems")
       self.__binaryitems = []
       self.count = 0

   def compare(self):       
       dicm = self.__dicom
       mint = self.__mint
              
       self.__check("Number of instances",
                    dicm.numInstances(), 
                    mint.numInstances())
       
       numInstances = dicm.numInstances()
       for n in range(0, numInstances):
           instance = dicm.instances(n)
           self.__compareInstances(instance, mint)
           
       if self.count != 0:
          print self.count, "difference(s) found."

   def __compareInstances(self, instance, mint): 

       # ---
       # Check Study Instance ID.
       # ---
       self.__check("UI",
                    instance.studyInstanceUID(), 
                    mint.studyInstanceUID())
                  
       # ---
       # Check Series Instance ID.
       # ---
       mintSeriesInstanceUID = "None"
       mintSeries = mint.seriesByUID(instance.seriesInstanceUID())
       if mintSeries != None:
          mintSeriesInstanceUID = mintSeries.seriesInstanceUID()
       
       self.__check("UI",
                    instance.seriesInstanceUID(),
                    mintSeriesInstanceUID,
                    instance.seriesInstanceUID())

       # ---
       # Check SOP Instance ID.
       # ---
       mintSopInstanceUID = "None"
       mintInstance = mint.instanceByUID(instance.sopInstanceUID())
       if mintInstance != None:
          mintSopInstanceUID = mintInstance.sopInstanceUID()
       
       self.__check("UI",
                    instance.sopInstanceUID(),
                    mintSopInstanceUID,
                    instance.seriesInstanceUID(),
                    instance.sopInstanceUID())                  

       # ---
       # Check tags.
       # ---
       numTags = instance.numTags()
       for n in range(0, numTags):
           tag = instance.tag(n)
           if not instance.isPrivateHeader(tag):
              self.__checkTag(instance, mint, tag)
                                  
   def __check(self, msg, obj1, obj2, series="", sop=""):
       if obj1 != obj2:
          self.count += 1
          print "- Study Instance UID", self.__studyInstanceUID
          if series != "":
             print " - Series Instance UID", series
             if sop != "":
                print "  - SOP Instance UID", sop
          print "+++", msg, ":", obj1, "!=", obj2
       
   def __checkTag(self, instance, mint, tag):
       attr = mint.find(tag, instance.seriesInstanceUID(), instance.sopInstanceUID())
       if attr == None:
          self.__check("Data Element", 
                       tag, 
                       "None", 
                       instance.seriesInstanceUID(), 
                       instance.sopInstanceUID())
       else:
          val = instance.value(tag)
          
          if not instance.isImplicit(tag):
             self.__check(tag+"VR",
                          instance.vr(tag),
                          attr.vr(),
                          instance.seriesInstanceUID(), 
                          instance.sopInstanceUID())
                            
          if instance.isBinary(tag):
             self.__checkBinary(instance, mint, tag, attr)
          else:
             self.__check(tag+" Value",
                          instance.value(tag),
                          attr.val(),
                          instance.seriesInstanceUID(), 
                          instance.sopInstanceUID())

   def __checkBinary(self, instance, mint, tag, attr):

       binaryitem = attr.bid()
       
       # ---
       # Check for MINT binary item.
       # ---
       dat = os.path.join(self.__binary, binaryitem+".dat")
       if not os.access(dat, os.F_OK):
          self.count += 1
          print "File not found", ":", dat
          pass
           
       # ---
       # Check binary item sizes.
       # ---
       size1 = instance.length(tag)
       size2 = os.path.getsize(dat)
       self.__check(tag+" "+binaryitem+".dat size",
                    size1,
                    size2,
                    instance.seriesInstanceUID(), 
                    instance.sopInstanceUID())

       # ---
       # Check binary item byte for byte.
       # ---
       if size1 == size2:
          bid = open(dat, "rb")
          val = instance.value(tag)
          assert size1 == len(val) # These better be equal

          # ---
          # Read in a block.
          # ---
          bufsize = 1024
          block = 0
          buf = bid.read(bufsize)
          bytes = unpack('B'*len(buf), buf)
          n = len(bytes)
          bytesCompared = 0
          while n > 0:          

             # ---
             # Loop through block.
             # ---
             diff = False
             for i in range(0, n):
                 self.__check(binaryitem+".dat byte "+str(block*bufsize+i),
                              val[bytesCompared],
                              bytes[i],
                              instance.seriesInstanceUID(), 
                              instance.sopInstanceUID())
                 
                 if val[bytesCompared] != bytes[i]:
                    diff = True
                    break
                    
                 bytesCompared += 1

             # ---
             # Skip to end if difference was found.
             # ---
             if diff:
                n = -1
             else:
                buf = bid.read(bufsize)
                bytes = unpack('B'*len(buf), buf)
                n = len(bytes)
                block += 1
             
          bid.close()
 
# -----------------------------------------------------------------------------
# main
# -----------------------------------------------------------------------------
def main():
    progName = sys.argv[0]
    (options, args)=getopt.getopt(sys.argv[1:], "")
    
    try:
       if len(args) != 2:
          print "Usage", progName, "<dicom_study_dir> <mint_study.xml>"
          sys.exit(1)
          
       # ---
       # Read MINT metadata.
       # ---
       dicomStudyDir = sys.argv[1];
       mintStudyXml = sys.argv[2];
       studies = MintDicomCompare(dicomStudyDir, mintStudyXml)
       studies.compare()
       
    except Exception, exception:
       traceback.print_exception(sys.exc_info()[0], 
                                 sys.exc_info()[1],
                                 sys.exc_info()[2])
       sys.exit(1)
       
if __name__ == "__main__":
   main()
