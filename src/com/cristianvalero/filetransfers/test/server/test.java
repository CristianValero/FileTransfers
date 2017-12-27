package com.cristianvalero.filetransfers.test.server;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class test
{
    public static void main(String args[]) throws IOException, InterruptedException
    {
        /**String a = "255";
        byte[] bytesEncoded = Base64.encodeBase64(a.getBytes());
        String encoded = new String(bytesEncoded);
        System.out.println("Ecncoded value is " + encoded);

        byte[] valueDecoded= Base64.decodeBase64(bytesEncoded);
        String b = new String(valueDecoded);
        System.out.println("Decoded value is " + b);

        for (byte c : encoded.getBytes())
            System.out.println(c);*/

        /**byte a[] = new byte[5];
        for (int i=0; i<a.length; i++)
            a[i] = 127;

        int latest = -1;
        for (int i=0; i<a.length+1; i++)
        {
            int percent = (100*i) / a.length;

            if (latest != percent)
            {
                System.out.println(i+": "+percent+"%");
                latest = percent;
            }
            //Thread.sleep(1*1000);
        }*/

        /*Buscador frame = new Buscador();
        frame.setVisible(true);*/
    }

    static class Buscador extends JFrame
    {
        public Buscador()
        {
            JFileChooser fc = new JFileChooser();
            fc.setMultiSelectionEnabled(true);
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int seleccion = fc.showOpenDialog(new JPanel());

            if (seleccion == JFileChooser.APPROVE_OPTION)
            {
                File[] ficheros = fc.getSelectedFiles();

                for(File f : ficheros)
                {
                    if (f.isDirectory())
                        System.out.println("Carpeta: "+f.getPath());
                    else if (f.isFile())
                        System.out.println("Fichero: "+f.getPath());
                }
                System.exit(1);
            }
        }
    }
}
