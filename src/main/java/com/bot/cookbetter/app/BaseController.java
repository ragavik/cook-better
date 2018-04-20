package com.bot.cookbetter.app;

import com.bot.cookbetter.utils.RequestHandlerUtil;
import com.bot.cookbetter.utils.ResponseConstructionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Handles post request from slack to respective business class.
 */

@RestController
public class BaseController {

    public static final String API_TOKEN = "xoxp-334900294064-335571428500-349543974417-294dfe7ff4cd0dddb3d0d6f9d0ef65d5";

    final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @RequestMapping("/")
    public ResponseEntity<String> index() { return ResponseEntity.ok(":pushpin: Welcome to CookBetter!"); }

    @RequestMapping(method = RequestMethod.POST, value = "/slack", consumes = "application/x-www-form-urlencoded")
    public void slack(HttpServletRequest request) {
        RequestHandlerUtil.getInstance().handleSlackRequest(request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/slack-interactive")
    public void slackInteractive(HttpServletRequest request) {
        RequestHandlerUtil.getInstance().handleInteractiveSlackRequest(request);
    }

}