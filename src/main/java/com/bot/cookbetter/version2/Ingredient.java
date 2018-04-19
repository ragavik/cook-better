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

    public Ingredient(String name) {
        this.receivedName = name;
        this.name = matchWithExisting(name);
        this.exists = this.name != null;
    }

    private  static int computeScore(String ingredient1, String ingredient2){

          ingredient1 = ingredient1.replaceAll("[^A-Za-z]+", "");
        ingredient2 = ingredient2.replaceAll("[^A-Za-z]+", "");

        ingredient1 = ingredient1.toLowerCase().trim();
        ingredient2 = ingredient2.toLowerCase().trim();



        return EditDistance.computeScore(ingredient1,ingredient2);
    }



    private static String matchWithExisting(String receivedIngredient){

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
    public String toString() {
        return "Ingredient{" +
                "name='" + name + '\'' +
                ", exisits=" + exists +
                '}';
    }
}