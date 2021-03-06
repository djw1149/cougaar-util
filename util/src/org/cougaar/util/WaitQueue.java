/*
 * <copyright>
 *  
 *  Copyright 2003-2004 BBNT Solutions, LLC
 *  under sponsorship of the Defense Advanced Research Projects
 *  Agency (DARPA).
 * 
 *  You can redistribute this software and/or modify it under the
 *  terms of the Cougaar Open Source License as published on the
 *  Cougaar Open Source Website (www.cougaar.org).
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 * </copyright>
 */

package org.cougaar.util;

import java.util.HashMap;

/** Implement an asynchronous wait/notify map with value pass-through, selected by an opaque key.
 **/
class WaitQueue {
  private long counter = 0L;
  private HashMap selects = new HashMap(11); // assume not too many at a time

  /** Construct a key to be used in this wait queue **/
  Object getKey() {
    Object key;
    synchronized (selects) {
      key = new Long(counter++);
      selects.put(key, new Waiter());
    }
    return key;
  }

  /** Activate any thread(s) which are waiting for a response from the key **/
  void trigger(Object key, Object value) {
    Waiter waiter = null;
    synchronized (selects) {
      waiter = (Waiter) selects.get(key);
      if (waiter == null) return; // bail if no waiter
    }
    waiter.trigger(value);
  }

  /** Block the current thread until there is a triggerKey has been called
   * on the referenced key.
   * Returns immediately if the key is unknown or has previously been triggered.
   * @throw InterruptedException if the wait is interrupted.  In this case,
   * the trigger is <em>not</em> cleared.
   * @param key The key as returned by #getKey()
   * @param timeout How long to wait or 0 (forever)
   **/
  Object waitFor(Object key, long timeout) throws InterruptedException {
    Waiter waiter = null;
    synchronized (selects) {
      waiter = (Waiter) selects.get(key);
      if (waiter == null) return null; // bail if no waiter
    }

    // non-null waiter: pass control to it
    Object value = waiter.waitFor(timeout);
    
    // when done, clear it
    synchronized (selects) {
      selects.remove(key);
    }

    return value;
  }

  /** alias for waitFor(key,0L);
   **/
  Object waitFor(Object key) throws InterruptedException {
    return waitFor(key,0L);
  }

  /** Return true IFF waitFor(key) call would have blocked for some amount of time **/
  boolean wouldBlock(Object key) {
    Waiter waiter = null;
    synchronized (selects) {
      waiter = (Waiter) selects.get(key);
      if (waiter == null) return false;
    }

    // non-null waiter: pass control to it
    return waiter.wouldBlock();
  }

  private static class Waiter {
    private boolean trigger = false;
    private Object value = null;

    synchronized Object waitFor(long timeout) throws InterruptedException{
      if (trigger) return value;
      this.wait(timeout);
      return value;
    }

    synchronized void trigger(Object value) {
      trigger = true;
      this.value = value;
      this.notify();
    }

    synchronized boolean wouldBlock() {
      return (!trigger);
    }
  }
}
