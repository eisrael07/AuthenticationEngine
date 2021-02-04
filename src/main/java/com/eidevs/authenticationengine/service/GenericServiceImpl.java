/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eidevs.authenticationengine.service;

import com.google.gson.Gson;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author eisrael
 */
@Service
public class GenericServiceImpl implements GenericService {

    @Autowired
    Environment env;
    @Autowired
    MessageSource messageSource;
    Gson gson;
    RestTemplate restTemplate;

    GenericServiceImpl() {
        gson = new Gson();
        restTemplate = new RestTemplate();
    }
    
    @Override
    public String getAuthorizationHeader() {
        String auth = "Basic " + Base64.getEncoder().encodeToString((env.getProperty("api.authorization.username") + ":" + env.getProperty("api.authorization.password")).getBytes());
        return auth;
    }

    @Override
    public HttpHeaders getHttpHeader() {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.add("Authorization", getAuthorizationHeader());
        return header;
    }
}
