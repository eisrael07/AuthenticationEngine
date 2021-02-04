/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eidevs.authenticationengine.service;

import com.google.gson.Gson;
import com.eidevs.authenticationengine.model.Users;
import com.eidevs.authenticationengine.payload.LoginPayload;
import com.eidevs.authenticationengine.payload.MessageContent;
import com.eidevs.authenticationengine.payload.ResponsePayload;
import com.eidevs.authenticationengine.payload.UserPayload;
import com.eidevs.authenticationengine.repository.APIRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author eisrael
 */
@Service
public class APIServiceImpl implements APIService {

    @Autowired
    APIRepository APIRepo;
    @Autowired
    GenericService genericService;
    @Autowired
    Environment env;
    @Autowired
    MessageSource messageSource;
    Gson gson;
    RestTemplate restTemplate;
    ResponsePayload responsePayload;

    private static String AUTHORIZATION = "";
    private final BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();
    static int loginTry = 0;

    APIServiceImpl() {
        responsePayload = new ResponsePayload();
        gson = new Gson();
        restTemplate = new RestTemplate();
    }

    @Override
    public boolean checkRequestHeaderValidity(String authorization) {
        //Check if the request header is valid
        AUTHORIZATION = genericService.getAuthorizationHeader();
        return AUTHORIZATION.equals(authorization);
    }

    @Override
    public String validateUserLogin(String requestBody) {
        Users userDetails;
        LoginPayload login = gson.fromJson(requestBody, LoginPayload.class);

        if (login.getUsernameEmail() == null || login.getPassword() == null) {
            responsePayload.setResponseCode("415");
            responsePayload.setResponseMessage("Required filed(s) missing in the request");
            //Reset the loginRetry counter
            loginTry = 0;
            return gson.toJson(responsePayload, ResponsePayload.class);
        }

        //Check if username/email exist
        userDetails = APIRepo.getUserDetailsWithUserInput(login.getUsernameEmail());

        //Account not found
        if (userDetails == null) {
            responsePayload.setResponseCode("415");
            responsePayload.setResponseMessage("Login account not found. Contact your administrator");
            return gson.toJson(responsePayload, ResponsePayload.class);
        }

        //Account is Disabled
        if (userDetails.getStatus().equalsIgnoreCase("Disabled")) {
            responsePayload.setResponseCode("415");
            responsePayload.setResponseMessage("Login account disabled. Contact your administrator");
            return gson.toJson(responsePayload, ResponsePayload.class);
        }

        //Check password match
        Boolean passwordMatch = bCryptEncoder.matches(login.getPassword(), userDetails.getPassword());
        if (!passwordMatch) {
            loginTry += 1;

            //Get the password retry count
            String passwordRetryCount = env.getProperty("password.retry.count");
            int retryCount = 0;
            try {
                retryCount = Integer.parseInt(passwordRetryCount);
            } catch (NumberFormatException nfe) {
                retryCount = 5;
            }

            //Check if the maximum login try is reached
            if (loginTry == retryCount) {
                //Lock the user for some time
                lockUser(login.getUsernameEmail());
                responsePayload.setResponseCode("415");
                responsePayload.setResponseMessage("Your account is locked due to multiple login attempt");
                loginTry = 0;
                return gson.toJson(responsePayload, ResponsePayload.class);

            }
            responsePayload.setResponseCode("415");
            responsePayload.setResponseMessage("Invalid login credentials. Username or Password incorrect");
            //Update the user password try
            updatePasswordTry(login.getUsernameEmail());
            return gson.toJson(responsePayload, ResponsePayload.class);
        }

        //Check if the user needs to change password
        if (LocalDate.now().isAfter(APIRepo.getPasswordExpiryDate(userDetails.getUsername()))) {
            responsePayload.setResponseCode("94");
            responsePayload.setResponseMessage("Current password has expired. Please change password");
            return gson.toJson(responsePayload, ResponsePayload.class);
        }

        //ResponsePayload responsePayload = new ResponsePayload();
        responsePayload.setResponseCode("200");
        responsePayload.setResponseMessage("Login successful");
        return gson.toJson(responsePayload, ResponsePayload.class);
    }

