/*
 * <copyright>
 *  Copyright 1997-2003 BBNT Solutions, LLC
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
package org.cougaar.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Wraps a Set disabling all mutator methods.
 * @deprecated Use java.util.Collections.unmodifiableCollection() method
 **/
public class ReadOnlyCollection implements Collection {
  private Collection inner;

  /**
   * @deprecated Use java.util.Collections.unmodifiableCollection() method
   **/
  public ReadOnlyCollection(Collection s) {
    inner = s;
  }

  // Accessors

  public int size() { return inner.size(); }
  public boolean isEmpty() { return inner.isEmpty(); }
  public boolean contains(Object o) { return inner.contains(o); }
  public boolean containsAll(Collection c) { return inner.containsAll(c); }
  public Iterator iterator() {
    return new Iterator() {
      Iterator it = inner.iterator();
      public boolean hasNext() {
        return it.hasNext();
      }
      public Object next() {
        return it.next();
      }
      public void remove() {
        throw new UnsupportedOperationException("ReadOnlyCollection: remove disallowed");
      }
    };
  }
  public boolean equals(Object o) { return inner.equals(o); }
  public int hashCode() { return inner.hashCode(); }
  public Object[] toArray() { return inner.toArray(); }
  public Object[] toArray(Object[] a) { return inner.toArray(a); }

  // Mutators

  public boolean add(Object o) {
    throw new UnsupportedOperationException("ReadOnlyCollection: add disallowed");
  }

  public boolean remove(Object o) {
    throw new UnsupportedOperationException("ReadOnlyCollection: remove disallowed");
  }

  public boolean addAll(Collection c) {
    throw new UnsupportedOperationException("ReadOnlyCollection: addAll disallowed");
  }

  public boolean retainAll(Collection c) {
    throw new UnsupportedOperationException("ReadOnlyCollection: retainAll disallowed");
  }

  public boolean removeAll(Collection c) {
    throw new UnsupportedOperationException("ReadOnlyCollection: removeAll disallowed");
  }

  public void clear() {
    throw new UnsupportedOperationException("ReadOnlyCollection: clear disallowed");
  }
}
