package com.bot.cookbetter.version2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FeedbackUtil {

    private static FeedbackUtil feedbackUtil;
    final Logger logger = LoggerFactory.getLogger(FeedbackUtil.class);

    public static FeedbackUtil getInstance() {
        if(feedbackUtil == null) {
            feedbackUtil = new FeedbackUtil();
        }
        return feedbackUtil;
    }

    public static void addFeedback(int recipeID, String userID, String comment) {

    }

    public static List<String> getFeedback(int recipeID) {

        return null;
    }

}
