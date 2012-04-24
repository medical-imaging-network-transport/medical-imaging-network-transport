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

from org.nema.medical.mint.DataDictionaryElement import DataDictionaryElement
from org.nema.medical.mint.DataDictionary        import DataDictionary 

from org.nema.medical.mint.DCM4CHE_Dictionary    import DCM4CHE_Dictionary

# -----------------------------------------------------------------------------
# MINT_Dictionary
# -----------------------------------------------------------------------------
class MINT_Dictionary(DataDictionary):
   
   ROOT_TAG = "metadata"

   def __init__(self, hostname, port):
       DataDictionary.__init__(self, self.ROOT_TAG, "http://"+hostname+":"+str(port)+"/MINTServer/types/DICOM")

   def _parseXml(self):
       attributes = self._xml.childWithName("attributes")
       if attributes != None:
          nodes = attributes.childrenWithName("element")
          for node in nodes:
              element = DataDictionaryElement(node)
              self._elements[element.tag()] = element
                                
# -----------------------------------------------------------------------------
# main
# -----------------------------------------------------------------------------
def main():
    progName = sys.argv[0]
    (options, args)=getopt.getopt(sys.argv[1:], "p:o:vwh")

    # ---
    # Check for output option.
    # ---
    port = "8080"
    for option in options:
        if option[0] == "-p": port = option[1]

    # ---
    # Check for output option.
    # ---
    output = ""
    for opt in options:
        if opt[0] == "-o":
           output = opt[1]
           
    # ---
    # Check for verbose option.
    # ---
    verbose = False
    for opt in options:
        if opt[0] == "-v":
           verbose = True
           
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
       if help or argc != 1:
          print "Usage:", progName, "[options] <hostname>"
          print "  -p <port>:   defaults to 8080"
          print "  -o <output>: output filename (defaults to stdout)"
          print "  -v:          verbose"
          print "  -w:          show warnings"
          print "  -h:          displays usage"
          sys.exit(1)
          
       # ---
       # Read data dictionary.
       # ---
       hostname = args[0]
       dataDictionary = MINT_Dictionary(hostname, port)
       dataDictionary.setOutput(output)
       dataDictionary.setVerbose(verbose)
       dataDictionary.setWarnings(warnings)

       refDataDictionary = DCM4CHE_Dictionary()
       diffs = dataDictionary.compare(refDataDictionary)
       refDataDictionary.tidy()
       dataDictionary.tidy()

    except Exception, exception:
       traceback.print_exception(sys.exc_info()[0], 
                                 sys.exc_info()[1],
                                 sys.exc_info()[2])
       sys.exit(1)
       
if __name__ == "__main__":
   main()
