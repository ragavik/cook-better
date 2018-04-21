package com.bot.cookbetter.version2;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import java.util.Set;

/**
 * @author snaraya7 Shrikanth N C
 */
public class Ingredient {

    private String receivedName;
    private String name;
    private  boolean exists;

    public static void main(String[] arg){

        System.out.println(new Ingredient("egg", true));

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



    private static String matchWithExisting(String receivedIngredient){
        System.out.println("receivedIngredient = " + receivedIngredient);

        String result = null;

        int currentScore = 0;

        if (!Util.isNullString(receivedIngredient)){

            for (String availableIngredient : Util.getAllIngredientNames()){

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