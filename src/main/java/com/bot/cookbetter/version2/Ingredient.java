package com.bot.cookbetter.version2;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.util.SerializationUtils.serialize;

/**
 * @author snaraya7 Shrikanth N C
 */
public class Ingredient {

    private String receivedName;
    private String name;
    private  boolean exists;

    public static void main(String[] arg){

        System.out.println(new Ingredient("egg, eggs1", false));
    }

    public Ingredient(String name){

        this(name, true);
    }

    public Ingredient(String name, boolean existing) {

        if (existing){

            this.receivedName = name;
            this.name = this.receivedName;
            this.exists = true;

        }else{
            this.receivedName = name;
            this.name = matchWithExisting(name);
            this.exists = this.name != null;
        }

        System.out.println("Constructor : " );
        System.out.println(this.receivedName);
        System.out.println(this.name);
        System.out.println(this.exists);

    }

    private  static int computeScore(String ingredient1, String ingredient2){

          ingredient1 = ingredient1.replaceAll("[^A-Za-z]+", "");
        ingredient2 = ingredient2.replaceAll("[^A-Za-z]+", "");

        ingredient1 = ingredient1.toLowerCase().trim();
        ingredient2 = ingredient2.toLowerCase().trim();



        return EditDistance.computeScore(ingredient1,ingredient2);
    }


    private static Set<String> dbIngredients = new HashSet<>();
    static{
        dbIngredients = Util.getAllIngredientNames();
    }

    private static HashMap<String,String> ingredientCache = new HashMap<>();

    static {

        ingredientCache = desiralize();
    }

    private static HashMap<String,String> desiralize() {

        HashMap<String,String> cache = new HashMap<>();

        try {
            FileInputStream fileIn = new FileInputStream("ingrcache.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            cache = (HashMap<String,String>) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }

        return  cache;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        serialize();
    }

    private static void serialize() {

        try {
            FileOutputStream fileOut =
                    new FileOutputStream("ingrcache.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(ingredientCache);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    private static String matchWithExisting(String receivedIngredient){

        String matchingIngredient = ingredientCache.get(receivedIngredient);

        if (matchingIngredient == null){

            ingredientCache.put(receivedIngredient, find(receivedIngredient));
        }

        return ingredientCache.get(receivedIngredient);
    }

    private static String find(String receivedIngredient){

        String result = null;

        int currentScore = 0;

        if (!Util.isNullString(receivedIngredient)){

            for (String availableIngredient : dbIngredients){

               int computedScore = computeScore(availableIngredient, receivedIngredient);


                if (   computedScore   > currentScore ){
                    currentScore = computedScore;
                    result = availableIngredient;
                }

            }



        }
        System.out.println("method result = " + result);
        return  result;
    }



    public String getName() {
        return name;
    }

    public boolean isExisits() {

        return this.exists;
    }

    public void setExisits(boolean exisits) {
        this.exists = exisits;
    }

    @Override
    public int hashCode() {

        return this.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof  Ingredient){
            return ((Ingredient) obj).getName().equalsIgnoreCase(this.getName());
        }

        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "name='" + name + '\'' +
                ", exisits=" + exists +
                '}';
    }
}