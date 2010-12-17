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
       self.__tag     = node.attributeWithName("tag")
       self.__vr      = node.attributeWithName("vr")
       self.__val     = node.attributeWithName("val")
       self.__bid     = node.attributeWithName("bid")
       self.__bytes   = node.attributeWithName("bytes")
       self.__bsize   = node.attributeWithName("bsize")
       self.__items   = []
       
       if self.__val == None:
          self.__val = ""
       if self.__bid == None:
          self.__bid = ""
       if self.__bytes == None:
          self.__bytes = ""
       if self.__bsize == None:
          self.__bsize = "0"
          
       if self.__vr == "SQ":
          items = node.childrenWithName("item")
          if items != None:
             for item in items:
                 attributeList = []
                 self.__items.append(attributeList)
                 attributes = item.childWithName("attributes")
                 if attributes != None:
                    attrs = attributes.childrenWithName("attr")
                    for attr in attrs:
                        attributeList.append(MintAttribute(attr))

   def tag(self)     : return self.__tag
   def vr (self)     : return self.__vr
   def val(self)     : return self.__val
   def bid(self)     : return self.__bid
   def bytes(self)   : return self.__bytes
   def bsize(self)   : return self.__bsize
   
   def numItems(self): return len(self.__items)
   def item(self, i):  return self.__items[i]
   
   def numItemAttributes(self, i): return len(self.item(i))
   def itemAttribute(self, i, j): return self.item(i)[j]
   
   def isBinary(self): return self.__vr in MintAttribute.binaryVRs
       
   def debug(self, indent=""):
       print indent+"tag =", self.__tag, "vr =", self.__vr,
       if self.__val != "":
          print "val =", self.__val.encode('ascii', 'replace'),
       if self.__bid != "":
          print "bid =", self.__bid,
       if self.__bytes != "":
          print "bytes =", self.__bytes,
       if self.__bsize != 0:
          print "bsize =", self.__bsize,
       print

       numItems = self.numItems()
       for i in range(0, numItems):
           indent += " "
           print indent, "- item"
           numItemAttributes = self.numItemAttributes(i)
           indent += " "
           print indent, "- attributes"
           indent += " "
           for j in range(0, numItemAttributes):
               self.itemAttribute(i, j).debug(indent)
           indent = indent[0:-1]
           print indent, "- attributes"
           indent = indent[0:-1]
           print indent, "- item"
           indent = indent[0:-1]

   def __repr__(self):
       if self.__vr == "SQ":
          raise ValueError("No string representation for sequence attribute "+self.__tag)
       s  = self.__tag+" "+self.__vr+" "+self.__val.encode('ascii', 'replace')+" "
       s += self.__bid+" "+self.__bytes+" "+self.__bsize

       return s
