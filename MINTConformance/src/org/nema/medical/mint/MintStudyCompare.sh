#!/bin/sh
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

# ---
# Check if study metadata file and binaryitems directory exist.
# ---
check()
{
   study=$1
   binary=$2

   if [ ! -f $study ]
   then
      echo "$study not found"
      exit 2
   fi

   if [ ! -d $binary ]
   then
      echo "$binary not found"
      exit 2
   fi
}

# ---
# Get command line arguments.
# ---
progName=`basename $0`
if [ $# != 2 ]
then
   echo "Usage: $progName <mint_study1.xml> <mint_study2.xml>"
   exit 1
fi

study1=$1
binary1=`dirname $study1`/binaryitems
check $study1 $binary1

study2=$2
binary2=`dirname $study2`/binaryitems
check $study2 $binary2

# ---
# Compare studies.
# ---
diff $study1 $study2

# --- 
# Compare binary items.
# ---
items=`find $binary1 -name "*.dat"`
for item1 in $items
do
   dat=`basename $item1`
   item2=$binary2/$dat
   cmp $item1 $item2
done

exit 0
