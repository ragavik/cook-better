package com.bot.cookbetter.version2;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

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

    public static JSONObject addComment(int recipeID, String userID, String comment){
        System.out.println("HELP : in addComment method - recipeID = " + recipeID);
        System.out.println("userID = " + userID);
        System.out.println("comment = " + comment);
        Boolean success = false;
        JSONObject response = new JSONObject();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if(recipeID>0){
            String fbRow = "insert into comments (recipeid, userid, comment) values ('" + recipeID + "','" + userID + "','" + comment + "')";
            logger.info("HELP : insert query = " + fbRow);
            String dupQuery = "select * from comments where userid='" + userID + "' and recipeid = '" + recipeID + "'";
            logger.info("HELP : select query = " + dupQuery);
            try {
                // Database connection
                // Class.forName("com.mysql.jdbc.Driver");
                // String connectionUrl = "jdbc:mysql://mydbinstance.ckzbitlijtbu.us-west-2.rds.amazonaws.com:3306/cookbetter?useUnicode=true&characterEncoding=UTF-8&user=cookbetter&password=cookbetter";
                Connection conn = DatabaseUtil.getConnection();
                ResultSet rs = conn.prepareStatement(dupQuery).executeQuery();
                logger.info("Selected:)");
                int rowCount = 0;
                while(rs.next()) {
                    rowCount++;
                }
                logger.info("row count = " + rowCount);
                if(rowCount > 0){
                    fbRow = "update comments set comment = '" + comment + "' ' where userid = '" + userID + "' and recipeid = '" + recipeID + "'";
                    conn.prepareStatement(fbRow).executeUpdate();
                    success = true;
                } else {
                    conn.prepareStatement(fbRow).executeUpdate();
                    success = true;
                }
                logger.info("query = "  + fbRow);
                conn.close();
            } catch(Exception e){
                e.printStackTrace();
                success = false;
            }


        }

        if(success) {
            response.put("text", "Comment added!");
        }
        else {
            response.put("text", "Error adding comment! :worried:");
        }
        logger.info("success = " + success);

        return response;
    }

    public static Boolean addLikes(int recipeID, Boolean like){
        // This function must be called only after calling addViews
        Boolean success = false;
        int likes = 0, dislikes = 0, curr_likes = 0, curr_dislikes = 0;
        if(like){
            likes = 1;
        }else{
            dislikes = 1;
        }
        String dupQuery = "select * from feedback where recipeid = '" + recipeID + "'";
        try{
            // Class.forName("com.mysql.jdbc.Driver");
            // String connectionUrl = "jdbc:mysql://mydbinstance.ckzbitlijtbu.us-west-2.rds.amazonaws.com:3306/cookbetter?useUnicode=true&characterEncoding=UTF-8&user=cookbetter&password=cookbetter";
            Connection conn = DatabaseUtil.getConnection();
            ResultSet rs = conn.prepareStatement(dupQuery).executeQuery();
            if(rs.next()){
                curr_likes = rs.getInt(4);
                curr_dislikes = rs.getInt(5);
            }
            likes += curr_likes;
            dislikes += curr_dislikes;
            logger.info("like=" + likes + " dislikes="+dislikes);
            String statement = "update comments set likes = '" + likes + "', dislikes = '" + dislikes + "' where recipeid = '" + recipeID + "'";
            logger.info("LIKE/DISLIKE stmt = " + statement);
            conn.prepareStatement(statement).executeUpdate();
            conn.close();
        }catch(Exception e){
            System.err.println(e);
        }

        return success;
    }

    public static List<String> getComments(int recipeId){
        List<String> comments = new ArrayList<>();
        String query = "select * from comments where recipeid = '" + recipeId + "'";
        try{
            // Class.forName("com.mysql.jdbc.Driver");
            // String connectionUrl = "jdbc:mysql://mydbinstance.ckzbitlijtbu.us-west-2.rds.amazonaws.com:3306/cookbetter?useUnicode=true&characterEncoding=UTF-8&user=cookbetter&password=cookbetter";
            Connection conn = DatabaseUtil.getConnection();
            ResultSet rs = conn.prepareStatement(query).executeQuery();
            while(rs.next()){
                comments.add(rs.getString(3));
            }
            conn.close();
        }catch(Exception e){
            System.err.println(e);
        }
        return comments;
    }

    public static int[] getViewsLikes(int recipeId){
        int[] stats = new int[3];
        stats[1] = 0;
        stats[2] = 0;
        stats[3] = 0;
        String query = "select * from feedback where recipeid = '" + recipeId + "'";
        try{
            // Class.forName("conn.mysql.jdbc.Driver");
            // String connectionUrl = "jdbc:mysql://mydbinstance.ckzbitlijtbu.us-west-2.rds.amazonaws.com:3306/cookbetter?useUnicode=true&characterEncoding=UTF-8&user=cookbetter&password=cookbetter";
            Connection conn = DatabaseUtil.getConnection();
            ResultSet rs = conn.prepareStatement(query).executeQuery();
            while(rs.next()) {
                stats[1] = rs.getInt(3);
                stats[2] = rs.getInt(4);
                stats[3] = rs.getInt(5);
            }
            conn.close();
            }catch(Exception e){
                System.err.println(e);
            }
        return stats;
    }

    public static void addViews(int recipeId){
        String dupQuery = "select * from feedback where recipeid = '" + recipeId + "'";
        try{
            // Class.forName("conn.mysql.jdbc.Driver");
            // String connectionUrl = "jdbc:mysql://mydbinstance.ckzbitlijtbu.us-west-2.rds.amazonaws.com:3306/cookbetter?useUnicode=true&characterEncoding=UTF-8&user=cookbetter&password=cookbetter";
            Connection conn = DatabaseUtil.getConnection();
            ResultSet rs = conn.prepareStatement(dupQuery).executeQuery();
            if(rs.next()) {
                int new_views = rs.getInt(3) + 1;
                String statement = "update feedback set views = '" + new_views + "' where recipeid = '" + recipeId + "'";
                logger.info("in if - stmt = " + statement);
                conn.prepareStatement(statement).executeUpdate();
            }else{
                int rating = 5;                                                                 // TBD calculate rating
                String statement = "insert into feedback  (recipeid, views, likes, dislikes) values ('"+ recipeId + "','" + rating + "','0','0')";
                logger.info("in else - stmt = " + statement);
                conn.prepareStatement(statement).executeUpdate();
            }
            conn.close();
        }catch(Exception e){
            System.err.println(e);
        }
    }

}
