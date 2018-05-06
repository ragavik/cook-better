package com.bot.cookbetter.handler;

import com.bot.cookbetter.model.Recipe;
import com.bot.cookbetter.utils.IngredReportUtil;
import com.bot.cookbetter.utils.IngredientNetwork;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class IngredientReportHandler {

    private IngredReportUtil ingredReportUtil;
    private IngredientNetwork network;
    private Set<Recipe> recommendedRecipe;

    public JSONObject buildReport(String ingreds) {
        ingredReportUtil = new IngredReportUtil();
        network = new IngredientNetwork(ingreds);
        JSONObject result = new JSONObject();

        try {
            if (!network.getIngredPMI().isEmpty()) {

                ingredReportUtil.buildCombinations(network);

                ingreds = ingredReportUtil.removeCommonIngred(network.getCommonIngred(), ingreds, network.getNoSuchIngredient());
                listNonIngredientString(network);
                String exclude = excludeIngredients(ingredReportUtil, ingreds);
                Set<String> bestIngreds = bestCombinations(ingredReportUtil);
                getPredicatedRating(network, bestIngreds);
                result.put("text", buildString(network, exclude, bestIngreds));

            } else {
                result.put("text", "please provide the appropriate input");
            }
        } catch (Exception e) {
            result.put("text", "please provide the appropriate input");
        }
        return result;
    }

    private Set<String> listNonIngredientString(IngredientNetwork network) {
        return network.getNoSuchIngredient();
    }

    private String excludeIngredients(IngredReportUtil ingredReportUtil, String ingreds) {
        return ingredReportUtil.excludeIngredient(ingreds);
    }

    private Set<String> bestCombinations(IngredReportUtil ingredReportUtil) {
        return ingredReportUtil.findRecommendedIngred();
    }

    private double getPredicatedRating(IngredientNetwork network, Set<String> bestIngred) {
        this.recommendedRecipe = getRecommendedRecipe(network, bestIngred);
        double rating = 0;
        int count = 0;
        if(this.recommendedRecipe != null && this.recommendedRecipe.size() > 0){
            for (Recipe recipe: this.recommendedRecipe){
                rating = rating + recipe.getRating();
                count++;
            }
            rating = rating/count;
        }
        return rating;
    }

    private Set<Recipe> getRecommendedRecipe(IngredientNetwork network, Set<String> bestIngred) {
        Set<Recipe> recommendedRecipe = new HashSet<>();
        Set<Recipe> filteredRecipe = network.getFilteredRecipe();
        for (Recipe recipe : filteredRecipe) {
            Set<String> tempBestIngred = new HashSet<>(bestIngred);
            for (String st : recipe.getIngredients()) {
                for (String ingred : bestIngred) {
                    if (st.contains(ingred)) {
                        tempBestIngred.remove(ingred);
                    }
                }
            }
            if (tempBestIngred.isEmpty()) {
                recommendedRecipe.add(recipe);
            }
        }
        return recommendedRecipe;
    }

    private String buildString(IngredientNetwork network, String exclude, Set<String> bestIngreds) {

        String finalOutput = "";

        String tempOutlier = buildOuput(network.getNoSuchIngredient());
        if (tempOutlier.isEmpty()) {
            tempOutlier = "Input accepted. Generating report .... \n" + "Going through more than 20000 options.... this might take a few seconds (5-10s)...  ";
        } else {
            tempOutlier = "Generating report ...." +
                    "Going through more than 20000 options.... this might take a few seconds (5-10s)...\n-- " + "Following ingredient does not exist in our data: " + tempOutlier;
        }
        String bestIngred = "We need to add more ingredients, Since all inout ingredients contradicts each other in terms of taste.";
        if (bestIngreds != null && !bestIngreds.isEmpty()) {
            bestIngred = "Optimum compatible ingredient found by algorithms are: " + buildOuput(bestIngreds) + ".";
        }

        String predictedRating = "";
        if (getPredicatedRating(network, bestIngreds) !=0){
            predictedRating = String.valueOf(getPredicatedRating(network, bestIngreds));
            predictedRating =  "Predicted rating of the recipe cooked using the combination of suggested ingredients is " + predictedRating;
        }

        if (network.getCommonIngred() != null && !network.getCommonIngred().isEmpty()) {
            String commonIngred = " -- Our algorithm doesn't consider " + buildOuput(network.getCommonIngred()) + " ingredients as these are used in" +
                    " most of the recipes.";
            finalOutput = tempOutlier + "\n" + commonIngred + "\n-- " + exclude + "\n-- " + bestIngred + "\n-- " + predictedRating;

        } else {
            finalOutput = tempOutlier + "\n-- " + exclude + "\n-- " + bestIngred + "\n-- " + predictedRating;
        }
        return finalOutput;
    }

    private String buildOuput(Set<String> set) {
        String temp = "";
        if (set != null && !set.isEmpty()) {
            for (String best : set) {
                temp = temp + best + ", ";
            }
            int index = temp.lastIndexOf(",");
            temp = temp.substring(0, index);
            if (temp.contains(",")) {
                index = temp.lastIndexOf(",");
                temp = temp.substring(0, index) + ", and" + temp.substring(index + 1, temp.length());
            }
        }
        return temp;
    }
}
