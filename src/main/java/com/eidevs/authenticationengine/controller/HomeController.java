/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eidevs.authenticationengine.controller;

import com.eidevs.authenticationengine.payload.LoginPayload;
import com.eidevs.authenticationengine.service.GenericService;
import com.google.gson.Gson;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author eisrael
 */
@Controller
public class HomeController {

    @Autowired
    Environment env;
    @Autowired
    MessageSource messageSource;
    @Autowired
    GenericService genericService;
    Gson gson;
    private String alertMessage;
    private String alertMessageClass;
    private String alertDisplayType;

    public HomeController() {
        gson = new Gson();
    }

    @GetMapping("/")
    public String index(Model model, HttpServletRequest request, HttpServletResponse response, Principal principal) {
        model.addAttribute("loginPayload", new LoginPayload());
        model.addAttribute("alertMessage", alertMessage);
        model.addAttribute("alertDisplayType", alertDisplayType);
        model.addAttribute("alertMessageClass", alertMessageClass);
        resetAlertMessage();
        return "index";
    }

    private void resetAlertMessage() {
        alertMessage = "";
        alertMessageClass = "";
        alertDisplayType = "";
    }
}
