# -----------------------------------------------------------------------------
# $Id$
#
# Copyright (C) 2010-2012 MINT Working group. All rights reserved.
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

import sys
import traceback

from struct import unpack
from struct import pack

# -----------------------------------------------------------------------------
# DicomTransfer
# -----------------------------------------------------------------------------
class DicomTransfer():
      
   IMPLICIT_VR_LITTLE_ENDIAN          = "1.2.840.10008.1.2"
   EXPLICIT_VR_LITTLE_ENDIAN          = "1.2.840.10008.1.2.1"
   DEFLATED_EXPLICIT_VR_LITTLE_ENDIAN = "1.2.840.10008.1.2.1.99"
   EXPLICIT_VR_BIG_ENDIAN             = "1.2.840.10008.1.2.2" 
   JPEG_COMPRESSION                   = "1.2.840.10008.1.2.4."
   LOSSLESS_RUN_LENGTH_ENCODING       = "1.2.840.10008.1.2.5"
   
   def __init__(self, transferSyntaxUI="1.2.840.10008.1.2.1"):

       self.__transferSyntaxUI = transferSyntaxUI
       self.__explicit = True
       self.__littleEndian = True
       self.__deflated = False
       self.__uncompressed = True

       if transferSyntaxUI == self.IMPLICIT_VR_LITTLE_ENDIAN:
          self.__explicit = False
       elif transferSyntaxUI == self.EXPLICIT_VR_LITTLE_ENDIAN:
          pass
       elif transferSyntaxUI == self.DEFLATED_EXPLICIT_VR_LITTLE_ENDIAN:
          self.__deflated = True
          self.__uncompressed = False
          assert self.__deflated == True
          assert self.isDeflated() == True
       elif transferSyntaxUI == self.EXPLICIT_VR_BIG_ENDIAN:
          self.__littleEndian = False
       elif transferSyntaxUI != None and transferSyntaxUI[0:20] == self.JPEG_COMPRESSION:
          self.__uncompressed = False
          # xx = 50-64: Lossy JPEG
          # xx = 65-70: Lossless JPEG
       elif transferSyntaxUI == self.LOSSLESS_RUN_LENGTH_ENCODING:
          self.__uncompressed = False
       else:
          raise IOError("Unknown Transfer Syntax UID - "+str(transferSyntaxUI))
       
   def isExplicit(self)       : return     self.__explicit
   def isImplicit(self)       : return not self.__explicit
   def isLittleEndian(self)   : return     self.__littleEndian
   def isBigEndian(self)      : return not self.__littleEndian
   def isSameArch(self, other): return     (self.isLittleEndian() and other.isLittleEndian()) or (self.isBigEndian() and other.isBigEndian()) 
   def isDeflated(self)       : return     self.__deflated
   def isInflated(self)       : return not self.__deflated 
   def isUncompressed(self)   : return     self.__uncompressed
   def isCompressed(self)     : return not self.__uncompressed 
   def uid(self)              : return     self.__transferSyntaxUI
       
   def unpack(self, type, bytes):
       unpacked = []
       if self.__littleEndian:
          unpacked = unpack("<"+type, bytes)
       else:
          unpacked = unpack(">"+type, bytes)
       return unpacked[0]

   def unpackByteArray(self, bytes1, requestTransferSyntax):

       # ---
       # Unpack binary from byte string.
       # ---
       len1 = len(bytes1)
       bytes2 = unpack('B'*len1, bytes1)

       # ---
       # If this is the same transfer syntax requested, we're done.
       # ---
       if self.uid() == requestTransferSyntax.uid():
          return bytes2

       # ---
       # If compressed, we're screwed.
       # ---
       if self.isCompressed() or requestTransferSyntax.isCompressed():
          raise IOError("Unsupported transfer syntax conversion: "+str(self)+" > "+str(requestTransferSyntax))

       # ---
       # If same architecture, we're done.
       # ---
       if self.isSameArch(requestTransferSyntax):
          return bytes2

       # ---
       # If different architecture, then byteswap.
       # ---
       if not self.isSameArch(requestTransferSyntax):
          bytes3 = list(bytes2)
          for i in range(0, len1, 2):
              bytes3[i] = bytes2[i+1]
              bytes3[i+1] = bytes2[i]
          return bytes3

       raise IOError("Unable to unpack: "+str(self)+" > "+str(requestTransferSyntax))

   def __repr__(self):
       if self.__transferSyntaxUI == self.IMPLICIT_VR_LITTLE_ENDIAN:
          return "Implicit VR Little Endian"
       if self.__transferSyntaxUI == self.EXPLICIT_VR_LITTLE_ENDIAN:
          return "Explicit VR Little Endian"
       if self.__transferSyntaxUI == self.DEFLATED_EXPLICIT_VR_LITTLE_ENDIAN:
          return "Deflated Explicit VR Little Endian"
       if self.__transferSyntaxUI == self.EXPLICIT_VR_BIG_ENDIAN:
          return "Explicit VR Big Endian"
       if self.__transferSyntaxUI[0:20] == self.JPEG_COMPRESSION:
          return "JPEG Compression"
       if self.__transferSyntaxUI == self.LOSSLESS_RUN_LENGTH_ENCODING:
          return "Lossless Run Length Encoding"
         
       return None

   def __str__(self):
       return self.__repr__()

   def __eq__(self, other):
       return self.__transferSyntaxUI == other.__transferSyntaxUI

   def __nq__(self, other):
       return self.__transferSyntaxUI != other.__transferSyntaxUI

   def test_unpackByteArray(bytes, transferSyntax, requestedTransferSyntax, expectedResults):

       try:
          bytes2 = transferSyntax.unpackByteArray(bytes, requestedTransferSyntax)
          assert bytes2 == expectedResults

       except IOError, e:
          if not expectedResults == None: raise e

   test_unpackByteArray = staticmethod(test_unpackByteArray)

