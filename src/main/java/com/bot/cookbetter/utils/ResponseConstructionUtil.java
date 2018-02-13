package com.bot.cookbetter.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResponseConstructionUtil {

    private static ResponseConstructionUtil responseConstructionUtil;

    public static ResponseConstructionUtil getInstance() {
        if(responseConstructionUtil == null) {
            responseConstructionUtil = new ResponseConstructionUtil();
        }
        return responseConstructionUtil;
    }

    public JSONObject invokeSearch() throws JSONException {
        // Sample JSON constructed
        // Values should be populated from the database
        JSONObject response = new JSONObject();
        response.put("text", "Choose Ingredients");
        response.put("response_type", "in_channel");
        JSONArray attachments = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("text", "Ingredients");
        item.put("attachment_type", "default");
        JSONArray actions = new JSONArray();
        JSONObject actionsItem = new JSONObject();
        actionsItem.put("name", "ingredients_list");
        actionsItem.put("text", "Select ingredients...");
        actionsItem.put("type", "select");
        JSONArray options = new JSONArray();
        JSONObject option1 = new JSONObject();
        option1.put("text", "Onion");
        option1.put("value", "onion");
        JSONObject option2 = new JSONObject();
        option2.put("text", "Tomato");
        option2.put("value", "tomato");
        options.put(option1);
        options.put(option2);
        actionsItem.put("options", options);
        actions.put(actionsItem);
        item.put("actions", actions);
        attachments.put(item);
        response.put("attachments", attachments);
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
        actions.put(actionsitem_age);
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
        actionsitem_allergies.put("options", options);
        actions.put(actionsitem_allergies);
        item_personalise.put("actions", actions);
        attachments.put(item_personalise);
        response.put("attachments", attachments);
        return response;
    }

}
