package com.cristianvalero.filetransfers.main.server;

import com.cristianvalero.filetransfers.main.Utils.Colors;
import com.cristianvalero.filetransfers.main.Utils.Utils;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class Server extends Thread
{
    private static ArrayList<PClient> pClients = new ArrayList<>();

    private static final int SERVER_PORT = 12678;
    private static ServerSocket serverSocket = null;
    private static Database database;

    @Override
    public void run()
    {
        try
        {
            checkProgram();
            serverSocket = new ServerSocket(SERVER_PORT);
            Utils.logMessage(Colors.GREEN+"Server socket running correctly on port "+SERVER_PORT+"!!"+Colors.RESET);

            Socket cliente;

            while (!serverSocket.isClosed())
            {
                Utils.logMessage("Waiting connection...");
                cliente = serverSocket.accept();

                final String adress = cliente.getInetAddress().getHostAddress();
                Utils.logMessage("Accepted connection from: "+Colors.YELLOW+adress+Colors.RESET);
                database.registerNewConnection(adress);

                PClient pClient = new PClient(cliente);
                Thread thread = new Thread(pClient);
                thread.start();
                pClient.setActualThread(thread);
                pClients.add(pClient);

                delay(1); //Wait 1 second before new connection.
            }

            database.disconect();
        }
        catch (IOException e)
        {
            Utils.logMessage(Colors.RED+"An error has ocurred trying to start server socket."+Colors.RESET);
            e.printStackTrace();
        }
    }

    public static void main(String args[])
    {
        showCopiright();
        delay(5);

        database = new Database(); //Start MySQL database

        Server server = new Server();
        server.run(); //Start ServerSocket server.
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

    private static void checkProgram() throws IOException
    {
        final String path = new File(".").getCanonicalPath();
        File downlF = new File(path+"\\uploads");
        if (!downlF.exists())
            downlF.mkdir();
    }

    private static void delay(int secs)
    {
        secs = secs*1000;
        try { sleep(secs); }
        catch (InterruptedException e)
        {
            Utils.logMessage(Colors.RED+"An error has ocurred trying to sleep server socket thread."+Colors.RESET);
            e.printStackTrace();
        }
    }

    public static void removeClient(PClient c)
    {
        pClients.remove(c);
    }

    public static Database getDatabase() //For use database class methods in other classes.
    {
        return database;
    }

    static class Database
    {
        private String host;
        private String name;
        private String user;
        private String password;
        private String driver;
        private String url;

        private Connection connection = null;
        private String[] tables;

        public Database()
        {
            tables = new String[]
                    {
                            "CREATE TABLE IF NOT EXISTS users_log ( id INT PRIMARY KEY AUTO_INCREMENT, adress VARCHAR(60), day VARCHAR(50), hour VARCHAR(50) ) Engine=InnoDB;",
                            "CREATE TABLE IF NOT EXISTS uploads_log ( id INT PRIMARY KEY AUTO_INCREMENT, adress VARCHAR(60), file VARCHAR(150), size_mb float, file_path BLOB(65535), day VARCHAR(50), hour VARCHAR(50) ) Engine=InnoDB;",
                            "CREATE TABLE IF NOT EXISTS downloads_log ( id INT PRIMARY KEY AUTO_INCREMENT, adress VARCHAR(60), file VARCHAR(150), size_mb float, file_path BLOB(65535), day VARCHAR(50), hour VARCHAR(50) ) Engine=InnoDB;",
                    };

            host = "192.168.1.42";
            name = "filetransfer";
            user = "filetransfer";
            password = "filetransfer";
            driver = "com.mysql.jdbc.Driver";
            url = "jdbc:mysql://"+host+"/"+name+"?autoReconect=true";

            connection = upConnection();
        }

        private Connection upConnection()
        {
            Connection con = null;
            try
            {
                Class.forName(driver);
                con = DriverManager.getConnection(url, user, password);
                Utils.logMessage(Colors.GREEN+"MySQL: MySQL connection is up and ready to use!!"+Colors.RESET);
                checkTables(con); //Create tables if not exists
            }
            catch (ClassNotFoundException e)
            {
                Utils.logMessage(Colors.RED+"MySQL: An error has ocurred when try to up MySQL driver."+Colors.RESET);
                e.printStackTrace();
            }
            catch (SQLException e)
            {
                Utils.logMessage(Colors.RED+"MySQL: An error has ocurred when try to up MySQL connection."+Colors.RESET);
                e.printStackTrace();
            }
            return con;
        }

        private void checkTables(Connection conx) throws SQLException
        {
            if (!conx.isClosed())
            {
                for (String table : tables)
                {
                    conx.prepareStatement(table).execute();
                    Utils.logMessage("MySQL: Table (" + table.substring(table.indexOf('S') + 4, table.indexOf('(') - 1) + ") comprobated.");
                }
            }
        }

        public void disconect()
        {
            try {
                connection.close();
                Utils.logMessage(Colors.GREEN+"MySQL: Connection has been closed correctly."+Colors.RESET);
            } catch (SQLException e) {
                Utils.logMessage(Colors.RED+"MySQL: An error has ocurred when try to close MySQL connection."+Colors.RESET);
                e.printStackTrace();
            }
            connection = null;
        }

        public void registerNewConnection(String adress)
        {
            Calendar cal = Calendar.getInstance();

            StringBuilder day = new StringBuilder();
            day.append(cal.get(Calendar.DAY_OF_MONTH));
            day.append("/");
            day.append(cal.get(Calendar.MONTH));
            day.append("/");
            day.append(cal.get(Calendar.YEAR));

            StringBuilder hour = new StringBuilder();
            hour.append(cal.get(Calendar.HOUR));
            hour.append(":");
            hour.append(cal.get(Calendar.MINUTE));
            hour.append(":");
            hour.append(cal.get(Calendar.SECOND));

            StringBuilder query = new StringBuilder();
            query.append("INSERT INTO users_log ( adress, day, hour ) VALUES ( '");
            query.append(adress);
            query.append("', '");
            query.append(day);
            query.append("', '");
            query.append(hour);
            query.append("' );");

            try {
                connection.prepareStatement(query.toString()).execute();
                Utils.logMessage("MySQL: A new connection has been saved into database.");
            } catch (SQLException e) {
                Utils.logMessage(Colors.RED+"MySQL: An error has ocurred when try to store data."+Colors.RESET);
                e.printStackTrace();
            }
        }

        public void saveNewDownload(String adress, String fileName, String sizemb, String path)
        {
            Calendar cal = Calendar.getInstance();

            StringBuilder day = new StringBuilder();
            day.append(cal.get(Calendar.DAY_OF_MONTH));
            day.append("/");
            day.append(cal.get(Calendar.MONTH));
            day.append("/");
            day.append(cal.get(Calendar.YEAR));

            StringBuilder hour = new StringBuilder();
            hour.append(cal.get(Calendar.HOUR));
            hour.append(":");
            hour.append(cal.get(Calendar.MINUTE));
            hour.append(":");
            hour.append(cal.get(Calendar.SECOND));

            StringBuilder query = new StringBuilder();
            query.append("INSERT INTO downloads_log ( adress, file, size_mb, file_path, day, hour ) VALUES ( '");
            query.append(adress);
            query.append("', '");
            query.append(fileName);
            query.append("', '");
            query.append(sizemb);
            query.append("', '");
            query.append(path);
            query.append("', '");
            query.append(day);
            query.append("', '");
            query.append(hour);
            query.append("' );");

            try {
                connection.prepareStatement(query.toString()).execute();
                Utils.logMessage("MySQL: A new download has been saved into database.");
            } catch (SQLException e) {
                Utils.logMessage(Colors.RED+"MySQL: An error has ocurred when try to store data."+Colors.RESET);
                e.printStackTrace();
            }
        }

        public void saveNewUpload(String adress, String fileName, String sizemb, String path)
        {
            Calendar cal = Calendar.getInstance();

            StringBuilder day = new StringBuilder();
            day.append(cal.get(Calendar.DAY_OF_MONTH));
            day.append("/");
            day.append(cal.get(Calendar.MONTH));
            day.append("/");
            day.append(cal.get(Calendar.YEAR));

            StringBuilder hour = new StringBuilder();
            hour.append(cal.get(Calendar.HOUR));
            hour.append(":");
            hour.append(cal.get(Calendar.MINUTE));
            hour.append(":");
            hour.append(cal.get(Calendar.SECOND));

            StringBuilder query = new StringBuilder();
            query.append("INSERT INTO uploads_log ( adress, file, size_mb, file_path, day, hour ) VALUES ( '");
            query.append(adress);
            query.append("', '");
            query.append(fileName);
            query.append("', '");
            query.append(sizemb);
            query.append("', '");
            query.append(path);
            query.append("', '");
            query.append(day);
            query.append("', '");
            query.append(hour);
            query.append("' );");

            try {
                connection.prepareStatement(query.toString()).execute();
                Utils.logMessage("MySQL: A new upload has been saved into database.");
            } catch (SQLException e) {
                Utils.logMessage(Colors.RED+"MySQL: An error has ocurred when try to store data."+Colors.RESET);
                e.printStackTrace();
            }
        }
    }
}
