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


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 * A Collection which implements a set of TimeSpan elements
 * which are maintained sorted first by start time and then by
 * end time.  The order of temporally-equivalent but non-equal
 * objects is undefined but stable.
 **/
public class NonOverlappingTimeSpanSet extends TimeSpanSet {

  // constructors
  public NonOverlappingTimeSpanSet() {
    super();
  }

  public NonOverlappingTimeSpanSet(int i) {
    super(i);
  }

  public NonOverlappingTimeSpanSet(NonOverlappingTimeSpanSet set) {
    super(set.size());

    unsafeUpdate(set);
  }

  public NonOverlappingTimeSpanSet(Collection c) {
    super(c.size());

    // insert them carefully.
    for (Iterator i = c.iterator(); i.hasNext();) {
      add(i.next());
    }
  }

  //Add check for overlapping
  public boolean add(Object o) {
    if (!(o instanceof TimeSpan)) {
      throw new ClassCastException();
    }

    Collection collection = intersectingSet((TimeSpan)o);
    if (collection.size() != 0) {
      throw new IllegalArgumentException();
    } else {
      return super.add(o);
    }
  }


  /** 
   * @return the element which intersects with 
   * the specified time. Will return null if time is not
   * covered by the set.
   **/
  public TimeSpan intersects(final long time) {
    Collection collection = intersectingSet(time);

    switch (collection.size()) {
      
    case 0:
      return null;
      
    case 1:
      return (TimeSpan)(collection.iterator().next());

    default:
      throw new RuntimeException("Overlapping elements");
    }
  }
  
  
  /** 
   * @return a NonOverlappingTimeSpanSet with no gaps. Continuous tiling 
   * generated by filling all gaps with the specified filler. 
   * @param filler NewTimeSpan to be used to fill gaps. Must be cloneable.
   **/
  public NonOverlappingTimeSpanSet fill(NewTimeSpan filler) {
    if (!(filler instanceof Cloneable)) {
      throw new ClassCastException("org.cougaar.util.NonOverlappingTimeSpanSet.fill(): " + 
                           filler + " does not implement cloneable");
    } 
    
    Method clone;
    try {
      clone = filler.getClass().getMethod("clone", new Class[0]);
    } catch (NoSuchMethodException nsme) {
      throw new IllegalArgumentException("org.cougaar.util.NonOverlappingTimeSpanSet.fill(): " + 
                           filler + " does not have a clone() method");
    } 

    long start = TimeSpan.MIN_VALUE;

    NonOverlappingTimeSpanSet fill = new NonOverlappingTimeSpanSet();
    
    Object []cloneArgs = new Object[0];
    for (int i = 0; i < size(); i++) {
      TimeSpan ts = (TimeSpan)get(i);
      if (ts.getStartTime() > start) {
        try {
          NewTimeSpan fillerClone = (NewTimeSpan)clone.invoke(filler, cloneArgs);
          fillerClone.setTimeSpan(start, ts.getStartTime());
          fill.add(fillerClone);
        } catch (IllegalAccessException iae) {
          iae.printStackTrace();
          throw new IllegalArgumentException("org.cougaar.util.NonOverlappingTimeSpanSet.fill(): unable to execute clone method on " + filler);
        } catch (InvocationTargetException ite) {
          ite.printStackTrace();
          throw new IllegalArgumentException("org.cougaar.util.NonOverlappingTimeSpanSet.fill(): unable to execute clone method on " + filler);
        }
      }
      fill.add(ts);

      start = ts.getEndTime();
    }

    if (start != TimeSpan.MAX_VALUE) {
      try {
        NewTimeSpan fillerClone = (NewTimeSpan)clone.invoke(filler, cloneArgs);
        fillerClone.setTimeSpan(start, TimeSpan.MAX_VALUE);
        fill.add(fillerClone);      
      } catch (IllegalAccessException iae) {
        iae.printStackTrace();
        throw new IllegalArgumentException("org.cougaar.util.NonOverlappingTimeSpanSet.fill(): unable to execute clone method on " + filler);
      } catch (InvocationTargetException ite) {
        ite.printStackTrace();
        throw new IllegalArgumentException("org.cougaar.util.NonOverlappingTimeSpanSet.fill(): unable to execute clone method on " + filler);
      }
    }

    return fill;
  }
    
  // Implement for testing
  public static void main(String arg[]) {
    class X implements TimeSpan {
      long start;
      long end;

      public X(long x){ start = x; end = x + 3;}
      public long getStartTime() { return start; }
      public long getEndTime() { return end;}
      public String toString() { return "{" + start + " => " + end + "}";}
    }
    
    class Fill implements NewTimeSpan, Cloneable {
      long start = TimeSpan.MIN_VALUE;
      long end = TimeSpan.MAX_VALUE;

      public Fill() {}
      public long getStartTime() { return start; }
      public long getEndTime() { return end;}
      public void setTimeSpan(long s, long e) {
        start = s;
        end = e;
      }
      
      public Object clone() {
        Fill fill = new Fill();
        fill.setTimeSpan(getStartTime(), getEndTime());
        return fill;
      }

      public String toString() { return "{Default: "+ start + 
                                   " => " + end+"}";}
    }
    NonOverlappingTimeSpanSet notss = new NonOverlappingTimeSpanSet();

    for (int i = 1; i < 25; i+=5) {
      notss.add(new X(i));
    }

    System.out.println(notss.toString());

    TimeSpan tss;

    tss = notss.intersects(0);
    
    System.out.println("Returned " + tss + " as intersection at " + 0);

    tss = notss.intersects(4);

    System.out.println("Returned " + tss + " as intersection at " + 4);   

    tss = notss.intersects(6);

    System.out.println("Returned " + tss + " as intersection at " + 6);   


    tss = notss.intersects(7);

    System.out.println("Returned " + tss + " as intersection at " + 7);   

    NonOverlappingTimeSpanSet notssClone = 
      new NonOverlappingTimeSpanSet(notss);

    System.out.println("notssClone " + notssClone);

    NonOverlappingTimeSpanSet notssEmpty = new NonOverlappingTimeSpanSet(new ArrayList());
    System.out.println("notssEmpty " + notssEmpty);

    /*
    tss = new X(6);

    notss.add(tss);
    */

    NonOverlappingTimeSpanSet filled = notss.fill(new Fill());
    Iterator iterator = filled.iterator();
    while (iterator.hasNext()) {
      System.out.println("filled : " + iterator.next());
    }
  }

}
  
  
