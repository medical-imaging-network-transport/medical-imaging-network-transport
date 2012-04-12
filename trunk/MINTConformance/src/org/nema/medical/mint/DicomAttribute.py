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
import os
import sys

from struct import unpack

from org.nema.medical.mint.DataDictionaryElement import DataDictionaryElement
from org.nema.medical.mint.DataDictionary        import DataDictionary
from org.nema.medical.mint.DicomTransfer         import DicomTransfer

# -----------------------------------------------------------------------------
# DicomAttribute
#
# For VRs of OB, OW, SQ and UN, the 16 bits following the two character VR Field 
# are reserved for use by later versions of the DICOM Standard. These reserved 
# bytes shall be set to 0000H and shall not be used or decoded. The Value Length 
# Field is a 32-bit unsigned integer. If the Value Field has an Explicit Length,
# then the Value Length Field shall contain a value equal to the length (in bytes)
# of the Value Field. Otherwise, the Value Field has an Undefined Length and a 
# Sequence Delimitation Item marks the end of the Value Field.
#
# For VRs of UT the 16 bits following the two character VR Field are reserved for 
# use by later versions of the DICOM Standard. These reserved bytes shall be set 
# to 0000H and shall not be used or decoded. The Value Length Field is a 32-bit 
# unsigned integer. The Value Field is required to have an Explicit Length, that 
# is the Value Length Field shall contain a value equal to the length (in bytes) 
# of the Value Field.
#
# Note: VRs of UT may not have an Undefined Length, ie. a Value Length of FFFFFFFFH.
#
# For all other VRs the Value Length Field is the 16-bit unsigned integer 
# following the two character VR Field. The value of the Value Length Field shall 
# equal the length of the Value Field.
# -----------------------------------------------------------------------------
class DicomAttribute():

   DEBUG = False
   skipPrivate = False
   skipItems = False
   
   MAX_BINARY_LENGTH           = 256
   UNDEFINED_LENGTH            = 0xffffffff
   TRANSFER_SYNTAX_UID_TAG     = "00020010"
   PIXEL_DATA_TAG              = "7fe00010"
   ITEM_TAG                    = "fffee000"
   ITEM_DELIMITATION_TAG       = "fffee00d"
   SQ_DELIMITATION_TAG         = "fffee0dd"
   SKIPPED_TAG                 = "11111111"
   
   reservedVRs = ("OB", "OW", "OF", "SQ", "UT", "UN")
   binaryVRs   = ("OB", "OW", "UN", "FL", "FD")

   # Other Binary VRs that are represented as text for clarity
   # ("SS", "US", "SL", "UL", "FL", "FD", "OF", "AT")
      
   def setSkipPrivate(skipPrivate):
       DicomAttribute.skipPrivate = skipPrivate
   setSkipPrivate = staticmethod(setSkipPrivate)

   def __init__(self, dcm, dataDictionary, transferSyntax, bytesToRead=0):
          
       self.__tag    = None
       self.__vr     = ""
       self.__vl     = -1
       self.__val    = ""
       self.__bytes  = ""
       self.__dicom  = ""
       self.__offset = -1
       self.__items  = []
       self.__bytesRead = 0
       self.__dataDictionary = dataDictionary
       self.__transferSyntax = transferSyntax

       # TODO:
       #if dcm == None:
       #   return

       # ---
       # Skip over bytesToRead
       # ---
       if bytesToRead > 0:
          self.__tag = DicomAttribute.SKIPPED_TAG
          self.__vr  = "UN"
          self.__vl  = bytesToRead
          self.__readVal(dcm)
          return
         
       # ---
       # Read the next tag (Group, Element)
       # ---
       group=self.__readDicom(dcm, 2)
       if group == "": return
       element=self.__readDicom(dcm, 2)
       self.__tag=self.__tagstr(group, element)

       # ---
       # Read the VR if the transfer syntax is explicit or this tag is a Part 10 header.
       # ---
       self.__vr=""
       if self.isItemStart() or self.isItemStop() or self.isSequenceStop():
          pass # no VR
       elif transferSyntax.isExplicit() or self.isPart10Header():
          self.__vr=self.__readDicom(dcm, 2)
       
       # ---
       # Read the length
       # ---
       if self.__vr in self.reservedVRs: # Explicit with reserve
          self.__readDicom(dcm, 2) # throw away reserve
          vl=self.__readDicom(dcm, 4)
          self.__vl = transferSyntax.unpack("L", vl) # unsigned long
       elif self.__vr != "": # Explicit without reserve
          vl=self.__readDicom(dcm, 2)
          self.__vl = transferSyntax.unpack("h", vl) # short
       else: # Implicit
          vrs = dataDictionary.vrs(self.__tag)
          # TODO: What to do if multiple VRs are present?
          self.__vr=vrs[0] 
          vl=self.__readDicom(dcm, 4)
          self.__vl = transferSyntax.unpack("L", vl) # unsigned long

          # ---
          # If this is private and the length is unknown promote to sequence
          # ---
          if self.isPrivate() and self.__vl == self.UNDEFINED_LENGTH:
             self.promote("SQ")
                
       if DicomAttribute.DEBUG:
          print self.__tag, self.__vr, self.__vl,
          if   self.isPrivate()     : print "<Private>",
          elif self.isItemStart()   : print "<item>",
          elif self.isItemStop()    : print "</item>",
          elif self.isSequenceStop(): print "</sequence>",
          raw_input()
       
       # ---
       # Skip over private tags
       # ---
       if DicomAttribute.skipPrivate and self.isPrivate() and self.__vl != self.UNDEFINED_LENGTH:
          self.__vr = "UN";
         
       # ---         
       # Read recursive tags
       # ---
       if   self.isSequenceStart() and self.__vl == self.UNDEFINED_LENGTH:
            self.__readUndefinedSequence(dcm)

       elif self.isSequenceStart() and self.__vl != self.UNDEFINED_LENGTH:
            self.__readDefinedSequence(dcm)

       elif self.isPixelData() and self.__vl == self.UNDEFINED_LENGTH:
            self.__readCompressed(dcm)

       elif self.isItemStart() and self.__vl == 4:
            self.__readFragment(dcm)

       elif self.isItemStart() and self.__vl == self.UNDEFINED_LENGTH:
            self.__readUndefinedItems(dcm)

       elif self.isItemStart() and self.__vl != self.UNDEFINED_LENGTH:
            self.__readDefinedItems(dcm) 
          
       elif self.__vl == self.UNDEFINED_LENGTH:
            self.__readUndefinedItems(dcm)
          
       # ---
       # Read the val
       # ---
       self.__readVal(dcm)
       if DicomAttribute.DEBUG: print self.valstr()
       
   def group(self)    : return self.__tag[0:4]
   def element(self)  : return self.__tag[4:]
   def tag(self)      : return self.__tag
   def vr (self)      : return self.__vr
   def vl (self)      : return self.__vl
   def val(self)      : return self.__val
   def bytes(self)    : return self.__bytes
   def bytesRead(self): return self.__bytesRead
   def hasBinary(self): return self.__dicom != ""
   def transferSyntax(self): return self.__transferSyntax

   def binary(self):
       """
       Returns a file pointer to the binary data. User's responsibility to close.
       """
       if self.__dicom == "": return None
       if not os.access(self.__dicom, os.F_OK): raise IOError("File not found: "+self.__dicom)
       if not self.__offset > 0: raise IOError("Binary offset uninitialized")
       binary = open(self.__dicom, "rb")
       binary.seek(self.__offset)
       return binary

   def valstr(self):
       if self.isPixelData()                         : return "<Pixel Data>"
       elif self.isItemStart()                       : return "<item>"
       elif self.isItemStop()                        : return "</item>"
       elif self.isSequenceStop()                    : return "</sequence>"
       elif self.isBinary() and self.__val == None   : return "<Binary Data>"
       elif self.isUnknown()                         : return "<Unknown>"
       elif self.__val != ""                         : return self.__val
       else                                          : return "None"

   def tagName(self):
       element = self.__dataDictionary.elementByTag(self.__tag)
       if element != None : return element.name()
       return "Unknown"

   def numItems(self)         : return len(self.__items)
   def item(self, i)          : return self.__items[i]
   def isValid(self)          : return self.__tag != None
   def isPixelData(self)      : return self.__tag == self.PIXEL_DATA_TAG
   def isPart10Header(self)   : return int(self.group(),16) < 8
   def isTransferSyntax(self) : return self.__tag == self.TRANSFER_SYNTAX_UID_TAG
   def isUnknown(self)        : return self.__vr == "UN"
   def isPrivate(self)        : return int(self.group(),16) % 2 != 0
   def isBinary(self)         : return (self.__vr in self.binaryVRs or self.__tag == self.PIXEL_DATA_TAG)
   def isSequenceStart(self)  : return self.__vr == "SQ"
   def isItemStart(self)      : return self.__tag == self.ITEM_TAG
   def isItemStop(self)       : return self.__tag == self.ITEM_DELIMITATION_TAG
   def isSequenceStop(self)   : return self.__tag == self.SQ_DELIMITATION_TAG
   def isGroupLength(self)    :
       if self.__tag == None:
         raise IOError("EOF found unexpectedly")
       else:
         return self.__tag[4:8] == "0000"
         
   def promote(self, vr):
       """
       This method is used to promote a dicom tag from UN to a more specific VR.
       """
   
       # Only promote unknown VR's.
       if self.__vr != "UN": return

       # Promote the VR.      
       self.__vr = vr    
      
       # If we are promoting from UN to another binary, we are done.
       if self.isBinary(): return
       
       # ---
       # If the promotion changes the val from inline binary to text, we need to unpack again.
       # ---
       if self.__bytes != "":
          self.__val = base64.b64decode(self.__bytes)
          self.__unpackToString()
          self.__bytes = ""

   def debug(self, output=None, indent=""):

       # ---
       # Output tag information.
       # ---
       if self.isItemStart():
          if output==None:
             print indent+"<item>",
          else:
             output.write(indent+"<item>")

       elif output==None:
          print indent+"tag =", self.__tag, "vr =", self.__vr, "vl = %7d" % self.__vl,
       else:
          output.write(indent+"tag = "+self.__tag+" vr = "+self.__vr+" vl = %7d " % self.__vl)

       # ---
       # Output tag value.
       # ---
       if self.isItemStart():
          pass

       elif self.isItemStop():
          pass

       elif self.isSequenceStop():
          pass

       elif self.__val != "" or self.__bytes != "": 
          if output==None:
             print "val =", self.valstr(),
          else:
             output.write("val = "+self.valstr()+" ")
          
       # ---
       # Output items.
       # ---
       if len(self.__items) > 0:
          if output==None: print
          else: output.write("\n")
          indent += " "
          numItems = self.numItems()
          for i in range(0, numItems):
              self.item(i).debug(output, indent)       
       elif self.isItemStop():
          pass
       elif self.isSequenceStop():
          pass
         
       # ---
       # Output tag description.
       # ---
       if (self.isItemStart() and self.vl() != 0):
          if output==None:
             print indent+"</item>"
          else:
             output.write(indent+"</item>\n")
       elif (self.isSequenceStart() and self.vl() != 0) or self.vl() == self.UNDEFINED_LENGTH:
          if output==None:
             print indent+"</sequence>"
          else:
             output.write(indent+"</sequence>\n")
       else:
          if output == None:
             print " # "+self.tagName().encode('ascii', 'replace')
          else:
             output.write(" # "+self.tagName().encode('ascii', 'replace')+"\n")

   def __readUndefinedSequence(self, dcm):

       done = False
       while not done:
          item = DicomAttribute(dcm, self.__dataDictionary, self.__transferSyntax)
          self.__bytesRead += item.bytesRead()
          if item.isSequenceStop(): done = True
          if not done and not item.isGroupLength():
             self.__items.append(item)
       
   def __readDefinedSequence(self, dcm):

       sequenceBytesToRead = self.__vl
       while sequenceBytesToRead > 0:
          item = DicomAttribute(dcm, self.__dataDictionary, self.__transferSyntax)
          self.__bytesRead += item.bytesRead()
          sequenceBytesToRead -= item.bytesRead()
          if not item.isGroupLength():
             self.__items.append(item)
       
   def __readCompressed(self, dcm):

       # ---
       # Skip over item
       # ---
       DicomAttribute.skipItems = True
       while DicomAttribute.skipItems:
          item = DicomAttribute(dcm, self.__dataDictionary, self.__transferSyntax)
          self.__bytesRead += item.bytesRead()
          if item.isSequenceStop(): 
             DicomAttribute.skipItems = False
          else:
             self.__items.append(item)

   def __readFragment(self, dcm):

       # ---
       # Read frame number
       # ---
       vl=self.__readDicom(dcm, self.vl())
       frameNumber = self.__transferSyntax.unpack("L", vl) # unsigned long
       
       # ---
       # Skip over item
       # ---
       DicomAttribute.skipItems = True
       while DicomAttribute.skipItems:
          item = DicomAttribute(dcm, self.__dataDictionary, self.__transferSyntax)
          self.__bytesRead += item.bytesRead()
          if item.isSequenceStop(): 
             DicomAttribute.skipItems = False
          else:
             self.__items.append(item)
             
   def __readUndefinedItems(self, dcm):

       if self.__vr != "OB" and self.__vr != "OW" and self.__vr != "UN":
          raise IOError(self.__tag+" "+self.__vr+" - Illegal undefined length")

       done = False
       while not done:
          item = DicomAttribute(dcm, self.__dataDictionary, self.__transferSyntax)
          self.__bytesRead += item.bytesRead()
          if item.isItemStop() or item.isSequenceStop(): done = True
          if not done and not item.isGroupLength():
             self.__items.append(item)
       
   def __readDefinedItems(self, dcm):

       itemBytesToRead = self.vl()
       while itemBytesToRead > 0:
          if DicomAttribute.skipItems:
             item = DicomAttribute(dcm, self.__dataDictionary, self.__transferSyntax, itemBytesToRead)
          else:
             item = DicomAttribute(dcm, self.__dataDictionary, self.__transferSyntax)
          self.__bytesRead += item.bytesRead()
          itemBytesToRead -= item.bytesRead()
          if not item.isGroupLength():
             self.__items.append(item)

   def __readVal(self, dcm):
      
       # Check for undefined length
       if self.__vl == DicomAttribute.UNDEFINED_LENGTH: return

       # Check for undefined length
       if self.isSequenceStart() or self.isItemStart(): return

       # ---
       # Read text
       # ---
       if not self.isBinary():
          self.__val=self.__readDicom(dcm, self.__vl)
          self.__unpackToString()
            
       # --- 
       # Read binary
       # ---
       else:
          # Store small binaries as inline base64
          if self.__vl <= self.MAX_BINARY_LENGTH:
             val = self.__readDicom(dcm, self.__vl)
             self.__bytes = base64.b64encode(val)
             if self.__vr == "FD" or self.__vr == "FL": 
                self.__val = val
                self.__unpackToString()
             else:
                self.__val = None
            
          # Store long binaries as a filename and an offset
          else:    
             self.__val = None
             self.__bytes = None
             self.__dicom = dcm.name             
             self.__offset = dcm.tell()             
             self.__bytesRead += self.__vl
             dcm.seek(self.__vl, os.SEEK_CUR)
             
   def __readDicom(self, dcm, numBytes):
       self.__bytesRead += numBytes
       return dcm.read(numBytes)

   def __bin2str(self, b):
       h = hex(b)
       s = str(h).replace("0x", "")
       for i in range(len(s),4):
           s = "0"+s
       return s
   
   def __tagstr(self, group, element):
       g = self.__transferSyntax.unpack("H", group) # unsigned short
       e = self.__transferSyntax.unpack("H", element) # unsigned short
       s = self.__bin2str(g) + self.__bin2str(e)
       s = s.lower()
       return s

   def __unpackToString(self):

       # ------------------------------------------------------------
       # Format C Type 			Python type 	Standard size
       # ------------------------------------------------------------
       # x 	pad byte 		no value 	  	 
       # c 	char 			string of length 	1
       # b 	signed char 		integer 		1
       # B 	unsigned char 		integer 		1
       # ? 	_Bool 			bool 			1
       # h 	short 			integer 		2
       # H 	unsigned short 		integer 		2
       # i 	int 			integer 		4
       # I 	unsigned int 		integer 		4
       # l 	long 			integer 		4
       # L 	unsigned long 		integer 		4
       # q 	long long 		integer 		8
       # Q 	unsigned long long 	integer 		8
       # f 	float 			float 			4
       # d 	double 			float 			8
       # s 	char[] 			string 	  	 
       # p 	char[] 			string 	  	 
       # P 	void * 			integer 	  	
       # ------------------------------------------------------------

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

