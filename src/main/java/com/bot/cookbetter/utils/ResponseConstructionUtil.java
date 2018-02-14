package com.bot.cookbetter.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResponseConstructionUtil {

    private static ResponseConstructionUtil responseConstructionUtil;
    final Logger logger = LoggerFactory.getLogger(ResponseConstructionUtil.class);

    public static ResponseConstructionUtil getInstance() {
        if(responseConstructionUtil == null) {
            responseConstructionUtil = new ResponseConstructionUtil();
        }
        return responseConstructionUtil;
    }

    public JSONObject invokeSearch() throws JSONException {
        JSONObject response;
        String result = "";
        try {
            InputStream is = getClass().getResourceAsStream("/search_options.json");
            //BufferedReader br = new BufferedReader(new FileReader("./resources/search_options.json"));
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

    public JSONObject personalize() {
        JSONObject response = new JSONObject();
        JSONArray attachments=new JSONArray();
        response.put("text", "Please select the below preferences in order to Personalise your search");
        response.put("response_type","in_channel");
        JSONObject item_personalise=new JSONObject();
        JSONArray actions = new JSONArray();
        JSONObject actionsitem_age=new JSONObject();
        actionsitem_age.put("name","age");
        actionsitem_age.put("text","Plase enter your Age");
        actionsitem_age.put("type","textbox");
        actionsitem_age.put("Age_action",actionsitem_age);
        JSONObject actionsitem_allergies=new JSONObject();
        actionsitem_allergies.put("name","Allergies");
        actionsitem_allergies.put("text","Please check the allergies you suffer from");
        actionsitem_allergies.put("type","checkbox");
        JSONArray options=new JSONArray();
        JSONObject option1 = new JSONObject();
        option1.put("text", "Lactose");
        option1.put("value", "Lactose");
        JSONObject option2 = new JSONObject();
        option2.put("text", "kiwi");
        option2.put("value", "kiwi");
        JSONObject option3 = new JSONObject();
        option3.put("text", "Peanut");
        option3.put("value", "Peanut");
        options.put(option1);
        options.put(option2);
        options.put(option3);
        return response;
    }

}
