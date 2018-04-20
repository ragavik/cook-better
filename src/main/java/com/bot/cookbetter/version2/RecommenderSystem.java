package com.bot.cookbetter.version2;

import com.bot.cookbetter.app.BaseController;
import com.bot.cookbetter.utils.RecipeCompare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author snaraya7 (Shrikanth N C)
 */
public class RecommenderSystem {

    private final Logger logger = LoggerFactory.getLogger(BaseController.class);

    private Stack<Recipe> recipeStack = new Stack();

    private List<RecipeWrapper> recipeWrappers = new ArrayList<>();

    public Stack<Recipe> recommend(Set<Ingredient> ingredientSet) {

        Set<Integer> recipeIds = new HashSet<>();

        for (Ingredient ingredient : ingredientSet){
            recipeIds.addAll( Util.getRecipeIDs(ingredient.getName()));
        }

        for (Integer recipeID : recipeIds) {

            Recipe recipe = Util.getRecipe(recipeID);

            if (recipe != null) {

                if (canAccomodate()) {

                    int score = computeScore(recipe, ingredientSet);

                    if (score == 0) continue;

                    logger.info(score+" - "+recipe);

                    recipeWrappers.add(new RecipeWrapper(recipe, score));

                }else{
                    break;
                }


            }
        }

        if (canAccomodate()){

            Collections.sort(recipeWrappers);

            for (RecipeWrapper recipeWrapper : recipeWrappers){

                if (canAccomodate()){
                    recipeStack.push(recipeWrapper.getRecipe());
                }

            }
        }

        return recipeStack;
    }

    private boolean canAccomodate() {

        return recipeStack.size() <= RecommenderConfiguration.RECOMMEND_UPTO;
    }

    private int computeScore(Recipe recipe, Set<Ingredient> ingredientSet) {

        int matchingIngredient = 0;

        for (Ingredient ingredient : recipe.getIngredients()){

            if ( ingredientSet.contains(ingredient)){
                matchingIngredient++;
            }

        }

        int extraIngredientCount = recipe.getIngredients().size() - matchingIngredient;

        int missingIngredientCount = ingredientSet.size() - matchingIngredient;

        int finalScore = RecommenderConfiguration.EXTRA_INGREDIENT * extraIngredientCount + RecommenderConfiguration.MISSING_INGREDIENT * missingIngredientCount;

        if (matchingIngredient > 0){
            return ( matchingIngredient * 1000 ) - finalScore;
        }else{
            return 0;
        }

    }




}
