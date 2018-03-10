package com.bot.cookbetter.utils;
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

    public void startSearch(String response_url) throws Exception{
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

        ResultSet rs = conn.prepareStatement(query).executeQuery();
        JSONObject jsonObject = new JSONObject();
        String result = "";
        while(rs.next()){
            String id = rs.getString(1);
            String title = rs.getString(2);
            result += title+"\n";
        }
        jsonObject.put("text",result);
        RequestHandlerUtil.getInstance().sendSlackResponse(response_url,jsonObject);
    }

}