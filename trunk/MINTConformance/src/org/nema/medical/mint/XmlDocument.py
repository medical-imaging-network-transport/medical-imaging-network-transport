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

import urllib

from xml.dom import minidom
from XmlNode import *

# -----------------------------------------------------------------------------
# XmlDocument
# -----------------------------------------------------------------------------
class XmlDocument(XmlNode):
    """
    This class creates an XML document.
    """

    def __init__(self, rootTagName = ''):
        """
        Construct from a root tag name string.
        """
        XmlNode.__init__(self, rootTagName)
        self.document = None

    def toString(self):
        """
        Return document as an XML string without indentation or newlines.
        """

        self.buildDocument()
        result = self.document.toxml()
        result = result.replace('\n','')
        return result

    def __str__(self):
        """
        Return nicely formatted XML string.
        """
        self.buildDocument()
        result = self.document.toprettyxml('   ')
        return result

    def buildDocument(self):
        """
        Build the document from the XML nodes.
        """
        
        # Create document with root node.
        self.document = minidom.Document()
        rootNode = self.document.createElement(self.name)
        self.document.appendChild(rootNode)

        # Add child nodes (including text) to document
        self.appendChildren(rootNode, self)

    def appendChildren(self, docNode, xmlNode):
        """
        Recursively append XML node children (including text) to document node.
        """

        # Append attributes.
        for key in xmlNode.attbDict.keys():
            docNode.attributes[key] = xmlNode.attbDict[key]

        # Append text node.
        text = xmlNode.getText()
        if text is not '':
            textNode = self.document.createTextNode(text)
            docNode.appendChild(textNode)

        # Append child nodes.
        for index in range(0, xmlNode.numChildren()):

            xmlChildNode = xmlNode.child(index)
            docChildNode = self.document.createElement(xmlChildNode.getName())
            docNode.appendChild(docChildNode)
            
            self.appendChildren(docChildNode, xmlChildNode)

    def readFromString(self, argString):
        """
        Parse argument string and populate document.
        """

        # Parse argument string and get document.
        dom = minidom.parseString(argString)
        doc = dom.documentElement

        # Initialize XML document.
        self.__init__(doc.tagName)

        # Parse child nodes.
        self.parseChildNodes(doc, self)

    def parseChildNodes(self, docNode, xmlNode):
        """
        Recursively parse child nodes of argument node.
        """

        # Parse attributes.
        attbs = docNode.attributes
        if attbs:
            for name in attbs.keys():
                attbNode = attbs.get(name)
                value = attbs.get(name).nodeValue
                xmlNode.attbDict[name] = value

        # Examine child nodes.
        childNodes = docNode.childNodes
        for node in childNodes:

            # If text node, set text string.
            if node.nodeType == node.TEXT_NODE:
                text = node.data.replace('\n','').replace('\r','').strip()
                xmlNode.setText(xmlNode.getText() + text)
            else:
                # Create node and parse its children.
                newXmlNode = XmlNode(node.tagName)
                xmlNode.addChild(newXmlNode)
                self.parseChildNodes(node, newXmlNode)

    def readFromFile(self, fname):
        """
        Open and read data from file. Then parse data and populate document.
        """

        # Read data from file.
        f = open(fname)
        data = f.read()
        
        # Parse data.
        self.readFromString(data)
        
    def readFromURL(self, argURL):
        """
        Parse argument URL and populate document.
        """
        
        # Parse argument URL and get document.
        dom = minidom.parse(urllib.urlopen(argURL))
        doc = dom.documentElement

        # Initialize XML document.
        self.__init__(doc.tagName)

        # Parse child nodes.
        self.parseChildNodes(doc, self)
        doc.unlink()
        
