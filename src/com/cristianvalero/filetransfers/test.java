package com.cristianvalero.filetransfers;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class test
{
    public static void main(String args[]) throws IOException, InterruptedException
    {
        Calendar cal = Calendar.getInstance();
        System.out.println(cal.get(Calendar.DAY_OF_MONTH));


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

        /**Buscador frame = new Buscador();
        frame.setVisible(true);*/
    }

    static class Buscador extends JFrame
    {
        public Buscador()
        {
            JFileChooser fc = new JFileChooser();
            fc.setMultiSelectionEnabled(true);
            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

            int seleccion = fc.showOpenDialog(new JPanel());

            if (seleccion == JFileChooser.APPROVE_OPTION)
            {
                File[] ficheros = fc.getSelectedFiles();

                for(int i=0; i<ficheros.length; i++)
                {
                    if (ficheros[i].isDirectory())
                        System.out.println("Carpeta: "+ficheros[i].getPath());
                    else if (ficheros[i].isFile())
                        System.out.println("Fichero: "+ficheros[i].getPath());
                }
                System.exit(1);
            }
        }
    }
}
