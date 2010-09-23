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

import string

from org.nema.medical.mint.XmlNode import XmlNode

# -----------------------------------------------------------------------------
# DataDictionaryElement
# -----------------------------------------------------------------------------
class DataDictionaryElement():
   
   def __init__(self, node):
       self.__tag     = string.upper(node.attributeWithName("tag"))
       self.__keyword = node.attributeWithName("keyword")
       vrs            = node.attributeWithName("vr")
       self.__vm      = node.attributeWithName("vm")
       self.__ret     = node.attributeWithName("ret")
       self.__name    = node.getText()
       
       self.__vrs = vrs.split("|")
                           
   def tag(self)     : return self.__tag;
   def keyword(self) : return self.__keyword;
   def vrs(self  )   : return self.__vrs;
   def numVRs(self)  : return len(self.__vrs)
   def vr(self, n)   : return self.__vrs[n];
   def vrs(self  )   : return self.__vrs;
   def vm(self)      : return self.__vm;
   def ret(self)     : return self.__ret;
   def name(self)    : return self.__name;
   
   def __str__(self):
       s  = "tag="+self.__tag+", "
       s += "keyword="+self.__keyword+", "
       s += "vr="+string.join(self.__vrs, "|")+", "
       s += "vm="+self.__vm+", "
       s += "ret="+self.__ret+", "
       s += "name="+self.__name.encode('ascii', 'replace')
                  
       return s
