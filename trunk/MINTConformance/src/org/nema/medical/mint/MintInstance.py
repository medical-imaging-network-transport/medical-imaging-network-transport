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
from org.nema.medical.mint.XmlNode       import XmlNode

# -----------------------------------------------------------------------------
# MintInstance
# -----------------------------------------------------------------------------
class MintInstance():
   
   def __init__(self, root):    
       self.__sopInstanceUID = ""
       self.__transferSyntaxUID = ""
       self.__attributes = {}
       self.__tags = []
       self.__read(root)
       
   def sopInstanceUID(self): 
       return self.__sopInstanceUID;
       
   def transferSyntaxUID(self): 
       return self.__transferSyntaxUID;
       
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
       
   def find(self, tag):
       return self.attributeByTag(tag)
       
   def toString(self, indent=""):
       s  = indent+"- SOP Instance UID="+self.sopInstanceUID()+'\n'       
       s += indent+"- Transfer Syntax UID="+self.transferSyntaxUID()+"\n"       
       s += indent+"- Attributes\n"
       indent += " "
       numAttributes = self.numAttributes()
       for n in range(0, numAttributes):
           s += self.attribute(n).toString(indent)+'\n'
       indent = indent[0:-1]
       
       return s

   def __read(self, root):
       self.__sopInstanceUID = root.attributeWithName("sopInstanceUID")
       self.__transferSyntaxUID = root.attributeWithName("transferSyntaxUID")

       # ---
       # Read Attributes
       # ---
       node = root.childWithName("Attributes")
       if node != None:
          nodes = node.childrenWithName("Attr")
          for node in nodes:
              attb = MintAttribute(node)
              self.__attributes[attb.tag()] = attb
          self.__tags = self.__attributes.keys()
          self.__tags.sort()
       
   def __str__(self):
       return self.toString()
       