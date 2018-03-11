package com.bot.cookbetter.utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

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

    public void printDetails() {
        logger.info(this.userID);
        logger.info(this.ing1);
        logger.info(this.ing2);
        logger.info(this.ing3);
        logger.info("" + this.quickMeal);
        logger.info(this.recipeType);
        logger.info(this.specialOccasion);
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

        Class.forName("com.mysql.jdbc.Driver");
        String connectionUrl = "jdbc:mysql://aa7kep36bdpng2.c9oonpekeh8v.us-east-1.rds.amazonaws.com:3306/recipes?useUnicode=true&characterEncoding=UTF-8&user=cookbetter&password=cookbetter";
        Connection conn = DriverManager.getConnection(connectionUrl);

        String query = "select * from samplerecipes where";
        if(ing1 != null)
            query+= " "+ing1+" =1";
        if(ing2 != null)
            query+= " and "+ing2+" =1";
        if(ing3 != null)
            query+= " and "+ing3+" =1";
        if(quickMeal)
            query+= " and col_22_minute_meals = 1";
        if(recipeType != null)
            query+= " and "+recipeType+" =1";
        if(specialOccasion != null)
            query += " and "+specialOccasion+" =1";

        String personalQuery = "select * from personalize where userid = '"+this.userID+"';";
        ResultSet ps = conn.prepareStatement(personalQuery).executeQuery();
        //boolean isEmpty = true;
        while(ps.next()){

            String allergy_1 = ps.getString("allergy_1");
            if(!"-1".equals(allergy_1))
                query+= " and "+allergy_1+" !=1";


            String allergy_2 = ps.getString("allergy_2");
            if(!"-1".equals(allergy_2))
                query+= " and "+allergy_2+" !=1";

            String allergy_3 = ps.getString("allergy_3");
            if(!"-1".equals(allergy_3))
                query+= " and "+allergy_3+" !=1";

            // Veg/vegan
            String diet_res_1 = ps.getString("diet_res_1");
            if(!"-1".equals(diet_res_1))
                query+= " and "+diet_res_1+" =1";

            // Gluten free
            String diet_res_2 = ps.getString("diet_res_2");
            if(!"-1".equals(diet_res_2))
                query+= " and "+diet_res_2 +" =1";

            //Alcohol-free
            String diet_res_3 = ps.getString("diet_res_3");
            if(!"-1".equals(diet_res_3))
                query+= " and "+diet_res_3 +" =0";

            //Cholestrol
            String dis_1 = ps.getString("dis_1");
            if(!"-1".equals(dis_1))
                query+= " and "+dis_1 +" <= 20";

            //Diabetes
            Object dis_2 = ps.getString("dis_2");
            if(!"-1".equals(dis_2))
                query+= " and "+dis_2 +" = 1";

            //Weak Kidney - check for protein content
            Object dis_3 = ps.getString("dis_3");
            if(!"-1".equals(dis_3))
                query+= " and "+dis_3 +" <= 7";

            // Lose weight - foods less than 500 calories
            Object goal_lose_wt = ps.getString("goal_lose_wt");
            if(!"-1".equals(goal_lose_wt))
                query+= " and "+goal_lose_wt +" <= 500";

            // Gain weight
            Object goal_gain_wt = ps.getString("goal_gain_wt");
            if(!"-1".equals(goal_gain_wt))
                query+= " and "+goal_gain_wt +" >=  700";

            // Gain Muscle
            Object goal_gain_muscle = ps.getString("goal_gain_muscle");
            if(!"-1".equals(goal_gain_muscle))
                query+= " and "+goal_gain_muscle +" >= 20";
            break;
        }

        logger.info("Look here:");
        logger.info(query);
        ResultSet rs = conn.prepareStatement(query).executeQuery();
        JSONObject jsonObject = new JSONObject();
        String result = "";
        int resultCount = 0;
        while(rs.next()){
            resultCount++;
            String id = rs.getString(1);
            String title = rs.getString(2);
            //result += title+"\n";
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

        logger.info("The results are :");
        logger.info(result);
        jsonObject.put("text",result);
        RequestHandlerUtil.getInstance().sendSlackResponse(response_url,jsonObject);
    }

}