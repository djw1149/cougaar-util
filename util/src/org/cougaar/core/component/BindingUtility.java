/*
 * <copyright>
 *  Copyright 2001-2003 BBNT Solutions, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency (DARPA).
 * 
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 * 
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package org.cougaar.core.component;

import java.util.*;
import java.lang.reflect.*;

/** A collection of utilities to be used for binding components
 **/

public abstract class BindingUtility {

  public static boolean activate(Object child, BindingSite bindingSite, ServiceBroker serviceBroker) {
    setBindingSite(child, bindingSite);
    setServices(child, serviceBroker);
    initialize(child);
    load(child);
    start(child);
    return true;
  }

  /** Sets a the binding site of the child to the specified object
   * if possible.
   * @return true on success
   **/
  public static boolean setBindingSite(Object child, BindingSite bindingSite) { 
    Class childClass = child.getClass();
    try {
      Method m;
      try {
        m = childClass.getMethod("setBindingSite", new Class[]{BindingSite.class});
      } catch (NoSuchMethodException e) {
        return false;
      }

      /*
      // getMethod currently never returns null (it'll throw and exception instead)
      if (m == null) {
        return false;
      }
      */

      m.invoke(child, new Object[]{bindingSite});
      return true;
    } catch (Exception e) {
      throw new ComponentLoadFailure("Couldn't set BindingSite", child, e);
    }
  }

  public static boolean setServices(Object child, ServiceBroker servicebroker) {
    Class childClass = child.getClass();
    try {
      Method[] methods = childClass.getMethods();

      int l = methods.length;
      for (int i=0; i<l; i++) { // look at all the methods
        Method m = methods[i];
        String s = m.getName();
        if ("setBindingSite".equals(s)) continue;
        Class[] params = m.getParameterTypes();
        if (s.startsWith("set") &&
            params.length == 1) {
          Class p = params[0];
          if (Service.class.isAssignableFrom(p)) {
            String pname = p.getName();
            {                     // trim the package off the classname
              int dot = pname.lastIndexOf(".");
              if (dot>-1) pname = pname.substring(dot+1);
            }
            
            if (s.endsWith(pname)) { 
              // ok: m is a "public setX(X)" method where X is a Service.
              // create the revocation listener
              final Method fm = m;
              final Object fc = child;
              ServiceRevokedListener srl = new ServiceRevokedListener() {
                  public void serviceRevoked(ServiceRevokedEvent sre) {
                    Object[] args = new Object[] { null };
                    try {
                      fm.invoke(fc, args);
                    } catch (Throwable e) {
                      // ugly, but we don't want to pass it through.
                      e.printStackTrace();
                    }
                  }
                };
              // Let's try getting the service...
              Object service = servicebroker.getService(child, p, srl);
              Object[] args = new Object[] { null };
              try {
                m.invoke(child, args);
                if (service != null) {
                  args[0] = service;
                  m.invoke(child, args);
                }
              } catch (InvocationTargetException ite) {
                if (service != null) {
                  servicebroker.releaseService(child,p,service);
                }
                throw ite.getCause();
              }
            }
          }
        }
      }
    } catch (Throwable e) {
      throw new ComponentLoadFailure("Couldn't set services", child, e);
    }
    return true;
  }

  /** Run initialize on the child component if possible **/
  public static boolean initialize(Object child) { 
    return call0(child, "initialize");
  }

  /** Run load on the child component if possible **/
  public static boolean load(Object child) { 
    return call0(child, "load");
  }

  /** Run start on the child component if possible **/
  public static boolean start(Object child) { 
    return call0(child, "start");
  }

  public static boolean call0(Object child, String method) {
    Class childClass = child.getClass();
    Method init = null;
    try {
      try {
        init = childClass.getMethod(method, null);
      } catch (NoSuchMethodException e1) { }
      if (init != null) {
        init.invoke(child, new Object[] {});
        return true;
      }
    } catch (java.lang.reflect.InvocationTargetException ite) {
      throw new ComponentRuntimeException("failed while calling "+method+"()", 
                                          child,
                                          ite.getCause());
    } catch (Exception e) {
      throw new ComponentRuntimeException("failed to call "+method+"()", 
                                          child,
                                          e);
    }
    return false;
  }
}
