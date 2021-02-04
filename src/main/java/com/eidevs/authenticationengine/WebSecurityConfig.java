/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eidevs.authenticationengine;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 *
 * @author Emmanuel W. Israel israelewisdom@gmail.com
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/","/api/**").permitAll()
                .antMatchers("/auth/login","/generate-token","/validate-token","/generate/link","/generate/token","/submit/poll").permitAll()
                .antMatchers("/dashboard").permitAll()
                .antMatchers("/css/**", "/images/**", "/js/**", "/uploads/**","/fonts/**").permitAll()
                .antMatchers("/css/**", "/images/**", "/js/**", "/download/**","/fonts/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/").loginProcessingUrl("/")
                .defaultSuccessUrl("/dashboard", true)
                .failureForwardUrl("/error")
                .and()
                .logout().logoutSuccessUrl("/auth/logout")
                .deleteCookies("JSESSIONID")
                .and()
                .csrf().disable();
    }

}
