package com.bot.cookbetter.version2;

import org.junit.Test;

import java.util.Set;
import java.util.Stack;

import static org.junit.Assert.*;

public class RecommenderSystemTest {

    public static void main(String[] arg){

        RecommenderSystem recommenderSystem = new RecommenderSystem();
        Set<Ingredient> ingredients = Util.constructIngredients("gourmet, pork");

        Stack<Recipe> recipes = recommenderSystem.recommend(ingredients);

        for (Recipe recipe : recipes){

            System.out.println("We recommend : "+recipe);
        }



    }
    @Test
    public void recommend() {



    }

}