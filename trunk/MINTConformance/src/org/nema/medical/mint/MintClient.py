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

import xml.parsers.expat

from org.nema.medical.mint.XmlDocument import XmlDocument
from org.nema.medical.mint.XmlNode     import XmlNode

# -----------------------------------------------------------------------------
# MintClient
# -----------------------------------------------------------------------------
class MintClient():
   
   def __init__(self):
       pass
       
   def connect(self, hostname, port):
       self.__serverUrl = "http://"+hostname+":"+port+"/MINTServer"
       
   def lookupStudyUUID(self, studyUID):
       studiesUrl = self.__serverUrl+"/studies"
       studiesXml = XmlDocument("studySearchResults")
       studiesXml.readFromURL(studiesUrl)
       nodes = studiesXml.childrenWithName("study")
       for node in nodes:
           studyUUID = node.attributeWithName("studyUUID")
           if self.matches(studyUUID, studyUID): return studyUUID
       return "UNKNOWN_UID"
           
   def lookupStudyUID(self, studyUUID):
       studiesUrl = self.__serverUrl+"/studies"
       studiesXml = XmlDocument("studySearchResults")
       studiesXml.readFromURL(studiesUrl)
       nodes = studiesXml.childrenWithName("study")
       for node in nodes:
           uuid = node.attributeWithName("studyUUID")
           if uuid == studyUUID:
              summaryUrl = self.__serverUrl+"/studies/"+studyUUID+"/DICOM/summary"
              summaryXml = XmlDocument("study")
              summaryXml.readFromURL(summaryUrl)
              return summaryXml.attributeWithName("studyInstanceUID")   
       return "UNKNOWN_UUID"
           
   def matches(self, studyUUID, studyUID):
       summaryUrl = self.__serverUrl+"/studies/"+studyUUID+"/DICOM/summary"
       summaryXml = XmlDocument("study")
       summaryXml.readFromURL(summaryUrl)
       if studyUID == summaryXml.attributeWithName("studyInstanceUID") : return studyUUID   
       return False
       
# -----------------------------------------------------------------------------
# main
# -----------------------------------------------------------------------------
def main():
    progName = sys.argv[0]
    (options, args)=getopt.getopt(sys.argv[1:], "p:u:i:o:h")
    
    try:
       help = False
       for option in options:
           if option[0] == "-h": help = True
       
       if len(args) != 1 or help:
          print "Usage", progName, "[options] <hostname>"
          print "  -p <port>:   defaults to 8080"
          print "  -i <UID>:    outputs server UUID that maps to study UID"
          print "  -u <UUID>:   outputs study UID that maps to server UUID"
          print "  -o <output>: output filename (defaults to stdout)"
          print "  -h:          help"
          sys.exit(1)
          
       hostname = args[0];

       port = "8080"
       for option in options:
           if option[0] == "-p": port = option[1]

       studyUID = ""
       for option in options:
           if option[0] == "-i": studyUID = option[1]

       studyUUID = ""
       for option in options:
           if option[0] == "-u": studyUUID = option[1]

       output = ""
       for option in options:
           if option[0] == "-o": 
              output = option[1]
              out = open(output, "w")

       mintClient = MintClient()
       mintClient.connect(hostname, port)

       if studyUID != "":
          if output == "":
             print mintClient.lookupStudyUUID(studyUID);
          else:
             out.write(mintClient.lookupStudyUUID(studyUID)+"\n");
                     
       if studyUUID != "":
          if output == "":
             print mintClient.lookupStudyUID(studyUUID);
          else:
             out.write(mintClient.lookupStudyUID(studyUUID)+"\n");
                    
       if output != "": out.close()
       
    except IOError, exception:
       print "Bad port?", exception
       sys.exit(2)

    except xml.parsers.expat.ExpatError:
       print "Bad hostname?"
       sys.exit(2)

    except Exception, exception:
       traceback.print_exception(sys.exc_info()[0], 
                                 sys.exc_info()[1],
                                 sys.exc_info()[2])
       sys.exit(2)
       
if __name__ == "__main__":
   main()
