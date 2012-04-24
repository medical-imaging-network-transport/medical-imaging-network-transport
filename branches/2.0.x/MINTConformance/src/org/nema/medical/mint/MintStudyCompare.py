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
import glob
import os
import sys
import traceback

from os.path import join
from struct import unpack

from org.nema.medical.mint.MintStudy   import MintStudy
from org.nema.medical.mint.MintStudyFS import MintStudyFS

# -----------------------------------------------------------------------------
# MintStudyCompare
# -----------------------------------------------------------------------------
class MintStudyCompare():
   
   def __init__(self, refMintStudy, newMintStudy):
                
       self.__study1 = refMintStudy
       self.__study2 = newMintStudy
       self.__binary1 = refMintStudy.binaryitems()
       self.__bids = []
       self.__offsets1 = {}
       self.__offsets2 = {}
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
       self.__lazy= False
       self.__output = None

       self.__readOffsets()

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

   def compare(self):
       s1 = self.__study1
       s2 = self.__study2

       # ---
       # Compare study level attributes
       # ---
       self.check("Study xmlns",
                  s1.xmlns(), 
                  s2.xmlns())
                      
       self.check("Study Instance UID",
                  s1.studyInstanceUID(), 
                  s2.studyInstanceUID())
                  
       self.check("Study Type",
                  s1.type(), 
                  s2.type())
                  
       self.check("Study Version",
                  s1.version(), 
                  s2.version())
                  
       self.check("Study Instance Count",
                  s1.instanceCount(), 
                  s2.instanceCount())
                  
       self.check("Number of study attributes",
                  s1.numAttributes(), 
                  s2.numAttributes())

       numAttributes = min(s1.numAttributes(), s2.numAttributes)
       for n in range(0, numAttributes):
           attr1 = s1.attribute(n)
           attr2 = s2.attributeByTag(attr1.tag())
           self.__checkAttributes("Study Attribute", attr1, attr2)
              
       self.check("Number of series",
                  s1.numSeries(), 
                  s2.numSeries())

       numSeries = min(s1.numSeries(), s2.numSeries())
       for n in range(0, numSeries):
           series1 = s1.series(n)
           series2 = s2.seriesByUID(series1.seriesInstanceUID())
           self.__compareSeries(series1, series2)
           self.__seriesCompared += 1
           
       self.__checkOffsets()
       self.__checkBinary()

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
          else:
             self.__output.write("%10d series compared.\n" % (self.__seriesCompared))
             self.__output.write("%10d instance(s) compared.\n" % (self.__instancesCompared))
             self.__output.write("%10d text tag(s) compared.\n" % (self.__textTagsCompared))
             self.__output.write("%10d sequence(s) compared.\n" % (self.__sequencesCompared))
             self.__output.write("%10d items(s) compared.\n" % (self.__itemsCompared))
             self.__output.write("%10d inline binary item(s) compared.\n" % (self.__inlineBinaryCompared))
             self.__output.write("%10d binary item(s) compared.\n" % (self.__binaryItemsCompared))
             self.__output.write("%10d byte(s) compared.\n" % (self.__bytesCompared))

       # ---
       # Always print differences.
       # ---
       if self.__count != 0:
          if self.__output == None:
             print "%10d difference(s) found." % (self.__count)
          else:
             self.__output.write("%10d difference(s) found.\n" % (self.__count))

       return self.__count
      
   def check(self, msg, obj1, obj2):
       if obj1 != obj2:
          self.__count += 1
          print msg, ":", obj1, "!=", obj2

   def __readOffsets(self):
   
       # ---
       # TODO
       # ---
       #
       # offsets1 = os.path.join(self.__binary1, "offsets.dat")
       # if offsets1 in self.__binaryitems: self.__binaryitems.remove(offsets1)
       # if os.path.isfile(offsets1):
       #    self.__readOffsetTable(offsets1, self.__offsets1)
       #
       # offsets2 = os.path.join(self.__binary2, "offsets.dat")
       # if os.path.isfile(offsets2):
       #    self.__readOffsetTable(offsets2, self.__offsets2)
       
       pass
       
   def __readOffsetTable(self, offsetsName, offsets):
       
       # ---
       # TODO
       # ---
       #
       # table = open(offsetsName, "r")
       # line = table.readline()
       # while line != "":
       #    tokens = line.split()
       #    assert len(tokens) == 2
       #    offsets[tokens[0]] = tokens[1]
       #    line = table.readline()
       # table.close()
       
       pass
       
   def __checkAttributes(self, msg, attr1, attr2):
       if attr2 == None:
          self.check(msg, attr1.toString(), "None") 
       elif attr1.numItems() > 0:
          self.__checkItems(msg, attr1, attr2)
       elif attr1.vr() == "SQ" and attr1.numItems() == 0:
          pass
       else:
          self.check(msg, str(attr1), str(attr2))
          if attr1.bytes() != "": self.__inlineBinaryCompared += 1
          else: self.__textTagsCompared += 1
          if attr1.bid() != "":
             self.__bids.append(attr1.bid())

       if attr1.vr() == "SQ": self.__sequencesCompared += 1
   
   def __checkItems(self, msg, attr1, attr2):
       numItems1 = attr1.numItems()
       numItems2 = attr2.numItems()
       self.check(msg+" number of items",
                  numItems1, 
                  numItems2)
                  
       if numItems1 == numItems2:
          for i in range(0, numItems1):
              item1 = attr1.item(i)
              item2 = attr2.item(i)
              self.__checkAttributes("item", item1, item2)
              self.__itemsCompared += 1       
                                
   def __compareSeries(self, series1, series2):
                 
       uid = series1.seriesInstanceUID()
       series = "Series ("+uid+")"
       
       if series2 == None:
          self.check(series+" Instance UID",
                     series1.seriesInstanceUID(), 
                     "None")
          return
       
       self.check(series+" instanceCount",
                  series1.instanceCount(), 
                  series2.instanceCount())
                  
       self.check("Number of "+series+" attributes",
                  series1.numAttributes(), 
                  series2.numAttributes())
                  
       numAttributes = min(series1.numAttributes(), series2.numAttributes())
       for n in range(0, numAttributes):
           attr1 = series1.attribute(n)
           attr2 = series2.attributeByTag(attr1.tag())
           self.__checkAttributes(series+" Attribute", attr1, attr2)

       numNormalizedInstanceAttributes = min(series1.numNormalizedInstanceAttributes(),
                                             series2.numNormalizedInstanceAttributes())

       for n in range(0, numNormalizedInstanceAttributes):
           attr1 = series1.normalizedInstanceAttribute(n)
           attr2 = series2.normalizedInstanceAttributeByTag(attr1.tag())
           self.__checkAttributes(series+" Normalized Instance Attribute", attr1, attr2)       

       self.check("Number of "+series+" Instances",
                  series1.numInstances(), 
                  series2.numInstances())

       numInstances = min(series1.numInstances(), series2.numInstances())
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
                  
       numAttributes = min(instance1.numAttributes(), instance2.numAttributes())
       for n in range(0, numAttributes):
           attr1 = instance1.attribute(n)
           attr2 = instance2.attributeByTag(attr1.tag())
           self.__checkAttributes(instance+" Attribute", attr1, attr2)

   def __checkBinary(self):

       if self.__lazy: return

       # ---
       # Loop through each binary item.
       # ---
       for bid in self.__bids:

           binaryitem = os.path.join(self.__binary1, bid+".dat")

           # ---
           # Check for binary item in study 1.
           # ---
           dat1 = binaryitem
           if not os.access(dat1, os.F_OK):
              self.__count += 1
              print "File not found", ":", dat1
              pass
                         

           # ---
           # Check binary item byte for byte.
           # ---
           bid1 = open(dat1, "rb")
           bid2 = self.__study2.open(bid)
           
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
                  if bytes1[i] != bytes2[i]:
                     self.check(binaryitem+".dat byte "+str(block*bufsize+i),
                                bytes1[i],
                                bytes2[i])
                
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

   def __checkOffsets(self):

       # ---
       # TODO
       # ---
       #
       #
       # if self.__lazy: return
       # 
       # bids1 = self.__offsets1.keys()
       # bids2 = self.__offsets2.keys()
       # 
       # if len(bids1) != len(bids2):
       #    self.check("Number of bid offsets",
       #               len(bids1),
       #               len(bids2))
       #    return
       #
       # for bid in bids1:
       #     offset1 = self.__offsets1[bid]
       #     offset2 = self.__offsets2[bid]
       #     if offset1 == offset2:
       #        self.check("bid "+bid+" boffset",
       #                   offset1,
       #                   offset2)
       #     self.__offsetsCompared += 1
       
       pass
       
