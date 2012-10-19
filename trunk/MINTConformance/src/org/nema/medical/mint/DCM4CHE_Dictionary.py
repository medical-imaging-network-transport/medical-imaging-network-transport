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

# -----------------------------------------------------------------------------
# DCM4CHE_Dictionary
# -----------------------------------------------------------------------------
class DCM4CHE_Dictionary(DataDictionary):
   
   ROOT_TAG = "dictionary"
   URL = "http://soft-trac.net/dicom/dictionary.xml"

   def __init__(self):
       DataDictionary.__init__(self, self.ROOT_TAG, self.URL)

   def _parseXml(self):
       nodes = self._xml.childrenWithName("element")
       for node in nodes:
           element = DataDictionaryElement(node)
           self._elements[element.tag()] = element
                                
# -----------------------------------------------------------------------------
# main
# -----------------------------------------------------------------------------
def main():
    progName = sys.argv[0]
    (options, args)=getopt.getopt(sys.argv[1:], "o:h")

    # ---
    # Check for output option.
    # ---
    output = ""
    for opt in options:
        if opt[0] == "-o":
           output = opt[1]
           
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
       if help or argc > 0:
          print "Usage:", progName, "[options]"
          print "  -o <output>: output filename (defaults to stdout)"
          print "  -h:          displays usage"
          sys.exit(1)
          
       # ---
       # Read data dictionary.
       # ---
       dataDictionary = DCM4CHE_Dictionary()
       dataDictionary.setOutput(output)
       dataDictionary.debug()
       dataDictionary.tidy()

    except Exception, exception:
       traceback.print_exception(sys.exc_info()[0], 
                                 sys.exc_info()[1],
                                 sys.exc_info()[2])
       sys.exit(1)
       
if __name__ == "__main__":
   main()
