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

from struct import unpack

STUDY_INSTANCE_UID_TAG  = "0020000d"
SERIES_INSTANCE_UID_TAG = "0020000e" 
SOP_INSTANCE_UID_TAG    = "00080018" 
PIXEL_DATA_TAG          = "7fe00010"

reservedVRs = ("OB", "OW", "OF", "SQ", "UT", "UN")
binaryVRs   = ("SS", "US", "SL", "UL", "FL", "FD", "OB", "OW", "OF", "AT")

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

# -----------------------------------------------------------------------------
# DicomInstance
# -----------------------------------------------------------------------------
class DicomInstance():
   def __init__(self, dcmName):
       self.__dcmName = dcmName
       self.__attbs = {}
       self.__tags = []
       self.__open()   
       
   def studyInstanceUID(self):
       return self.value(STUDY_INSTANCE_UID_TAG)
       
   def seriesInstanceUID(self):
       return self.value(SERIES_INSTANCE_UID_TAG)

   def sopInstanceUID(self):
       return self.value(SOP_INSTANCE_UID_TAG)
       
   def numTags(self):
       return len(self.__tags)
       
   def tag(self, n):
       return self.__tags[n]
       
   def isUnknown(self, tag):
       return int(self.group(tag),16) < 8
       
   def isPrivate(self, tag):
       return int(self.group(tag),16) % 2 != 0
       
   def isImplicit(self, tag):
       return self.vr(tag) == "  "
       
   def isBinary(self, tag):
       return self.vr(tag) in binaryVRs or tag == PIXEL_DATA_TAG
       
   def tagName(self, tag):
       if part10Tags.has_key(tag):
          return part10Tags[tag]
       elif studyTags.has_key(tag):
          return studyTags[tag]
       elif seriesTags.has_key(tag):
          return seriesTags[tag]
       else:
          return "Unknown"
   
   def group(self, tag):
       return tag[0:4]
   
   def element(self, tag):
       return tag[4:]
   
   def vr(self, tag):
       return self.__attb(tag, 0)
       
   def length(self, tag):
       return self.__attb(tag, 1)
       
   def value(self, tag):
       return self.__attb(tag, 2)
       
   def _print(self):
       s = self
       numTags = s.numTags()
       for n in range(0, numTags):
           tag = s.tag(n)
           val = s.value(tag)
           if tag == PIXEL_DATA_TAG:
              val = "<Pixel Data>"
           elif self.isPrivate(tag):
              val = "<Private>"
           elif self.isUnknown(tag):
              val = "<Unknown>"
           print tag, s.tagName(tag), s.vr(tag), s.length(tag), val
           
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
       group=dcm.read(2)
       while group != "":
          # ---
          # Read the tag
          # ---
          element=dcm.read(2)
          tag=self.__getTag(group, element)
          
          # ---
          # If this is an unknown tag then the VR is explicit
          # ---
          vr="  "    
          if self.isUnknown(tag):
             vr=dcm.read(2)
          
          # ---
          # Read the length
          # ---
          if vr in reservedVRs: # Explicit with reserve
             dcm.read(2) # throw away reserve
             n = 4
             vl=dcm.read(4)
             vl = unpack('<L', vl)
          elif vr != "  ": # Explicit without reserve
             vl=dcm.read(2)
             vl = unpack('<h', vl)
          else: # Implicit
             n = 4
             vl=dcm.read(4)
             vl = unpack('<L', vl)
          vl=vl[0]
          
          # ---
          # Read the val
          # ---
          val=dcm.read(vl)
          if self.isBinary(tag):
             val = unpack('B'*len(val), val)
             vl = len(val)
          else:
             val = val[0:vl]
             if len(val) > 0 and not val[-1].isalnum():
                val = val.rstrip(val[-1]) # strip non alphanumerics
             val = val.rstrip() # strip whitespace
             vl = len(val) # reset length
                 
          # ---
          # Store this element and get next one.
          # ---
          self.__attbs[tag] = (vr, vl, val)
          group=dcm.read(2)
          
       dcm.close()
       self.__tags = self.__attbs.keys()
       self.__tags.sort()
   
   def __bin2str(self, b):
       h = hex(b[0])
       s = str(h).replace("0x", "")
       for i in range(len(s),4):
           s = "0"+s
       return s
   
   def __getTag(self, group, element):
       g = unpack('<h', group)
       e = unpack('<h', element)
       s = self.__bin2str(g) + self.__bin2str(e)
       s = s.lower()
       return s
   
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
    progName = sys.argv[0]
    (options, args)=getopt.getopt(sys.argv[1:], "")
    
    try:
       if len(args) != 1:
          print "Usage", progName, "<dicom_file>"
          sys.exit(1)
          
       # ---
       # Read dicom.
       # ---
       dcmName = sys.argv[1];
       instance = DicomInstance(dcmName)
       instance._print()
                        
    except Exception, exception:
       traceback.print_exception(sys.exc_info()[0], 
                                 sys.exc_info()[1],
                                 sys.exc_info()[2])
       sys.exit(1)
       
if __name__ == "__main__":
   main()

