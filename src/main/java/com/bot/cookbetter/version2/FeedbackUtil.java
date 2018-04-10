package com.bot.cookbetter.version2;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FeedbackUtil {

    private static FeedbackUtil feedbackUtil;
    final static Logger logger = LoggerFactory.getLogger(FeedbackUtil.class);

    public static FeedbackUtil getInstance() {
        if(feedbackUtil == null) {
            feedbackUtil = new FeedbackUtil();
        }
        return feedbackUtil;
    }

    public static JSONObject addFeedback(String userID, String text) {

        JSONObject response = new JSONObject();
        String result = "";

        int recipeID = 0;

        String[] input = text.split("}");
        String recipeTitle = input[0].replace("{", "").trim();
        String comment = input[1].trim();

        recipeID = Recipe.getRecipeIdFromTitle(recipeTitle);

        if(recipeID > 0) {
            if(addFeedback(recipeID, userID, comment)) {
                result = "Comment added successfully!";
            }
            else {
                result = "There was an error adding comment. Please try again!";
            }
        }
        else {
            // Invalid recipe title
            result = "Looks like you entered an invalid recipe. Please try again!";
        }

        response.put("text", result);
        return response;

    }

    public static boolean addFeedback(int recipeID, String userID, String comment) {

        // TODO: insert in database - return true if success, false if error

        return true;
    }

    public static List<String> getFeedback(int recipeID) {

        return null;
    }

}
