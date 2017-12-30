package com.cristianvalero.filetransfers.main.client;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public enum Actions
{
    UPLOADING ("upldng"),
    DOWNLOADING ("dwnldng"),
    NONE ("nne");

    private String action;
    private Actions now;

    Actions(String act)
    {
        action = act;
    }

    public String getAction()
    {
        return action;
    }

    public Actions getActionDoing()
    {
        return now;
    }

    public void sendCommandHelp()
    {
        System.out.println("-------------------------------------------");
        System.out.println("------------------ [HELP] -----------------");
        System.out.println("-------------------------------------------");
        System.out.println("HELP       -To show this information again.");
        System.out.println("UPLOAD     -To upload file to the server.");
        System.out.println("DOWNLOAD   -To download file from the server.");
        System.out.println("CLOSE      -For exit from this program.");
        System.out.println("-------------------------------------------");

    }

    public void uploadFile(DataInputStream dis, DataOutputStream dos) throws IOException
    {
        now = Actions.UPLOADING;

        FileExplorer explorer = new FileExplorer("Select your file to upload: ");
        final FileUtil file = explorer.open();
        byte[] fileBytes = Files.readAllBytes(new File(file.getPath()).toPath());
        file.setFileBytes(fileBytes);
        file.setSize(fileBytes.length);
        fileBytes = null;

        Scanner teclado = new Scanner(System.in);
        System.out.print("Are you sure want to upload the file: "+file.getFileName()+"? [y/n]: ");
        final String typed = teclado.nextLine().toLowerCase();
        teclado.close();
        if (typed.equals("y"))
        {
            dos.writeInt(file.getSize());
            dos.flush();
            dos.writeUTF(file.getFileName());
            dos.flush();

            for (byte b : file.getFile())
            {
                dos.writeByte(b);
                dos.flush();
                System.out.println(dis.readUTF());
            }
        }

        now = Actions.NONE;
    }

    class FileExplorer extends JFrame
    {
        private String title;

        public FileExplorer(String title)
        {
            this.title = title;
            this.setTitle(title);
        }

        public FileUtil open()
        {
            FileUtil f = null;

            JFileChooser fc = new JFileChooser();
            fc.setMultiSelectionEnabled(true);
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int seleccion = fc.showOpenDialog(new JPanel());

            if (seleccion == JFileChooser.APPROVE_OPTION)
            {
                File fichero = fc.getSelectedFile();
                if (fichero != null)
                {
                    f = new FileUtil(null, 0, fichero.getName(), fichero.getPath());
                }
            }

            return f;
        }
    }

    class FileUtil
    {
        private byte[] file;
        private int size;
        private String fileName;
        private String path;

        public FileUtil(byte[] f, int s, String n, String p)
        {
            this.file = f;
            this.size = s;
            this.fileName = n;
        }

        public byte[] getFile() {
            return file;
        }

        public int getSize() {
            return size;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileBytes(byte[] bs)
        {
            this.file = bs;
        }

        public void setSize(int sze)
        {
            this.size = sze;
        }

        public String getPath()
        {
            return path;
        }
    }
}
