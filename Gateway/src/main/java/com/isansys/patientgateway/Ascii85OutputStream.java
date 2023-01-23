/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.isansys.patientgateway;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class represents an ASCII85 output stream.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 *
 */
class Ascii85OutputStream extends FilterOutputStream
{
    private int lineBreak;
    private int count;

    private byte[] in_data;
    private byte[] out_data;

    /**
     * Function produces five ASCII printing characters from
     * four bytes of binary data.
     */
    private int max_line;
    private boolean flushed;
    private char terminator;
    private static final char OFFSET = '!';
    private static final char NEWLINE = '\n';
    private static final char Z = 'z';

    /**
     * Constructor.
     *
     * @param out The output stream to write to.
     */
    public Ascii85OutputStream(OutputStream out)
    {
        super(out);
        lineBreak = 36 * 2;
        max_line = 36 * 2;
        count = 0;
        in_data = new byte[4];
        out_data = new byte[5];
        flushed = true;
        terminator = '~';
    }

    /**
     * This will set the terminating character.
     *
     * @param term The terminating character.
     */
    public void setTerminator(char term)
    {
        if (term < 118 || term > 126 || term == Z)
        {
            throw new IllegalArgumentException("Terminator must be 118-126 excluding z");
        }
        terminator = term;
    }

    /**
     * This will get the terminating character.
     *
     * @return The terminating character.
     */
    public char getTerminator()
    {
        return terminator;
    }

    /**
     * This will set the line length that will be used.
     *
     * @param l The length of the line to use.
     */
    public void setLineLength(int l)
    {
        if (lineBreak > l)
        {
            lineBreak = l;
        }
        max_line = l;
    }

    /**
     * This will get the length of the line.
     *
     * @return The line length attribute.
     */
    public int getLineLength()
    {
        return max_line;
    }

    /**
     * This will transform the next four ascii bytes.
     */
    private void transformASCII85()
    {
        long word = ((((in_data[0] << 8) | (in_data[1] & 0xFF)) << 16) | ((in_data[2] & 0xFF) << 8) | (in_data[3] & 0xFF)) & 0xFFFFFFFFL;

        if (word == 0)
        {
            out_data[0] = (byte) Z;
            out_data[1] = 0;
            return;
        }
        long x;
        x = word / (85L * 85L * 85L * 85L);
        out_data[0] = (byte) (x + OFFSET);
        word -= x * 85L * 85L * 85L * 85L;

        x = word / (85L * 85L * 85L);
        out_data[1] = (byte) (x + OFFSET);
        word -= x * 85L * 85L * 85L;

        x = word / (85L * 85L);
        out_data[2] = (byte) (x + OFFSET);
        word -= x * 85L * 85L;

        x = word / 85L;
        out_data[3] = (byte) (x + OFFSET);

        out_data[4] = (byte) ((word % 85L) + OFFSET);
    }

    /**
     * This will write a single byte.
     *
     * @param b The byte to write.
     *
     * @throws IOException If there is an error writing to the stream.
     */
    public final void write(int b) throws IOException
    {
        flushed = false;
        in_data[count++] = (byte) b;
        if (count < 4)
        {
            return;
        }
        transformASCII85();
        for (int i = 0; i < 5; i++)
        {
            if (out_data[i] == 0)
            {
                break;
            }
            out.write(out_data[i]);
            if (--lineBreak == 0)
            {
                out.write(NEWLINE);
                lineBreak = max_line;
            }
        }
        count = 0;
    }

    /**
     * This will flush the data to the stream.
     *
     * @throws IOException If there is an error writing the data to the stream.
     */
    public final void flush() throws IOException
    {
        if (flushed)
        {
            return;
        }
        if (count > 0)
        {
            for (int i = count; i < 4; i++)
            {
                in_data[i] = 0;
            }
            transformASCII85();
            if (out_data[0] == Z)
            {
                for (int i = 0; i < 5; i++) // expand 'z',
                {
                    out_data[i] = (byte) OFFSET;
                }
            }
            for (int i = 0; i < count + 1; i++)
            {
                out.write(out_data[i]);
                if (--lineBreak == 0)
                {
                    out.write(NEWLINE);
                    lineBreak = max_line;
                }
            }
        }
        if (--lineBreak == 0)
        {
            out.write(NEWLINE);
        }
        out.write(terminator);
        out.write(NEWLINE);
        count = 0;
        lineBreak = max_line;
        flushed = true;
        super.flush();
    }

    /**
     * This will close the stream.
     *
     * @throws IOException If there is an error closing the wrapped stream.
     */
    public void close() throws IOException
    {
        try
        {
            flush();
            super.close();
        }
        finally
        {
            in_data = out_data = null;
        }
    }
}
