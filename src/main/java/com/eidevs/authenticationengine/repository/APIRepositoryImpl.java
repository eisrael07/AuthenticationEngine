/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eidevs.authenticationengine.repository;

import com.eidevs.authenticationengine.model.Users;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author eisrael
 */
@Repository
public class APIRepositoryImpl implements APIRepository{
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<String> getListOfSubscribers() {
        TypedQuery<String> query = em.createQuery("SELECT n.email FROM NewsLetterSubscribers n", String.class);
        List<String> subscribers = query.getResultList();
        if(subscribers.isEmpty()){
            return null;
        }
        return subscribers;
    }

    @Override
    public Users getUserDetailsWithUserInput(String input) {
        TypedQuery<Users> query = em.createQuery("SELECT u FROM Users AS u WHERE u.username = :input OR u.email = :input", Users.class)
                .setParameter("input", input);
        List<Users> user = query.getResultList();
        if(user.isEmpty()){
            return null;
        }
        return user.get(0);
    }

    @Override
    @Transactional
    public void updateUserRecord(Users user) {
        em.merge(user);
        em.flush();
    }

    @Override
    public LocalDate getPasswordExpiryDate(String username) {
        TypedQuery<LocalDate> query = em.createQuery("SELECT u.passwordChangeDate FROM Users AS u WHERE u.username = :input", LocalDate.class)
                .setParameter("input", username);
        List<LocalDate> user = query.getResultList();
        if(user.isEmpty()){
            return null;
        }
        return user.get(0);
    }
}
