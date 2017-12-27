package com.cristianvalero.filetransfers.main.Utils;

import com.sun.istack.internal.NotNull;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Calendar;

public class Utils
{
    public static String encrypt(String text)
    {
        char[] a = new char[text.length()];
        int contador = 0;
        for (int i=text.length()-1; i>=0; i--)
        {
            a[contador] = text.charAt(i);
            contador++;
        }

        String cad = "";
        for (char ch : a)
            cad += ch;

        return DigestUtils.sha1Hex(DigestUtils.md5Hex(cad));
    }

    public static void logMessage(@NotNull final String message)
    {
        Calendar cal = Calendar.getInstance();
        System.out.println("["+cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND)+"] "+message);
    }

    public static String reverse(String txt)
    {
        int cont = 0;
        char[] rev = new char[txt.length()];
        for (int i=txt.length()-1; i>=0; i--)
        {
            rev[cont] = txt.charAt(i);
            cont++;
        }

        txt = "";
        for (char c : rev)
            txt += c;

        return txt;
    }
}
