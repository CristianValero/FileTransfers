package com.cristianvalero.filetransfers.main.server;

import com.cristianvalero.filetransfers.main.Utils.Colors;
import com.cristianvalero.filetransfers.main.Utils.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PClient implements Runnable
{
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Actions actions;

    private Thread actualThread = null;
    private String address;

    public PClient(Socket socket)
    {
        this.socket = socket;
        this.address = socket.getInetAddress().getHostAddress();
    }

    @Override
    public void run()
    {
        try
        {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            actions = new Actions(this);

            dos.writeUTF("acptd"); //Confirm the correct connection to the client
            dos.flush();

            while (socket.isConnected())
            {
                if (actions.getAction() == Actions.ActionDoing.NONE) //If is not working in any task.
                {
                    final String toDo = dis.readUTF().toLowerCase();
                    Utils.logMessage(address+" executed '"+Colors.BLUE+toDo+Colors.RESET+"' command.");

                    switch (toDo)
                    {
                        case "upload":
                            actions.saveFile(actions.getFileUploaded());
                            break;
                        case "download":
                            actions.sendFile();
                            break;
                        case "close":
                            close_all();
                            break;
                        case "help":
                            actions.sendInfo();
                            break;
                        case "list":
                            actions.sendListOfFilesAvaliable();
                            break;
                        default:
                            dos.writeUTF(Colors.RED+"The command '"+toDo+"' not exists."+Colors.RESET);
                                break;
                    }
                }
            }

            close_all();
        }
        catch (IOException e)
        {
            Utils.logMessage("An error has ocurred when try to execute any action.");
            e.printStackTrace();
        }
    }

    private void close_all() throws IOException
    {
        Server.removeClient(this);
        dos.close();
        dis.close();
        socket.close();
        actualThread.interrupt();
    }

    public DataInputStream getDis() {
        return dis;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public Socket getSocket()
    {
        return socket;
    }

    public void setActualThread(Thread a) { this.actualThread = a; }
}
