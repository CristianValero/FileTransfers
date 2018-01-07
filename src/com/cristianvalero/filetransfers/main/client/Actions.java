package com.cristianvalero.filetransfers.main.client;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

public enum Actions
{
    UPLOADING ("upldng"),
    DOWNLOADING ("dwnldng"),
    NONE ("nne");

    private String action;

    Actions(String act)
    {
        action = act;
    }

    public String getAction()
    {
        return action;
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

    public void showAvaliableDownloads(DataInputStream dis) throws IOException
    {
        final int avaliables = dis.readInt();
        for (int i=0; i<avaliables; i++)
        {
            System.out.println(dis.readUTF());
        }
    }

    public void downloadFile(DataInputStream dis, DataOutputStream dos, Scanner teclado) throws IOException
    {
        action = Actions.DOWNLOADING.toString();

        System.out.println("There are this files avaliables to download: ");
        dos.writeUTF("list");
        dos.flush();
        final int totalFiles = dis.readInt();

        ArrayList<String> fileNames = new ArrayList<>();
        for (int i=0; i<totalFiles; i++)
        {
            final String fileName = dis.readUTF();
            fileNames.add(fileName);
            System.out.println(fileName);
        }

        dos.writeUTF("download");
        dos.flush();

        String fileToDownload;
        boolean exists;
        do
        {
            System.out.print("Write the name of the file (case sensitive): ");
            fileToDownload = teclado.nextLine();
            exists = containsString(fileNames, fileToDownload);
        } while (!exists);

        dos.writeUTF(fileToDownload);
        dos.flush();

        final int fileSize = dis.readInt();
        byte[] file = new byte[fileSize];
        int latestIndex = -1;

        System.out.println("The file will be download into your desktop.");

        for (int i=0; i<fileSize; i++)
        {
            file[i] = dis.readByte();
            final int percnt = (100*i) / file.length;

            if (percnt != latestIndex)
            {
                System.out.println(" -> "+fileToDownload+": "+percnt+"% downloaded.");
                latestIndex = percnt;
            }
        }

        Files.write(new File("C://users//"+System.getProperty("user.name")+"//Desktop//"+fileToDownload).toPath(), file);

        System.out.println("The file '"+fileToDownload+" has been downloaded.");

        action = Actions.NONE.toString();
    }

    private boolean containsString(ArrayList<String> a, String find)
    {
        boolean exists = false;
        for (String i : a)
            if (i.equals(find))
                exists = true;
        return exists;
    }

    public void uploadFile(DataInputStream dis, DataOutputStream dos, Scanner teclado) throws IOException
    {
        action = Actions.UPLOADING.toString();

        FileExplorer explorer = new FileExplorer();
        explorer.setVisible(true);
        explorer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final FileUtil file = explorer.open();
        explorer.setVisible(false);
        explorer.dispatchEvent(new WindowEvent(explorer, WindowEvent.WINDOW_CLOSING));
        byte[] fileBytes = Files.readAllBytes(new File(file.getPath()).toPath());
        file.setFileBytes(fileBytes);
        file.setSize(fileBytes.length);

        System.out.print("Are you sure want to upload the file: "+file.getFileName()+"? [y/n]: ");
        final String typed = teclado.nextLine().toLowerCase();

        dos.writeUTF("upload");
        dos.flush();

        if (typed.equals("y"))
        {
            dos.writeInt(file.getSize());
            dos.flush();
            dos.writeUTF(file.getFileName());
            dos.flush();

            int latestIndex = -1;
            byte[] b = file.getFile();
            for (int i=0; i<b.length; i++)
            {
                final int percnt = (100*i) / b.length;
                dos.writeByte(b[i]);
                dos.flush();
                System.out.println(dis.readUTF());

                if (percnt != latestIndex)
                {
                    System.out.println(" -> "+file.getFileName()+": "+percnt+"% uploaded.");
                    latestIndex = percnt;
                }
            }

            System.out.println("The file '"+file.getFileName()+" has been uploaded.");
        }

        action = Actions.NONE.toString();
    }

    @Override
    public String toString()
    {
        return action;
    }

    class FileExplorer extends JFrame
    {
        public FileExplorer() { }

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
            this.path = p;
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
