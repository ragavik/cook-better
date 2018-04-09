package com.bot.cookbetter.version2;

import javax.swing.text.html.HTMLDocument;
import java.sql.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author snaraya7
 * Shrikanth N C
 */
public class Util {

    //@kashyap
    public static String extractIngredients(String naturalQuery){

        // return csv : apple,milk,orange
        return null;
    }

    //@kashyap
    public  static Set<Ingredient> constructIngredients(String csvIngredients){
        return null;
    }


    //@kashyap
    public static boolean ingredientExist(String ingredient){

        //return true if 'ingredient' is a valid ingredient in database columnn.
        // Handle plurality!

        return false;
    }

    //@charan
    public static Recipe getRecipe(int recipeID) {
        return  null;
    }

    //@karthik
    public static Set<Ingredient> getIngredients(int recipeID){
        Recipe Rec = getRecipe(recipeID);
        return Rec.getIngredients();
    }

    //@karthik
    public static Set<Ingredient> getAllIngredients(){
        // Database connection
        ResultSet column_names = null;
        Set<Ingredient> ing = new HashSet<>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectionUrl = "jdbc:mysql://mydbinstance.ckzbitlijtbu.us-west-2.rds.amazonaws.com:3306/cookbetter?useUnicode=true&characterEncoding=UTF-8&user=cookbetter&password=cookbetter";
            Connection conn = DriverManager.getConnection(connectionUrl);
            String query = "select column_name from information_schema.columns where table_name = 'data'";
            column_names = conn.prepareStatement(query).executeQuery();

            int size = 14;
            while(column_names.next()){
                size++;
            }

            for(int num = 14; num<size+1; num++){
                String ing_name = column_names.getString(num);
                Ingredient i = new Ingredient(ing_name);
                ing.add(i);
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        return ing;
    }
}
