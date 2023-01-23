package com.isansys.patientgateway;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

class Ascii85Coder
{
    public static byte[] decodeAscii85StringToBytes(String ascii85)
    {
        ArrayList<Byte> list = new ArrayList<>();
        ByteArrayInputStream in_byte = new ByteArrayInputStream(ascii85.getBytes(StandardCharsets.US_ASCII));
        Ascii85InputStream in_ascii = new Ascii85InputStream(in_byte);
        try
        {
            int r;
            while ((r = in_ascii.read()) != -1)
            {
                list.add((byte) r);
            }
            in_ascii.close();               // MK added to remove resource leak
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        byte[] bytes = new byte[list.size()];
        int bytes_length = bytes.length;
        for (int i = 0; i < bytes_length; i++)
        {
            bytes[i] = list.get(i);
        }
        return bytes;
    }

/*
    public static String encodeBytesToAscii85(byte[] bytes)
    {
        ByteArrayOutputStream out_byte = new ByteArrayOutputStream();
        Ascii85OutputStream out_ascii = new Ascii85OutputStream(out_byte);

        try
        {
            out_ascii.write(bytes);
            out_ascii.flush();
            out_ascii.close();      // MK added to remove resource leak
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        String res = "";
        try
        {
            res = out_byte.toString("ascii");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return res;
    }
*/
}
