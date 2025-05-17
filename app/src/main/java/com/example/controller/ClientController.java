package com.example.controller;

import com.example.client.AuthClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

@Controller
@Slf4j
public class ClientController {

    private final AuthClient authClient;

    public ClientController(AuthClient authClient) {
        this.authClient = authClient;
    }

    @GetMapping("/login")
    public RedirectView startAuthorization() {
        String state = UUID.randomUUID().toString();
        String authorizationUrl = authClient.generateAuthorizationUrl(state);
        return new RedirectView(authorizationUrl);
    }

    @GetMapping("/callback")
    public ModelAndView handleCallback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String error_description) {
        log.info("Callback received with code: {}, state: {}, error: {}, error_description: {}",
                code, state, error, error_description);
        if (error != null) {
            log.error("OAuth2 error: {} - {}", error, error_description);
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("error", error);
            mav.addObject("error_description", error_description);
            return mav;
        }

        log.info("Processing callback with code: {}, state: {}", code, state);
        String token = authClient.exchangeCodeForToken(code, state);
        String resourceResponse = authClient.accessProtectedResource(token);
        ModelAndView mav = new ModelAndView("result");
        mav.addObject("response", resourceResponse);
        return mav;
    }
}
