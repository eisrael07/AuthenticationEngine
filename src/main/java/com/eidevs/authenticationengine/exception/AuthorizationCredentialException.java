/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eidevs.authenticationengine.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author eisrael
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Invalid user credentials")
public class AuthorizationCredentialException extends Exception {

    public AuthorizationCredentialException() {
    }
}