    @Override
    public String getUserDetails(String requesBody) {
        UserPayload userPayload = gson.fromJson(requesBody, UserPayload.class);
        if (userPayload.getUsername() != null) {
            Users user = APIRepo.getUserDetailsWithUserInput(userPayload.getUsername());
            if (user != null) {
                UserPayload userDetails = new UserPayload();
                userDetails.setEmail(user.getEmail());
                userDetails.setFirstName(user.getFirstName());
                userDetails.setLastName(user.getLastName());
                userDetails.setStatus(user.getStatus());
                userDetails.setUsername(user.getUsername());
                userDetails.setLastLogin(user.getLastLogin().toString());
                userDetails.setPasswordChangeDate(user.getPasswordChangeDate().toString());
                userDetails.setProfilePhoto(user.getProfilePhoto());
                userDetails.setJobRole(user.getJobRole());
                String details = gson.toJson(userDetails, UserPayload.class);
                return details;
            }
        }
        return "";
    }

    @Override
    public String updatePasswordChange(String request) {
        LocalDate date = LocalDate.now().plusMonths(3);
        ResponsePayload response = new ResponsePayload();
        UserPayload userPayload = gson.fromJson(request, UserPayload.class);
        if (userPayload != null) {
            Users user = APIRepo.getUserDetailsWithUserInput(userPayload.getUsername());
            if (user != null) {
                String encryptedPassword = bCryptEncoder.encode(userPayload.getNewPassword());
                user.setPassword(encryptedPassword);
                user.setPasswordChangeDate(date);
                APIRepo.updateUserRecord(user);
                response.setResponseCode("00");
                response.setResponseMessage("Password Change Successful. Please re-login");
                response.setResponseStatus("success");
                String responseMessge = gson.toJson(response, ResponsePayload.class);
                return responseMessge;
            }
        }
        response.setResponseCode("99");
        response.setResponseMessage("Password Change Failed");
        response.setResponseStatus("failed");
        String responseMessge = gson.toJson(response, ResponsePayload.class);
        return responseMessge;
    }

    @Override
    public String updateUserAsOnline(String request) {
        ResponsePayload response = new ResponsePayload();
        UserPayload userPayload = gson.fromJson(request, UserPayload.class);
        if (userPayload != null) {
            Users user = APIRepo.getUserDetailsWithUserInput(userPayload.getUsername());
            if (user != null) {
                user.setUserOnline(userPayload.isUserOnline());
                APIRepo.updateUserRecord(user);
                response.setResponseCode("00");
                response.setResponseMessage("User is now online");
                response.setResponseStatus("success");
                String responseMessge = gson.toJson(response, ResponsePayload.class);
                return responseMessge;
            }
        }
        response.setResponseCode("99");
        response.setResponseMessage("update user online failed");
        response.setResponseStatus("failed");
        String responseMessge = gson.toJson(response, ResponsePayload.class);
        return responseMessge;
    }

    @Override
    public String updateLastLogin(String request) {
        ResponsePayload response = new ResponsePayload();
        UserPayload userPayload = gson.fromJson(request, UserPayload.class);
        if (userPayload != null) {
            Users user = APIRepo.getUserDetailsWithUserInput(userPayload.getUsername());
            if (user != null) {
                user.setLastLogin(LocalDateTime.now());
                APIRepo.updateUserRecord(user);
                response.setResponseCode("00");
                response.setResponseMessage("User last login set");
                response.setResponseStatus("success");
                String responseMessge = gson.toJson(response, ResponsePayload.class);
                return responseMessge;
            }
        }
        response.setResponseCode("99");
        response.setResponseMessage("update user last login failed");
        response.setResponseStatus("failed");
        String responseMessge = gson.toJson(response, ResponsePayload.class);
        return responseMessge;
    }

    @Override
    public String updateUserProfilePhoto(String request) {
        ResponsePayload response = new ResponsePayload();
        UserPayload userPayload = gson.fromJson(request, UserPayload.class);
        if (userPayload != null) {
            Users user = APIRepo.getUserDetailsWithUserInput(userPayload.getUsername());
            if (user != null) {
                user.setProfilePhoto(userPayload.getProfilePhoto());
                APIRepo.updateUserRecord(user);
                response.setResponseCode("00");
                response.setResponseMessage("User profile photo update success");
                response.setResponseStatus("success");
                String responseMessge = gson.toJson(response, ResponsePayload.class);
                return responseMessge;
            }
        }
        response.setResponseCode("99");
        response.setResponseMessage("update user profile photo failed");
        response.setResponseStatus("failed");
        String responseMessge = gson.toJson(response, ResponsePayload.class);
        return responseMessge;
    }
    