# -----------------------------------------------------------------------------
# main
# -----------------------------------------------------------------------------
def main():
   
    # ---
    # Get options.
    # ---
    progName = os.path.basename(sys.argv[0])
    (options, args)=getopt.getopt(sys.argv[1:], "o:p:vlh")

    # ---
    # Check for output option.
    # ---
    output = ""
    for opt in options:
        if opt[0] == "-o":
           output = opt[1]
           
    # ---
    # Check for port option.
    # ---
    port = "8080"
    for opt in options:
        if opt[0] == "-p":
           port = opt[1]
           
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
       if help or len(args) != 3:
          print "Usage:", progName, "[options] <mint_study_dir> <hostname> <uuid>"
          print "  -o <output>: output filename (defaults to stdout)"
          print "  -p <port>:   defaults to 8080"
          print "  -v:          verbose"
          print "  -l:          lazy check (skips binary content)"
          print "  -h:          displays usage"
          sys.exit(1)
          
       # ---
       # Read MINT metadata.
       # ---
       mintStudyDir = args[0];
       hostname = args[1];
       uuid = args[2];
       refMintStudy = MintStudyFS(mintStudyDir)
       newMintStudy = MintStudy(hostname, port, uuid)
       studies = MintStudyCompare(refMintStudy, newMintStudy)
       studies.setVerbose(verbose)
       studies.setLazy(lazy)
       studies.setOutput(output)

       status = studies.compare()
       studies.tidy()
       return status

    except Exception, exception:
       traceback.print_exception(sys.exc_info()[0], 
                                 sys.exc_info()[1],
                                 sys.exc_info()[2])
       sys.exit(1)
       
if __name__ == "__main__":
   main()
