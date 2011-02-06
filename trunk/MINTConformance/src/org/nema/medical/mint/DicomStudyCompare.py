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

import base64
import getopt
import glob
import os
import re
import sys
import traceback

from os.path import join
from struct import unpack

from org.nema.medical.mint.DataDictionary import DataDictionary
from org.nema.medical.mint.DicomStudy     import DicomStudy
from org.nema.medical.mint.MintAttribute  import MintAttribute
from org.nema.medical.mint.MintStudy      import MintStudy

# -----------------------------------------------------------------------------
# DicomStudyCompare
# -----------------------------------------------------------------------------
class DicomStudyCompare():
   
   def __init__(self, refDicomStudy, newDicomStudy):
       
       self.__dicom1 = refDicomStudy
       self.__studyInstanceUID = self.__dicom1.studyInstanceUID()
       self.__dicom2 = newDicomStudy
       self.__count = 0
       self.__verbose = False
       self.__binaryTagsCompared = 0
       self.__textTagsCompared = 0
       self.__inlineBinaryTagsCompared = 0
       self.__bytesCompared = 0
       self.__itemsCompared = 0
       self.__excludedTags = 0
       self.__lazy= False
       self.__output = None
       self.__exclude = []

   def setVerbose(self, verbose):
       self.__verbose = verbose
       
   def setLazy(self, lazy):
       self.__lazy = lazy
       
   def setOutput(self, output):
       if output != "": 
          self.__output = open(output, "w")       
       
   def setExclude(self, exclude):
       self.__exclude = exclude

   def compare(self):       
       dicm1 = self.__dicom1
       dicm2 = self.__dicom2
              
       self.__check("Number of instances",
                    dicm1.numInstances(), 
                    dicm2.numInstances())
       
       numInstances = dicm1.numInstances()
       instancesCompared = 0
       for n in range(0, numInstances):
           instance1 = dicm1.instance(n)
           instance2 = dicm2.instanceByUID(instance1.sopInstanceUID())
           self.__compareInstances(instance1, instance2)
           instancesCompared += 1

       # ---
       # Print out stats if verbose.
       # ---       
       if self.__verbose:
          if self.__output == None:
             print "%10d instance(s) compared." % (instancesCompared)
             print "%10d text tag(s) compared." % (self.__textTagsCompared)
             print "%10d items(s) compared." % (self.__itemsCompared)
             print "%10d inline binary tag(s) compared." % (self.__inlineBinaryTagsCompared)
             print "%10d binary tag(s) compared." % (self.__binaryTagsCompared)
             print "%10d byte(s) compared." % (self.__bytesCompared)          
             print "%10d excluded tag(s)." % (self.__excludedTags)
          else:
            self.__output.write("%10d instance(s) compared.\n" % (instancesCompared))
            self.__output.write("%10d text tag(s) compared.\n" % (self.__textTagsCompared))
            self.__output.write("%10d items(s) compared.\n" % (self.__itemsCompared))
            self.__output.write("%10d inline binary tag(s) compared.\n" % (self.__inlineBinaryTagsCompared))
            self.__output.write("%10d binary tag(s) compared.\n" % (self.__binaryTagsCompared))
            self.__output.write("%10d byte(s) compared.\n" % (self.__bytesCompared))
            self.__output.write("%10d excluded tag(s).\n" % (self.__excludedTags))

       # ---
       # Always print differences.
       # ---
       if self.__count != 0:
          if self.__output == None:
             print "%10d difference(s) found." % (self.__count)
          else:
             self.__output.write("%10d difference(s) found.\n" % (self.__count))

       self.__dicom1.tidy()
       self.__dicom2.tidy()
       
       return self.__count

   def __compareInstances(self, instance1, instance2): 

       # ---
       # Check Study Instance ID.
       # ---
       self.__check("Study Instance UID",
                    instance1.studyInstanceUID(), 
                    instance2.studyInstanceUID())
                  
       # ---
       # Check Series Instance ID.
       # ---       
       self.__check("Seriese Instance UID",
                    instance1.seriesInstanceUID(),
                    instance2.seriesInstanceUID(),
                    instance1.seriesInstanceUID())

       # ---
       # Check SOP Instance ID.
       # ---
       self.__check("SOP Instance UID",
                    instance1.sopInstanceUID(),
                    instance2.sopInstanceUID(),
                    instance1.seriesInstanceUID(),
                    instance1.sopInstanceUID())                  

       # ---
       # Check tags.
       # ---
       numAttributes = instance1.numAttributes()       
       for n in range(0, numAttributes):
           tag = instance1.tag(n)
           self.__checkTag(instance1, instance2, tag)
                                  
   def __check(self, msg, obj1, obj2, series="", sop=""):
       if obj1 != obj2:
          self.__count += 1
          print "- Study Instance UID", self.__studyInstanceUID
          if series != "":
             print " - Series Instance UID", series
             if sop != "":
                print "  - SOP Instance UID", sop
          print "+++", msg, ":", obj1, "!=", obj2
       
   def __checkTag(self, instance1, instance2, tag):
   
       # ---
       # Optional and deprecated Group Length tags are not included so we don't need to look for them,
       # except for Group 2 which is a required tag.
       # ---
       if tag[0:4] != "0002" and tag[4:8] == "0000": return
       
       # ---
       # User might want to exclude problem tags.
       # ---
       for exclude in self.__exclude:
           search = re.search(exclude, tag)
           if search != None:
              self.__excludedTags += 1
              return
          
       attr2 = instance2.attributeByTag(tag)
       if attr2 == None:
          self.__check("Data Element", 
                       tag, 
                       "None", 
                       instance1.seriesInstanceUID(), 
                       instance1.sopInstanceUID())
       else:
          attr1 = instance1.attributeByTag(tag)
          self.__checkAttribute(attr1, attr2, instance1.seriesInstanceUID(), instance1.sopInstanceUID())
             
   def __checkAttribute(self, attr1, attr2, seriesInstanceUID, sopInstanceUID):
       
       if attr1.vr() != "":
          self.__check(attr1.tag()+" VR",
                       attr1.vr(),
                       attr2.vr(),
                       seriesInstanceUID, 
                       sopInstanceUID)

       # ---
       # Check binary items and values.
       # ---
       if attr1.isBinary():
          self.__checkBinary(attr1, attr2, seriesInstanceUID, sopInstanceUID)
       else:
          self.__check(attr1.tag()+" Value",
                       attr1.val(),
                       attr2.val(),
                       seriesInstanceUID, 
                       sopInstanceUID)
          self.__textTagsCompared += 1
          
       # ---
       # Check number of items.
       # ---
       numItems1 = attr1.numItems()
       numItems2 = attr2.numItems()
       self.__check(attr1.tag()+" Number of items",
                    numItems1,
                    numItems2)
          
       for i in range(0, numItems1):
          
           # ---
           # Check items.
           # ---
           numAttributes1 = attr1.numItemAttributes(i)
           numAttributes2 = attr2.numItemAttributes(i)
           self.__check(attr1.tag()+" number of item attributes",
                        numAttributes1,
                        numAttributes2)

           # ---
           # Check item attributes.
           # ---
           for j in range(0, numAttributes1):
               itemAttribute1 = attr1.itemAttribute(i, j)
               itemAttribute2 = attr2.itemAttribute(i, j)
               self.__checkAttribute(itemAttribute1, itemAttribute2, seriesInstanceUID, sopInstanceUID)
           
           self.__itemsCompared += 1
           
   def __checkBinary(self, attr1, attr2, seriesInstanceUID, sopInstanceUID):

       if attr1.dat() == "":
          self.__checkInlineBinary(attr1, attr2, seriesInstanceUID, sopInstanceUID)
          return

       if self.__lazy: return
       
       # ---
       # Check for DICOM binary items
       # ---
       dat1 = attr1.dat()
       if not os.access(dat1, os.F_OK):
          self.__count += 1
          print "Binary not found for", attr1.tag(), ":", dat1
          return

       dat2 = attr2.dat()
       if not os.access(dat2, os.F_OK):
          self.__count += 1
          print "Binary not found for", attr2.tag(), ":", dat2
          return

       size1 = os.path.getsize(dat1)
       size2 = os.path.getsize(dat2)
       self.__check(attr1.tag()+" binary sizes differ",
                    size1,
                    size2,
                    seriesInstanceUID, 
                    sopInstanceUID)
       if size1 != size2 : return
       
       # ---
       # Check binary item byte for byte.
       # ---
       bid1 = open(dat1, "rb")
       bid2 = open(dat2, "rb")

       # ---
       # Read in a block.
       # ---
       bufsize = 1024
       block = 0
       buf1 = bid1.read(bufsize)
       buf2 = bid2.read(bufsize)
          
       bytes1 = unpack('B'*len(buf1), buf1)
       bytes2 = unpack('B'*len(buf2), buf2)
       n = len(bytes1)
       while n > 0:        

          # ---
          # Loop through block.
          # ---
          diff = False
          for i in range(0, n):              
              if bytes1[i] != bytes2[i]:
                 self.__check("byte "+str(block*bufsize+i),
                              hex(bytes1[i]),
                              hex(bytes2[i]),
                              seriesInstanceUID, 
                              sopInstanceUID)
                 diff = True
                 break
                    
              self.__bytesCompared += 1

          # ---
          # Skip to end if difference was found.
          # ---
          if diff:
             n = -1
          else:
             buf1 = bid1.read(bufsize)
             buf2 = bid2.read(len(buf1))

             bytes1 = unpack('B'*len(buf1), buf1)
             bytes2 = unpack('B'*len(buf2), buf2)
             n = len(bytes1)
             block += 1
             
       bid1.close()
       bid2.close()
       self.__binaryTagsCompared += 1
 
   def __checkInlineBinary(self, attr1, attr2, seriesInstanceUID, sopInstanceUID):
 
       self.__check(attr1.tag()+" <Binary Data>",
                    attr1.val(),
                    attr2.val(),
                    seriesInstanceUID, 
                    sopInstanceUID)
 
       self.__inlineBinaryTagsCompared += 1

