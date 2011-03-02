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
import os
import tempfile
import sys

from struct import unpack

from org.nema.medical.mint.DataDictionaryElement import DataDictionaryElement
from org.nema.medical.mint.DataDictionary        import DataDictionary
from org.nema.medical.mint.DicomTransfer         import DicomTransfer

# -----------------------------------------------------------------------------
# DicomAttribute
# -----------------------------------------------------------------------------
class DicomAttribute():

   MAX_BINARY_LENGTH = 256
      
   def __init__(self, dcm, dataDictionary, transferSyntax):
          
       self.__tag   = None
       self.__vr    = ""
       self.__vl    = -1
       self.__val   = ""
       self.__dat   = ""
       self.__items = []
       self.__bytesRead = 0
       self.__dataDictionary = dataDictionary
       self.__transferSyntax = transferSyntax
       
       # ---
       # Read the next tag
       # ---
       group=dcm.read(2)
       if group == "": return
       self.__bytesRead += 2
          
       element=dcm.read(2)
       self.__bytesRead += 2
       self.__tag=self.__getTag(group, element)
                 
       # ---
       # If explicit or Part 10 header tag then read the VR.
       # ---
       self.__vr=""
       if self.isItemStart() or self.isItemStop() or self.isSequenceStop():
          pass # no VR
       elif transferSyntax.isExplicit() or self.isPart10Header():
          self.__vr=dcm.read(2)
          self.__bytesRead += 2
          
       # ---
       # Read the length
       # ---
       if self.__vr in self.reservedVRs: # Explicit with reserve
          dcm.read(2) # throw away reserve
          self.__bytesRead += 2
          vl=dcm.read(4)
          self.__bytesRead += 4
          self.__vl = transferSyntax.unpack("L", vl)
       elif self.__vr != "": # Explicit without reserve
          vl=dcm.read(2)
          self.__bytesRead += 2
          self.__vl = transferSyntax.unpack("h", vl)
       else: # Implicit
          vrs = dataDictionary.vrs(self.__tag)
          # TODO: What to do if multiple VRs are present?
          self.__vr=vrs[0] 
          vl=dcm.read(4)
          self.__bytesRead += 4
          self.__vl = transferSyntax.unpack("L", vl)

       # ---
       # Read Sequence Data Elements
       # ---
       if self.__vr == "SQ":
          sequenceBytesToRead = self.__vl
          readingSequence = True
          while readingSequence and sequenceBytesToRead > 0:
             item = DicomAttribute(dcm, dataDictionary, transferSyntax)
             if item.isSequenceStop(): readingSequence = False
             self.__bytesRead += item.bytesRead()
             sequenceBytesToRead -= item.bytesRead()
             if readingSequence:
                assert item.isItemStart()
                attrs = []
                self.__items.append(attrs)
                itemBytesToRead = item.vl()
                readingItem = True

                while readingItem and itemBytesToRead > 0:
                   itemAttr = DicomAttribute(dcm, dataDictionary, transferSyntax)
                   if itemAttr.isItemStop(): readingItem = False
                   self.__bytesRead += itemAttr.bytesRead()
                   if readingItem and not itemAttr.isGroupLength():
                      attrs.append(itemAttr)
                   itemBytesToRead -= itemAttr.bytesRead()
                   sequenceBytesToRead -= itemAttr.bytesRead()

       # Read the val
       self.__readVal(dcm)
       
   def tidy(self):
       """
       Removes a tempory binary item.
       """
       if os.path.exists(self.__dat): os.unlink(self.__dat)

   def group(self)   :  return self.__tag[0:4]
   def element(self)  : return self.__tag[4:]
   def tag(self)      : return self.__tag
   def vr (self)      : return self.__vr
   def vl (self)      : return self.__vl
   def val(self)      : return self.__val
   def dat(self)      : return self.__dat
   def bytesRead(self): return self.__bytesRead
   
   def valstr(self):
       if self.isPixelData()      : return "<Pixel Data>"
       elif self.isBinary()       : return "<Binary Data>"
       elif self.isUnknown()      : return "<Unknown>"
       elif self.isSequenceStart(): return "<Sequence Data>"
       elif self.__val != ""      : return self.__val
       else                       : return "None"

   def tagName(self):
       element = self.__dataDictionary.elementByTag(self.__tag)
       if element != None : return element.name()
       return "Unknown"

   def numItems(self)            : return len(self.__items)
   def item(self, i)             : return self.__items[i]
   def numItemAttributes(self, i): return len(self.item(i))
   def itemAttribute(self, i, j) : return self.item(i)[j]

   def isValid(self)          : return self.__tag != None
   def isPixelData(self)      : return self.__tag == self.PIXEL_DATA_TAG
   def isPart10Header(self)   : return int(self.group(),16) < 8
   def isTransferSyntax(self) : return self.__tag == self.TRANSFER_SYNTAX_UID_TAG
   def isUnknown(self)        : return self.__vr == "UN"
   def isPrivate(self)        : return int(self.group(),16) % 2 != 0
   def isBinary(self)         : return (self.__vr in self.binaryVRs or self.__tag == self.PIXEL_DATA_TAG)
   def isItemStart(self)      : return self.__tag == self.ITEM_TAG
   def isItemStop(self)       : return self.__tag == self.ITEM_DELIMITATION_TAG
   def isSequenceStart(self)  : return self.__vr == "SQ"
   def isSequenceStop(self)   : return self.__tag == self.SQ_DELIMITATION_TAG
   def isGroupLength(self)    : return self.__tag[4:8] == "0000"
   
   def promote(self, vr):
       """
       This method is used to promote a dicom tag from UN to a more specific VR.
       DCM4CHE will fill in the VR for implicit DICOM if it recognizes the tag.
       Normally only private tags need to be promoted since the VR for standard tags are known.
       """
   
       # Only promote unknown VR's.
       if self.__vr != "UN": return

       # Promote the VR.      
       self.__vr = vr    
      
       # If we are promoting from UN to another binary, we are done.
       if self.isBinary(): return
       
       # If the promotion changes the val from inline binary to text, we need to unpack again.
       if self.__val != "":
          self.__val = base64.b64decode(self.__val)
          self.__unpackToString()

   def __readVal(self, dcm):
      
       # Check for undefined length or sequence/item start
       if self.__vl == DicomAttribute.UNDEFINED_LENGTH or self.isSequenceStart() or self.isItemStart():
          return
          
       if not self.isBinary():
          self.__val=dcm.read(self.__vl)
          self.__bytesRead += self.__vl
          self.__unpackToString()
             
       # Binary
       else:
          # Store small binaries as inline base64
          if self.__vl <= self.MAX_BINARY_LENGTH:
             val = dcm.read(self.__vl)
             self.__bytesRead += self.__vl
             self.__val = base64.b64encode(val)
             
          # Write long binaries to tmp file
          else:             
             tmp = tempfile.NamedTemporaryFile(delete=False)
             self.__dat = tmp.name
             BLOCK_SIZE = 1024 * 1024    
             blocks = int(self.__vl / BLOCK_SIZE)
             bytes = self.__vl % BLOCK_SIZE
             for b in range(0, blocks):
                tmp.write(dcm.read(BLOCK_SIZE))
                self.__bytesRead += BLOCK_SIZE
             if bytes > 0:
                tmp.write(dcm.read(bytes))
                self.__bytesRead += bytes
             self.__val = None    
             tmp.close()
             
   def debug(self, output=None, indent=""):

       if output==None:
          print indent+"tag =", self.__tag, "vr =", self.__vr, "vl = %7d" % self.__vl,
       else:
          output.write(indent+"tag = "+self.__tag+" vr = "+self.__vr+" vl = %7d " % self.__vl)
       if self.__val != "": 
          if output==None:
             print "val =", self.valstr(),
          else:
             output.write("val = "+self.valstr()+" ")
          
       if self.__vr == "SQ":
          if output == None:
             print " # "+self.tagName().encode('ascii', 'replace')
          else:
             output.write(" # "+self.tagName().encode('ascii', 'replace')+"\n")
          indent += " "
          numItems = self.numItems()
          for i in range(0, numItems):
              if output == None:
                 print indent+"- item"
              else:
                 output.write(indent+"- item\n")
              numItemAttributes = self.numItemAttributes(i)
              indent += " "
              if output == None:
                 print indent+"- attributes"
              else:
                 output.write(indent+"- attributes\n")
              indent += " "
              for j in range(0, numItemAttributes):
                  self.itemAttribute(i, j).debug(output, indent)
              indent = indent[0:-1]
              if output == None:
                 print indent+"- attributes"
              else:
                 output.write(indent+"- attributes\n")
              indent = indent[0:-1]
              if output == None:
                 print indent+"- item"
              else:
                 output.write(indent+"- item\n")
       
       else:
          if output == None:
             print " # "+self.tagName().encode('ascii', 'replace')
          else:
             output.write(" # "+self.tagName().encode('ascii', 'replace')+"\n")
               
   def __bin2str(self, b):
       h = hex(b)
       s = str(h).replace("0x", "")
       for i in range(len(s),4):
           s = "0"+s
       return s
   
   def __getTag(self, group, element):
       g = self.__transferSyntax.unpack("H", group)
       e = self.__transferSyntax.unpack("H", element)
       s = self.__bin2str(g) + self.__bin2str(e)
       s = s.lower()
       return s

   def __unpackToString(self):
       
       # Signed Short
       if   self.__vr=="SS": self.__val = self.__val2str(2, "h", "%d")
       # Unsigned Short
       elif self.__vr=="US": self.__val = self.__val2str(2, "H", "%d")
       # Signed Long
       elif self.__vr=="SL": self.__val = self.__val2str(4, "l", "%d")
       # Unsigned Long
       elif self.__vr=="UL": self.__val = self.__val2str(4, "L", "%d")
       # Single Precision Float
       elif self.__vr=="FL": self.__val = self.__val2str(4, "f", "%.5f")
       # Double Precision Float
       elif self.__vr=="FD": self.__val = self.__val2str(8, "d", "%.5f")
       # TODO: Other Float 4 FLOAT
       elif self.__vr=="OF": self.__val = self.__val2str(4, "f", "%.5f")
       # Attribute Tag 4 ULONG
       elif self.__vr=="AT": self.__val = self.__val2str(4, "L", "%d")
       # Text
       else:
          self.__val = self.__val[0:self.__vl]
          
          # ---
          # Strip NULL character and whitespace.
          # ---
          if len(self.__val) > 0 and ord(self.__val[-1]) == 0:
             self.__val = self.__val[0:len(self.__val)-1]
          self.__val = self.__val.rstrip()
          self.__vl = len(self.__val)
          
   def __val2str(self, size, type, format):
       vals = ""
       numVals = self.__vl / size
       for n in range(0, numVals):
           i = n * size
           val = self.__transferSyntax.unpack(type, self.__val[i:i+size])
           vals += str(format % val)+"\\"    
       return vals[0:-1]

   UNDEFINED_LENGTH            = 0xffffffff
   TRANSFER_SYNTAX_UID_TAG     = "00020010"
   PIXEL_DATA_TAG              = "7fe00010"
   ITEM_TAG                    = "fffee000"
   ITEM_DELIMITATION_TAG       = "fffee00d"
   SQ_DELIMITATION_TAG         = "fffee0dd"
   
   reservedVRs = ("OB", "OW", "OF", "SQ", "UT", "UN")
   binaryVRs   = ("OB", "OW", "UN")

   # Other Binary VRs that are represented as text for clarity
   # ("SS", "US", "SL", "UL", "FL", "FD", "OF", "AT")
