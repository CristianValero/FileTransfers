package com.cristianvalero.filetransfers.test.client;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class test
{
    public static void main(String[] args)
    {



        /**
         * To read JSON file
         *
         */

    }

    private static void writeJSON()
    {
        /**
         * To write JSON file
         */
        JSONObject obj = new JSONObject();
        obj.put("name", "mkyong.com");
        obj.put("age", new Integer(100));

        JSONArray list = new JSONArray();
        list.add("msg 1");
        list.add("msg 2");
        list.add("msg 3");

        obj.put("messages", list);

        try (FileWriter file = new FileWriter("D://test.json"))
        {
            file.write(obj.toJSONString());
            file.flush();

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.print(obj);
    }

    private static void readJSON()
    {
        JSONParser parser = new JSONParser();

        try
        {

            Object obj = parser.parse(new FileReader("D://test.json"));

            JSONObject jsonObject = (JSONObject) obj;
            System.out.println(jsonObject);

            String name = (String) jsonObject.get("name");
            System.out.println(name);

            long age = (Long) jsonObject.get("age");
            System.out.println(age);

            // loop array
            JSONArray msg = (JSONArray) jsonObject.get("messages");
            Iterator<String> iterator = msg.iterator();
            while (iterator.hasNext())
            {
                System.out.println(iterator.next());
            }

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }
}
