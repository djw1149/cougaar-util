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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/** Simple implementation of Cougaar component services layer.  
 * No propagation, nothing fancy.
 * @see org.cougaar.core.component.ServiceBroker
 **/

public class ServiceBrokerSupport 
  implements ServiceBroker 
{
  /** the current set of Listeners.  Elements are of type ServiceListener **/
  private ArrayList listeners = new ArrayList();

  /** add a ServiceListener to this ServiceBroker Context **/
  public void addServiceListener(ServiceListener sl) {
    if (sl == null) 
      throw new IllegalArgumentException("Add of null ServiceListener");
    synchronized (listeners) {
      listeners.add(sl);
    }
  }
      
  /** remove a services listener **/
  public void removeServiceListener(ServiceListener sl) {
    if (sl == null) 
      throw new IllegalArgumentException("Remove of null ServiceListener");
    synchronized (listeners) {
      listeners.remove(sl);
    }
  }
  
  /** Apply each listener appropriately to the event **/
  protected void applyListeners(ServiceEvent se) {
    // Copy out the listeners while synchronized. but don't call the
    // listeners while synchronized. Avoids a deadlock when the
    // service broker is used within the listener.
    ServiceListener[] listenerArray;
    synchronized (listeners) {
      listenerArray = new ServiceListener[listeners.size()];
      listenerArray = (ServiceListener[]) listeners.toArray(listenerArray);
    }
    for (int i = 0; i < listenerArray.length; i++) {
      applyListener(listenerArray[i], se);
    }
  }

  protected void applyListener(ServiceListener sl, ServiceEvent se) {
    if (sl instanceof ServiceAvailableListener) {
      if (se instanceof ServiceAvailableEvent) {
        ((ServiceAvailableListener)sl).serviceAvailable((ServiceAvailableEvent)se);
      }
    } else if (sl instanceof ServiceRevokedListener) {
      if (se instanceof ServiceRevokedEvent) {
        ((ServiceRevokedListener)sl).serviceRevoked((ServiceRevokedEvent)se);
      }
    } else {
      // what is this?
    }
  }

  /** the current set of services.  A map of Class serviceClass to ServiceProvider
   * Access to services is guarded by servicesLock.
   **/
  private final HashMap services = new HashMap(89);
  /** Lock for services.  Protected so that extending classes can 
   * synchronize over multiple calls.
   **/
  protected final Object servicesLock = new Object();

  /** add a Service to this ServiceBroker Context **/
  public boolean addService(Class serviceClass, ServiceProvider serviceProvider) {
    if (serviceClass == null)
      throw new IllegalArgumentException("serviceClass null");
    if(serviceProvider == null)
      throw new IllegalArgumentException("serviceProvider null");
      
    synchronized (servicesLock) {
      Object old = services.get(serviceClass);
      if (old != null) {
        return false;
      } else {
        services.put(serviceClass, serviceProvider);
        // fall through
      }
    }

    // notify any listeners
    applyListeners(new ServiceAvailableEvent(this, serviceClass));

    return true;
  }

  /** remoke or remove an existing service **/
  public void revokeService(Class serviceClass, ServiceProvider serviceProvider) {
    if (serviceClass == null)
      throw new IllegalArgumentException("serviceClass null");
    if(serviceProvider == null)
      throw new IllegalArgumentException("serviceProvider null");
      
    synchronized (servicesLock) {
      Object old = services.remove(serviceClass); 
      if (old == null) {
        return;                 // bail out - already revoked
      } 
      // else fall through
    }

    // notify any listeners
    applyListeners(new ServiceRevokedEvent(this, serviceClass));
  }

  /** is the service currently available? **/
  public boolean hasService(Class serviceClass) {
    if (serviceClass == null)
      throw new IllegalArgumentException("serviceClass null");
    synchronized (servicesLock) {
      return (null != services.get(serviceClass));
    }
  }

  /** gets the currently available services for this context.
   * This version copies the keyset to keep the iterator safe.
   **/
  public Iterator getCurrentServiceClasses() {
    //We could cache the answer if this turns out to be a hot spot.
    synchronized (servicesLock) {
      ArrayList l = new ArrayList(services.keySet());
      return l.iterator();
    }
  }

  /** get an instance of the requested service from a service provider associated
   * with this context.
   **/
  public Object getService(
      Object requestor,
      Class serviceClass,
      ServiceRevokedListener srl) {
    Object service = 
      getServiceAllowNull(requestor, serviceClass, srl);
    if (service instanceof NullService) {
      service = null; // blocked
    }
    return service;
  }

  /**
   * get the service and allow a NullService result, which the
   * usual "getService(..)" replaces with null.
   */ 
  protected Object getServiceAllowNull(
      Object requestor,
      final Class serviceClass,
      final ServiceRevokedListener srl) {
    if (requestor == null) throw new IllegalArgumentException("null requestor");
    if (serviceClass == null) throw new IllegalArgumentException("null serviceClass");

    Object service;
    ServiceProvider sp;
    synchronized (servicesLock) {
      sp = (ServiceProvider) services.get(serviceClass);
      if (sp == null) return null; // bail
    }
    synchronized (sp) {
      service = sp.getService(this, requestor, serviceClass);
      if (service != null && !(service instanceof NullService)) {
        if (! serviceClass.isAssignableFrom(service.getClass())) {
          throw new ClassCastException("ServiceProvider "+sp+
                                       " returned a Service ("+service+
                                       ") which is not an instance of "+serviceClass);
        }
      }
      // if we're going to succeed and they passed a revoked listener...
      if (srl != null) {
        addServiceListener(new ServiceRevokedListener() {
            public void serviceRevoked(ServiceRevokedEvent re) {
              if (serviceClass.equals(re.getService()))
                srl.serviceRevoked(re);
            }
          });
      }
    }
    return service;
  }

  public void releaseService(Object requestor, Class serviceClass, Object service) {
    if (requestor == null) throw new IllegalArgumentException("null requestor");
    if (serviceClass == null) throw new IllegalArgumentException("null serviceClass");

    ServiceProvider sp;
    synchronized (servicesLock) {
      sp = (ServiceProvider) services.get(serviceClass);
    }
    if (sp != null) {
      synchronized (sp) {
        sp.releaseService(this, requestor, serviceClass, service);
      }
    }
  }
}
