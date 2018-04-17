package com.bot.cookbetter.utils;

import com.bot.cookbetter.model.Recipe;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * This util class is used to handle recipes.json file
 */
public class RecipeDataHandler {

    /**
     * Read recipes.json file
     *
     * @return lis of recipes with all attributes
     */
    public List<Recipe> getRecipes() {
        List<Recipe> recipesList = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            InputStream is = getClass().getResourceAsStream("/recipes.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            recipesList = objectMapper.readValue(
                    br,
                    objectMapper.getTypeFactory().constructCollectionType(
                            List.class, Recipe.class));
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recipesList;
    }
}
