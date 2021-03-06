#!/usr/bin/perl
# -*- Perl -*-

# <copyright>
#  
#  Copyright 2003-2004 BBNT Solutions, LLC
#  under sponsorship of the Defense Advanced Research Projects
#  Agency (DARPA).
# 
#  You can redistribute this software and/or modify it under the
#  terms of the Cougaar Open Source License as published on the
#  Cougaar Open Source Website (www.cougaar.org).
# 
#  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
#  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
#  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
#  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
#  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
#  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
#  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
#  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
#  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
#  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
#  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#  
# </copyright>


# fixAV fixes cougaar 9.* code which calls AspectValue constructors (now private)
# by replacing such references with calls to AspectValue factory methods.
#
# This script does *not* take care of the following cases:
# - AspectValue no longer extends AspectType.  There are very few cases where
# this would be a problem, and those couldn't be easily fixed by a simple script.
# - custom AspectValue classes are free to use whatever construction options
# they want, although they are strongly advised to match the 10.0 pattern.
# - There are new accessors which will lead to significantly better performance
# than before.  Although, in theory, a sufficiently smart script could perform
# some of these optimizations, it would be far easier for programmers to
# do it than to write a such a script.  Example:  time-based AVs now use
# longs internally, so clients should use longValue() instead of getValue().
# - there may be some lesser-used permutations which are not caught.

# usage example:
# find . -name "*.java" | perl fixAV.pl
#

$total=0;
$fixed=0;
$repls = 0;

print "Fixing AspectValue constructors for Cougaar 10.0\n";

while (<>) {
  chop;
  $file = $_;

  &process($file);
}

print "Total files = $total\n";
print "Total files modified = $fixed\n";
print "Total lines modified = $repls\n";

exit(0);

sub process {
  local($file) = @_;
  local($found)=0;		# if != 0, copy the file back
  local($tmp) = "/tmp/deleteme";
  open(IN, "<$file");
  unlink($tmp);
  open(OUT, ">$tmp");
  while (<IN>) {
    # fix standard constructor
    $found++ if (s/new AspectValue/AspectValue\.newAspectValue/g);

    print OUT "$_";
  }
  close(OUT);
  close(IN);

  # copy it back
  if ($found) {
    $fixed++;
    $repls = $repls+$found;

    unlink($file);
    open(IN, "<$tmp");
    open(OUT, ">$file");
    while(<IN>)  {
      print OUT "$_";
    }
    close(OUT);
    close(IN);
  }

  #unlink($tmp);
  $total++;
}
