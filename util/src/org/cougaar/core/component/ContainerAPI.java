/*
 * <copyright>
 *  Copyright 2000-2003 BBNT Solutions, LLC
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


/** ContainerAPI is the interface used by Binder and/or BinderFactory to
 * invoke Binder-privileged methods on the parent Container.  Note that
 * the object implementing ContainerAPI will usually not be the the Container
 * itself.
 * <p>
 * The API itself represents the minimal set of services which all binders need
 * from the container.
 **/
public interface ContainerAPI
  extends BindingSite
{
  /** Remove the component (presumably the child) from the container **/
   boolean remove(Object childComponent);

  /** getState() and setState() methods required to pass through Binder **/
  //ComponentDescription[] getState();

  //void setState(Object o);
}
