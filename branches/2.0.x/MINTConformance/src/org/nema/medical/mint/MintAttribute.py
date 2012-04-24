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

import sys

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
       self.__bytes = node.attributeWithName("bytes")
       self.__bsize = node.attributeWithName("bsize")
       self.__items = []

       items = node.childrenWithName("item")
       attributes = node.childWithName("attributes")

       # ---
       # Read nested tags, if any.
       # ---
       if len(items) > 0:
          for item in items:
              #self.__tag == "fffee000"
              self.__items.append(MintAttribute(item))
              
       elif attributes != None:
          attrs = attributes.childrenWithName("attr")
          for attr in attrs:
              #self.__tag == "fffee000"
              self.__items.append(MintAttribute(attr))
       
       if self.__tag != None:
          self.__tag = self.__tag.lower()
       if self.__val == None:
          self.__val = ""
       if self.__vr == None:
          self.__vr = "UN"
       if self.__bid == None:
          self.__bid = ""
       if self.__bytes == None:
          self.__bytes = ""
       if self.__bsize == None:
          self.__bsize = "0"
              
   def tag(self)     : return self.__tag
   def vr (self)     : return self.__vr
   def val(self)     : return self.__val
   def bid(self)     : return self.__bid
   def bytes(self)   : return self.__bytes
   def bsize(self)   : return self.__bsize
   def numItems(self): return len(self.__items)
   def item(self, i) : return self.__items[i]
   def isBinary(self): return self.__vr in MintAttribute.binaryVRs

   def toString(self):
       return self.tag()+" "+self.vr()+" "+self.bid()+" "+self.bytes()+" "+self.bsize()
       
   def debug(self, indent=""):

       if self.__tag != None:
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
       if numItems > 0:
          label = "item"
          if numItems > 1: label = "attributes"
          print indent, "-", label
          indent += " "
          for i in range(0, numItems):
              self.item(i).debug(indent)
          indent = indent[0:-1]
          print indent, "-", label

   def __repr__(self):
       if self.__vr == "SQ":
          raise ValueError("No string representation for sequence attribute "+self.__tag)
       s  = self.__tag+" "+self.__vr+" "+self.__val.encode('ascii', 'replace')+" "
       s += self.__bid+" "+self.__bytes+" "+self.__bsize

       return s
