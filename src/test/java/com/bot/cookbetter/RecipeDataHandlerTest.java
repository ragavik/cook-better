package com.bot.cookbetter;

import com.bot.cookbetter.app.CookBetterApplication;
import com.bot.cookbetter.model.Recipe;
import com.bot.cookbetter.utils.HSQLConnection;
import com.bot.cookbetter.utils.IngredientNetwork;
import com.bot.cookbetter.utils.RecipeDataHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CookBetterApplication.class)
public class RecipeDataHandlerTest {

    private final static String INPUT_INGREDS = "Chicken, thyme, butter, pepper, egg, bread, honey, salt, abcde";
    RecipeDataHandler handler;
    IngredientNetwork ingredientNetwork;

    @Test
    public void testGetRecipes(){
        handler = new RecipeDataHandler();
        List<Recipe> data = handler.getRecipes();

        ingredientNetwork = new IngredientNetwork(INPUT_INGREDS);
        ingredientNetwork.getAnalysis();
        assertNotNull(data);
    }

    @Test
    public void testJDBC() throws Exception {
        HSQLConnection sql = new HSQLConnection();
        sql.setConnection();
    }
}
