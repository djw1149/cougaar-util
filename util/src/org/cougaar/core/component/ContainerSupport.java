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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.beans.beancontext.*; /*make @see reference work*/

import org.cougaar.util.GenericStateModel;
import org.cougaar.util.GenericStateModelAdapter;
import org.cougaar.util.log.Logger;
import org.cougaar.util.log.Logging;

/** A basic implementation of a Container.
 **/
public abstract class ContainerSupport
extends GenericStateModelAdapter
implements Container, StateObject
{
  protected final ComponentFactory componentFactory = specifyComponentFactory();
  /** this is the prefix that all subcomponents must have as a prefix **/
  protected final String containmentPrefix = specifyContainmentPoint()+".";

  protected Object loadState = null;

  private ComponentDescriptions externalComponentDescriptions = null;

  // my service broker
  protected ServiceBroker serviceBroker = null;

  // the service broker for all child components
  protected ServiceBroker childServiceBroker = null;

  /** The actual set of child BoundComponent loaded. 
   * @see org.cougaar.core.component.BoundComponent
   **/
  protected final ArrayList boundComponents = new ArrayList(11);

  /** a Sorted Collection of child BinderFactory components.
   * Note that we cannot use TreeSet because of the Collection API's
   * braindamaged insistance on conflating identity and order, so
   * this is a regular arraylist which we keep sorted by hand.
   **/
  protected final ArrayList binderFactories = new ArrayList();

  /**
   * The ComponentDescriptions for loaded binder factories.
   **/
  protected final ArrayList binderFactoryDescriptions = new ArrayList();

  private Logger log = Logging.getLogger(ContainerSupport.class.getName());

  protected ContainerSupport() {
    // child service broker
    ServiceBroker csb = specifyChildServiceBroker();
    if (csb != null) setChildServiceBroker(csb);

    // binder factory
    BinderFactory bf = createBinderFactory();
    if (bf != null && !attachBinderFactory(bf)) {
      throw new RuntimeException(
          "Failed to load the DefaultBinderFactory");
    }
  }

  /** Overridable by extending classes to capture the BindingSite. **/
  public void setBindingSite(BindingSite bs) {
    this.serviceBroker = bs.getServiceBroker();

    // late-binding child service broker
    ServiceBroker csb = createChildServiceBroker(bs);
    if (csb != null) {
      setChildServiceBroker(csb);
    }
  }

  protected ServiceBroker getServiceBroker() {
    return serviceBroker;
  }

  /** override to specify a different component factory class. 
   * Called once during initialization.
   **/
  protected ComponentFactory specifyComponentFactory() {
    return new ComponentFactory() {};
  }

  /** override to specify insertion point of this component, 
   * the parent insertion point which sub Components must match,
   * e.g. "Node.AgentManager.Agent.PluginManager"
   * this is called once during initialization.
   **/
  protected abstract String specifyContainmentPoint();

  /** override to specify a the ServiceBroker object to use for children. 
   * this is called once during initialization.
   * Note that this value might be only part of the process for 
   * actually finding the services for children and/or peers.
   * <p>
   * Either this method must be overridden to return a non-null value,
   * or the subclass must call setChildServiceBroker exactly once with 
   * a non-null value.
   **/
  protected ServiceBroker specifyChildServiceBroker() {
    return null;
  }

  protected ServiceBroker createChildServiceBroker(BindingSite bs) {
    return new DefaultServiceBroker(bs);
  }
  
  protected void destroyChildServiceBroker(ServiceBroker csb) {
    if (csb instanceof DefaultServiceBroker) {
      ((DefaultServiceBroker) csb).myDestroy();
    }
  }

  protected final void setChildServiceBroker(ServiceBroker sb) {
    if (sb == null) {
      throw new IllegalArgumentException("Specified ServiceBroker must not be null");
    }
    if (childServiceBroker != null) {
      throw new IllegalArgumentException("ServiceBroker already set");
    }
    childServiceBroker = sb;
  }

  protected ServiceBroker getChildServiceBroker() {
    return childServiceBroker;
  }

  /** Return (or construct) a serviceBroker instance for 
   * a particular child component.
   * <p>The default is to ignore the child and return the value
   * of getChildServiceBroker with no arguments.
   * <p>This can be used to build per-child ServiceBroker instances
   * as a simpler option than adding an additional high-priority wrapping binder.
   * See also getChildContainerProxy.
   **/
  protected ServiceBroker getChildServiceBroker(Object child) {
    return getChildServiceBroker();
  }

  protected BinderFactory createBinderFactory() {
    return new DefaultBinderFactory();
  }


  //
  // implement collection
  //

  
  public int size() {
    synchronized(boundComponents) {
      return boundComponents.size();
    }
  }

  public boolean isEmpty() {
    return (size() == 0);
  }

  public boolean contains(Object o) {
    if (o instanceof ComponentDescription) {
      ComponentDescription cd = (ComponentDescription) o;
      String ip = cd.getInsertionPoint();
      if (!(ip.startsWith(containmentPrefix))) {
        return false;
      }
      final boolean isDirectChild = 
        (0 >= ip.indexOf('.', containmentPrefix.length()));
      synchronized (boundComponents) {
        for (int i = 0, n = boundComponents.size(); i < n; i++) {
          Object oi = boundComponents.get(i);
          if (!(oi instanceof BoundComponent)) {
            continue;
          }
          BoundComponent bc = (BoundComponent) oi;
          Object bcc = bc.getComponent();
          if (!(bcc instanceof ComponentDescription)) {
            continue;
          }
          ComponentDescription bccd = (ComponentDescription) bcc;
          if (isDirectChild) {
            // at this level in hierarchy
            if (cd.equals(bccd)) {
              return true;
            }
          } else {
            // child container
            Binder bcb = bc.getBinder();
            if ((bcb instanceof ContainerBinder) &&
                (ip.startsWith(bccd.getInsertionPoint())) &&
                (((ContainerBinder) bcb).contains(cd))) {
              return true;
            }
          }
        }
      }
    } else if (o instanceof Component) {
      // FIXME no good way to find the insertion point!
      synchronized(boundComponents) {
        int l = boundComponents.size();
        for (int i=0; i<l; i++) {
          BoundComponent bc = (BoundComponent) boundComponents.get(i);
          if (bc.getComponent().equals(o)) return true;
        }
      }
    }
    return false;
  }

  public Iterator iterator() {
    synchronized(boundComponents) {
      int l = boundComponents.size();
      ArrayList tmp = new ArrayList(l);
      for (int i=0; i<l; i++) {
        BoundComponent bc = (BoundComponent) boundComponents.get(i);
        tmp.add(bc.getComponent());
      }
      return tmp.iterator();
    }
  }

  /** Get a List of all child components */
  protected List listComponents() {
    synchronized (boundComponents) {
      int n = boundComponents.size();
      List result = new ArrayList(n);
      for (int i = 0; i < n; i++) {
        BoundComponent bc = (BoundComponent) boundComponents.get(i);
        Object comp = bc.getComponent();
        if (comp instanceof ComponentDescription) {
          ComponentDescription cd = (ComponentDescription) comp;
          result.add(cd);
        }
      }
      return result;
    }
  }

  /**
   * Get an Iterator of all child Binders.
   *
   * @see #listBinders
   */
  protected Iterator binderIterator() {
    return listBinders().iterator();
  }

  /**
   * Get a List of all child Binders.
   * <p>
   * Need a "ContainerSupport.lock()" to make this safe...
   */
  protected List listBinders() {
    synchronized(boundComponents) {
      int l = boundComponents.size();
      ArrayList tmp = new ArrayList(l);
      for (int i=0; i<l; i++) {
        BoundComponent bc = (BoundComponent) boundComponents.get(i);
        tmp.add(bc.getBinder());
      }
      return tmp;
    }
  }

  /**
   * Add to the container, returning true if the object 
   * is added, false if it is already contained, and throw an 
   * exception in all other cases.
   */
  public boolean add(Object o) {
    ComponentDescription cd;
    Object cstate;
    if (log.isDebugEnabled()) {
      log.debug("add " + o);
    }

    if (o instanceof ComponentDescription) {
      // typical component description
      cd = (ComponentDescription)o;
      cstate = null;
    } else if (o instanceof StateTuple) {
      // description plus initial state
      StateTuple st = (StateTuple)o;
      cd = st.getComponentDescription();
      cstate = st.getState();
    } else if (o instanceof BinderFactory) {
      // unusual case -- prefer to load from cd
      if (log.isDebugEnabled()) {
        log.debug("add BinderFactory " + o);
      }
      return attachBinderFactory((BinderFactory)o);
    } else if (o instanceof Component) {
      // unusual case -- prefer to load from cd
      return loadComponent(o, null);
    } else {
      // not a clue.
      throw new IllegalArgumentException(
          "Unsupported container element type: "+
          ((o != null) ? o.getClass().getName() : "null"));
    }

    // load from a component description
    String ip = ((cd != null) ? cd.getInsertionPoint() : null);
    if (ip == null) {
      // description or insertion point not specified
      throw new IllegalArgumentException(
          "ComponentDescription must specify an insertion point");
    }
    if (!(ip.startsWith(containmentPrefix))) {
      // wrong insertion point
      throw new IncorrectInsertionPointException(
          "Insertion point "+ip+" doesn't match container's "+
          containmentPrefix, cd);
    }

    // match! - now do we load it here or below - look for any more 
    // dots beyond the one trailing the prefix...
    String tail = ip.substring(containmentPrefix.length());
    if ("Binder".equals(tail) || "BinderFactory".equals(tail)) {
      // load binder factory, ignore cstate?
      return loadBinderFactory(cd);
    }

    boolean isDirectChild = (0 >= tail.indexOf('.'));

    if (isDirectChild) {
      // no more dots
      // check to see if the component is already loaded
      synchronized (boundComponents) {
        for (int i = 0, n = boundComponents.size(); i < n; i++) {
          Object oi = boundComponents.get(i);
          if (!(oi instanceof BoundComponent)) {
            continue;
          }
          BoundComponent bc = (BoundComponent) oi;
          Object bcc = bc.getComponent();
	  if (cd.equals(bcc)) {
	    // already loaded
	    return false;
	  }
        }
        // FIXME should add within lock to prevent duplicates
      }
      // insert here
      return loadComponent(cd, cstate);
    }

    // more dots: try inserting in subcomponents
    synchronized (boundComponents) {
      for (int i = 0, n = boundComponents.size(); i < n; i++) {
        Object oi = boundComponents.get(i);
        if (!(oi instanceof BoundComponent)) {
          continue;
        }
        BoundComponent bc = (BoundComponent) oi;
        Binder b = bc.getBinder();
        if (!(b instanceof ContainerBinder)) {
          continue;
        }
        Object bcc = bc.getComponent();
        if (bcc instanceof ComponentDescription) {
          ComponentDescription bccd = (ComponentDescription) bcc;
          if (!(ip.startsWith(bccd.getInsertionPoint()))) {
            continue;
          }
        } else {
          // non-desc child, but okay
        }
        try {
          boolean ret = ((ContainerBinder)b).add(o);
          // child already contains or added it
          return ret;
        } catch (IncorrectInsertionPointException ipE) {
          // wrong insertion point
        }
      }
    }

    // not at this level, and no child accepted it
    throw new ComponentLoadFailure(
        "Component not loaded by this container ("+
        containmentPrefix+") or its children",
        cd);
  }

  public boolean remove(Object o) {
    if (!(o instanceof ComponentDescription)) {
      // could support Components, but not required at this time
      throw new UnsupportedOperationException(
          "Removal of non-ComponentDescription not supported");
    }

    ComponentDescription cd = (ComponentDescription) o;
    String ip = cd.getInsertionPoint();
    if ((ip == null) ||
        (!(ip.startsWith(containmentPrefix)))) {
      return false;
    }
    String tail = ip.substring(containmentPrefix.length());

    if ("Binder".equals(tail) || "BinderFactory".equals(tail)) {
      throw new UnsupportedOperationException(
          "Binder and BinderFactory removal not supported ("+
          ip+")");
    }

    boolean isDirectChild = (0 >= tail.indexOf('.'));

    // find the child and remove it
    Binder removedBinder;
    synchronized (boundComponents) {
      for (int i = 0, n = boundComponents.size(); ; i++) {
        if (i >= n) {
          // not found
          return false;
        }
        Object oi = boundComponents.get(i);
        if (!(oi instanceof BoundComponent)) {
          continue;
        }
        BoundComponent bc = (BoundComponent) oi;
        Object bcc = bc.getComponent();
        if (!(bcc instanceof ComponentDescription)) {
          continue;
        }
        ComponentDescription bccd = (ComponentDescription) bcc;
        if (isDirectChild) {
          // at this level in hierarchy
          if (cd.equals(bccd)) {
            removedBinder = bc.getBinder();
            boundComponents.remove(i);
            break;
          }
        } else {
          // child container
          String bctail = 
            bccd.getInsertionPoint().substring(
                containmentPrefix.length());
          if (tail.startsWith(bctail)) {
            Binder bcb = bc.getBinder();
            if ((bcb instanceof ContainerBinder) &&
                (((ContainerBinder) bcb).remove(cd))) {
              return true;
            }
          }
        }
      }
    }

    // unload the removed direct child
    try {
      switch (removedBinder.getModelState()) {
        case GenericStateModel.ACTIVE:
          removedBinder.suspend();
          // fall-through
        case GenericStateModel.IDLE:
          removedBinder.stop();
          // fall-through
        case GenericStateModel.LOADED:
          removedBinder.unload();
          // fall-through
        case GenericStateModel.UNLOADED:
          // unloaded
          break;
        default:
          // should never happen
          throw new IllegalStateException(
              "Illegal binder state: "+
              removedBinder.getModelState());
      }
    } catch (RuntimeException e) {
      throw new ComponentRuntimeException(
          "Removed component with unclean unload", cd, e);
    }

    return true;
  }

  /** Add all elements of Collection to the collection, in the order specified **/
  public boolean addAll(Collection c) {
    boolean allAdded = true;
    for (Iterator it = c.iterator(); it.hasNext(); ) {
      Object o = it.next();
      boolean succ = add(o);
      allAdded = allAdded && (!succ);
    }
    return allAdded;
  }

  public void clear() {
    throw new UnsupportedOperationException();
  }

  // unsupported Collection ops
  public Object[] toArray() {
    throw new UnsupportedOperationException();
  }
  public Object[] toArray(Object[] ignore) {
    throw new UnsupportedOperationException();
  }
  public boolean containsAll(Collection c) {
    throw new UnsupportedOperationException();
  }
  public boolean removeAll(Collection c) {
    throw new UnsupportedOperationException();
  }
  public boolean retainAll(Collection c) {
    throw new UnsupportedOperationException();
  }

  /** load a component into our set.  We are sure that this is
   * the requested level, but might not be certain how much we trust
   * it as of yet.  In particular, we may need to treat different classes
   * of Components differently.
   *<P>
   * The component (and the binder tree) should be loaded and started
   * when this loadComponent complete successfully.
   *
   * @return true on success.
   * @throws ComponentLoadFailure When the component Cannot be loaded.
   **/
  protected boolean loadComponent(Object c, Object cstate) {
    Binder b = bindComponent(c); // cannot return null any more
    BoundComponent bc = new BoundComponent(b,c);
    synchronized (boundComponents) {
      boundComponents.add(bc);
    }
    if (cstate != null) {
      // provide the state during load
      b.setState(cstate);
    }
    b.load();
    b.start();
    return true;
  }

  /**  These BinderFactories
   * are used to generate the primary containment
   * binders for the child components.  If the child
   * component is the first BinderFactory, then we'll
   * call bindBinderFactory after failing to find a binder.
   * <p>
   * A Component is initialized (but not loaded) s a side-effect of binding 
   **/
  protected Binder bindComponent(Object c) {
    synchronized (binderFactories) {
      ArrayList wrappers = null;
      Binder b = null;
      for (Iterator i = binderFactories.iterator(); i.hasNext(); ) {
        BinderFactory bf = (BinderFactory) i.next();
        if (bf instanceof BinderFactoryWrapper) {
          if (wrappers==null) wrappers=new ArrayList(1);
          wrappers.add(bf);
        } else {
          b = bf.getBinder(c);
          if (b != null) {
            break;
          }
        }
      }

      // now apply any wrappers.
      if (wrappers != null) {
        int l = wrappers.size();
        for (int i=l-1; i>=0; i--) { // last ones innermost
          BinderFactoryWrapper bf = (BinderFactoryWrapper) wrappers.get(i);
          Binder w = bf.getBinder((b==null)?c:b);
          if (w!= null) {
            b = w;
          }
        }
      }

      if (b != null) {
        BindingUtility.setBindingSite(b, getChildContainerProxy(c));
        BindingUtility.setServices(b, getChildServiceBroker(c));
        BindingUtility.initialize(b);
        // done
        return b;
      } else {
        throw new ComponentLoadFailure("No binder found", c);
      }
    }    
  }

  /** Called when a componentDescription insertion point ends in .Binder or .BinderFactory 
   * @throws ComponentFactoryException if the BinderFactory Cannot be loaded.
   **/
  protected boolean loadBinderFactory(ComponentDescription cd) {
    if (checkBinderFactory(cd)) {
      Component bfc = componentFactory.createComponent(cd);
      if (bfc instanceof BinderFactory) {
        if (log.isDebugEnabled()) {
          log.debug("loadBinderFactory " + cd);
        }
        binderFactoryDescriptions.add(cd);
        return attachBinderFactory((BinderFactory)bfc);
      } else {
        throw new ComponentLoadFailure("Not a BinderFactory", cd);
      }
    } else {
      throw new ComponentLoadFailure("Failed BinderFactory test", cd);
    }
  }

  /** @return true iff the binderfactory is trusted enought to load **/
  protected boolean checkBinderFactory(ComponentDescription cd) {
    return true;
  }

  /** Activate a binder factory
   **/
  protected boolean attachBinderFactory(BinderFactory c) {
    synchronized (binderFactories) {
      binderFactories.add(c);
      Collections.sort(binderFactories, BinderFactory.comparator);
    }
    return BindingUtility.activate(
        c, getChildContainerProxy(c), getChildServiceBroker());
  }

  /** Specifies an object to use as the "parent" proxy object
   * for otherwise unbound BinderFactory instances.
   * This will be either be the Container itself (this) or a
   * simple proxy for the container so that BinderFactory instances
   * cannot downcast the object to get additional privileges.
   * @note Most uses should use/override getChildContainerProxy instead.
   **/
  protected ContainerAPI getContainerProxy() {
    return new DefaultProxy();
  }

  /** Specifies an object to use as the "parent" proxy object
   * for a particular object (generally an <em>unbound</em> component).
   * This will be either be the Container itself (this) or a
   * simple proxy for the container so that BinderFactory instances
   * cannot downcast the object to get additional privileges.
   **/
  protected ContainerAPI getChildContainerProxy(Object o) {
    return getContainerProxy();
  }

  /** Matching setter for getExternalComponentDescriptions **/
  protected final void setExternalComponentDescriptions(ComponentDescriptions cds) {
    if (log.isDebugEnabled()) {
      log.debug("setExternalComponentDescriptions = " + cds);
    }
    externalComponentDescriptions = cds;
  }

  public void setState(Object loadState) {
    this.loadState = loadState;
  }

  public Object getState() {
    return captureState();
  }

  protected ComponentDescriptions captureState() {
    synchronized (boundComponents) {
      int n = boundComponents.size();
      List l = new ArrayList(n + binderFactoryDescriptions.size());
      l.addAll(binderFactoryDescriptions);
      for (int i = 0; i < n; i++) {
        BoundComponent bc = (BoundComponent) boundComponents.get(i);
        Object comp = bc.getComponent();
        if (comp instanceof ComponentDescription) {
          ComponentDescription cd = (ComponentDescription)comp;
          Binder b = bc.getBinder();
          Object state = b.getState();
          StateTuple ti = new StateTuple(cd, state);
          l.add(ti);
        } else {
          // error?
        }
      }
      return new ComponentDescriptions(l);
    }
  }

  protected abstract ComponentDescriptions findInitialComponentDescriptions();

  /** return a ComponentDescriptions object which contains a set of ComponentDescriptions
   * defined externally to be loaded into this Container. 
   * This will return the value computed by findExternalComponentDescriptions()
   * Note that this value is not filled until ContainerSupport.load() has been called.
   * @see #findExternalComponentDescriptions
   * @return null if no component descriptions specified.
   */
  protected final ComponentDescriptions getExternalComponentDescriptions() {
    return externalComponentDescriptions;
  }

  /** Extending classes may override this method to define a set of externally-specified
   * Component to load.  Will be called during load() immediately after super.load().
   * load will set the value returned by getExternalComponentDescriptions(), so that 
   * loadInternalSubcomponents, etc can retrieve the values.
   * @return null if none.
   */
  protected ComponentDescriptions findExternalComponentDescriptions() {
    if (loadState instanceof ComponentDescriptions) {
      ComponentDescriptions descs = (ComponentDescriptions) loadState;
      loadState = null;
      return descs;
    } else {
      // ask the container
      return findInitialComponentDescriptions();
    }
  }

  /** 
   * Called by super.load() to transit the state, sets the value of
   * getExternalComponentDescriptions() 
   * by calling
   * findExternalComponentDescriptions(),
   * then invokes each of
   * <ul>
   * <li>loadHighPriorityComponents()</li>
   * <li>loadInternalPriorityComponents()</li>
   * <li>loadBinderPriorityComponents()</li>
   * <li>loadComponentPriorityComponents()</li>
   * <li>loadLowPriorityComponents()</li>
   * </ul>
   * in order.
   */
  public void load() {
    super.load();
    setExternalComponentDescriptions(findExternalComponentDescriptions());
    loadHighPriorityComponents();
    loadInternalPriorityComponents();
    loadBinderPriorityComponents();
    loadComponentPriorityComponents();
    loadLowPriorityComponents();
  }

  public void suspend() {
    super.suspend();
    List childBinders = listBinders();
    for (int i = childBinders.size() - 1; i >= 0; i--) {
      Binder b = (Binder) childBinders.get(i);
      b.suspend();
    }
  }

  public void resume() {
    super.resume();
    for (Iterator childBinders = binderIterator();
        childBinders.hasNext();
        ) {
      Binder b = (Binder)childBinders.next();
      b.resume();
    }
  }

  public void stop() {
    super.stop();
    List childBinders = listBinders();
    for (int i = childBinders.size() - 1; i >= 0; i--) {
      Binder b = (Binder) childBinders.get(i);
      b.stop();
    }
  }

  public void halt() {
    suspend();
    stop();
  }

  public void unload() {
    super.unload();

    List childBinders = listBinders();
    for (int i = childBinders.size() - 1; i >= 0; i--) {
      Binder b = (Binder) childBinders.get(i);
      b.unload();
    }
    synchronized (boundComponents) {
      // unsafe?
      boundComponents.clear();
    }

    if (childServiceBroker != null) {
      destroyChildServiceBroker(childServiceBroker);
      childServiceBroker = null;
    }
  }
  
  /** Should check the ComponentDescription to see if it should
   * be loaded into the Container.  Implementations may use any rules 
   * they'd like.  The default implementation returns true IFF the
   * insertion point of the ComponentDescription is a direct child
   * of the ContainmentPoint.  The basic component model only calls
   * this method during the default load() methods - e.g. only
   * to load components specified by getExternalComponentDescriptions().
   *
   * @param o either a StateTuple or ComponentDescription
   * @return true iff the specified ComponentDescription should be loaded.
   **/
  protected boolean isSubComponentLoadable(Object o) {
    ComponentDescription cd =
      ((o instanceof StateTuple) ?
       (((StateTuple) o).getComponentDescription()) : ((ComponentDescription) o));
    return isInsertionPointLoadable(cd.getInsertionPoint());
  }

  /** validate a child component's InsertionPoint.  Simpler but
   * less flexible mechanism to isSubComponentLoadable.
   * <p>The default behavior is to return true IFF the insertion point
   * is a direct child of the container's containement prefix.
   **/
  protected boolean isInsertionPointLoadable(String ip) {
    String cpr = containmentPrefix;
    int cprl = cpr.length();
    boolean r = (ip.startsWith(cpr) && // starts with the containment point+'.'
                 ip.indexOf(".", cprl+1) == -1 // no more '.'
                 );
    return r;
  }

  /** Hook for loading all the high priority subcomponents.  The default implementation
   * loads all high priority components which match getSubComponentInsertionPoints().
   **/
  protected void loadHighPriorityComponents() {
    loadSubComponentsByPriority(ComponentDescription.PRIORITY_HIGH);
  }

  /** Hook for loading all the internal priority subcomponents.  The default implementation
   * loads all internal priority components which match getSubComponentInsertionPoints().
   **/
  protected void loadInternalPriorityComponents() {
    loadSubComponentsByPriority(ComponentDescription.PRIORITY_INTERNAL);
  }

  /** Hook for loading all the binder priority subcomponents.  The default implementation
   * loads all binder priority components which match getSubComponentInsertionPoints().
   **/
  protected void loadBinderPriorityComponents() {
    loadSubComponentsByPriority(ComponentDescription.PRIORITY_BINDER);
  }

  /** Hook for loading all the component priority subcomponents.  The default implementation
   * loads all component priority components which match getSubComponentInsertionPoints().
   **/
  protected void loadComponentPriorityComponents() {
    loadSubComponentsByPriority(ComponentDescription.PRIORITY_COMPONENT);
  }

  /** Hook for loading all the low priority subcomponents.  The default implementation
   * loads all low priority components which match getSubComponentInsertionPoints().
   **/
  protected void loadLowPriorityComponents() {
    loadSubComponentsByPriority(ComponentDescription.PRIORITY_LOW);
  }

  /** Utility method used buy the Load*PriorityComponents methods.
   * adds components as specified by getExternalComponentDescriptions
   * of the specified priority.
   **/
  protected void loadSubComponentsByPriority(int priority) {
    ComponentDescriptions cds = getExternalComponentDescriptions();
    if (cds == null) return;

    List pcd = cds.selectComponentDescriptions(priority);
    if (pcd == null) return;
    
    for (Iterator it = pcd.iterator(); it.hasNext(); ) {
      Object o = it.next();
      if (isSubComponentLoadable(o)) {
        try {
          add(o);
        } catch (RuntimeException re) {
          Logger l = Logging.getLogger(ContainerSupport.class);
          l.error("Skipping load of "+o+" into "+this, re);
        }
      }
    }
  }

  //
  // support classes
  //

  private static class DefaultBinderFactory
    extends BinderFactorySupport {
      public Binder getBinder(Object child) {
        return new DefaultBinder(this, child);
      }
      private static class DefaultBinder 
        extends ContainerBinderSupport
        implements BindingSite {
          public DefaultBinder(BinderFactory bf, Object child) {
            super(bf, child);
          }
          protected BindingSite getBinderProxy() {
            return this;
          }
        }
    }

  private static class DefaultServiceBroker 
    extends PropagatingServiceBroker {
      public DefaultServiceBroker(BindingSite bs) {
        super(bs);
      }
      private void myDestroy() {
        super.destroy();
      }
    }

  private class DefaultProxy
    implements ContainerAPI {
      public ServiceBroker getServiceBroker() {
        return ContainerSupport.this.getChildServiceBroker();
      }
      public boolean remove(Object childComponent) {
        return ContainerSupport.this.remove(childComponent);
      }
      public void requestStop() {}
    }
}
