/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eidevs.authenticationengine.controller;

import com.google.gson.Gson;
import com.eidevs.authenticationengine.exception.AuthorizationCredentialException;
import com.eidevs.authenticationengine.payload.ResponsePayload;
import com.eidevs.authenticationengine.service.APIService;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author eisrael
 */
@RestController
@RequestMapping("/api/service")
public class APIController {

    @Autowired
    APIService apiService;
    @Autowired
    Environment env;
    Gson gson;
    ResponsePayload responsePayload;
    
    APIController(){
        responsePayload = new ResponsePayload();
        gson = new Gson();
    }

    @PostMapping("/email/send")
    public String sendEmail(@RequestHeader(value = "Authorization") String authorization, @RequestBody String requestBody, HttpServletRequest request) throws AuthorizationCredentialException {
        Boolean requestHeaderValid = apiService.checkRequestHeaderValidity(authorization);
        if (!requestHeaderValid) {
            throw new AuthorizationCredentialException();
        }

        return apiService.sendEmail(requestBody);
    }
    
    @PostMapping(value = "/auth/user/login", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String validateUserLogin(@RequestHeader(value = "Authorization") String authorization, @RequestBody String requestBody) {
        Boolean requestHeaderValid = apiService.checkRequestHeaderValidity(authorization);
        if (!requestHeaderValid) {
            //ResponsePayload authMessage = new ResponsePayload();
            responsePayload.setResponseCode("401");
            responsePayload.setResponseMessage("Authorization failed. Invalid header parameters");
            return gson.toJson(responsePayload, ResponsePayload.class);
        }

        //Call the service to authenticate the user
        return apiService.validateUserLogin(requestBody);
    }
    
    @PostMapping(value = "/user/details", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String UserDetails(@RequestHeader(value = "Authorization") String authorization, @RequestBody String requestBody) {
        Boolean requestHeaderValid = apiService.checkRequestHeaderValidity(authorization);
        if (!requestHeaderValid) {
            //ResponsePayload authMessage = new ResponsePayload();
            responsePayload.setResponseCode("401");
            responsePayload.setResponseMessage("Authorization failed. Invalid header parameters");
            return gson.toJson(responsePayload, ResponsePayload.class);
        }

        //Call the service to authenticate the user
        return apiService.getUserDetails(requestBody);
    }
    
    @PostMapping(value = "/user/change-password", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String UserPasswordChange(@RequestHeader(value = "Authorization") String authorization, @RequestBody String requestBody) {
        Boolean requestHeaderValid = apiService.checkRequestHeaderValidity(authorization);
        if (!requestHeaderValid) {
            //ResponsePayload authMessage = new ResponsePayload();
            responsePayload.setResponseCode("401");
            responsePayload.setResponseMessage("Authorization failed. Invalid header parameters");
            return gson.toJson(responsePayload, ResponsePayload.class);
        }

        //Call the service to authenticate the user
        return apiService.updatePasswordChange(requestBody);
    }
    
    @PostMapping(value = "/user/update/online", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String UpdateUserOnline(@RequestHeader(value = "Authorization") String authorization, @RequestBody String requestBody) {
        Boolean requestHeaderValid = apiService.checkRequestHeaderValidity(authorization);
        if (!requestHeaderValid) {
            //ResponsePayload authMessage = new ResponsePayload();
            responsePayload.setResponseCode("401");
            responsePayload.setResponseMessage("Authorization failed. Invalid header parameters");
            return gson.toJson(responsePayload, ResponsePayload.class);
        }

        //Call the service to authenticate the user
        return apiService.updateUserAsOnline(requestBody);
    }
    
    @PostMapping(value = "/user/update/last-login", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String UpdateUserLastLogin(@RequestHeader(value = "Authorization") String authorization, @RequestBody String requestBody) {
        Boolean requestHeaderValid = apiService.checkRequestHeaderValidity(authorization);
        if (!requestHeaderValid) {
            //ResponsePayload authMessage = new ResponsePayload();
            responsePayload.setResponseCode("401");
            responsePayload.setResponseMessage("Authorization failed. Invalid header parameters");
            return gson.toJson(responsePayload, ResponsePayload.class);
        }

        //Call the service to authenticate the user
        return apiService.updateLastLogin(requestBody);
    }
    
    @PostMapping(value = "/user/update/profile-photo", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String UpdateUserProfilePhoto(@RequestHeader(value = "Authorization") String authorization, @RequestBody String requestBody) {
        Boolean requestHeaderValid = apiService.checkRequestHeaderValidity(authorization);
        if (!requestHeaderValid) {
            //ResponsePayload authMessage = new ResponsePayload();
            responsePayload.setResponseCode("401");
            responsePayload.setResponseMessage("Authorization failed. Invalid header parameters");
            return gson.toJson(responsePayload, ResponsePayload.class);
        }

        //Call the service to authenticate the user
        return apiService.updateUserProfilePhoto(requestBody);
    }
    
    @PostMapping(value = "/user/update/email", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String UpdateUserEmail(@RequestHeader(value = "Authorization") String authorization, @RequestBody String requestBody) {
        Boolean requestHeaderValid = apiService.checkRequestHeaderValidity(authorization);
        if (!requestHeaderValid) {
            //ResponsePayload authMessage = new ResponsePayload();
            responsePayload.setResponseCode("401");
            responsePayload.setResponseMessage("Authorization failed. Invalid header parameters");
            return gson.toJson(responsePayload, ResponsePayload.class);
        }

        //Call the service to authenticate the user
        return apiService.updateUserEmail(requestBody);
    }
}
