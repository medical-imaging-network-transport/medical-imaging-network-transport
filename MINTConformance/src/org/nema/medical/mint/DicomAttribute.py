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

import sys 

from struct import unpack

# -----------------------------------------------------------------------------
# DicomAttribute
# -----------------------------------------------------------------------------
class DicomAttribute():
   
   def __init__(self, dcm, explicit, endian):
       self.__tag    = None
       self.__vr     = ""
       self.__vl     = -1
       self.__val    = ""
       self.__endian = endian
       self.__items  = []
       
       # ---
       # Read the next tag
       # ---
       group=dcm.read(2)
       if group == "": return
          
       element=dcm.read(2)
       self.__tag=self.__getTag(group, element)
          
       # ---
       # If explicit or Part 10 header tag then read the VR.
       # ---
       self.__vr=""
       if self.isItemStart() or self.isItemStop() or self.isSequenceStop():
          pass # no VR
       elif explicit or self.isPart10Header():
          self.__vr=dcm.read(2)
                 
       # ---
       # Read the length
       # ---
       if self.__vr in self.reservedVRs: # Explicit with reserve
          dcm.read(2) # throw away reserve
          vl=dcm.read(4)
          vl = unpack(endian+"L", vl)
       elif self.__vr != "": # Explicit without reserve
          vl=dcm.read(2)
          vl = unpack(endian+"h", vl)
       else: # Implicit
          if self.implicitVRs.has_key(self.__tag):
             self.__vr=self.implicitVRs[self.__tag]
          vl=dcm.read(4)
          vl = unpack(endian+"L", vl)
       self.__vl=vl[0]

       # ---
       # Read Sequence Data Elements
       # ---
       if self.__vr == "SQ":
          item = DicomAttribute(dcm, True, endian)
          while not item.isSequenceStop():
             attrs = []
             self.__items.append(attrs)
             itemAttr = DicomAttribute(dcm, True, endian)
             while not itemAttr.isItemStop():
                 attrs.append(itemAttr)
                 itemAttr = DicomAttribute(dcm, True, endian)
             item = DicomAttribute(dcm, True, endian)

       # Read the val
       self.__readVal(dcm, endian)
          
   def group(self)   : return self.__tag[0:4]
   def element(self) : return self.__tag[4:]
   def tag(self)     : return self.__tag;
   def vr (self)     : return self.__vr;
   def vl (self)     : return self.__vl;
   def val(self)     : return self.__val;
   
   def valstr(self):
       if self.isPixelData():
          return "<Pixel Data>"
       elif self.isBinary():
          return "<Binary Data>"
       elif self.isUnknown():
          return "<Unknown>"
       elif self.__val != "":
          return self.__val

   def tagName(self):
       tag = self.__tag
       if self.part10Tags.has_key(tag):
          return self.part10Tags[tag]
       elif self.studyTags.has_key(tag):
          return self.studyTags[tag]
       elif self.seriesTags.has_key(tag):
          return self.seriesTags[tag]
       else:
          return "Unknown"

   def numItems(self): return len(self.__items)
   def item(self, i):  return self.__items[i]
   
   def numItemAttributes(self, i): return len(self.item(i))
   def itemAttribute(self, i, j): return self.item(i)[j]
          
   def isValid(self)        : return self.__tag != None
   def isPixelData(self)    : return self.__tag == self.PIXEL_DATA_TAG
   def isPart10Header(self) : return int(self.group(),16) < 8
   def isUnknown(self)      : return self.__vr == "UN"
   def isPrivate(self)      : return int(self.group(),16) % 2 != 0
   def isBinary(self)       : return self.__vr in self.binaryVRs or self.__tag == self.PIXEL_DATA_TAG
   def isItemStart(self)    : return self.__tag == self.ITEM_TAG
   def isItemStop(self)     : return self.__tag == self.ITEM_DELIMITATION_TAG
   def isSequencStart(self) : return self.__ve == "SQ"
   def isSequenceStop(self) : return self.__tag == self.SQ_DELIMITATION_TAG

   def toString(self, indent=""):
       s = "tag="+self.__tag+" vr="+self.__vr+" val= "

       if self.__vr == "SQ":
          indent += " "
          numItems = self.numItems()
          for i in range(0, numItems):
              s += "\n"+indent+"- Item\n"
              numItemAttributes = self.numItemAttributes(i)
              indent += " "
              s += indent+"- Attributes\n"
              indent += " "
              for j in range(0, numItemAttributes-1):
                s += indent+"- "+self.itemAttribute(i, j).toString(indent)+"\n"
              s += indent+"- "+self.itemAttribute(i, numItemAttributes-1).toString(indent)
              indent = indent[0:-2]

       elif self.__val != "":
          if self.isPixelData():
             s += "<Pixel Data>"
          elif self.isBinary():
             s += "<Binary Data>"
          elif self.isUnknown():
             s += "<Unknown>"
          elif self.__val != "":
             s += self.__val

       return s
       
   def __readVal(self, dcm, endian):
   
       # Check for undefined length
       if self.__vl == 0xffffffff:
          return
          
       self.__val=dcm.read(self.__vl)
          
       # Signed Short
       if   self.__vr=="SS":
          self.__val = self.__val2str(2, "h", "%d")

       # Unsigned Short
       elif self.__vr=="US":
          self.__val = self.__val2str(2, "H", "%d")

       # Signed Long
       elif self.__vr=="SL":
          self.__val = self.__val2str(4, "l", "%d")
          
       # Unsigned Long
       elif self.__vr=="UL":
          self.__val = self.__val2str(4, "L", "%d")

       # Single Precision Float
       elif self.__vr=="FL":
          self.__val = self.__val2str(4, "f", "%.5f")

       # Double Precision Float
       elif self.__vr=="FD":
          self.__val = self.__val2str(8, "d", "%.5f")
          
       # TODO: Other Float 4 FLOAT
       elif self.__vr=="OF":                       
          self.__val = self.__val2str(4, "f", "%.5f")

       # Attribute Tag 4 ULONG
       elif self.__vr=="AT":                       
          self.__val = self.__val2str(4, "L", "%d")
          
       elif self.isBinary():
          self.__val = unpack('B'*len(self.__val), self.__val)
          self.__vl = len(self.__val)
          
       else:
          self.__val = self.__val[0:self.__vl]
          if len(self.__val) > 0 and not self.__val[-1].isalnum():
             self.__val = self.__val.rstrip(self.__val[-1]) # strip non alphanumerics
          self.__val = self.__val.rstrip() # strip whitespace
          self.__vl = len(self.__val) # reset length

   def __bin2str(self, b):
       h = hex(b[0])
       s = str(h).replace("0x", "")
       for i in range(len(s),4):
           s = "0"+s
       return s
   
   def __getTag(self, group, element):
       endian = self.__endian
       g = unpack(endian+"H", group)
       e = unpack(endian+"H", element)
       s = self.__bin2str(g) + self.__bin2str(e)
       s = s.lower()
       return s

   def __val2str(self, size, type, format):
       vals = ""
       numVals = self.__vl / size
       for n in range(0, numVals):
           i = n * size
           val = unpack(self.__endian+type, self.__val[i:i+size])[0]
           vals += str(format % val)+"\\"    
       return vals[0:-1]
   
   def __str__(self):
       return self.toString()

   PIXEL_DATA_TAG          = "7fe00010"
   ITEM_TAG                = "fffee000"
   ITEM_DELIMITATION_TAG   = "fffee00d"
   SQ_DELIMITATION_TAG     = "fffee0dd"
   
   reservedVRs = ("OB", "OW", "OF", "SQ", "UT", "UN")
   binaryVRs   = ("OB", "OW", "UN")

   # Other Binary VRs that are represented as text for clarity
   # ("SS", "US", "SL", "UL", "FL", "FD", "OF", "AT")

   part10Tags = {
      "00020000" : "File Meta Information",
      "00020001" : "File Meta Information Version",
      "00020002" : "Media Storage SOP Class UID",
      "00020003" : "Media Storage SOP Instance UID",
      "00020010" : "Transfer Syntax UID",
      "00020012" : "Implementation Class UID",
      "00020013" : "Implementation Version Name",
      "00020016" : "Source Application Entity Title" 
   }
 
   studyTags = {
      "00080020" : "Study Date",
      "00080030" : "Study Time",
      "00080050" : "Accession Number",
      "00080051" : "Issuer Of Accession Number Sequence",
      "00080090" : "Referring Physician Name",
      "00080096" : "Referring Physician Identification Sequence",
      "00081030" : "Study Description",
      "00081032" : "Procedure Code Sequence",
      "00081048" : "Physicians Of Record",
      "00081049" : "Physicians Of Record Identification Sequence",
      "00081060" : "Name Of Physicians Reading Study",
      "00081062" : "Physicians Reading Study Identification Sequence",
      "00081080" : "Admitting Diagnoses Description",
      "00081084" : "Admitting Diagnoses Code Sequence",
      "00081110" : "Referenced Study Sequence",
      "00081120" : "Referenced Patient Sequence",
      "00100010" : "Patient Name",
      "00100020" : "Patient ID",
      "00100030" : "Patient Birth Date",
      "00100032" : "Patient Birth Time",
      "00100040" : "Patient Sex",
      "00101000" : "Other Patient ID",
      "00101001" : "Other Patient Names",
      "00101002" : "Other Patient IDs Sequence",
      "00101010" : "Patient Age",
      "00101020" : "Patient Size",
      "00101030" : "Patient Weight",
      "00102160" : "Ethnic Group",
      "00102180" : "Occupation",
      "001021b0" : "Additional Patient History",
      "00102201" : "Patient Species Description",
      "00102202" : "Patient Species Code Sequence",
      "00102203" : "Patient Sex Neutered",
      "00102292" : "Patient Breed Description",
      "00102293" : "Patient Breed Code Sequence",
      "00102294" : "Breed Registration Sequence",
      "00102297" : "Responsible Person",
      "00102298" : "Responsible Person Role",
      "00102299" : "Responsible Organization",
      "00104000" : "Patient Comments",
      "00120010" : "Clinical Trial Sponsor Name",
      "00120020" : "Clinical Trial Protocol ID",
      "00120021" : "Clinical Trial Protocol Name",
      "00120030" : "Clinical Trial Site ID",
      "00120031" : "Clinical Trial Site Name",
      "00120040" : "Clinical Trial Subject ID",
      "00120042" : "Clinical Trial Subject Reading ID",
      "00120050" : "Clinical Trial Time Point ID",
      "00120051" : "Clinical Trial Time Point Description",
      "00120062" : "Patient Identity Removed",
      "00120063" : "Deidentification Method",
      "00120064" : "Deidentification Method Code Sequence",
      "00120081" : "Clinical Trial Protocol Ethics Committee Name",
      "00120082" : "Clinical Trial Protocol Ethics Committee Approval Number",
      "00120083" : "Consent For Clinical Trial Use Sequence",
      "0020000d" : "Study Instance UID",
      "00200010" : "Study ID",
      "00321034" : "Requesting Service Code Sequence",
      "00380010" : "Admission ID",
      "00380014" : "Issuer Of Admission ID Sequence",
      "00380060" : "Service Episode ID",
      "00380062" : "Service Episode Description",
      "00380064" : "Issuer Of Service Episode ID Sequence",
      "00401012" : " Reason For Performed Procedure Code Sequence"
   }

   seriesTags = {
      "00080018" : "Image SOP Instance UID",
      "00080021" : "Series Date",
      "00080031" : "Series Time",
      "00080060" : "Modality",
      "0008103e" : "Series Description",
      "00081050" : "Performing Physician's Name",
      "00081052" : "Performing Physician Identification Sequence",
      "00081070" : "Operator's Name",
      "00081072" : "Operator Identification Sequence",
      "00081111" : "Referenced Performed Procedure Step Sequence",
      "00081250" : "Related Series Sequence",
      "00180015" : "Body Part Examined",
      "00181030" : "Protocol Name",
      "00185100" : "Patient Position",
      "0020000e" : "Series Instance UID",
      "00200011" : "Series Number",
      "00200060" : "Laterality",
      "00280108" : "Smallest Pixel Value in Series",
      "00280109" : "Largest Pixel Value in Series",
      "00400244" : "Performed Procedure Step Start Date",
      "00400245" : "Performed Procedure Step Start Time",
      "00400253" : "Performed Procedure Step ID",
      "00400254" : "Performed Procedure Step Description",
      "00400260" : "Performed Procedure Step Code Sequence",
      "00400275" : "Request Attribute Sequence",
      "00400280" : "Comments on the Performed Procedure Step",
      "7fe00010" : "Pixel Data"
   } 

   # TODO: Replace with DCM4CHE data dictionary
   implicitVRs = {
      "00280002" : "US",
      "00280006" : "US",
      "00280010" : "US",
      "00280011" : "US",
      "00280100" : "US",
      "00280101" : "US",
      "00280102" : "US",
      "00280103" : "US",
      "00080000" : "UL"
   }
       