package com.bot.cookbetter.utils;

import com.bot.cookbetter.version2.FeedbackUtil;
import com.bot.cookbetter.version2.Recipe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ResponseConstructionUtil {

    private static ResponseConstructionUtil responseConstructionUtil;
    final Logger logger = LoggerFactory.getLogger(ResponseConstructionUtil.class);

    public static ResponseConstructionUtil getInstance() {
        if(responseConstructionUtil == null) {
            responseConstructionUtil = new ResponseConstructionUtil();
        }
        return responseConstructionUtil;
    }

    public JSONObject readJSONFile(String fileName) {
        JSONObject response;
        String result = "";
        try {
            InputStream is = getClass().getResourceAsStream(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
            logger.info(result);
        } catch(Exception e) {
            e.printStackTrace();
        }
        response = new JSONObject(result);
        return response;
    }

    public JSONObject invokeSearch() throws JSONException {
        return readJSONFile("/search_options.json");
    }

    public JSONObject personalize() {
        return readJSONFile("/personalize_search.json");
    }

    public JSONObject help() {
        return readJSONFile("/helpdoc.json");
    }

    public JSONObject surpriseMe(String userID) throws Exception {

        // Database connection
        Class.forName("com.mysql.jdbc.Driver");
        String connectionUrl = "jdbc:mysql://mydbinstance.ckzbitlijtbu.us-west-2.rds.amazonaws.com:3306/cookbetter?useUnicode=true&characterEncoding=UTF-8&user=cookbetter&password=cookbetter";
        Connection conn = DriverManager.getConnection(connectionUrl);

        String query = "select * from data where title is not null";

        // Getting user's personalization conditions
        String personalQuery = "select * from personalize where userid = '"+ userID+"';";
        ResultSet ps = conn.prepareStatement(personalQuery).executeQuery();

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

            // Vegan
            String diet_res_1 = ps.getString("diet_res_1");
            if(!"-1".equals(diet_res_1))
                query+= " and "+diet_res_1+" =1";

            // TODO: add vegetarian option

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

        // Selecting 1 random row
        query += " order by rand() limit 1";

        ResultSet rs = conn.prepareStatement(query).executeQuery();
        JSONObject jsonObject = new JSONObject();
        String result = "";

        List<Recipe> recipes = new ArrayList<>();
        while(rs.next()){

            Recipe recipe = new Recipe();
            int ID = rs.getInt(1); // Unused for now
            String name = rs.getString(2);
            recipe.setID(ID);
            recipe.setName(name);
            recipes.add(recipe);

            result+="<";
            String link = "https://www.epicurious.com/search/";
            String modTitle = name.replaceAll(" ", "%20");
            link+=modTitle+"%20";
            result+= link + "|"+name+"> \n";
        }

        // Error message when no recipes are found
        if(recipes.isEmpty()) {
            result = "Sorry, we couldn't find any recipes based on your search criteria right now.:worried:\nWe are working on adding more recipes *very* soon!\nPlease try searching again with different ingredients!";
        }

        jsonObject.put("text",result);

        return jsonObject;
    }

    public void noIngredientsSelectedResponse(String response_url) throws Exception {
        JSONObject response = new JSONObject();
        JSONArray attachments = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("color", "#FF0000");
        item.put("text", "Oops! Looks like you have not selected any ingredients. Please select at least 1 ingredient & try again!");
        attachments.put(item);
        response.put("attachments", attachments);
        RequestHandlerUtil.getInstance().sendSlackResponse(response_url, response);
    }

    public JSONObject constructRecipeResponse(Recipe recipe) {
        JSONObject response = new JSONObject();
        int recipeID = recipe.getID();
        String title = recipe.getName();

        // Displaying title & link
        response.put("title", title);
        String link = "https://www.epicurious.com/search/";
        String modTitle = title.replaceAll(" ", "%20");
        link += modTitle + "%20";
        response.put("title_link", link);

        // General configuration
        response.put("color","#36a64f");
        response.put("attachment_type", "default");
        response.put("callback_id", "recipe_" + recipeID + "_callback");

        // Displaying rating & likes data
        JSONArray fields = new JSONArray();
        JSONObject ratingData = new JSONObject();
        ratingData.put("title", "Rating: " + recipe.getRating());
        ratingData.put("short", false);
        int[] likesData = FeedbackUtil.getInstance().getLikesData(recipeID);
        if(likesData != null) {
            int likes = likesData[0];
            int dislikes = likesData[1];
            int people = likes + dislikes;
            ratingData.put("value", people + " people have tried this recipe. " + likes + " liked it & " + dislikes + " didn't like it!");
        }
        fields.put(ratingData);
        response.put("fields", fields);

        // Displaying buttons
        JSONArray actions = new JSONArray();
        JSONObject likeButton = new JSONObject();
        likeButton.put("name", "likeButton_" + recipeID);
        likeButton.put("text", "Like");
        likeButton.put("type", "button");
        likeButton.put("value", "like");
        actions.put(likeButton);
        JSONObject dislikeButton = new JSONObject();
        dislikeButton.put("name", "dislikeButton_" + recipeID);
        dislikeButton.put("text", "Dislike");
        dislikeButton.put("type", "button");
        dislikeButton.put("value", "dislike");
        actions.put(dislikeButton);
        JSONObject viewComments = new JSONObject();
        viewComments.put("name", "viewComments_" + recipeID);
        viewComments.put("text", "View Comments");
        viewComments.put("type", "button");
        viewComments.put("value", "view_comments");
        actions.put(viewComments);
        JSONObject addComment = new JSONObject();
        addComment.put("name", "addComment_" + recipeID);
        addComment.put("text", "Add Comment");
        addComment.put("type", "button");
        addComment.put("value", "add_comment");
        actions.put(addComment);
        response.put("actions", actions);

        return response;
    }

}
