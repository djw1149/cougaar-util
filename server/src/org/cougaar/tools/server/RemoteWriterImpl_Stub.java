/*
 * <copyright>
 * Copyright 1997-2001 Defense Advanced Research Projects
 * Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 * Raytheon Systems Company (RSC) Consortium).
 * This software to be used only in accordance with the
 * COUGAAR licence agreement.
 * </copyright>
 */
// Stub class generated by rmic, do not edit.
// Contents subject to change without notice.

package org.cougaar.tools.server;

public final class RemoteWriterImpl_Stub
    extends java.rmi.server.RemoteStub
    implements org.cougaar.tools.server.RemoteOutputStream, java.rmi.Remote
{
    private static final long serialVersionUID = 2;
    
    private static java.lang.reflect.Method $method_close_0;
    private static java.lang.reflect.Method $method_write_1;
    
    static {
	try {
	    $method_close_0 = org.cougaar.tools.server.RemoteOutputStream.class.getMethod("close", new java.lang.Class[] {});
	    $method_write_1 = org.cougaar.tools.server.RemoteOutputStream.class.getMethod("write", new java.lang.Class[] {org.cougaar.tools.server.RemoteOutputStream.ByteArray.class});
	} catch (java.lang.NoSuchMethodException e) {
	    throw new java.lang.NoSuchMethodError(
		"stub class initialization failed");
	}
    }
    
    // constructors
    public RemoteWriterImpl_Stub(java.rmi.server.RemoteRef ref) {
	super(ref);
    }
    
    // methods from remote interfaces
    
    // implementation of close()
    public void close()
	throws java.io.IOException, java.rmi.RemoteException
    {
	try {
	    ref.invoke(this, $method_close_0, null, -4742752445160157748L);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.io.IOException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of write(RemoteOutputStream.ByteArray)
    public void write(org.cougaar.tools.server.RemoteOutputStream.ByteArray $param_RemoteOutputStream$ByteArray_1)
	throws java.io.IOException, java.rmi.RemoteException
    {
	try {
	    ref.invoke(this, $method_write_1, new java.lang.Object[] {$param_RemoteOutputStream$ByteArray_1}, -229716213762784618L);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.io.IOException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
}
