package com.cristianvalero.filetransfers.main.server;

import com.cristianvalero.filetransfers.main.Utils.Colors;
import com.cristianvalero.filetransfers.main.Utils.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Actions
{
    private PClient client;
    private DataOutputStream dos;
    private DataInputStream dis;
    private String address;

    private ActionDoing actionDoing = ActionDoing.NONE;

    public Actions(PClient c)
    {
        this.client = c;
        dos = client.getDos();
        dis = client.getDis();

        address = client.getSocket().getInetAddress().getHostAddress();
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
            else
            {
                dos.writeUTF("COOMING SOON -> "+entry.getName()+"\\");
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

        final String mb = bytesToMegabyte(file.getSize());

        Server.getDatabase().saveNewUpload(address, file.getFileName(), bytesToMegabyte(file.getSize()), folder.toString());

        Utils.logMessage(Colors.GREEN+"A new file has been uploaded and saved into uploads folder: "+file.getFileName()+" ("+mb+").");
    }

    public static String bytesToMegabyte(int bytes)
    {
        DecimalFormat decimalFormat = new DecimalFormat("#.000");
        float megabytes = (float) (bytes / Math.pow(2, 20));
        //decimalFormat.setRoundingMode(RoundingMode.CEILING);
        return decimalFormat.format(megabytes);
    }

    public void sendInfo() throws IOException
    {
        ArrayList<String> help = new ArrayList<>();
        help.add("DOWNLOAD <filename>     You can download the typed filename if exists.");
        help.add("UPLOAD <filename>       Select a file in the explorer for upload to the server.");
        help.add("LIST                    Get a list of all avaliable items to download.");
        help.add("HELP                    Get this information.");
        help.add("CLOSE                   Close the connection to the server and this program.");

        for (String h : help)
        {
            dos.writeUTF(h);
            dos.flush();
        }

        help.clear();
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
            dos.writeUTF(fileName+": "+percnt+"%");
            dos.flush();

            if (percnt != latestIndex)
            {
                Utils.logMessage(client.getSocket().getInetAddress().getHostAddress()+" -> "+fileName+": "+percnt);
                latestIndex = percnt;
            }
        }

        actionDoing = ActionDoing.NONE;

        return new FileUtil(file, size, fileName);
    }

    public void sendFile() throws IOException
    {
        actionDoing = ActionDoing.DOWNLOADING;

        String programPath = new File(".").getCanonicalPath()+"\\uploads\\";

        final String fileName = dis.readUTF(); //Get the name of the file
        programPath += fileName;

        byte[] file = Files.readAllBytes(new File(programPath).toPath());

        dos.writeInt(file.length); //Send size of the file
        dos.flush();

        for (int i=0; i<file.length; i++) //Send byte per byte of the file
        {
            dos.writeByte(file[i]);
            dos.flush();
        }

        Server.getDatabase().saveNewDownload(address, fileName, bytesToMegabyte(file.length), programPath);

        actionDoing = ActionDoing.NONE;
    }

    enum ActionDoing
    {
        DOWNLOADING ("dwnldng"),
        UPLOADING ("upldng"),
        NONE ("none");

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
