#!/bin/csh -f
# Usage : getbuild.csh <username> <MMDD> <directory>
#   username defaults to current user
#   MMDD defaults to today (e.g. last night's build)
#   directory defaults to /opt
# also maintains a symbolic link at /opt/org.cougaar.install pointing to the
# most recent build to be retrieved.
#

# <copyright>
#  Copyright 2001 BBNT Solutions, LLC
#  under sponsorship of the Defense Advanced Research Projects Agency (DARPA).
# 
#  This program is free software; you can redistribute it and/or modify
#  it under the terms of the Cougaar Open Source License as published by
#  DARPA on the Cougaar Open Source Website (www.cougaar.org).
# 
#  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
#  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
#  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
#  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
#  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
#  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
#  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
#  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
#  PERFORMANCE OF THE COUGAAR SOFTWARE.
# </copyright>


if ("$3" == "") then
    set dir="/opt"
else
    set dir="$3"
endif

if ("$2" == "") then
    set d=`date '+%m%d'`
else
    set d="$2"
endif

if ("$1" == "") then
    set u="$USER"
else
    set u="$1"
endif

set h="eiger.alpine.bbn.com"

echo "Getting build ${d} from $u@$h"

cd ${dir}
rm -rf alp-2000${d}
#mkdir alp-${d}
#cd alp-${d}

# Grab  the core zip files from the nightly build
scp ${u}@${h}:/build/dist/zips/core-HEAD-2000${d}.zip .
# unzip core. It will create an alp-2000${d} directory
unzip core-HEAD-2000${d}.zip

cd alp-2000${d}

# Get the rest of the zip files
scp ${u}@${h}:/build/dist/zips/alpine-HEAD-2000${d}.zip .
scp ${u}@${h}:/build/dist/zips/utility-HEAD-2000${d}.zip .
scp ${u}@${h}:/build/dist/zips/ants-HEAD-2000${d}.zip .
scp ${u}@${h}:/build/dist/zips/TOPS-HEAD-2000${d}.zip .
#scp ${u}@${h}:/build/dist/zips/sra-HEAD-2000${d}.zip .

# Unzip them
unzip alpine-HEAD-2000${d}.zip
unzip utility-HEAD-2000${d}.zip
unzip ants-HEAD-2000${d}.zip
unzip TOPS-HEAD-2000${d}.zip
#unzip sra-HEAD-2000${d}.zip


# Set up the TOPS stuff
cp -r TOPS/configs configs/topsconfig
cp -r TOPS/data/PROTOTYPES configs/tops
#cp configs/tops/*.gss.xml tops

/bin/rm -f ${dir}/org.cougaar.install
ln -s ${dir}/alp-2000${d} ${dir}/org.cougaar.install

echo "The build is set up : Remember to set your COUGAAR_INSTALL_PATH to "${dir}"/alp-2000${d}"

