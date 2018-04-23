package com.bot.cookbetter.utils;
import com.bot.cookbetter.model.Recipe;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserOptions {
    private String userID;
    private String ing1, ing2, ing3;
    private String recipeType;
    private String specialOccasion;
    private boolean quickMeal;

    final Logger logger = LoggerFactory.getLogger(RequestHandlerUtil.class);

    public UserOptions(String userID) {
        this.userID = userID;
    }

    public void setIngredient(int num, String value) {
        switch (num) {
            case 1:
                this.ing1 = value;
                break;
            case 2:
                this.ing2 = value;
                break;
            case 3:
                this.ing3 = value;
                break;
        }
    }

    public void setRecipeType(String value) {
        this.recipeType = value;
    }

    public void setQuickMeal(String value) {
        if ("yes".equals(value))
            this.quickMeal = true;
        else
            this.quickMeal = false;
    }

    public void setSpecialOccasion(String value) {
        this.specialOccasion = value;
    }

    private JSONObject getResult(Recipe recipe, String exclude, String inputIngString){
        ResponseConstructionUtil responseConstructionUtil = new ResponseConstructionUtil();
        JSONObject response = new JSONObject();
        JSONArray attachments = new JSONArray();
        JSONObject item = new JSONObject();
        JSONObject result = responseConstructionUtil.readJSONFile("/ynbutton.json");
        String show = "";

        if (exclude != ""){
            show += "Sorry, can't find any recipe match to *" + inputIngString + "*.\nBut if you exclude *" + exclude + "*, you can cook:\n";
        }
        show += "<https://www.epicurious.com/search/" + recipe.getTitle().replaceAll(" ", "%20") + "|" + recipe.getTitle() + ">\n";
        show += recipe.getDesc() +"\n\n";
        show += "Ingredients:\n" + recipe.getIngredientList() +"\n\n";
        show += "Rated: " + recipe.getRating() + ", Calories:" + recipe.getCalories() + ", Sodium: " + recipe.getSodium() + ", Fat" + recipe.getFat() + ", Protein: " + recipe.getProtein();
        result.put("text", show);
        attachments.put(result);
        attachments.put(item);
        response.put("attachments", attachments);
        return result;
    }

    private boolean checkAllergy(ArrayList<String> allergies, String[] ingredients){
        for (String allergy: allergies){
            for (String ingred: ingredients){
                if (ingred.toLowerCase().contains(allergy.toLowerCase()))
                    return true;
            }
        }
        return false;
    }

    public void startSearch(String response_url) throws Exception {

        // Error message when user does not select any ingredient
        if(ing1 == null && ing2 == null && ing3 == null) {
            JSONObject response = new JSONObject();
            JSONArray attachments = new JSONArray();
            JSONObject item = new JSONObject();
            item.put("color", "#FF0000");
            item.put("text", "Oops! Looks like you have not selected any ingredients. Please select at least 1 ingredient & try again!");
            attachments.put(item);
            response.put("attachments", attachments);
            RequestHandlerUtil.getInstance().sendSlackResponse(response_url, response);
            return;
        }

        int ingredcount = 0;
        String inputIngString = "";
        if (ing1 != null) {
            ingredcount++;
            inputIngString += ing1 + " ";
        }
        if (ing2 != null) {
            ingredcount++;
            inputIngString += ing2 + " ";
        }
        if (ing3 != null) {
            ingredcount++;
            inputIngString += ing3 + " ";
        }
        String excludeString = "";
        Boolean existTwoIng = false;
        Recipe twoIngRecipe = new Recipe();
        Boolean existOneIng = false;
        Recipe oneIngRecipe = new Recipe();


        Class.forName("com.mysql.jdbc.Driver");
        //String connectionUrl = "jdbc:mysql://mydbinstance.ckzbitlijtbu.us-west-2.rds.amazonaws.com:3306/cookbetter?useUnicode=true&characterEncoding=UTF-8&user=cookbetter&password=cookbetter";
        String connectionUrl = "jdbc:mysql://cookbetter.ci2drxnp952j.us-east-1.rds.amazonaws.com:3306/cookbetter?useUnicode=true&characterEncoding=UTF-8&user=cookbetter&password=cookbetter";
        Connection conn = DriverManager.getConnection(connectionUrl);

        List<Recipe> recipes = RecipeDataHandler.RECIPE_LIST;

        boolean firstConditionSet = false;

        String query = "select * from data where";
        if(ing1 != null) {
            firstConditionSet = true;
            query += " " + ing1 + " =1";
        }
        if(ing2 != null) {
            if(firstConditionSet) {
                query += " and " + ing2 + " =1";
            }
            else {
                firstConditionSet = true;
                query += " " + ing2 + " =1";
            }
        }
        if(ing3 != null) {
            if(firstConditionSet) {
                query += " and " + ing3 + " =1";
            }
            else {
                firstConditionSet = true;
                query += " " + ing3 + " =1";
            }
        }
        if(quickMeal)
            query+= " and col_22_minute_meals = 1";
        if(recipeType != null)
            query+= " and "+recipeType+" =1";
        if(specialOccasion != null)
            query += " and "+specialOccasion+" =1";

        String personalQuery = "select * from personalize where userid = '"+this.userID+"';";
        ResultSet ps = conn.prepareStatement(personalQuery).executeQuery();

        String allergy_1 = "", allergy_2 = "", allergy_3 = "";
        ArrayList<String> allergiesList = new ArrayList<>();
        Boolean vegan = false, gluten_free = false, alcohol_free = false, cholesterol = false, muscle = false, lose = false, gain = false;
        ArrayList<String> checkList = new ArrayList<String>();

        while(ps.next()){

            allergy_1 = ps.getString("allergy_1");
            if(!"-1".equals(allergy_1)) {
                query += " and " + allergy_1 + " !=1";
                allergiesList.add(allergy_1);
            }

            allergy_2 = ps.getString("allergy_2");
            if(!"-1".equals(allergy_2)) {
                query += " and " + allergy_2 + " !=1";
                allergiesList.add(allergy_2);
            }

            allergy_3 = ps.getString("allergy_3");
            if(!"-1".equals(allergy_3)) {
                query += " and " + allergy_3 + " !=1";
                allergiesList.add(allergy_3);
            }

            // Vegan
            String diet_res_1 = ps.getString("diet_res_1");
            if(!"-1".equals(diet_res_1)) {
                query += " and " + diet_res_1 + " =1";
                checkList.add("vegan");
            }

            // TODO: add vegetarian option

            // Gluten free
            String diet_res_2 = ps.getString("diet_res_2");
            if(!"-1".equals(diet_res_2)) {
                query += " and " + diet_res_2 + " =1";
                checkList.add("gluten-free");
            }

            //Alcohol-free
            String diet_res_3 = ps.getString("diet_res_3");
            if(!"-1".equals(diet_res_3)){
                query+= " and "+diet_res_3 +" =0";
                checkList.add("Non-Alcoholic");
            }
            //Cholestrol
            String dis_1 = ps.getString("dis_1");
            if(!"-1".equals(dis_1)) {
                query += " and " + dis_1 + " <= 20";
                checkList.add("Low Cholesterol");
            }

            //Diabetes
            Object dis_2 = ps.getString("dis_2");
            if(!"-1".equals(dis_2)) {
                query += " and " + dis_2 + " = 1";
                checkList.add("No Sugar");
            }

            //Weak Kidney - check for protein content
            Object dis_3 = ps.getString("dis_3");
            if(!"-1".equals(dis_3))
                query+= " and "+dis_3 +" <= 7";

            // Lose weight - foods less than 500 calories
            Object goal_lose_wt = ps.getString("goal_lose_wt");
            if(!"-1".equals(goal_lose_wt)) {
                query += " and " + goal_lose_wt + " <= 500";
                lose = true;
            }

            // Gain weight
            Object goal_gain_wt = ps.getString("goal_gain_wt");
            if(!"-1".equals(goal_gain_wt)){
                query+= " and "+goal_gain_wt +" >=  700";
                gain = true;
            }

            // Gain Muscle
            Object goal_gain_muscle = ps.getString("goal_gain_muscle");
            if(!"-1".equals(goal_gain_muscle)) {
                query += " and " + goal_gain_muscle + " >= 20";
                muscle = true;
            }

            break;
        }

        //TODO: our
        for (Recipe recipe : recipes) {


            boolean flag_1 = false;
            boolean flag_2 = false;
            boolean flag_3 = false;

            //other
            //Boolean vegan, gluten_free , alcohol_free , cholesterol , Diabetes;

            // muscle, lose, gain
            if (lose) {
                if (recipe.getCalories() > 500)
                    continue;
            } else if (gain) {
                if (recipe.getCalories() < 700)
                    continue;
            }
            if (muscle) {
                if (recipe.getProtein() < 20)
                    continue;
            }

            if (recipe.getIngredients() != null) {
                for (String ingreds : recipe.getIngredients()) {

                    if (ingreds != null) {
                        if (ing1 != null && ingreds.toLowerCase().contains(ing1.toLowerCase())) {
                            flag_1 = true;
                        } else if (ing2 != null && ingreds.toLowerCase().contains(ing2.toLowerCase())) {
                            flag_2 = true;
                        } else if (ing3 != null && ingreds.toLowerCase().contains(ing3.toLowerCase())) {
                            flag_3 = true;
                        }
                    }

                    //algorithm
                    if (ingredcount == 3 && flag_1 && flag_2 && flag_3) {
                        if (checkAllergy(allergiesList, recipe.getIngredients()))
                            break;
                        if (checkList.size() > 0) {
                            if (recipe.isInCategories(checkList))
                                break;
                        }
                        RequestHandlerUtil.getInstance().sendSlackResponse(response_url, getResult(recipe, "", inputIngString));
                        return;
                    }

                    if (ingredcount >= 2 && ((flag_1 && flag_2) || (flag_1 && flag_3) || (flag_2 && flag_3))) {
                        if (checkAllergy(allergiesList, recipe.getIngredients()))
                            break;
                        if (checkList.size() > 0) {
                            if (recipe.isInCategories(checkList))
                                break;
                        }
                        if (!existTwoIng && ingredcount == 3){
                            if (!flag_1)
                                excludeString = ing1;
                            else if (!flag_2)
                                excludeString = ing2;
                            else
                                excludeString = ing3;

                            existTwoIng = true;
                            twoIngRecipe = recipe;
                        }
                        else if (ingredcount == 2){
                            RequestHandlerUtil.getInstance().sendSlackResponse(response_url, getResult(recipe, "", inputIngString));
                            return;
                        }
                        //}
                    }

                    if(ingredcount >= 1 && (flag_1 || flag_2 || flag_3)){
                        if (checkAllergy(allergiesList, recipe.getIngredients()))
                            break;
                        if (checkList.size() > 0) {
                            if (recipe.isInCategories(checkList))
                                break;
                        }

                        if (!existOneIng && ingredcount > 1){
                            if (!flag_1)
                                excludeString = ing1 + " ";
                            else if (!flag_2)
                                excludeString += ing2 + " ";
                            else
                                excludeString += ing3 + " ";

                            existOneIng = true;
                            oneIngRecipe = recipe;
                        }
                        else if (ingredcount == 1){
                            RequestHandlerUtil.getInstance().sendSlackResponse(response_url, getResult(recipe, "", inputIngString));
                            return;
                        }
                    }

                }
            }
        }

        if(existTwoIng){
            RequestHandlerUtil.getInstance().sendSlackResponse(response_url, getResult(twoIngRecipe, excludeString, inputIngString));
            return;
        }
        else if(existOneIng){
            RequestHandlerUtil.getInstance().sendSlackResponse(response_url, getResult(oneIngRecipe, excludeString, inputIngString));
            return;
        }

        ResultSet rs = conn.prepareStatement(query).executeQuery();
        JSONObject jsonObject = new JSONObject();
        String result = "";
        int resultCount = 0;
        while(rs.next()){
            resultCount++;
            String id = rs.getString(1); // Unused for now
            String title = rs.getString(2);

            result+="<";
            String link = "https://www.epicurious.com/search/";
            String modTitle = title.replaceAll(" ", "%20");
            link+=modTitle+"%20";
            result+= link + "|"+title+"> \n";
        }

        // Error message when no recipes are found
        if(resultCount == 0) {
            result = "Sorry, we couldn't find any recipes based on your search criteria right now.:worried:\nWe are working on adding more recipes *very* soon!\nPlease try searching again with different ingredients!";
        }

        logger.info(result);
        jsonObject.put("text",result);
        RequestHandlerUtil.getInstance().sendSlackResponse(response_url,jsonObject);
    }


    public void setLike(String response_url, String selectedValue) throws Exception{
        JSONObject response = new JSONObject();
        JSONArray attachments = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("color", "#FF0000");
        item.put("text", "Your response *"+ selectedValue +"* is saved.");
        attachments.put(item);
        response.put("attachments", attachments);
        RequestHandlerUtil.getInstance().sendSlackResponse(response_url, response);
        return;
    }
}