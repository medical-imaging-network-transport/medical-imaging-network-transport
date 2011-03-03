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

from org.nema.medical.mint.DicomInstance  import DicomInstance

# -----------------------------------------------------------------------------
# DicomSeries
# -----------------------------------------------------------------------------
class DicomSeries():
   def __init__(self):
       self.__seriesInstanceUID = ""
       self.__instances = {}
       self.__sopInstanceUIDs = []
       
   def tidy(self):
       """
       Removes tempory binary items.
       """
       numInstances = self.numInstances()
       for n in range(0, numInstances):
           instance = self.instance(n).tidy()

   def seriesInstanceUID(self):
       return self.__seriesInstanceUID
       
   def numInstances(self):
       return len(self.__instances)
       
   def instance(self, n):
       if len(self.__sopInstanceUIDs) == 0:
          self.__sopInstanceUIDs = self.__instances.keys()
          self.__sopInstanceUIDs.sort()
	  
       return self.__instances[self.__sopInstanceUIDs[n]]
       
   def instanceByUID(self, sopInstanceUID):
       return self.__instances[sopInstanceUID]

   def append(self, instance):
   
       if self.__seriesInstanceUID == "":
          self.__seriesInstanceUID = instance.seriesInstanceUID()
       else:
          assert self.__seriesInstanceUID == instance.seriesInstanceUID()

       self.__sopInstanceUIDs = []	  
       self.__instances[instance.sopInstanceUID()] = instance
       
   def debug(self, output):
       if output == None:
          print ">> Series", self.seriesInstanceUID()
       else:
          output.write(">> Series "+self.seriesInstanceUID()+"\n")
       numInstances = self.numInstances()
       for n in range(0, numInstances):
           instance = self.instance(n)
           instance.debug(output)
                 