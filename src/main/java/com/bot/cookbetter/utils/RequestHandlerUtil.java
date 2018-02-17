package com.bot.cookbetter.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;

import org.springframework.web.client.RestTemplate;


import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.*;

public class RequestHandlerUtil {

    private static RequestHandlerUtil requestHandlerUtil;
    final Logger logger = LoggerFactory.getLogger(RequestHandlerUtil.class);

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
            String result = sendSlackResponse(requestMap, responseObj);
            logger.debug(requestMap.get("command") + ": " + result);
        }
        catch (Exception e) {
            logger.error("Error while handling Slack request: " + e.getMessage());
        }
    }

    public void handleInteractiveSlackRequest(HttpServletRequest request) {
        logger.info("Inside actual interactive method");
        Enumeration<String> headerNames = request.getHeaderNames();
        Enumeration<String> params = request.getParameterNames();
        while(params.hasMoreElements()){
            String paramName = params.nextElement();
            logger.info("Parameter Name - "+paramName+", Value - "+request.getParameter(paramName));
        }
        String payload = request.getParameter("payload");
        JSONObject payloadObject = new JSONObject(payload);
        JSONArray actions = payloadObject.getJSONArray("actions");
        JSONObject actionObject = actions.getJSONObject(0);
        String selectedValue = "";
        String type = actionObject.getString("type");
        String name = actionObject.getString("name");

        JSONObject userObject = payloadObject.getJSONObject("user");
        String userID = userObject.getString("id");

        if("button".equals(type)){
            selectedValue = actionObject.getString("value");
            logger.info("---------------- !!!!!!!!!!!! -------------------");
            logger.info("Selected value is : "+selectedValue);
            logger.info("---------------- !!!!!!!!!!!! -------------------");
        }
        else{
            logger.info("---------------- !!!!!!!!!!!! -------------------");
            logger.info("Selected name is : "+name);
            logger.info("---------------- !!!!!!!!!!!! -------------------");

            JSONArray selectedOptionsArray = actionObject.getJSONArray("selected_options");
            selectedValue = selectedOptionsArray.getJSONObject(0).getString("value");
            logger.info("---------------- !!!!!!!!!!!! -------------------");
            logger.info("Selected value is : "+selectedValue);
            logger.info("---------------- !!!!!!!!!!!! -------------------");
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
        return responseObj;
    }

    private String sendSlackResponse(Map<String, String> requestMap, JSONObject responseObj) throws Exception {
        String response_url = requestMap.get("response_url");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<String>(responseObj.toString(), httpHeaders);
        String result = restTemplate.postForObject(response_url, httpEntity, String.class);
        return result;
    }

    private Map<String, String> getUserSearchSelection() {
        Map<String, String> selection = new HashMap<>();

        // TODO: Get user selection from Slack request & populate map
        selection.put("user_id", "123");
        selection.put("ingredient_1", "beef");

        return selection;
    }

}