    @Override
    public String updateUserEmail(String request) {
        ResponsePayload response = new ResponsePayload();
        UserPayload userPayload = gson.fromJson(request, UserPayload.class);
        if (userPayload != null) {
            Users user = APIRepo.getUserDetailsWithUserInput(userPayload.getUsername());
            if (user != null) {
                user.setEmail(userPayload.getEmail());
                APIRepo.updateUserRecord(user);
                response.setResponseCode("00");
                response.setResponseMessage("User update email successful");
                response.setResponseStatus("success");
                String responseMessge = gson.toJson(response, ResponsePayload.class);
                return responseMessge;
            }
        }
        response.setResponseCode("99");
        response.setResponseMessage("update user email failed");
        response.setResponseStatus("failed");
        String responseMessge = gson.toJson(response, ResponsePayload.class);
        return responseMessge;
    }

    private void lockUser(String username) {
        Users user = APIRepo.getUserDetailsWithUserInput(username);
        user.setStatus("Disabled");
        APIRepo.updateUserRecord(user);
    }

    private void updatePasswordTry(String username) {
        Users user = APIRepo.getUserDetailsWithUserInput(username);
        user.setPasswordRetry(user.getPasswordRetry() + 1);
        APIRepo.updateUserRecord(user);
    }

    @Override
    public String sendEmail(String emailContent) {
        ResponsePayload resp = new ResponsePayload();
        MessageContent newsLetter = gson.fromJson(emailContent, MessageContent.class);
        String newsLetterContent = "";
        String subject = "";
        if (newsLetter != null) {
            newsLetterContent = newsLetter.getPostContent();
            subject = newsLetter.getSubject();

            try {
                JavaMailSender emailSender = getJavaMailSender();
                MimeMessage emailDetails = emailSender.createMimeMessage();
                emailDetails.setFrom(env.getProperty("spring.mail.from").trim());
                if (newsLetter.getEmailType().equalsIgnoreCase("FeedBack") || newsLetter.getEmailType().equalsIgnoreCase("Subscriber")) {
                    Address address = new InternetAddress(env.getProperty("tms.default.send.email").trim());
                    emailDetails.setRecipient(Message.RecipientType.TO, address);
                    emailDetails.setSubject(subject);
                    emailDetails.setText(newsLetterContent);
                    emailDetails.setContent(newsLetterContent, "text/html");
                    emailSender.send(emailDetails);
                } else {
                    List<String> addresses = APIRepo.getListOfSubscribers();
                    Address addrss[] = {};
                    for (String addr : addresses) {
                        Address address = new InternetAddress(addr);
                        emailDetails.setRecipient(Message.RecipientType.TO, address);
                        emailDetails.setSubject(subject);
                        emailDetails.setText(newsLetterContent);
                        emailDetails.setContent(newsLetterContent, "text/html");
                        emailSender.send(emailDetails);
                    }
                }
                resp.setResponseCode("00");
                resp.setResponseDescription("Success");
                resp.setResponseStatus("Sent");
                String response = gson.toJson(resp, ResponsePayload.class);
                return response;
            } catch (MailException | MessagingException ex) {
                resp.setResponseCode("99");
                resp.setResponseDescription("Failed");
                resp.setResponseMessage(ex.getMessage());
                resp.setResponseStatus("Not Sent");
                String response = gson.toJson(resp, ResponsePayload.class);
                System.out.println(ex.getMessage());
                return response;
            }
        }
        resp.setResponseCode("99");
        resp.setResponseDescription("Failed");
        resp.setResponseMessage("No Content");
        resp.setResponseStatus("Not Sent");
        String response = gson.toJson(resp, ResponsePayload.class);
        return response;
    }

    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(env.getProperty("spring.mail.host").trim());
        mailSender.setPort(Integer.parseInt(env.getProperty("spring.mail.port").trim()));

        mailSender.setUsername(env.getProperty("spring.mail.username").trim());
        mailSender.setPassword(env.getProperty("spring.mail.password").trim());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", env.getProperty("spring.mail.properties.mail.transport.protocol").trim());
        props.put("mail.smtp.auth", env.getProperty("spring.mail.properties.mail.smtps.auth").trim());
        props.put("mail.smtp.starttls.enable", env.getProperty("spring.mail.properties.mail.smtps.starttls.enable").trim());
        props.put("mail.debug", "true");
        props.put("mail.smtp.ssl.trust", env.getProperty("spring.mail.properties.mail.smtps.ssl.trust").trim());

        return mailSender;
    }
}
