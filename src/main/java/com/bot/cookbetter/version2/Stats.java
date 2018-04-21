package com.bot.cookbetter.version2;

import java.sql.ResultSet;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Set;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.util.HashSet;

import static com.bot.cookbetter.version2.FeedbackUtil.logger;

public class Stats {
    public static JSONObject recipestats() {
        JSONObject response = new JSONObject();
        Connection conn = DatabaseUtil.getConnection();
        final Logger logger = LoggerFactory.getLogger(Stats.class);
        logger.info("STATS TESTING BEGINS");
        String query = "SELECT * FROM feedback";
        int recipe_vegetarian_likes = 0;
        int recipe_vegan_likes = 0;
        int recipe_other_dishes_likes = 0;
        try {
            ResultSet rs = conn.prepareStatement(query).executeQuery();
            logger.info("STATS TESTING : query" + query);
            int i = 0;
            int j = 0;

            int[][] feedback_db = new int[2000][2000];
            while (rs.next()) {
                feedback_db[i][j] = rs.getInt("recipeid");
                j++;
                feedback_db[i][j] = rs.getInt("likes");
                i++;
                j=0;
                logger.info("Entering the while block");
                System.out.println(i);
            }
            int k = 0;
            while (k < i) {
                String query2 = "SELECT id, vegan, vegetarian FROM data where id ="+feedback_db[k][0];
                System.out.println(feedback_db[k][0]);
                ResultSet rs2 = conn.prepareStatement(query2).executeQuery();
                while (rs2.next()) {
                    int vegan_flag = rs2.getInt("vegan");
                    int vegetarian_flag = rs2.getInt("vegetarian");
                    if (vegan_flag == 1)
                        recipe_vegan_likes = recipe_vegan_likes + feedback_db[k][1];
                    else if (vegetarian_flag == 1)
                        recipe_vegetarian_likes = recipe_vegetarian_likes + feedback_db[k][1];
                    else
                        recipe_other_dishes_likes = recipe_other_dishes_likes + feedback_db[k][1];
                    System.out.println("Entering the second while loop");
                }
                k++;
            }
            logger.info("STATS TESTING : recipe_vegan_likes" + recipe_vegan_likes + "recipe_vegetarian_likes" + recipe_vegetarian_likes + "recipe_other_dishes_likes" + recipe_other_dishes_likes);

        }
        catch(Exception e)
        {
            System.out.print(e);
        }
        String stats_URL = "https://chart.googleapis.com/chart?chs=500x200&chd=t:" + recipe_vegan_likes + "," + recipe_vegetarian_likes + "," + recipe_other_dishes_likes + "&cht=p3&chl=Vegan_Dishes|Vegetarian_Dishes|Other_Dishes";
        //System.out.print(URL);
        response.put("text", stats_URL);
        logger.info("STATS TESTING : url = " + stats_URL);
        logger.info("STATS TESTING : response = " + response);
        return response;
    }
}
//                feedback_db[i][j] = rs.getInt("recipeid");
//                j++;
//                feedback_db[i][j] = rs.getInt("views");
//                i++;
//                int recipe_id = rs.getInt("recipeid");
//                int recipe_likes = rs.getInt("likes");
//                String query2 = "SELECT id, vegan, vegetarian FROM data where id = " + recipe_id;
//                ResultSet rs2 = conn.prepareStatement(query2).executeQuery();
//                logger.info("STATS TESTING : query2" + query2);
//                while (rs2.next()) {
//                    int vegan_flag = rs.getInt("vegan");
//                    int vegetarian_flag = rs.getInt("vegetarian");
//                    if (vegan_flag == 1)
//                        recipe_vegan_likes = recipe_vegan_likes + recipe_likes;
//                    else if (vegetarian_flag == 1)
//                        recipe_vegetarian_likes = recipe_vegetarian_likes + recipe_likes;
//                    else
//                        recipe_other_dishes_likes = recipe_other_dishes_likes + recipe_likes;
//                }
//

//    public static void main(String[] args) {
//        recipestats();

//        int[][] feedback_db = new int[][]; // This is an array of 10 arrays
//        try {
//            i = 0, j = 0;
//            Class.forName("com.mysql.jdbc.Driver");
//            String connectionUrl = "jdbc:mysql://http://cookbetter.ci2drxnp952j.us-east-1.rds.amazonaws.com:3306/cookbetter?useUnicode=true&characterEncoding=UTF-8&user=cookbetter&password=cookbetter";
//            Connection conn = DriverManager.getConnection(connectionUrl);
//            String query = "SELECT recipeid, views FROM feedback";
//            ResultSet rs = conn.prepareStatement(query).executeQuery();
//            while (rs.next()) {
//                feedback_db[i][j] = rs.getInt("recipeid");
//                feedback_db[i][j] = rs.getInt("views");
//                i++
//                j++;
//            }
//            sortbyColumn(feedback_db, col - 1);
//    }
