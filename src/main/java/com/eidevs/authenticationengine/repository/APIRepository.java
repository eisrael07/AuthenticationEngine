/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eidevs.authenticationengine.repository;

import com.eidevs.authenticationengine.model.Users;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author eisrael
 */
public interface APIRepository {
    
    List<String> getListOfSubscribers();
    
    Users getUserDetailsWithUserInput(String input);
    
    void updateUserRecord(Users user);
    
    LocalDate getPasswordExpiryDate(String username);
}
