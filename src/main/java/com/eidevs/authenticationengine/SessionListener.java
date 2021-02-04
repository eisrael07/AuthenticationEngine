/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eidevs.authenticationengine;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 *
 * @author eisrael
 */
public class SessionListener implements HttpSessionListener{
    @Autowired
    Environment env;

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        se.getSession().setMaxInactiveInterval(Integer.valueOf(env.getProperty("session.inactive.time")));
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        
    }
    
}
