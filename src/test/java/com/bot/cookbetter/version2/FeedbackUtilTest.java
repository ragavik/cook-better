package com.bot.cookbetter.version2;

import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class FeedbackUtilTest {

    @Test
    public void addComment() {
        int recipeID = 1;
        String userID = "Tagore";
        String comment = "I really liked this recipe...";
        JSONObject response = new JSONObject();
        response.put("userid",userID);
        response.put("comment",comment);
        assertEquals(response, FeedbackUtil.addComment(recipeID, userID, comment));
    }

    @Test
    public void addLikes() {
        int recipeID = 1;
        Boolean like = true, response = true;
        assertEquals(response,FeedbackUtil.addLikes(recipeID,like));
    }

    @Test
    public void getComments() {
        int recipeID = 1;
        List<String> response = new ArrayList<>();
        response.add("I really liked this recipe...");
        assertEquals(response, FeedbackUtil.getComments(recipeID));
    }

    @Test
    public void getViewsLikes() {
        int recipeID = 1;
        int[] stats = new int[3];
        stats[1] = 0;
        stats[2] = 0;
        stats[3] = 0;
        assertEquals(stats, FeedbackUtil.getViewsLikes(recipeID));
    }

    @Test
    public void addViews() {
        int recipeID = 1;
        FeedbackUtil.addViews(recipeID);
    }
}