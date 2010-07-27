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

from org.nema.medical.mint.XmlNode import XmlNode

# -----------------------------------------------------------------------------
# MintAttribute
# -----------------------------------------------------------------------------
class MintAttribute():

   binaryVRs = ("SS", "US", "SL", "UL", "FL", "FD", "OB", "OW", "OF", "AT")
   
   def __init__(self, node):
       self.__tag = node.attributeWithName("tag")
       self.__vr  = node.attributeWithName("vr")
       self.__val = node.attributeWithName("val")
       self.__bid = node.attributeWithName("bid")
       
       if self.__val == None:
          self.__val = ""
                
   def tag(self): return self.__tag;
   def vr (self): return self.__vr;
   def val(self): return self.__val;
   def bid(self): return self.__bid;
   
   def isBinary(self): return self.__vr in MintAttribute.binaryVRs
       
   def toString(self):
       return self.__str__()
       
   def __str__(self):
       s = "tag="+self.__tag+" vr="+self.__vr
       s += " val="+self.__val
       if self.__bid != None:
          s += " bid="+self.__bid
       return s
       