package com.bot.cookbetter.utils;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import com.bot.cookbetter.version2.DatabaseUtil;
import com.bot.cookbetter.version2.FeedbackUtil;
import com.bot.cookbetter.version2.Ingredient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;

import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;


/* To hanlde Requests*/
public class RequestHandlerUtil {

    private static RequestHandlerUtil requestHandlerUtil;
    final Logger logger = LoggerFactory.getLogger(RequestHandlerUtil.class);
    private static Map<String, UserOptions> searchSession = new HashMap<>();
    private static Map<String, PersonalizeOptions> personalizeSession=new HashMap<>();


    public static RequestHandlerUtil getInstance() {
        if(requestHandlerUtil == null) {
            requestHandlerUtil = new RequestHandlerUtil();
        }
        return requestHandlerUtil;
    }

    /*
     * Method to handle incoming Slack request:
     * 1) Read request & extract necessary parameters
     * 2) Construct JSON object for slash command
     * 3) Send POST response
     */
    public void handleSlackRequest(HttpServletRequest request) {
        try {
            Map requestMap = readSlackRequest(request);
            JSONObject responseObj = handleSlashCommand(requestMap);
            String response_url = (String) requestMap.get("response_url");
            String result = sendSlackResponse(response_url, responseObj);
            logger.debug(requestMap.get("command") + ": " + result);
        }
        catch (Exception e) {
            logger.error("Error while handling Slack request: " + e.getMessage());
        }
    }

    public void handleInteractiveSlackRequest(HttpServletRequest request) {
        try {

            String payload = request.getParameter("payload");
            JSONObject payloadObject = new JSONObject(payload);
            JSONArray actions = payloadObject.getJSONArray("actions");
            JSONObject actionObject = actions.getJSONObject(0);
            String selectedValue = "";
            String type = actionObject.getString("type");
            String name = actionObject.getString("name");
            String response_url = payloadObject.getString("response_url");
            JSONObject userObject = payloadObject.getJSONObject("user");
            String userID = userObject.getString("id");
            String triggerID = payloadObject.getString("trigger_id");

            if ("button".equals(type)) {
                selectedValue = actionObject.getString("value");
            } else {
                JSONArray selectedOptionsArray = actionObject.getJSONArray("selected_options");
                selectedValue = selectedOptionsArray.getJSONObject(0).getString("value");
            }

            UserOptions user = searchSession.get(userID);
            if (user == null) {
                user = new UserOptions(userID);
            }

            PersonalizeOptions p_user = personalizeSession.get(userID);
            if (p_user == null) {
                p_user = new PersonalizeOptions(userID);
            }

            switch (name) {
                // Handling user selections for /searchrecipes command
                case "ingredient_1":
                    user.setIngredients(selectedValue);
                    break;

                case "ingredient_2":
                    user.setIngredients(selectedValue);
                    break;

                case "ingredient_3":
                    user.setIngredients(selectedValue);
                    break;

                case "recipe_type":
                    user.setRecipeType(selectedValue);
                    break;

                case "quick_meal":
                    user.setQuickMeal(selectedValue);
                    break;

                case "special_occasions":
                    user.setSpecialOccasion(selectedValue);
                    break;

                case "search_button":
                    user.startSearch(response_url);
                    user = null;
                    break;

                // Handling user selections for /personalize command
                case "agegroup_1":
                    p_user.setAge(selectedValue);
                    break;

                case "allergy_1":
                    p_user.setAllegies(1, selectedValue);
                    break;

                case "allergy_2":
                    p_user.setAllegies(2, selectedValue);
                    break;

                case "allergy_3":
                    p_user.setAllegies(3, selectedValue);
                    break;

                case "diet_rest_1":
                    p_user.setDietRestrictions(1, selectedValue);
                    break;

                case "diet_rest_2":
                    p_user.setDietRestrictions(2, selectedValue);
                    break;

                case "diet_rest_3":
                    p_user.setDietRestrictions(3, selectedValue);
                    break;

                case "ailment_1":
                    p_user.setDisease(1, selectedValue);
                    break;

                case "ailment_2":
                    p_user.setDisease(2, selectedValue);
                    break;

                case "ailment_3":
                    p_user.setDisease(3, selectedValue);
                    break;

                case "diet_goal_1":
                    p_user.setGoals(1, selectedValue);
                    break;

                case "diet_goal_2":
                    p_user.setGoals(2, selectedValue);
                    break;

                case "diet_goal_3":
                    p_user.setGoals(3, selectedValue);
                    break;

                case "submit_button":
                    p_user.submitPreferences(response_url);
                    p_user = null;
                    break;


                // Handling feedback buttons
                case "like_button":
                    //FeedbackUtil.getInstance().userLikeDislike(selectedValue, true);
                    break;

                case "dislike_button":
                    //FeedbackUtil.getInstance().userLikeDislike(selectedValue, false);
                    break;

                case "view_comments":
                    ResponseConstructionUtil.getInstance().viewComments(selectedValue, response_url);
                    break;
                case "add_comment":
                    // ResponseConstructionUtil.getInstance().promptForAddComment(selectedValue, response_url);
                    // JSONObject response = ResponseConstructionUtil.getInstance().constructRecipeDialog(triggerID, response_url, selectedValue);
                    break;
            }

            searchSession.put(userID, user);
            personalizeSession.put(userID, p_user);
        }
        catch(Exception e) {
            logger.info(e.getMessage());
        }
    }

