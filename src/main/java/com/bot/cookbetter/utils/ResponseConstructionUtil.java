package com.bot.cookbetter.utils;

import com.bot.cookbetter.version2.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResponseConstructionUtil {

    private static ResponseConstructionUtil responseConstructionUtil;
    final Logger logger = LoggerFactory.getLogger(ResponseConstructionUtil.class);

    public static ResponseConstructionUtil getInstance() {
        if (responseConstructionUtil == null) {
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
        } catch (Exception e) {
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

        Connection conn = DatabaseUtil.getConnection();
        String query = "select * from data where title is not null";

        // Getting user's personalization conditions
        String personalQuery = "select * from personalize where userid = '" + userID + "';";
        ResultSet ps = conn.prepareStatement(personalQuery).executeQuery();

        while (ps.next()) {

            String allergy_1 = ps.getString("allergy_1");
            if (!"-1".equals(allergy_1))
                query += " and " + allergy_1 + " !=1";

            String allergy_2 = ps.getString("allergy_2");
            if (!"-1".equals(allergy_2))
                query += " and " + allergy_2 + " !=1";

            String allergy_3 = ps.getString("allergy_3");
            if (!"-1".equals(allergy_3))
                query += " and " + allergy_3 + " !=1";

            // Vegan
            String diet_res_1 = ps.getString("diet_res_1");
            if (!"-1".equals(diet_res_1))
                query += " and " + diet_res_1 + " =1";

            // TODO: add vegetarian option

            // Gluten free
            String diet_res_2 = ps.getString("diet_res_2");
            if (!"-1".equals(diet_res_2))
                query += " and " + diet_res_2 + " =1";

            //Alcohol-free
            String diet_res_3 = ps.getString("diet_res_3");
            if (!"-1".equals(diet_res_3))
                query += " and " + diet_res_3 + " =0";

            //Cholestrol
            String dis_1 = ps.getString("dis_1");
            if (!"-1".equals(dis_1))
                query += " and " + dis_1 + " <= 20";

            //Diabetes
            Object dis_2 = ps.getString("dis_2");
            if (!"-1".equals(dis_2))
                query += " and " + dis_2 + " = 1";

            //Weak Kidney - check for protein content
            Object dis_3 = ps.getString("dis_3");
            if (!"-1".equals(dis_3))
                query += " and " + dis_3 + " <= 7";

            // Lose weight - foods less than 500 calories
            Object goal_lose_wt = ps.getString("goal_lose_wt");
            if (!"-1".equals(goal_lose_wt))
                query += " and " + goal_lose_wt + " <= 500";

            // Gain weight
            Object goal_gain_wt = ps.getString("goal_gain_wt");
            if (!"-1".equals(goal_gain_wt))
                query += " and " + goal_gain_wt + " >=  700";

            // Gain Muscle
            Object goal_gain_muscle = ps.getString("goal_gain_muscle");
            if (!"-1".equals(goal_gain_muscle))
                query += " and " + goal_gain_muscle + " >= 20";

            break;
        }

        // Selecting 1 random row
        query += " order by rand() limit 1";

        ResultSet rs = conn.prepareStatement(query).executeQuery();
        JSONObject jsonObject = new JSONObject();
        String result = "";

        List<Recipe> recipes = new ArrayList<>();
        while (rs.next()) {

            Recipe recipe = new Recipe();
            int ID = rs.getInt(1); // Unused for now
            String name = rs.getString(2);
            recipe.setID(ID);
            recipe.setName(name);
            recipes.add(recipe);

            result += "<";
            String link = "https://www.epicurious.com/search/";
            String modTitle = name.replaceAll(" ", "%20");
            link += modTitle + "%20";
            result += link + "|" + name + "> \n";
        }

        // Error message when no recipes are found
        if (recipes.isEmpty()) {
            result = "Sorry, we couldn't find any recipes based on your search criteria right now.:worried:\nWe are working on adding more recipes *very* soon!\nPlease try searching again with different ingredients!";
        }

        jsonObject.put("text", result);
        conn.close();

        return jsonObject;
    }

    public void noIngredientsSelectedResponse(String response_url) throws Exception {
        JSONObject response = new JSONObject();
        response.put("text", ":pushpin: *Warning!*");
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

        // Increasing view count for recipe
        FeedbackUtil.addViews(recipeID);

        String title = recipe.getName();

        // Displaying title & link
        response.put("title", title);
        String link = "https://www.epicurious.com/search/";
        String modTitle = title.replaceAll(" ", "%20");
        link += modTitle + "%20";
        response.put("title_link", link);

        // General configuration
        response.put("color", "#36a64f");
        response.put("attachment_type", "default");
        response.put("callback_id", "recipe_" + recipeID + "_callback");

        String imageUrl = GoogleImageFetcher.getAnImageLink(recipe.getName());
        response.put("image_url", imageUrl);

        // Displaying rating & likes data
        /*JSONArray fields = new JSONArray();
        JSONObject ratingData = new JSONObject();
        ratingData.put("title", "Rating: " + recipe.getRating());
        ratingData.put("short", false);
        int[] likesData = FeedbackUtil.getInstance().getLikesData(recipeID);
        if(likesData != null) {
            int likes = likesData[0];
            int dislikes = likesData[1];
            int people = likes + dislikes;
            ratingData.put("value", people + " people have tried this recipe. " + likes + " liked it :thumbsup: & " + dislikes + " disliked it :thumbsdown:!");
        }
        fields.put(ratingData);
        response.put("fields", fields);*/

        // Displaying buttons
        JSONArray actions = new JSONArray();
        JSONObject likeButton = new JSONObject();
        likeButton.put("name", "like_button");
        likeButton.put("text", ":thumbsup:");
        likeButton.put("type", "button");
        likeButton.put("value", "likeButton_" + recipeID);
        actions.put(likeButton);
        JSONObject dislikeButton = new JSONObject();
        dislikeButton.put("name", "dislike_button");
        dislikeButton.put("text", ":thumbsdown:");
        dislikeButton.put("type", "button");
        dislikeButton.put("value", "dislikeButton_" + recipeID);
        actions.put(dislikeButton);
        JSONObject instructions = new JSONObject();
        instructions.put("name", "instructions");
        instructions.put("text", "Make Recipe!");
        instructions.put("type", "button");
        instructions.put("value", "instructions_" + recipeID);
        actions.put(instructions);
        JSONObject viewComments = new JSONObject();
        viewComments.put("name", "view_comments");
        viewComments.put("text", "View Feedback");
        viewComments.put("type", "button");
        viewComments.put("value", "viewComments_" + recipeID);
        actions.put(viewComments);
        JSONObject addComment = new JSONObject();
        addComment.put("name", "add_comment");
        addComment.put("text", "Add Feedback");
        addComment.put("type", "button");
        addComment.put("value", "addComment_" + recipeID);
        actions.put(addComment);
        response.put("actions", actions);

        return response;
    }

    /*
     * Method to construct a JSON object of comments for a recipe
     */
    public static void viewComments(String buttonValue, String response_url) throws Exception {
        JSONObject response = new JSONObject();

        String recipeIDStr = buttonValue.split("_")[1];
        int recipeID = Integer.parseInt(recipeIDStr);
        String recipeTitle = Util.getRecipe(recipeID).getName();
        response.put("text", ":pushpin: *Comments for `" + recipeTitle + "`:*");
        response.put("attachment_type", "default");
        response.put("replace_original", false);

        JSONArray attachments = new JSONArray();

        List<String> comments = FeedbackUtil.getInstance().getComments(recipeID);
        if (comments.isEmpty()) {
            JSONObject commentObj = new JSONObject();
            commentObj.put("color", "#ff0000");
            commentObj.put("text", "This recipe does not have any comments yet! :worried:");
            attachments.put(commentObj);
        }
        else {
            for(String comment : comments) {
                JSONObject commentObj = new JSONObject();
                commentObj.put("color", "#ffc299");
                commentObj.put("text", comment);
                attachments.put(commentObj);
            }
        }

        response.put("attachments", attachments);

        RequestHandlerUtil.getInstance().sendSlackResponse(response_url, response);
    }

    // Method that shows instruction on how to add comment
    public void promptForAddComment(String buttonValue, String response_url) throws Exception {

        String recipeIDStr = buttonValue.split("_")[1];
        int recipeID = Integer.parseInt(recipeIDStr);
        Recipe recipe = Util.getRecipe(recipeID);
        String recipeTitle = recipe.getName();

        JSONObject response = new JSONObject();
        response.put("text", ":pushpin: Type /addcomment {" + recipeTitle + "} followed by your comment.");
        response.put("replace_original", false);
        RequestHandlerUtil.getInstance().sendSlackResponse(response_url, response);
    }

    public static int getRecipeIDFromButton(String buttonValue) {
        String recipeIDStr = buttonValue.split("_")[1];
        int recipeID = Integer.parseInt(recipeIDStr);
        return recipeID;
    }

    public JSONObject suggestFromNaturalQuery(String query, String userID) throws Exception {
        logger.info("HELP : in suggest method");
        System.out.println("query = " + query);
        JSONObject response = new JSONObject();
        String ingredientsCsv = Util.extractIngredients(query);
        logger.info("extratced ingredients = " + ingredientsCsv);
        Set<Ingredient> ingredientSet = new HashSet<>();
        for(String ingredient : ingredientsCsv.split(",")) {
            logger.info("input = " + ingredient);
            Ingredient ing = new Ingredient(ingredient, false);
            if(ing.isExisits()) {
                ingredientSet.add(ing);
                logger.info(ing.toString());
            }
            logger.info("= output");
        }

        // Searching for recipes
        UserOptions user = new UserOptions(userID);
        user.setIngredientList(ingredientSet);
        logger.info("TESTING QUERY SEARCH : ");
        logger.info(ingredientSet.toString());
        JSONObject searchResults = user.startSearch(null);
        JSONArray searchAttachments = searchResults.getJSONArray("attachments");
        response = searchResults;
        logger.info("Response = " + response.toString());

        return response;
    }

}
//    public JSONObject constructRecipeDialog(String triggerID, String response_url, String buttonValue) throws Exception {
//        int recipeID = getRecipeIDFromButton(buttonValue);
//
//        logger.info("TESTING : in constructRecipeDialog for triggerID = " + triggerID + " & response_url = " + response_url);
//        JSONObject response = new JSONObject();
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        JSONObject dialog = new JSONObject();
//        JSONArray display_array = new JSONArray();
//        JSONObject display_box = new JSONObject();
//        display_box.put("type", "textarea");
//        display_box.put("label", "Add Feedback");
//        display_box.put("name", "user_feedback");
//        display_array.put(display_box);
//        dialog.put("callback_id", "comment_dialog");
//        dialog.put("title", "Feedback");
//        dialog.put("submit_label", "save");
//        dialog.put("elements", display_array);

////        JSONObject dialog = new JSONObject();
////        JSONObject body = new JSONObject();
////        JSONArray display_array = new JSONArray();
////        JSONObject display_box = new JSONObject();
////        display_box.put("type", "textarea");
////        display_box.put("label", "Add Feedback");
////        display_box.put("name", "user_feedback");
////        display_array.put(display_box);
////        dialog.put("callback_id", "comment_dialog");
////        dialog.put("title", "Feedback");
////        dialog.put("submit_label", "save");
////        dialog.put("elements", display_array);
////        body.put("trigger_id",triggerID);
////        body.put("dialog",dialog);
////
////        logger.info("TESTING: SENDING_SLACK_RESPONSE: "+body);
//
////        String popup_result = RequestHandlerUtil.getInstance().sendSlackResponse(response_url, body);
//
////        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
////        HttpEntity<String> httpEntity = new HttpEntity<String>(body.toString(), httpHeaders);
////        logger.info("TESTING : httpEntity : "+httpEntity);
////        String popup_result = restTemplate.postForObject(response_url, httpEntity, String.class);
////
////        logger.info("TESTING: DONE: "+popup_result);
//
//
//        String json_string = dialog.toString();
////        String json_string = body.toString();
//        String request_url = "https://slack.com/api/dialog.open?token=xoxp-334900294064-335571428500-349543974417-294dfe7ff4cd0dddb3d0d6f9d0ef65d5&trigger_id=" + triggerID;
//        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
//        map.add("dialog",URLEncoder.encode(json_string, "UTF-8") );
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, httpHeaders);
//        ResponseEntity<String> popup_response = restTemplate.postForEntity( request_url, request , String.class );
//        logger.info("TESTING: pop_up_request_url : " + request_url);
//        logger.info("TESTING: pop_up_request : " + request);
//        logger.info("TESTING :  DONE : " + popup_response);
//
//        return null;
//    }
//  /*      //response.put("trigger_id", triggerID);
//        JSONObject dialog = new JSONObject();
//        dialog.put("title", "Feedback");
//        dialog.put("callback_id", "feedback_dialog");
//        dialog.put("submit_label", "Done");
//
//        JSONArray elements = new JSONArray();
//        JSONObject comments = new JSONObject();
//        comments.put("type", "textarea");
//        comments.put("label", "Comment");
//        comments.put("name", "comment_for_" + recipeID);
//        elements.put(comments);
//
//        // Display like, dislike buttons
//        // Display textarea for adding comment
//
//        dialog.put("elements", elements);
//        response.put("dialog", dialog);
//
//
//        logger.info("TESTING : JSON sent = " + response.toString());
//
//        response_url = "https://slack.com/api/dialog.open?token=xoxp-334900294064-335571428500-335829077234-64535fff384c94d759986fbfb84a7c9a";
//        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
//        requestBody.add("dialog", URLEncoder.encode(response.toString()));
//        requestBody.add("trigger_id", triggerID);
//
//
//        logger.info("TESTING : ENC = " + URLEncoder.encode(response.toString()));
//                RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        HttpEntity httpEntity = new HttpEntity<MultiValueMap<String, String>>(requestBody, httpHeaders);
//        String result = restTemplate.postForObjeclogger.info("TESTING :  DONE : " + result);t(response_url, httpEntity, String.class);
//
//       // RequestHandlerUtil.getInstance().sendSlackResponse(response_url, response);
//        return response;
//
//    } */
//
//
//
//}