def main():
    try:
       default = DicomTransfer()
       assert default.isExplicit() == True
       assert default.isLittleEndian() == True
       assert default.isDeflated() == False
       assert default.isUncompressed() == True

       implicitLittleEndian = DicomTransfer("1.2.840.10008.1.2")
       assert implicitLittleEndian.isExplicit() == False
       assert implicitLittleEndian.isLittleEndian() == True
       assert implicitLittleEndian.isDeflated() == False
       assert implicitLittleEndian.isUncompressed() == True

       explicitLittleEndian = DicomTransfer("1.2.840.10008.1.2.1")
       assert default == explicitLittleEndian
       assert explicitLittleEndian.isExplicit() == True
       assert explicitLittleEndian.isLittleEndian() == True
       assert explicitLittleEndian.isDeflated() == False
       assert explicitLittleEndian.isUncompressed() == True

       deflatedExplicitLittleEndian = DicomTransfer("1.2.840.10008.1.2.1.99")
       assert deflatedExplicitLittleEndian.isExplicit() == True
       assert deflatedExplicitLittleEndian.isLittleEndian() == True
       assert deflatedExplicitLittleEndian.isDeflated() == True
       assert deflatedExplicitLittleEndian.isUncompressed() == False

       explicitBigEndian = DicomTransfer("1.2.840.10008.1.2.2")
       assert not default == explicitBigEndian
       assert explicitBigEndian.isExplicit() == True
       assert explicitBigEndian.isLittleEndian() == False
       assert explicitBigEndian.isDeflated() == False
       assert explicitBigEndian.isUncompressed() == True

       jpegCompressed = DicomTransfer("1.2.840.10008.1.2.4.")
       assert jpegCompressed.isExplicit() == True
       assert jpegCompressed.isLittleEndian() == True
       assert jpegCompressed.isDeflated() == False
       assert jpegCompressed.isUncompressed() == False

       losslessEncoded = DicomTransfer("1.2.840.10008.1.2.5")
       assert losslessEncoded.isExplicit() == True
       assert losslessEncoded.isLittleEndian() == True
       assert losslessEncoded.isDeflated() == False
       assert losslessEncoded.isUncompressed() == False

       bytes = pack('<BBBB', 1, 2, 3, 4)

       DicomTransfer.test_unpackByteArray(bytes, default, default, (1,2,3,4))
       DicomTransfer.test_unpackByteArray(bytes, default, implicitLittleEndian, (1,2,3,4))
       DicomTransfer.test_unpackByteArray(bytes, default, explicitLittleEndian, (1,2,3,4))
       DicomTransfer.test_unpackByteArray(bytes, default, deflatedExplicitLittleEndian, None)
       DicomTransfer.test_unpackByteArray(bytes, default, explicitBigEndian, [2,1,4,3])
       DicomTransfer.test_unpackByteArray(bytes, default, jpegCompressed, None)
       DicomTransfer.test_unpackByteArray(bytes, default, losslessEncoded, None)
       
       DicomTransfer.test_unpackByteArray(bytes, implicitLittleEndian, default, (1,2,3,4))
       DicomTransfer.test_unpackByteArray(bytes, implicitLittleEndian, implicitLittleEndian, (1,2,3,4))
       DicomTransfer.test_unpackByteArray(bytes, implicitLittleEndian, explicitLittleEndian, (1,2,3,4))
       DicomTransfer.test_unpackByteArray(bytes, implicitLittleEndian, deflatedExplicitLittleEndian, None)
       DicomTransfer.test_unpackByteArray(bytes, implicitLittleEndian, explicitBigEndian, [2,1,4,3])
       DicomTransfer.test_unpackByteArray(bytes, implicitLittleEndian, jpegCompressed, None)
       DicomTransfer.test_unpackByteArray(bytes, implicitLittleEndian, losslessEncoded, None)

       DicomTransfer.test_unpackByteArray(bytes, explicitLittleEndian, default, (1,2,3,4))
       DicomTransfer.test_unpackByteArray(bytes, explicitLittleEndian, implicitLittleEndian, (1,2,3,4))
       DicomTransfer.test_unpackByteArray(bytes, explicitLittleEndian, deflatedExplicitLittleEndian, None)
       DicomTransfer.test_unpackByteArray(bytes, explicitLittleEndian, explicitLittleEndian, (1,2,3,4))
       DicomTransfer.test_unpackByteArray(bytes, explicitLittleEndian, explicitBigEndian, [2,1,4,3])
       DicomTransfer.test_unpackByteArray(bytes, explicitLittleEndian, jpegCompressed, None)
       DicomTransfer.test_unpackByteArray(bytes, explicitLittleEndian, losslessEncoded, None)

       DicomTransfer.test_unpackByteArray(bytes, deflatedExplicitLittleEndian, default, None)
       DicomTransfer.test_unpackByteArray(bytes, deflatedExplicitLittleEndian, implicitLittleEndian, None)
       DicomTransfer.test_unpackByteArray(bytes, deflatedExplicitLittleEndian, explicitLittleEndian, None)
       DicomTransfer.test_unpackByteArray(bytes, deflatedExplicitLittleEndian, deflatedExplicitLittleEndian, (1,2,3,4))
       DicomTransfer.test_unpackByteArray(bytes, deflatedExplicitLittleEndian, explicitBigEndian, None)
       DicomTransfer.test_unpackByteArray(bytes, deflatedExplicitLittleEndian, jpegCompressed, None)
       DicomTransfer.test_unpackByteArray(bytes, deflatedExplicitLittleEndian, losslessEncoded, None)

       DicomTransfer.test_unpackByteArray(bytes, explicitBigEndian, default, [2,1,4,3])
       DicomTransfer.test_unpackByteArray(bytes, explicitBigEndian, implicitLittleEndian, [2,1,4,3])
       DicomTransfer.test_unpackByteArray(bytes, explicitBigEndian, explicitLittleEndian, [2,1,4,3])
       DicomTransfer.test_unpackByteArray(bytes, explicitBigEndian, deflatedExplicitLittleEndian, None)
       DicomTransfer.test_unpackByteArray(bytes, explicitBigEndian, explicitBigEndian, (1,2,3,4))
       DicomTransfer.test_unpackByteArray(bytes, explicitBigEndian, jpegCompressed, None)
       DicomTransfer.test_unpackByteArray(bytes, explicitBigEndian, losslessEncoded, None)

       DicomTransfer.test_unpackByteArray(bytes, jpegCompressed, default, None)
       DicomTransfer.test_unpackByteArray(bytes, jpegCompressed, implicitLittleEndian, None)
       DicomTransfer.test_unpackByteArray(bytes, jpegCompressed, explicitLittleEndian, None)
       DicomTransfer.test_unpackByteArray(bytes, jpegCompressed, deflatedExplicitLittleEndian, None)
       DicomTransfer.test_unpackByteArray(bytes, jpegCompressed, explicitBigEndian, None)
       DicomTransfer.test_unpackByteArray(bytes, jpegCompressed, jpegCompressed, (1,2,3,4))
       DicomTransfer.test_unpackByteArray(bytes, jpegCompressed, losslessEncoded, None)

       DicomTransfer.test_unpackByteArray(bytes, losslessEncoded, default, None)
       DicomTransfer.test_unpackByteArray(bytes, losslessEncoded, implicitLittleEndian, None)
       DicomTransfer.test_unpackByteArray(bytes, losslessEncoded, explicitLittleEndian, None)
       DicomTransfer.test_unpackByteArray(bytes, losslessEncoded, deflatedExplicitLittleEndian, None)
       DicomTransfer.test_unpackByteArray(bytes, losslessEncoded, explicitBigEndian, None)
       DicomTransfer.test_unpackByteArray(bytes, losslessEncoded, jpegCompressed, None)
       DicomTransfer.test_unpackByteArray(bytes, losslessEncoded, losslessEncoded, (1,2,3,4))

    except Exception, exception:
       traceback.print_exception(sys.exc_info()[0], 
                                 sys.exc_info()[1],
                                 sys.exc_info()[2])
       sys.exit(1)

if __name__ == "__main__":
   main()
   
