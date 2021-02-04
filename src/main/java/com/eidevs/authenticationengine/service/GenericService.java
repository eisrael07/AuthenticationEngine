/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eidevs.authenticationengine.service;

import org.springframework.http.HttpHeaders;

/**
 *
 * @author eisrael
 */
public interface GenericService {
    
    String getAuthorizationHeader();

    HttpHeaders getHttpHeader();
}
