package com.bot.cookbetter.version2;

import com.sun.org.apache.xpath.internal.operations.Bool;
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
        Boolean success = false;
        JSONObject response = new JSONObject();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if(recipeID>0){
            String fbRow = "insert into comments (recipeID, userID, comment, timeStamp) values ('" + recipeID + "','" + userID + "','" + comment + "','" + timestamp.getTime() + "')";
            String dupQuery = "select * from comments where userID='" + userID + "' and recipeID = '" + recipeID + "'";
            try {
                // Database connection
                // Class.forName("com.mysql.jdbc.Driver");
                // String connectionUrl = "jdbc:mysql://mydbinstance.ckzbitlijtbu.us-west-2.rds.amazonaws.com:3306/cookbetter?useUnicode=true&characterEncoding=UTF-8&user=cookbetter&password=cookbetter";
                Connection conn = DatabaseUtil.getConnection();
                ResultSet rs = conn.prepareStatement(dupQuery).executeQuery();
                if(rs.next()){
                    fbRow = "update comments set comment = '" + comment + "', timeStamp = '" + timestamp.getTime() + "' where userID = '" + userID + "' and recipeID = '" + recipeID + "'";
                    conn.prepareStatement(fbRow).executeQuery();
                    success = true;
                } else {
                    conn.prepareStatement(fbRow).executeQuery();
                    success = true;
                }
            } catch(Exception e){
                success = false;
            }
        }
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
            String statement = "update comments set likes = '" + likes + "', dislikes = '" + dislikes + "'";
            conn.prepareStatement(statement).executeQuery();
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
                conn.prepareStatement(statement).executeQuery();
            }else{
                int rating = 5;                                                                 // TBD calculate rating
                String statement = "insert into feedback  (recipeid, rating, views, likes, dislikes) values ('"+ recipeId + "','" + rating + "','0','0')";
                conn.prepareStatement(statement).executeQuery();
            }
        }catch(Exception e){
            System.err.println(e);
        }
    }

}
