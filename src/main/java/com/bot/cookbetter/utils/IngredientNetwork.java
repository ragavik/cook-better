package com.bot.cookbetter.utils;

import com.bot.cookbetter.model.Recipe;

import javax.validation.constraints.Null;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class IngredientNetwork {

    private HashMap<String, Integer> ingredCount = null;
    private String[] inputIngredList = null;
    private HashMap<String, Float> ingredPMI = null;
    private Set<Recipe> filteredRecipe = new HashSet<>();
    private String result;
    private Set<String> incompatibleIngred = new HashSet<>();

    public IngredientNetwork(String ingreds) {
        this.inputIngredList = ingreds.replaceAll(" ","").split(",");
        this.ingredCount = countIngreds();
        this.ingredPMI = generatePMINet();
    }

    private HashMap<String, Integer> updateIngredientCount(HashMap<String, Integer> ingredCount1) {
        HashMap<String, Integer> ingredCount = new HashMap<>(ingredCount1);
        Iterator it = ingredCount.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            if(ingredCount.get(name) == 0){
                if (result == null){
                    result = "Following ingrediants not found: " + name;
                }else {
                    result = result + "," + name;
                }
                ingredCount1.remove(name);
                List<String> list = new ArrayList<String>(Arrays.asList(this.inputIngredList));
                list.remove(name);
                this.inputIngredList = list.toArray(new String[0]);
            }
        }
        return ingredCount1;
    }

    private HashMap<String, Integer> countIngreds(){
        List<Recipe> recipeList = new RecipeDataHandler().getRecipes();
        HashMap<String, Integer> ingredCount = new HashMap<>();
        if (this.inputIngredList != null || this.inputIngredList.length > 0) {
            for (String ingred : this.inputIngredList) {
                int count = 0;
                for (Recipe recipe : recipeList) {
                    if (recipe != null && recipe.getIngredients() != null && recipe.getIngredients().length > 0) {
                        for (String re_ingred : recipe.getIngredients()) {
                            if (re_ingred.toLowerCase().contains(ingred.toLowerCase())) {
                                count++;
                                break;
                            }
                        }
                    }
                }
                ingredCount.put(ingred, count);
            }
        }
        ingredCount = updateIngredientCount(ingredCount);
        ingredCount = mutualIngred(recipeList, ingredCount);
        return ingredCount;
    }

    private HashMap<String, Integer> mutualIngred(List<Recipe> recipeList, HashMap<String, Integer> ingredCount){
        int size = this.inputIngredList.length;
        if (this.inputIngredList != null || this.inputIngredList.length > 0) {
            for (int i = 0; i < size; i++) {
                for (int j = i+1; j < size; j++){
                    ingredCount = mutualIngredCount(recipeList,ingredCount,this.inputIngredList[i], this.inputIngredList[j]);
                }
            }
        }
        return ingredCount;
    }

    private HashMap<String, Integer> mutualIngredCount(List<Recipe> recipeList, HashMap<String, Integer> ingredCount, String ingred1, String ingred2) {
        int count = 0;
        for (Recipe recipe : recipeList) {
            boolean ingred1F = false;
            boolean ingred2F = false;
            if (recipe != null && recipe.getIngredients() != null && recipe.getIngredients().length > 0) {
                for (String re_ingred : recipe.getIngredients()) {
                    if (re_ingred.toLowerCase().contains(ingred1.toLowerCase())) {
                        ingred1F = true;
                    }else if (re_ingred.toLowerCase().contains(ingred2.toLowerCase())) {
                        ingred2F = true;
                    }
                    if(ingred1F && ingred2F){
                        count++;
                        if(recipe.getRating() > 4.0){
                            filteredRecipe.add(recipe);
                        }
                        break;
                    }
                }
            }
        }
        ingredCount.put(ingred1+","+ingred2, count);
        return ingredCount;
    }

    private HashMap<String, Float> generatePMINet(){
        HashMap<String, Float> pmiMap = new HashMap<>();
        for (String name : this.ingredCount.keySet()){
            float pmi;
            if(name.contains(",")){
                String[] ingreds = name.split(",");
                pmi = ((float)this.ingredCount.get(name)/(float) (this.ingredCount.get(ingreds[0]) * this.ingredCount.get(ingreds[1]))) * 10000;
                pmiMap.put(name,pmi);
            }
        }
        return pmiMap;
    }

    /**
     * This is a temporary method to Analyze the results
     */
    public void getAnalysis(){
        System.out.println(this.result);
        for(String st: this.ingredPMI.keySet()){
            float value = this.ingredPMI.get(st);
            if(value < 5.0) {
                System.out.println(st + "," + this.ingredPMI.get(st));
            }else{
                System.out.println(st + "," + this.ingredPMI.get(st) + "--------------");
            }
        }
    }


}
