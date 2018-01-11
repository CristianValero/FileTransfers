package com.cristianvalero.filetransfers.main.client;

import com.cristianvalero.filetransfers.main.Utils.Colors;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread
{
    private int PORT = 5555;
    private String SERVER = "127.0.0.1";
    public static String CONFG = "";

    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket socket;
    private Actions actions;

    private Scanner teclado;

    public static void main(String args[]) throws IOException
    {
        showCopiright();
        delay(5);

        final String path = new File(".").getCanonicalPath() + "\\cnfg\\config.json";
        CONFG = path;

        File cnfg = new File(CONFG);
        if (!cnfg.exists())
            cnfg.mkdirs();

        Client client = new Client();
        client.run();
    }

    @Override
    public void run()
    {
        teclado = new Scanner(System.in);
        setConnection();

        try
        {
            System.out.println("Connecting...");
            socket = new Socket(SERVER, PORT);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            final String confirmation = dis.readUTF();
            if (confirmation.equals("acptd"))
            {
                System.out.println("Connected!!");

                //delay(1);

                actions = Actions.NONE;

                while (socket.isConnected())
                {
                    if (actions.getAction().equals(Actions.NONE.toString()))
                    {
                        final String act = askToDo();

                        switch (act)
                        {
                            case "help":
                                actions.sendCommandHelp();
                                break;
                            case "list":
                                dos.writeUTF("list");
                                dos.flush();
                                actions.showAvaliableDownloads(dis);
                                break;
                            case "upload":
                                actions.uploadFile(dis, dos, teclado);
                                break;
                            case "download":
                                actions.downloadFile(dis, dos, teclado);
                                break;
                            case "close":
                                closeConnection("You closed this program.");
                                break;
                        }
                    }
                }

                teclado.close();
            }
            else
                closeConnection("Your connecton have been refused by the server. Check your proxy, or contact with the System Administrator.");
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    private String askToDo()
    {
        System.out.print("What would you like to do? [help]: ");
        String a = teclado.nextLine().toLowerCase();
        return a;
    }

    private void closeConnection(final String closeMsg) throws IOException
    {
        dos.writeUTF("close");
        dos.flush();

        socket.close();
        dis.close();
        dos.close();
        System.out.println(closeMsg);
        this.interrupt();
        System.exit(-1);
    }

    private void setConnection() //Type of bookmarks "servers":["casa:1.1.1.1:3306", "trabajo:1.1.1.1:7809"]
    {
        System.out.print("New server [N] or Connect previous server [P]: ");
        final String typed = teclado.nextLine().toLowerCase();

        if (typed.equals("n"))
            noHaveServers(teclado);
        else if (typed.equals("p"))
        {
            if (ServerList.getAllServers(CONFG).size() == 0)
                noHaveServers(teclado);
            else
                haveServers(teclado);
        }
        else
        {
            System.out.println("Sorry, I can't understand you.");
            setConnection();
        }
    }

    private void noHaveServers(Scanner teclado)
    {
        String name, ip, prt;

        System.out.print("Type the name of the server: ");
        name = teclado.nextLine();
        System.out.print("Type the address of the server: ");
        ip = teclado.nextLine();
        System.out.print("Type the port of the server: ");
        prt = teclado.nextLine();

        System.out.print("Do you want to save this new server? [y/n]: ");
        final String typed  = teclado.nextLine().toLowerCase();
        if (typed.equals("y"))
            ServerList.saveNewServer(name, ip, prt);
        else
        {
            SERVER = ip;
            PORT = Integer.parseInt(prt);
        }
    }

    private void haveServers(Scanner teclado)
    {
        for (ServerList sv : ServerList.getAllServers(CONFG))
        {
            System.out.println(" - Name: "+sv.getServerName()+" || Address: "+sv.getIp()+" || Port: "+sv.getPort());
        }

        System.out.println("");
        System.out.println("Type the name of the server: ");
        final String serverName = teclado.nextLine();
        ServerList typedServer = ServerList.getServerList(serverName);
        PORT = typedServer.getPort();
        SERVER = typedServer.getIp();
    }

    private static void showCopiright()
    {
        System.out.println(Colors.BLUE+" _____ _ _     _____                     __");
        System.out.println("|  ___(_) | __|_   _| __ __ _ _ __  ___ / _| ___ _ __ ___");
        System.out.println("| |_  | | |/ _ \\| || '__/ _` | '_ \\/ __| |_ / _ \\ '__/ __|");
        System.out.println("|  _| | | |  __/| || | | (_| | | | \\__ \\  _|  __/ |  \\__ \\");
        System.out.println("|_|   |_|_|\\___||_||_|  \\__,_|_| |_|___/_|  \\___|_|  |___/"+Colors.RESET);
        System.out.println("\n");

        System.out.println(Colors.YELLOW+"Copyright (c) 1999 Cristian Valero");
        System.out.println("All Rights Reserved");
        System.out.println("");
        System.out.println("This product is protected by copyright and distributed under");
        System.out.println("licenses restricting copying, distribution and decompilation."+Colors.RESET);
        System.out.println("");
        System.out.println(Colors.RED+"¡Alert! This program need to run be in a folder."+Colors.RESET);
        System.out.println("");
    }

    private static void delay(int secs)
    {
        try {
            sleep(secs*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
