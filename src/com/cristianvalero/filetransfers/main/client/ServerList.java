package com.cristianvalero.filetransfers.main.client;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ServerList
{
    private String serverName;
    private String ip;
    private int port;

    public ServerList(String data)
    {
        String[] d = data.split(":");
        serverName = d[0];
        ip = d[1];
        port = Integer.parseInt(d[2]);
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static ServerList getServerList(String serverName)
    {
        ServerList sv = null;
        for (ServerList server : getAllServers(Client.CONFG))
        {
            if (server.getServerName().toLowerCase().equals(serverName.toLowerCase()))
            {
                sv =  server;
                break;
            }
        }
        return sv;
    }

    public static ArrayList<ServerList> getAllServers(String configPath)
    {
        ArrayList<ServerList> list = new ArrayList<>();
        try
        {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(configPath));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray serverList = (JSONArray) jsonObject.get("servers");
            Iterator<String> iterator = serverList.iterator();

            while (iterator.hasNext())
            {
                ServerList sv = new ServerList(iterator.next());
                list.add(sv);
            }
        }
        catch (IOException e) { e.printStackTrace(); }
        catch (ParseException e) { e.printStackTrace(); }

        return list;
    }

    public static void saveNewServer(String name, String adress, String port)
    {
        JSONObject obj = new JSONObject();
        JSONArray serverList = new JSONArray();
        serverList.add(name+":"+adress+":"+port);
        obj.put("servers", serverList);

        try
        {
            FileWriter file = new FileWriter(Client.CONFG);
            file.write(obj.toJSONString());
            file.flush();

            System.out.println("New server saved correctly.");
        }
        catch (IOException e) {  e.printStackTrace(); }
    }
}