    private Map<String, String> readSlackRequest(HttpServletRequest request) throws Exception {
        Map<String, String> requestMap = new HashMap<>();

        List<String> requiredParams = new ArrayList<>();
        requiredParams.add("command");
        requiredParams.add("user_id");
        requiredParams.add("user_name");
        requiredParams.add("team_id");
        requiredParams.add("text");
        requiredParams.add("response_url");
        requiredParams.add("channel");
        requiredParams.add("token");

        Map<String, String[]> paramMap = request.getParameterMap();
        // TODO: Remove this code
        for(String param : requiredParams) {
            if(paramMap.containsKey(param)) {
                requestMap.put(param, paramMap.get(param)[0]);
            }
        }
        return requestMap;
    }

    private JSONObject handleSlashCommand(Map<String, String> requestMap) throws Exception {
        String command = requestMap.get("command");
        // TODO: Validate the command with token
        JSONObject responseObj = new JSONObject();
        if("/searchrecipes".equals(command)) {
            responseObj = ResponseConstructionUtil.getInstance().invokeSearch();
        }
        else if("/personalize".equals(command)) {
            responseObj = ResponseConstructionUtil.getInstance().personalize();
        }
        else if("/cookbetterhelp".equals(command)) {
            responseObj = ResponseConstructionUtil.getInstance().help();
        }
        else if("/surpriseme".equals(command)) {
            String userID = requestMap.get("user_id");
            responseObj = ResponseConstructionUtil.getInstance().surpriseMe(userID);
        }
        else if("/addcomment".equals(command)) {
            String userID = requestMap.get("user_id");
            String text = requestMap.get("text");
            int recipeID = 0;
            responseObj = FeedbackUtil.getInstance().addComment(recipeID, userID, text);
        }
        else if("/imagesearch".equals(command)) {
            String userID = requestMap.get("user_id");
            responseObj = imageSearch(userID);
        }
        return responseObj;
    }

