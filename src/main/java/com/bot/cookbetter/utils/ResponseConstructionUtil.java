package com.bot.cookbetter.utils;

import com.bot.cookbetter.model.Recipe;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

//handle request for JSON file and the main code of /supriseme
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

    public JSONObject ynButton() {
        RecipeDataHandler handler = new RecipeDataHandler();
        List<Recipe> recipes = handler.getRecipes();
        JSONObject result = readJSONFile("/ynbutton.json");
        result.put("text", recipes.get(0).getTitle());
        return result;
        //return readJSONFile("/ynbutton.json");
    }




    /*
    public JSONObject surpriseMe() throws Exception{
        JSONObject jsonObject = new JSONObject();
        String result = "Welcome";

        try {
            JSONObject json = ResponseConstructionUtil.getInstance().getRecipeData();
            //result += json.length();
        }
        catch (Exception e){
                System.console().writer().println("Can't get recipe data");
        }
        //JSONArray names = json.names();
        //result +="\n" + names;
        //System.out.println(result);
        //recipe_data.

        //original
        jsonObject.put("text",result);
        return jsonObject;






    }
    */

    public JSONObject surpriseMe(String userID) throws Exception {

        // Database connection
        Class.forName("com.mysql.jdbc.Driver");
        //String connectionUrl = "jdbc:mysql://mydbinstance.ckzbitlijtbu.us-west-2.rds.amazonaws.com:3306/cookbetter?useUnicode=true&characterEncoding=UTF-8&user=cookbetter&password=cookbetter";
        String connectionUrl = "jdbc:mysql://cookbetter.ci2drxnp952j.us-east-1.rds.amazonaws.com:3306/cookbetter?useUnicode=true&characterEncoding=UTF-8&user=cookbetter&password=cookbetter";

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
        int resultCount = 0;
        while(rs.next()){
            resultCount++;
            String id = rs.getString(1); // Unused for now
            String title = rs.getString(2);

            result+="<";
            String link = "https://www.epicurious.com/search/";
            String modTitle = title.replaceAll(" ", "%20");
            link+=modTitle+"%20";
            result+= link + "|"+title+"> \n";
        }

        // Error message when no recipes are found
        if(resultCount == 0) {
            result = "Sorry, we couldn't find any recipes based on your search criteria right now.:worried:\nWe are working on adding more recipes *very* soon!\nPlease try searching again with different ingredients!";
        }

        jsonObject.put("text",result);
        return jsonObject;
    }


    public JSONObject recommend(String userID) {
        System.out.println("recommend1");
        RecipeDataHandler handler = new RecipeDataHandler();
        List<Recipe> recipes = handler.getRecipes();
        System.out.println("recommend3");
        JSONObject result = readJSONFile("/recommendresponse.json");
        System.out.println("recommend4");
        //result.put("text", handler(userID, recipes, result));
        System.out.println("recommend5");
        return handler(userID, recipes, result);
        //return readJSONFile("/ynbutton.json");
    }


    public  JSONObject handler(String userID, List<Recipe> rlist, JSONObject result) {
        //Make list of list for data
        List<List<Integer>> data = new LinkedList<List<Integer>>();
        userID = "100";

        Connection con = null;
        HashMap<String, List<Integer>> map = new HashMap<String, List<Integer>>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "Kapil.963");
            Statement statement = con.createStatement();
            ResultSet rs = null;

            rs = statement.executeQuery("Select * FROM cookdatabase.cookdata;");

            while(rs.next()) {
                String uid = rs.getString(1);
                int rid = rs.getInt(2);
                if(map.containsKey(uid)) {
                    List<Integer> thelist = map.get(uid);
                    if(!thelist.contains(rid)) {
                        thelist.add(rid);
                    }
                    //System.out.println(thelist);
                }else {
                    List<Integer> thelist = new LinkedList<Integer>();
                    thelist.add(rid);
                    map.put(uid, thelist);
                }
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(String str : map.keySet()) {
            data.add(map.get(str));
        }


        List<String> recipes = new LinkedList<String>();
//        System.out.println("handler1");
//        try {
//            //Scanner sc = new Scanner(new File(ResponseConstructionUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath()+"datafile.txt"));
//            InputStream is = getClass().getResourceAsStream("/datafile.txt");
//            System.out.println("handler2");
//            //BufferedReader br = new BufferedReader(new InputStreamReader(is));
//            Scanner sc = new Scanner(is);
//            System.out.println("handler3");
//            while(sc.hasNextLine()) {
//                List<Integer> u = new LinkedList<Integer>();
//                String s = sc.nextLine();
//                String[] arr = s.split("#");
//                for(int i=3; i<arr.length; i++) {
//                    u.add(Integer.parseInt(arr[i]));
//                }
//                data.add(u);
//            }



            //System.out.println("Data: " + data);
//            InputStream ist = getClass().getResourceAsStream("/recipelist.txt");
//            //BufferedReader br = new BufferedReader(new InputStreamReader(is));
//            Scanner scs = new Scanner(ist);
//            while(scs.hasNextLine()) {
//                String str = scs.nextLine();
//                recipes.add(str);
//                //System.out.print(str);
//            }
            //for(int i=0; i<200; i++){
            //    recipes.add("Recipe"+i);
            //}
            //System.out.println("Recipes: "+recipes);
            //sc.close();
//            scs.close();

//        } catch (Exception e) {
            //System.out.println("Error");
//            e.printStackTrace();
 //       }

        List<Integer> userRecipes = map.get(userID);
        List<List<Integer>> sol = recommendOnList(data, userRecipes);


        //System.out.println(rlist);
        for(int i=0; i<200; i++){
            recipes.add(rlist.get(i).getTitle());
        }



        //Display results
        return displayResults(sol, recipes, result);
    }

    public JSONObject displayResults(List<List<Integer>> list, List<String> recipes, JSONObject result) {
        int len = list.size();
        JSONObject obj = new JSONObject();
        List<JSONObject> objs = new LinkedList<JSONObject>();
        System.out.println(objs);
        for(int i=0; i<len; i++) {
            List<Integer> details = list.get(i);
            JSONObject o = readJSONFile("/recommendresponse.json");
            List<JSONObject> lis = new LinkedList<JSONObject>();
            JSONObject ac = readJSONFile("/recaction.json");
            ac.remove("value");
            ac.put("value", details.get(0));
            lis.add(ac);
            o.put("actions", lis);
            String answer = (details.get(1) + "% of the people who like \'" + recipes.get(details.get(2)) + "\' also like \'" + recipes.get(details.get(0)) + "\'");
            o.remove("text");
            o.put("text", answer);
            objs.add(o);
        }
        System.out.println(obj);
        obj.put("attachments", objs);
        System.out.println(obj);
        return obj;
    }

    public static List<Integer> recommendOnRecipe(List<List<Integer>> list, int recipe, List<Integer> userRecipes) {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        int userwithrecipe = 0;
        for(List<Integer> user: list) {
            if(user.contains(recipe)) {
                userwithrecipe++;
                for(int item : user) {
                    if(!userRecipes.contains(item)) {
                        if(map.containsKey(item)) {
                            int val = map.get(item);
                            map.replace(item, val+1);
                        }else {
                            map.put(item, 1);
                        }
                    }
                }
            }
        }

        int max_i = -1, max_count = 0;
        for(int i : map.keySet()) {
            if(i != recipe) {
                if(map.get(i) > max_count) {
                    max_i = i;
                    max_count = map.get(i);
                }
            }
        }

        List<Integer> details = new LinkedList<Integer>();
        if(max_count < 1) {
            return details;
        }
        details.add(max_i);
        int percentage = 100*max_count/userwithrecipe;
        details.add(percentage);
        details.add(recipe);
        return details;
    }

    //list - sublists has the users who like the recipe at that index in the list.
    //recipes - the list of recipes that the user likes.
    public static List<List<Integer>> recommendOnList(List<List<Integer>> data, List<Integer> user_recipes) {
        List<List<Integer>> solution = new LinkedList<List<Integer>>();
        for(int recipe : user_recipes) {
            List<Integer> details = recommendOnRecipe(data, recipe, user_recipes);
            if(!details.isEmpty())
                solution.add(details);
        }
        return solution;
    }


}
