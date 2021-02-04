/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eidevs.authenticationengine.service;

/**
 *
 * @author eisrael
 */
public interface APIService {
    
    boolean checkRequestHeaderValidity(String authorization);
    
    String sendEmail(String emailContent);
    
    String validateUserLogin(String requestBody);
    
    String getUserDetails(String requesBody);
    
    String updatePasswordChange(String request);
    
    String updateUserAsOnline(String request);
    
    String updateLastLogin(String request);
    
    String updateUserProfilePhoto(String request);
    
    String updateUserEmail(String request);
}
