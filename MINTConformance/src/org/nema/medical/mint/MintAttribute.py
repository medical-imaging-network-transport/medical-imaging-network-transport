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

from org.nema.medical.mint.DataDictionaryElement import DataDictionaryElement
from org.nema.medical.mint.XmlNode               import XmlNode

# -----------------------------------------------------------------------------
# MintAttribute
# -----------------------------------------------------------------------------
class MintAttribute():

   binaryVRs = ("SS", "US", "SL", "UL", "FL", "FD", "OB", "OW", "OF", "AT")
     
   def __init__(self, node):
       self.__tag   = node.attributeWithName("tag")
       self.__vr    = node.attributeWithName("vr")
       self.__val   = node.attributeWithName("val")
       self.__bid   = node.attributeWithName("bid")
       self.__items = []
       
       if self.__val == None:
          self.__val = ""
          
       if self.__vr == "SQ":
          items = node.childrenWithName("Item")
          if items != None:
             for item in items:
                 attributeList = []
                 self.__items.append(attributeList)
                 attributes = item.childWithName("Attributes")
                 if attributes != None:
                    attrs = attributes.childrenWithName("Attr")
                    for attr in attrs:
                        attributeList.append(MintAttribute(attr))
                
   def tag(self): return self.__tag;
   def vr (self): return self.__vr;
   def val(self): return self.__val;
   def bid(self): return self.__bid;
   
   def numItems(self): return len(self.__items)
   def item(self, i):  return self.__items[i]
   
   def numItemAttributes(self, i): return len(self.item(i))
   def itemAttribute(self, i, j): return self.item(i)[j]
   
   def isBinary(self): return self.__vr in MintAttribute.binaryVRs
       
   def __str__(self):
       return self.toString().encode(DataDictionaryElement.UNICODE)

   def toString(self, indent=""):
       s = indent+"tag="+self.__tag+" vr="+self.__vr
       if self.__val != "":
          s += " val="+self.__val
       if self.__bid != None:
          s += " bid="+self.__bid

       numItems = self.numItems()
       for i in range(0, numItems):
           indent += " "
           s += "\n"+indent+"- Item\n"
           numItemAttributes = self.numItemAttributes(i)
           indent += " "
           s += indent+"- Attributes\n"
           indent += " "
           for j in range(0, numItemAttributes):
               s += self.itemAttribute(i, j).toString(indent)
               if s != numItemAttributes-1: s += '\n'
           indent = indent[0:-1]
           s += indent+"- Attributes\n"
           indent = indent[0:-1]
           s += indent+"- Item"
           indent = indent[0:-1]
           
       return s
