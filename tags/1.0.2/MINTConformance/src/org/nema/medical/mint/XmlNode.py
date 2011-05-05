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

# -----------------------------------------------------------------------------
# XmlNode
# -----------------------------------------------------------------------------
class XmlNode:
    """
    This class handles an XML node.
    """

    def __init__(self, name):
        """
        Construct from XML tag name.
        """
        self.name     = name
        self.parent   = None
        self.children = []
        self.text     = ''
        self.attbDict = {}

    def getName(self):   return self.name
    def getParent(self): return self.parent
    def getText(self):   return self.text
    
    def setName(self, name):     self.name   = name
    def setParent(self, parent): self.parent = parent
    def setText(self, text):     self.text   = text

    def numChildren(self):
        """
        Returns number of XML child nodes.
        """
        return len(self.children)
    
    def child(self, index):
        """
        Returns XML child node at index.
        """
        return self.children[index]

    def addChild(self, node):
        """
        Add node to XML child nodes. Child node's parent is set to this node.
        """
        self.children.append(node)
        node.setParent(self)

    def removeChild(self, node):
        """
        Remove node from XML child nodes.
        """
        self.children.remove(node)

    def childrenWithName(self, name):
        """
        Return list of child nodes with argument name.
        Returns empty list of name is not found.
        """

        return [n for n in self.children if n.name == name]

    def childWithName(self, name):
        """
        Return first child with name. Returns None if name is not found.
        """
        child = None
        children = self.childrenWithName(name)
        if len(children) > 0: child = children[0]
        return child

    def attributes(self):
        """
        Return attribute names.
        """
        return self.attbDict.keys()

    def addAttribute(self, name, value):
        """
        Adds the name/value pair to the attribute dictionary.
        """
        if name == '':
            raise ValueError('Invalid attribute name')
        self.attbDict[name] = value

    def attributeWithName(self, name):
        """
        Returns attribute with argument name.
        """
        value = None
        if self.attbDict.has_key(name): value = self.attbDict[name]
        return value
    
