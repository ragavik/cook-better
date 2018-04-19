package com.bot.cookbetter.version2;

import com.mysql.jdbc.PreparedStatement;
import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class JSONLoader {

    public static void main(String[] args) {

        JSONParser parser = new JSONParser();
        JSONArray RecipeDB = null;

        int updated=0;

        try {
            String connectionUrl = "jdbc:mysql://cookbetter.ci2drxnp952j.us-east-1.rds.amazonaws.com:3306/cookbetter?useUnicode=true&characterEncoding=UTF-8&user=cookbetter&password=cookbetter";
            //For testing in Localhost
            //String DB_URL = "jdbc:mysql://localhost/";
            //  Database credentials
            //String USER = "root";
            //String PASS = "root";
            Connection conn = DriverManager.getConnection(connectionUrl);
            Class.forName("com.mysql.jdbc.Driver");

            Statement BatchQuery = conn.createStatement();
            String Query = null;
            //PreparedStatement BatchQuery = null;
            RecipeDB = (JSONArray) parser.parse(new FileReader("C:\\Users\\charan\\Documents\\NCSU CourseWork\\SEM II\\Software Engineering (CSC 510)\\CookBetter\\full_format_recipes.json"));

            //System.out.println("Conn established");
            //String query = "select title from data;";
            String query = "select * from data;";
            ResultSet rs = conn.prepareStatement(query).executeQuery();


            //java.sql.PreparedStatement BatchQuery = null;
            while (rs.next()) {

                String rname = rs.getString("title");
                //System.out.println("\nTitle retreived from LocalDB: "+rname);
                if (rs.getString("directions") == null || rs.getString("ingredients") == null) {
                    for (Object rec : RecipeDB) {
                        JSONObject recipe = (JSONObject) rec;
                        String title = (String) recipe.get("title");
                        System.out.println("rname DB: " + rname);
                        System.out.println("Title JSON: " + title);
                        if (rname.trim().equals(title.trim())) {
                            //System.out.println("\nTitle retreived from LocalDB: " + rname);
                            JSONArray directions = (JSONArray) recipe.get("directions");
                            String inst = "";
                            for (Object steps : directions) {
                                inst = inst + steps.toString() + "\n";
                            }
                            inst = inst.replace("\'","\\'");
                            System.out.println("Changed String: " + inst);
                            JSONArray ingredients = (JSONArray) recipe.get("ingredients");
                            String ingr = "";
                            for (Object items : ingredients) {
                                ingr = ingr + items.toString() + "\n";
                            }
                            ingr = ingr.replace("\'","\\'");
                            Query = "update data set directions = '"+inst+"', ingredients = '"+ingr+"' where title = '"+rname+"';";
                            //conn.prepareStatement(Query).addBatch();
                            //Query = "update data set directions = ? where title = ?;";
                            //BatchQuery = conn.prepareStatement(Query);
                            //System.out.println("The Query is: " + Query);
                            //BatchQuery.setString(1, inst);
                            //System.out.println("The Query is: " + Query);
                            //BatchQuery.setString(2, rname);
                            BatchQuery.addBatch(Query);
                            updated = 1;
                            System.out.println("The Query is: " + Query);
                            break;
                        }
                    }
                }
                /*  JSONArray directions = (JSONArray) recipe.get("directions");
                    System.out.println("\nDirections:");
                    String inst = "";
                    for (Object steps : directions) {
                        inst = inst+steps.toString()+"\n";
                        //String inst = steps.toString();
                        //inst = inst.replaceAll("[^0-9]\\. ",". \n");
                        //System.out.println(steps);
                    }
                    System.out.println(inst);
                    JSONArray ingredients = (JSONArray) recipe.get("ingredients");
                    System.out.println("\nIngredients");
                    for (Object items : ingredients) {
                        System.out.println(items);
                    }*/

            }
            //conn.prepareStatement(Query).executeBatch();
            if (updated == 1)
                BatchQuery.executeBatch();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
