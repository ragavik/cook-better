package com.bot.cookbetter.version2;

import java.util.Set;

/**
 * @author snaraya7 Shrikanth N C
 */
public class Util {



    //@kashyap
    public static String extractIngredients(String naturalQuery){
            String lower = naturalQuery.toLowerCase();
            String array1[]= lower.split(" ");
            for (String temp: array1){
                System.out.println(temp);
            }
            for (String temp: array1 )
               // Find a match from table of ingredients
                // select * from ingredients where value = temp;
                //if a match is found store it in a string to return.
            }
            // return csv : apple,milk,orange
            return null;
        }

        // return csv : apple,milk,orange
        return null;
    }

    //@kashyap
    public  static Set<Ingredient> constructIngredients(String csvIngredients){
        return null;
    }


    //@charan
    public static Recipe getRecipe(int recipeID) {
        return  null;
    }

    //@karthik
    public static Set<Ingredient> getIngredients(int recipeID){
        return null;
    }

    //@karthik
    public static Set<Ingredient> getAllIngredients(){
        return  null;
    }
}
