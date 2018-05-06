package com.bot.cookbetter;

import com.bot.cookbetter.app.CookBetterApplication;
import com.bot.cookbetter.handler.IngredientReportHandler;
import com.bot.cookbetter.model.Recipe;
<<<<<<< HEAD
import com.bot.cookbetter.utils.HSQLConnection;
=======
import com.bot.cookbetter.utils.IngredReportUtil;
import com.bot.cookbetter.handler.IngredientReportHandler;
>>>>>>> be5707b9b4a9b64ea0a545109dc28308c7c49178
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
    IngredReportUtil ingredReportUtil;
    IngredientReportHandler handler;

    @Test
    public void testGetRecipes(){

        ingredReportUtil = new IngredReportUtil();
        handler = new IngredientReportHandler();
        handler.buildReport(INPUT_INGREDS);
        assertNotNull(ingredReportUtil);
    }

    @Test
    public void testJDBC() throws Exception {
        HSQLConnection sql = new HSQLConnection();
        sql.setConnection();
    }
}
