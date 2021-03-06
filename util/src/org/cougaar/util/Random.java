/*
 * <copyright>
 *  
 *  Copyright 1997-2004 BBNT Solutions, LLC
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

public class Random extends java.util.Random {
  /**
    * 
    */
   private static final long serialVersionUID = 1L;
// Memoize for repeated use with the same mean rate (xm)
  private double oldXm = -1.0;		// Flag for whether xm has changed since last call,
  private double g, sq, logXm;

  /**
   * Returns an integer value that is a random deviate drawn from a
   * Poisson distribution of mean xm.
   **/
  public int nextPoisson(double xm) {
    double em, t;
    if (xm < 25.0) {                    // Use direct method (25 is the empirically
      if (xm != oldXm) {                // determined performance crossover).
	oldXm = xm;
	g = Math.exp(-xm);              // If Xta is new, compute the exponential.
      }
      em = -1.0;
      t = 1.0;
      while (true) {
	em += 1.0;			// Instead of adding exponential deviates it is equivalent to
        t *= nextDouble();              // multiply uniform deviates. Then we never actually have to
	if (t <= g) return (int) em;    // take the log, merely compare to the pre-computed exponential.
      }
    } else {                            // Use rejection method.
      if (xm != oldXm) {                // If xm has changed since the last call, then precompute
	oldXm = xm;                     // some functions which occur below.
	sq = Math.sqrt(2.0 * xm);
	logXm = Math.log(xm);
	g = xm * logXm -                // The function gammaLn is
          MoreMath.gammaLn(xm + 1.0);   // the natural log of the gamma function, as given in 6.2. 
      }
      double y;
      while (true) {
	y = Math.tan(Math.PI * nextDouble()); // y is a deviate from a Lorentzian comparison function.
	em = sq * y + xm;               // em is y, shifted and scaled.
	if (em < 0.0) continue;         // Reject if in regime of zero probability.
	em = Math.floor(em);            // The trick for integer-valued distributions.
	t = (0.9                        // the ratio of the desired distribution to
	     * (1.0 + y*y)              // the comparison function;
	     * Math.exp(em * logXm      // we accept or reject by comparing it
			- MoreMath.gammaLn(em + 1.0)
			- g));          // to another uniform deviate. The factor 0.9 is chosen
	if (nextDouble() > t) continue; // so that t never exceeds 1.
	break;
      }
    }
    return (int) em;			
  }

  /* Test harness. Generate a set of samples with specified mean */
  /*
  public static void main(String[] args) {
    double xm = 1.0;
    double nTrials = 100;
    if (args.length > 0) xm = Double.parseDouble(args[0]);
    if (args.length > 1) nTrials = Double.parseDouble(args[1]);
    SortedMap bins = new TreeMap();
    Random random = new Random();
    class Bin {
      int count;
    };
    double sum = 0.0;
    for (int i = 0; i < nTrials; i++) {
      int q = random.nextPoisson(xm);
      Integer k = new Integer(q);
      Bin bin = (Bin) bins.get(k);
      if (bin == null) {
	bin = new Bin();
	bins.put(k, bin);
      }
      bin.count++;
      sum += q;
    }
    for (Iterator i = bins.keySet().iterator(); i.hasNext(); ) {
      Integer k = (Integer) i.next();
      Bin bin = (Bin) bins.get(k);
      System.out.println(k + "\t" + bin.count);
    }
  }
  */
}
