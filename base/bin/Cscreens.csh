#/bin/csh
#
# First column

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

xterm -sl 50000 -title BJ-1BDE-NODE -geometry 80x6+0+120 -exec ssh alp-123 &
xterm -sl 50000 -title BJ-2BDE-NODE -geometry 80x6+0+230 -exec ssh alp-15 &
xterm -sl 50000 -title BJ-TCBN-MED-NODE -geometry 80x6+0+340 -exec ssh alp-119 &
xterm -sl 50000 -title BJ-CSB-NODE -geometry 80x6+0+450 -exec ssh alp-105 &
#
# Second column
xterm -sl 50000 -title BJ-COMMAND-3ID-NODE -geometry 80x6+520+120 -exec ssh alp-131 &
xterm -sl 50000 -title BJ-SUPPLY-NODE -geometry 80x6+520+230 -exec ssh alp-93 &
xterm -sl 50000 -title DELTA-PROXY-NODE -geometry 80x6+520+340 -exec ssh alp-95 &
xterm -sl 50000 -title TRANSCOM -geometry 80x6+520+450 -exec ssh alp-101 &
xterm -sl 50000 -title BJ-IBCT-NODE -geometry 80x6+520+550 -exec ssh alp-129 &




