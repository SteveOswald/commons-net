/*
 * Copyright 2001-2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.net;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/***
 * The FingerClient class implements the client side of the Internet Finger
 * Protocol defined in RFC 1288.  To finger a host you create a
 * FingerClient instance, connect to the host, query the host, and finally
 * disconnect from the host.  If the finger service you want to query is on
 * a non-standard port, connect to the host at that port.
 * Here's a sample use:
 * <pre>
 *    FingerClient finger;
 *
 *    finger = new FingerClient();
 *
 *    try {
 *      finger.connect("foo.bar.com");
 *      System.out.println(finger.query("foobar", false));
 *      finger.disconnect();
 *    } catch(IOException e) {
 *      System.err.println("Error I/O exception: " + e.getMessage());
 *      return;
 *    }
 * </pre>
 * <p>
 * <p>
 * @author Daniel F. Savarese
 ***/

public class FingerClient extends SocketClient
{
    /***
     * The default FINGER port.  Set to 79 according to RFC 1288.
     ***/
    public static final int DEFAULT_PORT = 79;

    private static final String __LONG_FLAG = "/W ";

    private transient StringBuffer __query = new StringBuffer(64);
    private transient char[] __buffer = new char[1024];

    /***
     * The default FingerClient constructor.  Initializes the
     * default port to <code> DEFAULT_PORT </code>.
     ***/
    public FingerClient()
    {
        setDefaultPort(DEFAULT_PORT);
    }


    /***
     * Fingers a user at the connected host and returns the output
     * as a String.  You must first connect to a finger server before
     * calling this method, and you should disconnect afterward.
     * <p>
     * @param longOutput Set to true if long output is requested, false if not.
     * @param username  The name of the user to finger.
     * @return The result of the finger query.
     * @exception IOException If an I/O error occurs while reading the socket.
     ***/
    public String query(boolean longOutput, String username) throws IOException
    {
        int read;
        StringBuffer result = new StringBuffer(__buffer.length);
        BufferedReader input;

        input =
            new BufferedReader(new InputStreamReader(getInputStream(longOutput,
                               username)));

        while (true)
        {
            read = input.read(__buffer, 0, __buffer.length);
            if (read <= 0)
                break;
            result.append(__buffer, 0, read);
        }

        input.close();

        return result.toString();
    }


    /***
     * Fingers the connected host and returns the output
     * as a String.  You must first connect to a finger server before
     * calling this method, and you should disconnect afterward.
     * This is equivalent to calling <code> query(longOutput, "") </code>.
     * <p>
     * @param longOutput Set to true if long output is requested, false if not.
     * @return The result of the finger query.
     * @exception IOException If an I/O error occurs while reading the socket.
     ***/
    public String query(boolean longOutput) throws IOException
    {
        return query(longOutput, "");
    }


    /***
     * Fingers a user and returns the input stream from the network connection
     * of the finger query.  You must first connect to a finger server before
     * calling this method, and you should disconnect after finishing reading
     * the stream.
     * <p>
     * @param longOutput Set to true if long output is requested, false if not.
     * @param username  The name of the user to finger.
     * @return The InputStream of the network connection of the finger query.
     *         Can be read to obtain finger results.
     * @exception IOException If an I/O error during the operation.
     ***/
    public InputStream getInputStream(boolean longOutput, String username)
    throws IOException
    {
        DataOutputStream output;

        __query.setLength(0);
        if (longOutput)
            __query.append(__LONG_FLAG);
        __query.append(username);
        __query.append(SocketClient.NETASCII_EOL);

        output =
          new DataOutputStream(new BufferedOutputStream(_output_, 1024));
        output.writeBytes(__query.toString());
        output.flush();

        return _input_;
    }


    /***
     * Fingers the connected host and returns the input stream from
     * the network connection of the finger query.  This is equivalent to
     * calling getInputStream(longOutput, "").  You must first connect to a
     * finger server before calling this method, and you should disconnect
     * after finishing reading the stream.
     * <p>
     * @param longOutput Set to true if long output is requested, false if not.
     * @return The InputStream of the network connection of the finger query.
     *         Can be read to obtain finger results.
     * @exception IOException If an I/O error during the operation.
     ***/
    public InputStream getInputStream(boolean longOutput) throws IOException
    {
        return getInputStream(longOutput, "");
    }

}