# -----------------------------------------------------------------------------
# main
# -----------------------------------------------------------------------------
def main():
   
    # ---
    # Get options.
    # ---
    progName = os.path.basename(sys.argv[0])
    (options, args)=getopt.getopt(sys.argv[1:], "d:o:x:vlh")

    # ---
    # Check for data dictionary.
    # ---
    dictionaryURL = DataDictionary.DCM4CHE_URL
    for opt in options:
        if opt[0] == "-d":
           dictionaryURL = opt[1]
           
    # ---
    # Check for output option.
    # ---
    output = ""
    for opt in options:
        if opt[0] == "-o":
           output = opt[1]

    # ---
    # Check for exclude option.
    # ---
    exclude = []
    for opt in options:
        if opt[0] == "-x":
           patterns = opt[1].replace('n', '.')
           exclude = patterns.split(',')

    # ---
    # Check for verbose option.
    # ---
    verbose = False
    for opt in options:
        if opt[0] == "-v":
           verbose = True
           
    # ---
    # Check for lazy option.
    # ---
    lazy = False
    for opt in options:
        if opt[0] == "-l":
           lazy = True
           
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
          print "Usage:", progName, "[options] <ref_dicom_study_dir> <new_dicom_study_dir>"
          print "  -d <data_dictionary_url>: defaults to DCM4CHE"
          print "  -o <output>:              output filename (defaults to stdout)"
          print "  -x <exclude>:             list of tags to exclude, ie. \"08590030,600001nn\""
          print "  -v:                       verbose"
          print "  -l:                       lazy check (skips binary content)"
          print "  -h:                       displays usage"
          sys.exit(1)
          
       # ---
       # Read MINT metadata.
       # ---
       refDicomStudyDir = args[0];
       newDicomStudyDir = args[1];
     
       refDicomStudy = DicomStudy(refDicomStudyDir, dictionaryURL)
       newDicomStudy = DicomStudy(newDicomStudyDir, dictionaryURL)
     
       studies = DicomStudyCompare(refDicomStudy, newDicomStudy)
       studies.setVerbose(verbose)
       studies.setLazy(lazy)
       studies.setOutput(output)
       studies.setExclude(exclude)

       differences = studies.compare()
       if differences != 0:
          raise AssertionError("Bad compare: "+str(differences)+" difference(s).")
       return 0
       
    except AssertionError, exception:
       print exception
       sys.exit(2)
    
    except Exception, exception:
       traceback.print_exception(sys.exc_info()[0], 
                                 sys.exc_info()[1],
                                 sys.exc_info()[2])
       sys.exit(3)
       
if __name__ == "__main__":
   main()
   