package com.cristianvalero.filetransfers.server;

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

    public PClient(Socket socket)
    {
        this.socket = socket;
        actions = new Actions(this);
    }

    @Override
    public void run()
    {
        try
        {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            dos.writeUTF("g"); //Confirm the correct connection to the client
            dos.flush();

            while (socket.isConnected())
            {
                if (actions.getAction() == Actions.ActionDoing.NONE) //If is not working in any task.
                {
                    final String toDo = dis.readUTF();

                    switch (toDo)
                    {
                        case "upload":
                            break;
                        case "download":
                            break;
                        case "init":
                            break;
                        case "close":
                            break;
                    }
                }
            }

            Server.removeClient(this);
            actualThread.interrupt();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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

    public void setActualThread(Thread a)
    {
        this.actualThread = a;
    }
}