    public String sendSlackResponse(String response_url, JSONObject responseObj) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<String>(responseObj.toString(), httpHeaders);
        String result = restTemplate.postForObject(response_url, httpEntity, String.class);
        return result;
    }

    public static JSONObject imageSearch(String userID) throws Exception {

        // Getting the last file the user uploaded by calling files.list method
        String url = "https://slack.com/api/files.list?token=xoxp-334900294064-335571428500-335829077234-64535fff384c94d759986fbfb84a7c9a&count=1&ts_to=now&user="+userID+"&pretty=1";

        // Sending GET request to slack
        RestTemplate restTemplate = new RestTemplate();
        String responseStr = restTemplate.getForObject(url, String.class);
        JSONObject response = new JSONObject(responseStr);

        JSONArray files = response.getJSONArray("files");
        JSONObject file = files.getJSONObject(0);
        String id = file.getString("id");
        String fileName = file.getString("name");
        String title = file.getString("title");

        // Making the file URL public
        String publicURL = "https://slack.com/api/files.sharedPublicURL?token=xoxp-334900294064-335571428500-335829077234-64535fff384c94d759986fbfb84a7c9a&file="+id+"&pretty=1";
        String publicResponse = restTemplate.getForObject(publicURL,String.class);
        System.out.println(publicResponse);
        //JSONObject publicFile = respo.getJSONObject("file");
        String permaPublic = file.getString("permalink_public");
        String[] splitWords = permaPublic.split("-");
        String secretKey = splitWords[splitWords.length-1];
        String finalURL = file.getString("url_private")+"?pub_secret="+secretKey;

        // Constructing response to be sent to user
        JSONObject result = new JSONObject();
        result.put("text", ":pushpin: *Recipe Search from Ingredients Image*");
        JSONArray attachments = new JSONArray();

        // Image Recognition
        ClarifaiBuilder cBuilder = new ClarifaiBuilder("cf85654f579d4a10ae216a48bcb0cf17");
        ClarifaiClient client = cBuilder.buildSync();
        ClarifaiResponse imageResponse =  client.getDefaultModels().generalModel().predict().withInputs(ClarifaiInput.forImage(finalURL)).executeSync();
        String imageResponseStr = imageResponse.rawBody();
        System.out.println(imageResponseStr);
        JSONObject imageJSON = new JSONObject(imageResponseStr);

        // Checking if valid image was uploaded
        JSONObject status = imageJSON.getJSONObject("status");
        String success = status.getString("description");
        if("ok".equalsIgnoreCase(success)) {
            JSONObject imageDetails = new JSONObject();
            imageDetails.put("color", "#BDB76B");
            imageDetails.put("title", "Image Details");
            imageDetails.put("text", "File Name: " + fileName + " | File Title: " + title);
            attachments.put(imageDetails);
        }
        else {
            JSONObject imageDetails = new JSONObject();
            imageDetails.put("color", "#FF0000");
            imageDetails.put("title", "Input image decoding failed. Check file format sent.");
            imageDetails.put("text", "File Name: " + fileName + " | File Title: " + title);
            attachments.put(imageDetails);
            result.put("attachments", attachments);
            return result;
        }

        // Filtering image recognition output to get ingredients identified
        JSONArray outputs = imageJSON.getJSONArray("outputs");
        JSONObject output = outputs.getJSONObject(0);
        JSONObject data = output.getJSONObject("data");
        JSONArray concepts = data.getJSONArray("concepts");
        List<String> initialIngredients = new ArrayList<>();
        for(int i = 0; i < concepts.length(); i++) {
            JSONObject prediction = concepts.getJSONObject(i);
            initialIngredients.add(prediction.getString("name"));
            System.out.println("ing = "+ prediction.getString("name"));
            double probability = prediction.getDouble("value");
            System.out.println("prob = " + probability);
        }

        // Validating if the ingredients identified are valid
        // Sample query: select display_name from ingredients where display_name in ('onion', 'tomato');
        Connection connection = DatabaseUtil.getConnection();
        String query = "select display_name from ingredients where display_name in (";
        int count = 0;
        for(String ingredient : initialIngredients) {
            count++;
            query += "'" + ingredient.toLowerCase().trim() + "'";
            if(count != initialIngredients.size()) {
                query += ",";
            }
        }
        query += ");";
        ResultSet rs = connection.prepareStatement(query).executeQuery();

        List<String> ingredients = new ArrayList<>(); // For printing
        Set<Ingredient> ingredientSet = new HashSet<>(); // For searching
        while(rs.next()) {
            String ingredient = rs.getString("display_name");
            ingredients.add(ingredient);
            ingredientSet.add(new Ingredient(ingredient));
        }

        // Displaying identitfied ingredients to user
        if(ingredients.isEmpty()) {
            JSONObject ingredientsObj = new JSONObject();
            ingredientsObj.put("color", "#FF0000");
            ingredientsObj.put("title", "Ingredients Identified");
            ingredientsObj.put("text", "No ingredients were identified. Try again with a different image!");
            attachments.put(ingredientsObj);
            result.put("attachments", attachments);
            return result;
        }
        else {
            JSONObject ingredientsObj = new JSONObject();
            ingredientsObj.put("color", "#FFA500");
            ingredientsObj.put("title", "Ingredients Identified");
            ingredientsObj.put("text", ingredients.toString().replace("[", "").replace("]", ""));
            attachments.put(ingredientsObj);
        }

        // Searching for recipes
        UserOptions user = new UserOptions(userID);
        user.setIngredientList(ingredientSet);
        JSONObject searchResults = user.startSearch(null);
        JSONArray searchAttachments = searchResults.getJSONArray("attachments");
        for(int i = 0; i < searchAttachments.length(); i++) {
            JSONObject recipeJSON = searchAttachments.getJSONObject(i);
            attachments.put(recipeJSON);
        }

        result.put("attachments", attachments);

        return result;
    }

}
