package com.bot.cookbetter.utils;

import com.bot.cookbetter.version2.FeedbackUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;

import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
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
                    logger.info("like button pressed");
                    FeedbackUtil.getInstance().userLikeDislike(selectedValue, true);
                    break;

                case "dislike_button":
                    logger.info("dislike button pressed");
                    FeedbackUtil.getInstance().userLikeDislike(selectedValue, false);
                    break;

                case "view_comments":
                    logger.info("view comments button pressed");
                    ResponseConstructionUtil.getInstance().viewComments(selectedValue, response_url);
                    break;
                case "add_comment":
                    ResponseConstructionUtil.getInstance().promptForAddComment(selectedValue, response_url);
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
            responseObj = FeedbackUtil.getInstance().addFeedback(userID, text);
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

}
