@echo OFF

REM "<copyright>"
REM " "
REM " Copyright 2001-2004 BBNT Solutions, LLC"
REM " under sponsorship of the Defense Advanced Research Projects"
REM " Agency (DARPA)."
REM ""
REM " You can redistribute this software and/or modify it under the"
REM " terms of the Cougaar Open Source License as published on the"
REM " Cougaar Open Source Website (www.cougaar.org)."
REM ""
REM " THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS"
REM " "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT"
REM " LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR"
REM " A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT"
REM " OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,"
REM " SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT"
REM " LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,"
REM " DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY"
REM " THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT"
REM " (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE"
REM " OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE."
REM " "
REM "</copyright>"

REM
REM Sample script to run a Node from an XML file.
REM First argument is the name of the society XML file to be
REM found on your config_path.
REM Second argument is the name of the Node to run.
REM

REM Make sure that COUGAAR_INSTALL_PATH is specified
IF NOT "%COUGAAR_INSTALL_PATH%" == "" GOTO L_2

REM Unable to find cougaar-install-path
ECHO COUGAAR_INSTALL_PATH not set!
GOTO L_END
:L_2

REM Make sure that COUGAAR_WORKSPACE is set
IF NOT "%COUGAAR_WORKSPACE%" == "" GOTO L_4

REM Path for runtime output not set.
REM Default is CIP/workspace
ECHO COUGAAR_WORKSPACE not set. Defaulting to CIP/workspace
SET COUGAAR_WORKSPACE=%COUGAAR_INSTALL_PATH%\workspace
:L_4

REM calls setlibpath.bat which sets the path to the required jar files.
REM calls setarguments.bat which sets input parameters for system behavior
CALL "%COUGAAR_INSTALL_PATH%\bin\setlibpath.bat"
CALL "%COUGAAR_INSTALL_PATH%\bin\setarguments.bat"

REM pass in "NodeName" to run a specific named Node
set MYNODEPROP=-Dorg.cougaar.node.name="%2"

REM Use the other version of this line to validate the schema - MAY BE SLOW!
REM SET VALIDATESCHEMA=-Dorg.cougaar.core.node.validate=true
SET VALIDATESCHEMA=


@ECHO ON

java.exe %MYPROPERTIES% %MYNODEPROP% -Dorg.cougaar.core.node.InitializationComponent=XML -Dorg.cougaar.society.file="%1" %VALIDATESCHEMA% %MYMEMORY% -classpath %LIBPATHS% %MYCLASSES% %3 %4

:L_END
