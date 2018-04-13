package com.bot.cookbetter.version2;

import java.sql.ResultSet;
import java.util.Set;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.util.HashSet;

/**
 * @author snaraya7
 * Shrikanth N C
 */
public class Util {


    public static void main(String[] args) {
        Recipe rec;
        Set<Ingredient> ing = getAllIngredients();
        Set<Ingredient> ing1 = getIngredients(2);
        rec = getRecipe(2);

        //Get Recipe ID
        System.out.println(rec.getID());
        //Get Recipe Name
        System.out.println(rec.getName());
        //Get Recipe Ingredients
        for (Ingredient in : rec.getIngredients()) {
            System.out.print(in.getName());
            System.out.print("\t");
        }
        System.out.println("\n******************************************************\n");
        for (Ingredient in1 : ing1){
            System.out.println(in1.getName());
        }
        System.out.println("\n******************************************************\n");
        for (Ingredient in1 : ing){
            System.out.println(in1.getName());
        }
    }

    //@kashyap
    public static String extractIngredients(String naturalQuery){

        String ingredients[] = naturalQuery.split(" ");

        StringBuffer ingredientsBuffer = new StringBuffer( );

        Set<Ingredient> validIngredients = getAllIngredients();

        for(Ingredient vIngredient : validIngredients){

            for (String userIngredient : ingredients){

                if (userIngredient.equalsIgnoreCase(vIngredient.getName())){
                    ingredientsBuffer.append(vIngredient.getName()+",");
                }

            }

        }

        // return csv : apple,milk,orange
        return ingredientsBuffer.toString().substring(0, ingredientsBuffer.toString().length() - 1);
    }

    public static Set<Ingredient> getAllIngredients(){

        Set<Ingredient> ingredients = new HashSet<>();
        for(String ingredientName : getAllIngredientNames()){

            ingredients.add(new Ingredient(ingredientName));
        }

        return  ingredients;
    }


    //@kashyap
    public  static Set<Ingredient> constructIngredients(String csvIngredients){

        Set<Ingredient> ingredients = new HashSet<Ingredient>();

        for(String csvIngredient : csvIngredients.split(",")){

            ingredients.add(new Ingredient(csvIngredient.trim()));
        }

        return ingredients;
    }




    //@charan
    public static Recipe getRecipe(int recipeID) {

        try {
            // Database connection
            Class.forName("com.mysql.jdbc.Driver");
            String connectionUrl = "jdbc:mysql://mydbinstance.ckzbitlijtbu.us-west-2.rds.amazonaws.com:3306/cookbetter?useUnicode=true&characterEncoding=UTF-8&user=cookbetter&password=cookbetter";
            Connection conn = DriverManager.getConnection(connectionUrl);
            //System.out.println("Conn established");
            String query = "select * from data where id = '"+recipeID+"';";

            ResultSet rs = conn.prepareStatement(query).executeQuery();

            //String result = "";
            Recipe recipe = new Recipe();
            while(rs.next()) {
                //System.out.println("id:" + rs.getInt(1));
                int ID = rs.getInt(1);
                //System.out.println("title:" + rs.getString(2));
                String name = rs.getString(2);
                recipe.setID(ID);
                recipe.setName(name);

                ResultSetMetaData rsMetaData = rs.getMetaData();
                int numberOfColumns = rsMetaData.getColumnCount();

                Set<Ingredient> ingredients = new HashSet<Ingredient>();
                for (int idx = 14; idx < numberOfColumns + 1; idx++) {
                    // Get the name of the column's name
                    if (rs.getInt(idx) == 1)
                        ingredients.add(new Ingredient(rsMetaData.getColumnName(idx)));
                }
                recipe.setIngredients(ingredients);

                //result+="<";
                //String link = "https://www.epicurious.com/search/";
                //String modTitle = name.replaceAll(" ", "%20");
                //link+=modTitle+"%20";
                //result+= link + "|"+name+"> \n";
            }
            return  recipe;
        }
        catch(Exception e) {
            //System.err.println(e);
            e.printStackTrace();
        }
        return null;
    }

    //@karthik
    public static Set<Ingredient> getIngredients(int recipeID){
        Recipe Rec = getRecipe(recipeID);
        return Rec.getIngredients();
    }


    private static Set<String> validIngredients = getAllIngredientNames();


    public static Set<String> getValidIngredients() {
        return validIngredients;
    }

    //@karthik
    public static Set<String> getAllIngredientNames(){
        // Database connection
        ResultSet column_names = null;
        Set<String> ing = new HashSet<>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectionUrl = "jdbc:mysql://mydbinstance.ckzbitlijtbu.us-west-2.rds.amazonaws.com:3306/cookbetter?useUnicode=true&characterEncoding=UTF-8&user=cookbetter&password=cookbetter";
            Connection conn = DriverManager.getConnection(connectionUrl);
            String query = "select column_name from information_schema.columns where table_name = 'data'";
            column_names = conn.prepareStatement(query).executeQuery();

            while(column_names.next()){

                ing.add(column_names.getString(1));

            }


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }

        return ing;
        
    }
}
