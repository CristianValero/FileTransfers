package com.cristianvalero.filetransfers.server;

import com.cristianvalero.filetransfers.Utils.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Actions
{
    private PClient client;
    private DataOutputStream dos;
    private DataInputStream dis;

    private ActionDoing actionDoing = ActionDoing.NONE;

    public Actions(PClient c)
    {
        this.client = c;
        dos = client.getDos();
        dis = client.getDis();
    }

    public ActionDoing getAction()
    {
        return actionDoing;
    }

    public void sendListOfFilesAvaliable() throws IOException
    {
        final File folder = new File(new File(".").getCanonicalPath()+"\\uploads");
        for (final File entry : folder.listFiles())
        {
            if (!entry.isDirectory())
            {
                dos.writeUTF(entry.getName());
                dos.flush();
            }
        }
    }

    public void saveFile(FileUtil file) throws IOException
    {
        StringBuilder folder = new StringBuilder();
        folder.append(new File(".").getCanonicalPath());
        folder.append("\\uploads\\");
        folder.append(file.getFileName());

        Files.write(new File(folder.toString()).toPath(), file.getFile());
    }

    public FileUtil getFileUploaded() throws IOException
    {
        actionDoing = ActionDoing.UPLOADING;

        final int size = dis.readInt(); //Size of file
        final String fileName = dis.readUTF(); //Get file name
        byte[] file = new byte[size];

        Utils.logMessage(client.getSocket().getInetAddress().getHostAddress()+" is uploading a file: "+fileName);

        int latestIndex = -1;
        for (int i=0; i<size; i++)
        {
            final int percnt = (100*i) / file.length;

            file[i] = dis.readByte();

            if (percnt != latestIndex)
            {
                Utils.logMessage(client.getSocket().getInetAddress().getHostAddress()+" -> "+fileName+": "+percnt);
                latestIndex = percnt;
            }
        }

        actionDoing = ActionDoing.NONE;

        return new FileUtil(file, size, fileName);
    }

    public void sendFile(File f) throws IOException
    {
        actionDoing = ActionDoing.DOWNLOADING;

        byte[] file = Files.readAllBytes(f.toPath());

        dos.writeInt(file.length); //Send size of the file
        dos.flush();

        for (int i=0; i<file.length; i++)
        {
            dos.writeByte(file[i]);
            dos.flush();
        }

        actionDoing = ActionDoing.NONE;
    }

    enum ActionDoing
    {
        DOWNLOADING ("dwnldng"),
        UPLOADING ("upldng"),
        NONE ("NNE");

        private String action;

        ActionDoing(String a)
        {
            this.action = a;
        }
    }

    class FileUtil
    {
        private byte[] file;
        private int size;
        private String fileName;

        public FileUtil(byte[] f, int s, String n)
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
    }
}
