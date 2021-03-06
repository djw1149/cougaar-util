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

package org.cougaar.util.log;

/**
 * The Logger provides a generic logging API.
 * <p>
 * This provides the basic: <pre>
 *    if (log.isDebugEnabled()) {
 *      log.debug("my message");
 *    }
 * </pre> and related logging methods.
 * <p>
 * This API is a subset of the "log4j" API, but the underlying
 * implementation may use a different logger, such as "jsr47".
 * <p>
 * Note that (currently) the Logger user is unable to alter the
 * underlying logger's threshold level.  A separate 
 * LoggerController must be used to make such modifications.
 * <p>
 * An enhancement idea is to allow a Logger user to alter this 
 * level -- for example, this could be used to increase logging 
 * detail when an error is deteted.  For now the equivalent 
 * behavior can be obtained by using "log(level, ..)" and 
 * selecting the "level" value at runtime.
 */
public interface Logger {

  /**
   * Generic logging levels.
   * <p>
   * The value of these constants may be modified in the future 
   * without notice.  For example, "DEBUG" may be changed from
   * "1" to some other integer constant.  However, the ordering 
   * of:<pre>
   *   DETAIL &lt; DEBUG &lt; INFO &lt; WARN &lt; ERROR &lt; SHOUT &lt; FATAL
   * </pre><br> is guaranteed.
   */
  int DETAIL  = 1;
  int DEBUG   = 2;
  int INFO    = 3;
  int WARN    = 4;
  int ERROR   = 5;
  int SHOUT   = 6;
  int FATAL   = 7;

  /**
   * Logger users should check "isEnabledFor(..)" before requesting 
   * a log message, to prevent unnecessary string creation.
   *
   * <p>
   * <pre>
   * When the log message requires constructing a String (e.g.
   * by using "+", or by calculating some value), then the
   * "is*Enabled(..)" check is preferred.  For example:
   *   if (isDebugEnabled()) {
   *     debug("good, this message will be logged, "+someArg);
   *   }
   * is prefered to:
   *   debug("maybe this will be logged, maybe wasteful, "+someArg);
   *
   * The one exception is when the message is a constant string,
   * in which case the "is*Enabled(..)" check is unnecessary. 
   * For example:
   *   debug("a constant string is okay");
   * is just as good as:
   *   if (isDebugEnabled()) {
   *     debug("isDebug check not needed, but harmless");
   *   }
   * However, developers often modify their logging statements,
   * so it's best to always use the "is*Enabled(..)" pattern.
   * </pre>
   * <p>
   *
   * Although this seems like a minor point, these string allocations
   * can add up to a potentially large (and needless) performance
   * penalty when the logging level is turned down.
   *
   * @param level a logging level, such as DEBUG
   */
  boolean isEnabledFor(int level);


  /**
   * Append the specified message to the log, but <i>only</i> if the 
   * logger includes the specified logging level.
   *
   * @param level the required logging level (DEBUG, WARN, etc)
   * @param message the string to log
   *
   * @see #isEnabledFor(int)
   */
  void log(int level, String message);

  /**
   * Append both specified message and throwable to the log, but 
   * <i>only</i> if the logger includes the specified logging level.
   * <p>
   * If the throwable is null then this is equivalent to:<pre>
   *   log(level, message).</pre>
   *
   * @param level the required logging level (DEBUG, WARN, etc)
   * @param message the string to log
   * @param t the throwable (e.g. RuntimeException) that is 
   *          related to the message
   *
   * @see #isEnabledFor(int)
   */
  void log(int level, String message, Throwable t);

  //
  // all methods after this point are all "shorthand" methods that
  // either call "isEnabledFor(..)" or "log(..)".  In some cases
  // the shorthand is slightly more efficient than the equivalent
  // (generic) "isEnabledFor(..)" and/or "log(..)" call.
  //

  //
  // specific "isEnabledFor(..)" shorthand methods:
  //

  boolean isDetailEnabled();
  boolean isDebugEnabled();
  boolean isInfoEnabled();
  boolean isWarnEnabled();
  boolean isErrorEnabled();
  boolean isShoutEnabled();
  boolean isFatalEnabled();

  //
  // specific "level" shorthand methods:
  //

  /**
   * Equivalent to "log(DETAIL, ..)".
   */
  void detail(String message);
  void detail(String message, Throwable t);

  /**
   * Equivalent to "log(DEBUG, ..)".
   */
  void debug(String message);
  void debug(String message, Throwable t);

  /**
   * Equivalent to "log(INFO, ..)".
   */
  void info(String message);
  void info(String message, Throwable t);

  /**
   * Equivalent to "log(WARN, ..)".
   */
  void warn(String message);
  void warn(String message, Throwable t);

  /**
   * Equivalent to "log(ERROR, ..)".
   */
  void error(String message);
  void error(String message, Throwable t);

  /**
   * Equivalent to "log(SHOUT, ..)".
   */
  void shout(String message);
  void shout(String message, Throwable t);

  /**
   * Equivalent to "log(FATAL, ..)".
   */
  void fatal(String message);
  void fatal(String message, Throwable t);

  void printDot(String dot);
}
