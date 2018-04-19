package com.bot.cookbetter.version2;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import java.util.ArrayList;
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
        List<String> comments = new ArrayList<>();
        // TODO: Query database and get comments for given recipe ID (assuming it is valid)

        // Hardcoding for testing
        //comments.add("Great recipe! Loved it.");
        //comments.add("Took too long to make..");
        //comments.add("Will definitely try again");

        return comments;

    }

    /*
    * Method returns data about likes for given recipe ID
    * Returns an integer array result
    * result[0] = no. of likes
    * result[1] = no. of dislikes
    */
    public static int[] getLikesData(int recipeID) {
        int[] result = new int[2];

        // TODO: Query database & get no. of likes & dislikes

        //return result;
        return null;
    }

    public static  void userLikeDislike(String buttonValue, boolean like) {

    }

    public static void addLikeDislike(int recipeID, boolean like) {
        /*
         TODO:
         Query database & get no. of likes & dislikes for recipe ID
         Increment likes/dislikes based on parameter 'like'
         Update row or create new row if it didn't exist before
        */

    }

}
