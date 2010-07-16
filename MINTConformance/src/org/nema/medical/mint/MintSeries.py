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

from org.nema.medical.mint.MintAttribute import MintAttribute
from org.nema.medical.mint.MintInstance  import MintInstance
from org.nema.medical.mint.XmlNode       import XmlNode

# -----------------------------------------------------------------------------
# MintSeries
# -----------------------------------------------------------------------------
class MintSeries():
   
   def __init__(self, root):
       self.__attributes = {}
       self.__tags = []
       self.__normalizedInstanceAttributes = {}
       self.__normalizedTags = []
       self.__instances = []
       self.__read(root)
       
   def seriesInstanceUID(self): 
       return self.__seriesInstanceUID;
       
   def numAttributes(self):
       return len(self.__tags)
          
   def attribute(self, n):
       """
       Returns a MintAttribute at index n.
       """
       tag = self.__tags[n]
       return self.__attributes[tag]

   def attributeByTag(self, tag):
       """
       Returns a MintAttribute if tag is found, otherwise None.
       """
       if self.__attributes.has_key(tag):
          return self.__attributes[tag]
       else:
          return None

   def numNormalizedInstanceAttributes(self):
       return len(self.__normalizedTags)
          
   def normalizedInstanceAttribute(self, n):
       """
       Returns a MintAttribute at index n.
       """
       tag = self.__normalizedTags[n]
       return self.__normalizedInstanceAttributes[tag]

   def normalizedInstanceAttributeByTag(self, tag):
       """
       Returns a MintAttribute if tag is found, otherwise None.
       """
       if self.__normalizedInstanceAttributes.has_key(tag):
          return self.__normalizedInstanceAttributes[tag]
       else:
          return None

   def numInstances(self):
       return len(self.__instances)
          
   def instance(self, n):
       """
       Returns a MintInstance at index n.
       """
       return self.__instances[n]

   def toString(self):
       return self.__str__()
       
   def __read(self, root):
       self.__seriesInstanceUID = root.attributeWithName("seriesInstanceUID")

       # ---
       # Read Attributes
       # ---
       node = root.childWithName("Attributes")
       nodes = node.childrenWithName("Attr")
       for node in nodes:
           attb = MintAttribute(node)
           self.__attributes[attb.tag()] = attb
       self.__tags = self.__attributes.keys()
       self.__tags.sort()

       # ---
       # Read Normalized Instance Attributes
       # ---
       node = root.childWithName("NormalizedInstanceAttributes")
       nodes = node.childrenWithName("Attr")
       for node in nodes:
           attb = MintAttribute(node)
           self.__normalizedInstanceAttributes[attb.tag()] = attb
       self.__normalizedTags = self.__normalizedInstanceAttributes.keys()
       self.__normalizedTags.sort()
               
       # ---
       # Read Instances
       # ---
       node = root.childWithName("Instances")
       nodes = node.childrenWithName("Instance")
       for node in nodes:
           self.__instances.append(MintInstance(node)) 

   def __str__(self):
       s  = "  - Series Instance UID=" + self.__seriesInstanceUID + '\n'
       
       s += "   - Attributes\n"       
       numAttributes = self.numAttributes()
       for n in range(0, numAttributes):
           s += "      "+self.attribute(n).toString()+'\n'

       s += "   - Normalized Instance Attributes\n"
       numNormalizedInstanceAttributes = self.numNormalizedInstanceAttributes()
       for n in range(0, numNormalizedInstanceAttributes):
           s += "      "+self.normalizedInstanceAttribute(n).toString()+'\n'

       s += "   - Instances\n"
       numInstances = self.numInstances()
       for n in range(0, numInstances):
           instance = self.instance(n)
           s += instance.toString()

       return s

