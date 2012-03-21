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

import base64
import getopt
import glob
import os
import re
import sys
import traceback

from os.path import join
from struct import unpack

from org.nema.medical.mint.DCM4CHE_Dictionary import DCM4CHE_Dictionary
from org.nema.medical.mint.DicomStudy         import DicomStudy
from org.nema.medical.mint.DicomSeries        import DicomSeries
from org.nema.medical.mint.DicomInstance      import DicomInstance
from org.nema.medical.mint.DicomTransfer      import DicomTransfer

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
       self.__seriesCompared = 0
       self.__instancesCompared = 0
       self.__textTagsCompared = 0
       self.__sequencesCompared = 0
       self.__itemsCompared = 0
       self.__inlineBinaryCompared = 0
       self.__binaryItemsCompared = 0
       self.__bytesCompared = 0
       self.__excludedTags = 0
       self.__warningCount = 0
       self.__lazy= False
       self.__warnings=False
       self.__output = None
       self.__exclude = []

   def tidy(self):
       if self.__output != None: self.__output.close()

   def setVerbose(self, verbose):
       self.__verbose = verbose
       
   def setLazy(self, lazy):
       self.__lazy = lazy
       
   def setOutput(self, output):
       if output == "": return
       if self.__output != None: self.__output.close()
       if os.access(output, os.F_OK):
          raise IOError("File already exists - "+output)
       self.__output = open(output, "w")
       
   def setExclude(self, exclude):
       self.__exclude = exclude

   def setWarnings(self, warnings):
       self.__warnings = warnings

   def compare(self):       
       dicm1 = self.__dicom1
       dicm2 = self.__dicom2

       seriesCompared = 0
       instancesCompared = 0
                     
       self.__check("Number of series",
                    dicm1.numSeries(), 
                    dicm2.numSeries())
       
       numSeries = min(dicm1.numSeries(), dicm2.numSeries()) 
       for n in range(0, numSeries):
       
           series1 = dicm1.series(n)
           series2 = dicm2.series(n)
           
           self.__check("Number of instances",
                        series1.numInstances(), 
                        series2.numInstances(),
                        series1.seriesInstanceUID())

           numInstances = min(series1.numInstances(), series2.numInstances()) 
           for m in range(0, numInstances):
               instance1 = series1.instance(m)
               instance2 = series2.instanceByUID(instance1.sopInstanceUID())
               self.__compareInstances(instance1, instance2)
               self.__instancesCompared += 1
           
           self.__seriesCompared += 1
       
       # ---
       # Print out stats if verbose.
       # ---       
       if self.__verbose:
          if self.__output == None:
             print "%10d series compared." % (self.__seriesCompared)
             print "%10d instance(s) compared." % (self.__instancesCompared)
             print "%10d text tags(s) compared." % (self.__textTagsCompared) 
             print "%10d sequence(s) compared." % (self.__sequencesCompared)
             print "%10d item(s) compared." % (self.__itemsCompared)
             print "%10d inline binary item(s) compared." % (self.__inlineBinaryCompared)
             print "%10d binary item(s) compared." % (self.__binaryItemsCompared)
             print "%10d byte(s) compared." % (self.__bytesCompared)             
             print "%10d excluded tag(s)." % (self.__excludedTags)
          else:
             self.__output.write("%10d series compared.\n" % (self.__seriesCompared))
             self.__output.write("%10d instance(s) compared.\n" % (self.__instancesCompared))
             self.__output.write("%10d text tag(s) compared.\n" % (self.__textTagsCompared))
             self.__output.write("%10d sequence(s) compared.\n" % (self.__sequencesCompared))
             self.__output.write("%10d items(s) compared.\n" % (self.__itemsCompared))
             self.__output.write("%10d inline binary item(s) compared.\n" % (self.__inlineBinaryCompared))
             self.__output.write("%10d binary item(s) compared.\n" % (self.__binaryItemsCompared))
             self.__output.write("%10d byte(s) compared.\n" % (self.__bytesCompared))
             self.__output.write("%10d excluded tag(s).\n" % (self.__excludedTags))

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
       self.__check("Series Instance UID",
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
       # Check header size.
       # ---
       header1 = instance1.header()
       header2 = instance2.header()
       self.__check("Number of header tags",
                    header1.numAttributes(),
                    header2.numAttributes(),
                    instance1.seriesInstanceUID(),
                    instance1.sopInstanceUID())
       
       # ---
       # Check header elements.
       # ---
       numAttributes = min(header1.numAttributes(), header2.numAttributes()) 
       for n in range(0, numAttributes):
           tag = header1.tag(n)
           self.__checkTag(header1,
                           header2,
                           tag,
                           instance1.seriesInstanceUID(),
                           instance1.sopInstanceUID())
           
       # ---
       # Check data elements.
       # ---
       numAttributes = instance1.numAttributes()       
       for n in range(0, numAttributes):
           tag = instance1.tag(n)
           self.__checkTag(instance1,
                           instance2,
                           tag,
                           instance1.seriesInstanceUID(),
                           instance1.sopInstanceUID())
                                  
   def __check(self, msg, obj1, obj2, series="", sop=""):
       if obj1 != obj2:
          self.__count += 1
          print "- Study Instance UID", self.__studyInstanceUID
          if series != "":
             print " - Series Instance UID", series
             if sop != "":
                print "  - SOP Instance UID", sop
          print "ERROR:", msg, ":", obj1, "!=", obj2
       
   def __warn(self, msg, obj1, obj2, series="", sop=""):
       if obj1 != obj2 and self.__warnings:
          self.__warningCount += 1
          print "- Study Instance UID", self.__studyInstanceUID
          if series != "":
             print " - Series Instance UID", series
             if sop != "":
                print "  - SOP Instance UID", sop
          print "WARNING:", msg, ":", obj1, "!=", obj2
       
   def __exception(self, msg, exception, series="", sop=""):
       self.__count += 1
       print "- Study Instance UID", self.__studyInstanceUID
       if series != "":
          print " - Series Instance UID", series
          if sop != "":
             print "  - SOP Instance UID", sop
       print "EXCEPTION:", msg, exception
       
   def __checkTag(self, obj1, obj2, tag, series, sop):
   
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
          
       attr2 = obj2.attributeByTag(tag)
       if attr2 == None:
          self.__check("Data Element", 
                       tag, 
                       "None", 
                       series, 
                       sop)
       else:
          attr1 = obj1.attributeByTag(tag)
          self.__checkAttribute(attr1, attr2, series, sop)
             
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
          
          # ---
          # Remove trailing characters before comparing.
          # ---
          val1 = attr1.val()
          val2 = attr2.val()
          
          if val1 == None: val1 = ""
          if val2 == None: val2 = ""
          
          if len(val1) > 0 and not val1[-1].isalnum(): val1 = val1.rstrip(val1[-1])
          if len(val2) > 0 and not val2[-1].isalnum(): val2 = val1.rstrip(val2[-1])

          self.__check(attr1.tag()+" Value",
                       val1,
                       val2,
                       seriesInstanceUID, 
                       sopInstanceUID)

          self.__textTagsCompared += 1
          
          # ---
          # Warn if trailing characters are different.
          # ---
          if (val1 == val2):
             self.__warn(attr1.tag()+" Value",
                         attr1.val(),
                         attr2.val(),
                         seriesInstanceUID, 
                         sopInstanceUID)

       # Check for sequence  
       if attr1.vr() == "SQ":
          self.__sequencesCompared += 1

       # ---
       # Check number of items.
       # ---
       numItems1 = attr1.numItems()
       numItems2 = attr2.numItems()
       self.__check(attr1.tag()+" Number of items",
                    numItems1,
                    numItems2,
                    seriesInstanceUID, 
                    sopInstanceUID)

       if numItems1 == numItems2:
          for i in range(0, numItems1):
          
              # ---
              # Check items.
              # ---
              numAttributes1 = attr1.numItemAttributes(i)
              numAttributes2 = attr2.numItemAttributes(i)
              self.__check(attr1.tag()+" number of item attributes",
                           numAttributes1,
                           numAttributes2,
                           seriesInstanceUID, 
                           sopInstanceUID)

              # ---
              # Check item attributes.
              # ---
              if numAttributes1 == numAttributes2:
                 for j in range(0, numAttributes1):
                     itemAttribute1 = attr1.itemAttribute(i, j)
                     itemAttribute2 = attr2.itemAttribute(i, j)
                     self.__checkAttribute(itemAttribute1, itemAttribute2, seriesInstanceUID, sopInstanceUID)
           
              self.__itemsCompared += 1

   def __checkBinary(self, attr1, attr2, seriesInstanceUID, sopInstanceUID):

       if not attr1.hasBinary():
          self.__checkInlineBinary(attr1, attr2, seriesInstanceUID, sopInstanceUID)
          return

       if self.__lazy: return
       
       # ---
       # Check for DICOM binary item
       # ---
       bid1 = attr1.binary()
       if bid1 == None:
          self.__check(attr1.tag()+" missing binary",
                       "None",
                       "<Binary>",
                       seriesInstanceUID, 
                       sopInstanceUID)
          return

       # ---
       # Check for DICOM binary item
       # ---
       bid2 = attr2.binary()
       if bid2 == None:
          self.__check(attr2.tag()+" missing binary",
                       "<Binary>",
                       "None",
                       seriesInstanceUID, 
                       sopInstanceUID)
          return
 
       # ---
       # Read in a block.
       # ---
       BUFLEN = 1024
       bytesToRead = attr1.vl()
       assert bytesToRead > 0
       bufsize = min(bytesToRead, BUFLEN)
       block = 0
       buf1 = bid1.read(bufsize)
       buf2 = bid2.read(bufsize)
       bytesToRead -= len(buf1)

       # ---
       # Unpack the blocks.
       # ---
       bytes1 = []
       bytes2 = []
       try:
          bytes1 = attr1.transferSyntax().unpackByteArray(buf1, attr1.transferSyntax())
          bytes2 = attr2.transferSyntax().unpackByteArray(buf2, attr1.transferSyntax())
       except IOError, e:
          self.__exception(attr1.tag(), e, seriesInstanceUID, sopInstanceUID)

       # ----
       # Check binary sizes
       # ---     
       n = len(bytes1)
       m = len(bytes2)
       self.__check(attr1.tag()+" binary sizes differ",
                    n,
                    m,
                    seriesInstanceUID, 
                    sopInstanceUID)
       if n != m : return
              
       while n > 0:       

          # ---
          # Loop through block.
          # ---
          diff = False
          for i in range(0, n):              
              if bytes1[i] != bytes2[i]:
                 self.__check(attr1.tag()+" byte "+str(block*bufsize+i),
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
          if diff or bytesToRead == 0:
             n = 0
          else:
             bufsize = min(bytesToRead, BUFLEN)
             buf1 = bid1.read(bufsize)
             buf2 = bid2.read(bufsize)
             bytesToRead -= len(buf1)
             assert bytesToRead >= 0

             try:
                bytes1 = attr1.transferSyntax().unpackByteArray(buf1, attr1.transferSyntax())
                bytes2 = attr2.transferSyntax().unpackByteArray(buf2, attr1.transferSyntax())
             except IOError, e:
                self.__exception(attr1.tag(), e, seriesInstanceUID, sopInstanceUID)
             n = len(bytes1)
             block += 1
             
       bid1.close()
       bid2.close()
       self.__binaryItemsCompared += 1
 
   def __checkInlineBinary(self, attr1, attr2, seriesInstanceUID, sopInstanceUID):
 
       self.__check(attr1.tag()+" <Binary>",
                    attr1.val(),
                    attr2.val(),
                    seriesInstanceUID, 
                    sopInstanceUID)
 
       self.__inlineBinaryCompared += 1

# -----------------------------------------------------------------------------
# main
# -----------------------------------------------------------------------------
def main():
   
    # ---
    # Get options.
    # ---
    progName = os.path.basename(sys.argv[0])
    (options, args)=getopt.getopt(sys.argv[1:], "o:x:vlwh")

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
    # Check for warning option.
    # ---
    warnings = False
    for opt in options:
        if opt[0] == "-w":
           warnings = True
           
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
          print "  -o <output>:  output filename (defaults to stdout)"
          print "  -x <exclude>: list of tags to exclude, ie. \"08590030,600001nn\""
          print "  -v:           verbose"
          print "  -l:           lazy check (skips binary content)"
          print "  -w:           show warnings"
          print "  -h:           displays usage"
          sys.exit(1)
          
       # ---
       # Read MINT metadata.
       # ---
       refDicomStudyDir = args[0];
       newDicomStudyDir = args[1];

       dataDictionary = DCM4CHE_Dictionary()
     
       refDicomStudy = DicomStudy(refDicomStudyDir, dataDictionary)
       newDicomStudy = DicomStudy(newDicomStudyDir, dataDictionary)
     
       studies = DicomStudyCompare(refDicomStudy, newDicomStudy)
       studies.setVerbose(verbose)
       studies.setLazy(lazy)
       studies.setOutput(output)
       studies.setExclude(exclude)
       studies.setWarnings(warnings)

       differences = studies.compare()
       studies.tidy()

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
   
