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

from org.nema.medical.mint.MintStudy import MintStudy

# -----------------------------------------------------------------------------
# MintStudy
# -----------------------------------------------------------------------------
class MintStudyCompare():
   
   def __init__(self, study1, study2):
       if not os.path.isfile(study1):
          print "File not found -", study1
          sys.exit(1)

       if not os.path.isfile(study2):
          print "File not found -", study2
          sys.exit(1)
                
       self.__study1 = MintStudy(study1)
       self.__study2 = MintStudy(study2)

       studydir1 = os.path.dirname(study1)
       studydir2 = os.path.dirname(study2)
       
       self.__binary1 = os.path.join(studydir1, "binaryitems")
       self.__binary2 = os.path.join(studydir2, "binaryitems")

       self.__binaryitems = []
       
       self.__count = 0
       self.__verbose = False
       self.__studyAttributesCompared = 0
       self.__seriesCompared = 0
       self.__seriesAttributesCompared = 0
       self.__normalizedInstanceAttributesCompared = 0
       self.__instancesCompared = 0
       self.__instanceAttributesCompared = 0
       self.__binaryItemsCompared = 0
       self.__bytesCompared = 0
       self.__lazy= False

   def setVerbose(self, verbose):
       self.__verbose = verbose
       
   def setLazy(self, lazy):
       self.__lazy = lazy
       
   def compare(self):
       s1 = self.__study1
       s2 = self.__study2
       
       self.check("Study xmlns",
                  s1.xmlns(), 
                  s2.xmlns())
                      
       self.check("Study Instance UID",
                  s1.studyInstanceUID(), 
                  s2.studyInstanceUID())
                  
       self.check("Number of study attributes",
                  s1.numAttributes(), 
                  s2.numAttributes())

       numAttributes = s1.numAttributes()
       for n in range(0, numAttributes):
           attr1 = s1.attribute(n)
           attr2 = s2.attributeByTag(attr1.tag())
           if attr2 == None:
              self.check("Study Attribute", attr1.toString(), "None")
           else:
              self.check("Study Attribute", attr1.toString(), attr2.toString())
           self.__studyAttributesCompared += 1
           self.__checkForBinary(attr1)
              
       self.check("Number of series",
                  s1.numSeries(), 
                  s2.numSeries())

       numSeries = s1.numSeries()
       for n in range(0, numSeries):
           series1 = s1.series(n)
           series2 = s2.seriesByUID(series1.seriesInstanceUID())
           self.__compareSeries(series1, series2)
           self.__seriesCompared += 1
           
       self.__checkBinary()

       # ---
       # Print out stats if verbose.
       # ---       
       if self.__verbose:
           print "%10d study attribute(s) compared." % (self.__studyAttributesCompared)
           print "%10d series compared." % (self.__seriesCompared)
           print "%10d series attribute(s) compared." % (self.__seriesAttributesCompared)
           print "%10d normalized instance attribute(s) compared." % (self.__normalizedInstanceAttributesCompared)
           print "%10d instance(s) compared." % (self.__instancesCompared)
           print "%10d instance attribute(s) compared." % (self.__instanceAttributesCompared)
           print "%10d binary item(s) compared." % (self.__binaryItemsCompared)
           print "%10d byte(s) compared." % (self.__bytesCompared)    

       # ---
       # Always print differences.
       # ---
       if self.__count != 0:
          print self.__count, "difference(s) found."

       return self.__count
      
   def check(self, msg, obj1, obj2):
       if obj1 != obj2:
          self.__count += 1
          print msg, ":", obj1, "!=", obj2

   def __compareSeries(self, series1, series2):
                 
       uid = series1.seriesInstanceUID()
       series = "Series ("+uid+")"
       
       if series2 == None:
          self.check(series+" Instance UID",
                     series1.seriesInstanceUID(), 
                     "None")
          return
       
       self.check("Number of "+series+" attributes",
                  series1.numAttributes(), 
                  series2.numAttributes())
                  
       numAttributes = series1.numAttributes()
       for n in range(0, numAttributes):
           attr1 = series1.attribute(n)
           attr2 = series2.attributeByTag(attr1.tag())
           if attr2 == None:
              self.check(series+" Attribute", attr1.toString(), "None")       
           else:
              self.check(series+" Attribute", attr1.toString(), attr2.toString())
           self.__checkForBinary(attr1)
           self.__seriesAttributesCompared += 1

       numNormalizedInstanceAttributes = series1.numNormalizedInstanceAttributes()
       for n in range(0, numNormalizedInstanceAttributes):
           attr1 = series1.normalizedInstanceAttribute(n)
           attr2 = series2.normalizedInstanceAttributeByTag(attr1.tag())
           if attr2 == None:
              self.check(series+" Normalized Instance Attribute", attr1.toString(), "None")       
           else:
              self.check(series+" Normalized Instance Attribute", attr1.toString(), attr2.toString())       
           self.__checkForBinary(attr1)
           self.__normalizedInstanceAttributesCompared += 1

       self.check("Number of "+series+" Instances",
                  series1.numInstances(), 
                  series2.numInstances())

       numInstances = series1.numInstances()
       for n in range(0, numInstances):
           instance1 = series1.instance(n)
           instance2 = None
           if series2.numInstances() >= n+1:
              instance2 = series2.instance(n)
           self.__compareInstances(instance1, instance2)
           self.__instancesCompared += 1

   def __compareInstances(self, instance1, instance2):
       instance = "Instance "+instance1.sopInstanceUID()
              
       if instance2 == None:
          self.check(instance+" SOP Instance UID",
                     instance1.sopInstanceUID(), 
                     "None")
          return
       
       self.check(instance+" SOP Instance UID",
                  instance1.sopInstanceUID(), 
                  instance2.sopInstanceUID())
                  
       self.check(instance+" Transfer Syntax UID",
                  instance1.transferSyntaxUID(), 
                  instance2.transferSyntaxUID()) 
                  
       numAttributes = instance1.numAttributes()
       for n in range(0, numAttributes):
           attr1 = instance1.attribute(n)
           attr2 = instance2.attributeByTag(attr1.tag())
           if attr2 == None:
              self.check(instance+" Attribute", attr1.toString(), "None")
           else:
              self.check(instance+" Attribute", attr1.toString(), attr2.toString())
           self.__checkForBinary(attr1)
           self.__instanceAttributesCompared += 1

   def __checkForBinary(self, attr):
       if attr.bid() != None and attr.isBinary() and attr.bid() not in self.__binaryitems:
          self.__binaryitems.append(attr.bid())
       
   def __checkBinary(self):
       
       # ---
       # Loop through each binary item.
       # ---
       for binaryitem in self.__binaryitems:
       
           # ---
           # Check for binary item in study 1.
           # ---
           dat1 = os.path.join(self.__binary1, binaryitem+".dat")
           if not os.access(dat1, os.F_OK):
              self.__count += 1
              print "File not found", ":", dat1
              pass
           
           # ---
           # Check for binary item in study 2.
           # ---
           dat2 = os.path.join(self.__binary2, binaryitem+".dat")
           if not os.access(dat2, os.F_OK):
              self.__count += 1
              print "File not found", ":", dat2
              pass
              
           # ---
           # Check binary item sizes.
           # ---
           size1 = os.path.getsize(dat1)
           size2 = os.path.getsize(dat2)
           self.check(binaryitem+".dat size",
                      size1,
                      size2)

           # ---
           # Check binary item byte for byte.
           # ---
           if not self.__lazy and size1 == size2:
              bid1 = open(dat1, "rb")
              bid2 = open(dat2, "rb")
           
              # ---
              # Read in a block.
              # ---
              bufsize = 1024
              block = 0
              buf1 = bid1.read(bufsize)
              bytes1 = unpack('B'*len(buf1), buf1)
              n1 = len(bytes1)
              while n1 > 0:   
                 buf2 = bid2.read(bufsize)
                 bytes2 = unpack('B'*len(buf2), buf2)
                 n2 = len(bytes2)
                 assert n1 == n2 # These better be equal
                 
                 # ---
                 # Loop through block.
                 # ---
                 diff = False
                 for i in range(0, n1):
                     self.check(binaryitem+".dat byte "+str(block*bufsize+i),
                                bytes1[i],
                                bytes2[i])
                
                     if bytes1[i] != bytes2[i]:
                        diff = True
                        break
                     
                     self.__bytesCompared += 1

                 # ---
                 # Skip to end if difference was found.
                 # ---
                 if diff:
                    n1 = -1
                 else:
                    buf1 = bid1.read(bufsize)
                    bytes1 = unpack('B'*len(buf1), buf1)
                    n1 = len(bytes1)
                    block += 1

              bid1.close()
              bid2.close()
              
           self.__binaryItemsCompared += 1

# -----------------------------------------------------------------------------
# main
# -----------------------------------------------------------------------------
def main():
   
    # ---
    # Get options.
    # ---
    progName = os.path.basename(sys.argv[0])
    (options, args)=getopt.getopt(sys.argv[1:], "vlh")

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
       if help or len(args) != 2:
          print "Usage:", progName, "[options] <mint_study1.xml> <mint_study2.xml>"
          print "  -v: verbose"
          print "  -l: lazy check (skips binary content)"
          print "  -h: displays usage"
          sys.exit(1)
          
       # ---
       # Read MINT metadata.
       # ---
       study1 = args[0];
       study2 = args[1];
       studies = MintStudyCompare(study1, study2)
       studies.setVerbose(verbose)
       studies.setLazy(lazy)

       return studies.compare()
       
    except Exception, exception:
       traceback.print_exception(sys.exc_info()[0], 
                                 sys.exc_info()[1],
                                 sys.exc_info()[2])
       sys.exit(1)
       
if __name__ == "__main__":
   main()
