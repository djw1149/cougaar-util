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

import java.io.IOException;
import java.io.InputStream;

/**
 * An efficient replacement for java.io.PipedInputStream.
 **/
public class PipedInputStream extends InputStream {
  private static final int DEFAULT_BUFSIZE = 10000;

  private byte[] buffer;
  private int in = 0;
  private int out = 0;
  private int space;
  private boolean eof = false;
  private boolean closed = false;

  /**
   * Creates an unconnected PipedInputStream with a default buffer size.
   **/
  public PipedInputStream() {
    this(DEFAULT_BUFSIZE);
  }

  /**
   * Creates an unconnected PipedInputStream with a specific buffer
   * size.
   * @param bufSize the size of the ring buffer (defaults to DEFAULT_BUFSIZE).
   **/
  public PipedInputStream(int bufSize) {
    buffer = new byte[bufSize];
    space = bufSize;
  }

  /**
   * Creates a PipedInputStream with a default buffer size connected
   * to the given PipedOutputStream.
   * @param pos the PipedOutputStream to connect to this PipedInputStream
   **/
  public PipedInputStream(PipedOutputStream pos) {
    this(DEFAULT_BUFSIZE);
    pos.connect(this);
  }

  /**
   * Creates a PipedInputStream with a specific buffer size connected
   * to the given PipedOutputStream.
   * @param pos the PipedOutputStream to connect to this PipedInputStream
   * @param bufSize the size of the ring buffer (defaults to DEFAULT_BUFSIZE).
   **/
  public PipedInputStream(PipedOutputStream pos, int bufSize) {
    this(bufSize);
    pos.connect(this);
  }

  /**
   * Used by PipedOutputStream to put a byte into this stream.
   * @param b the byte to put.;
   **/
  synchronized void put(int b) throws IOException {
    if (closed) throw new IOException("PipedInputStream Closed");
    while (space == 0) {
      if (closed) throw new IOException("PipedInputStream Closed");
      try {
        notify();               // Get the read thread going if necessary
        wait();
      }
      catch (InterruptedException ie) {
      }
    }
    buffer[in++] = (byte) (b & 0xff);
    if (in == buffer.length) in = 0;
    space--;
  }

  /**
   * Put bytes into this stream. Used by PipedOutputStream.
   * @param b an array of bytes to be inserted
   * @param off the offset in the array of the first byte to put
   * @param len the number of bytes to put
   **/
  synchronized void put(byte[] b, int off, int len) throws IOException {
    while (len > 0) {
      if (closed) throw new IOException("PipedInputStream Closed");
      while (space == 0) {
        if (closed) throw new IOException("PipedInputStream Closed");
        try {
          notify();             // Get the read thread going if necessary
          wait();
        }
        catch (InterruptedException ie) {
        }
      }
      int tail = buffer.length - in;
      int nb = Math.min(len, Math.min(tail, space));
      System.arraycopy(b, off, buffer, in, nb);
      in += nb;
      if (in == buffer.length) in = 0;
      off += nb;
      space -= nb;
      len -= nb;
    }
  }

  /**
   * Insure that everything that has been put into the stream is
   * available. Wakes the reader if the buffer is not empty.
   **/
  synchronized void flush() throws IOException {
    if (closed) throw new IOException("PipedInputStream Closed");
    if (buffer.length > space) notify();
  }

  synchronized void setEOF() throws IOException {
    if (closed) throw new IOException("PipedInputStream Closed");
    flush();
    eof = true;
  }

  /**
   * Read a single byte. Waits for the byte to be available if necessary.
   * @return the next byte in the buffer as an int.
   **/
  @Override
public synchronized int read() throws IOException {
    if (closed) throw new IOException("Closed");
    while (space == buffer.length) {
      try {
        if (eof) return -1;
        wait();
      }
      catch (InterruptedException ie) {
      }
    }
    int result = buffer[out++] & 0xff;
    if (out == buffer.length) out = 0;
    space++;
    if (space == buffer.length) notify();
    return result;
  }

  /**
   * Read an array full of bytes. Always reads at least one byte. May
   * return with the array not completely full.
   * @param b the byte array to fill.
   * @return the number of bytes read or -1 if eof has been reached.
   **/
  @Override
public synchronized int read(byte[] b) throws IOException {
    return read(b, 0, b.length);
  }

  /**
   * Read some bytes into an array. Allows only a portion of an array to be filled.
   * @param b the array to fill
   * @param off the offset into the array of the first byte to be read
   * @param len the maximum number of bytes to read.
   * @return the number of bytes read. If eof has been reached, -1 is
   * returned. if len is zero, then zero is returned.
   **/
  @Override
public synchronized int read(byte[] b, int off, int len) throws IOException {
    if (closed) throw new IOException("Closed");
    int result = 0;
    while (len > 0) {
      while (buffer.length == space) {
        if (eof) {
          if (result == 0) return -1;
          return result;
        }
        try {
          wait();
        }
        catch (InterruptedException ie) {
        }
      }
      int tail = buffer.length - out;
      int avail = buffer.length - space;
      int nb = Math.min(len, Math.min(tail, avail));
      System.arraycopy(buffer, out, b, off, nb);
      out += nb;
      if (out == buffer.length) {
        out = 0;
      }
      space += nb;
      notify();
      off += nb;
      len -= nb;
      result += nb;
      if (space == buffer.length) break;
    }
    return result;
  }
}
