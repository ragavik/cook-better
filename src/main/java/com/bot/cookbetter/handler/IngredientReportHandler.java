package com.bot.cookbetter.handler;

import com.bot.cookbetter.model.Recipe;
import com.bot.cookbetter.utils.IngredReportUtil;
import com.bot.cookbetter.utils.IngredientNetwork;

import java.util.HashSet;
import java.util.Set;

public class IngredientReportHandler {

    private IngredReportUtil ingredReportUtil;
    private IngredientNetwork network;

    public void buildReport(String ingreds){
        ingredReportUtil = new IngredReportUtil();
        network = new IngredientNetwork(ingreds);

        ingredReportUtil.buildCombinations(network);

        ingreds = ingredReportUtil.removeCommonIngred(network.getCommonIngred(), ingreds, network.getNoSuchIngredient());
        listNonIngredientString(network);
        excludeIngredients(ingredReportUtil, ingreds);
        Set<String> bestIngreds = bestCombinations(ingredReportUtil);
        getPredicatedRating(network, bestIngreds);
        getRecommendedRecipe(network, bestIngreds);

    }

    private Set<String> listNonIngredientString(IngredientNetwork network){
        return network.getNoSuchIngredient();
    }

    private String excludeIngredients(IngredReportUtil ingredReportUtil, String ingreds){
        return ingredReportUtil.excludeIngredient(ingreds);
    }

    private Set<String> bestCombinations(IngredReportUtil ingredReportUtil){
        return ingredReportUtil.findRecommendedIngred();
    }

    private double getPredicatedRating(IngredientNetwork network, Set<String> bestIngred){

        return 0;
    }

    private Set<Recipe> getRecommendedRecipe(IngredientNetwork network, Set<String> bestIngred){
        Set<Recipe> recommendedRecipe = new HashSet<>();
        Set<Recipe> filteredRecipe = network.getFilteredRecipe();
        for (Recipe recipe: filteredRecipe){
            Set<String> tempBestIngred =  new HashSet<>(bestIngred);
            for (String st: recipe.getIngredients()){
                for (String ingred: bestIngred){
                    if (st.contains(ingred)){
                        tempBestIngred.remove(ingred);
                    }
                }
            }
            if (tempBestIngred.isEmpty()){
                recommendedRecipe.add(recipe);
            }
        }
        return recommendedRecipe;
    }
}
