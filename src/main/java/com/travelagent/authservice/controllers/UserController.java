package com.travelagent.authservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travelagent.authservice.dto.UserInfoDto;
import com.travelagent.authservice.services.AuthService;
import com.travelagent.authservice.services.UserInfoService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    AuthService uAuthService;

    @PostMapping("/signup")
    public ResponseEntity<?> Signup(@RequestBody UserInfoDto userInfoDto) {

        if (!userInfoDto.isValid()) {
            return ResponseEntity.badRequest().body("Please validate the data");
        }
        try {
            userInfoService.createUser(userInfoDto);
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMostSpecificCause().getMessage());
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody UserInfoDto userInfoDto) {

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/changePassword")
    public void changePassword() {

    }

    @PutMapping("/forgotPassword")
    public void forgotPassword(@RequestParam String email) {
        //send otp here..
        throw new UnsupportedOperationException("not implmented yet..");
    }

    @PutMapping("/forgotPassword/{otp}")
    public ResponseEntity<?> forgotPassword(@PathVariable int otp, @RequestBody UserInfoDto user) {
        if (otp < 1000 || otp > 9999) {
            return ResponseEntity.badRequest().body("OTP should of 4 digit is required");
        } else {
            if (uAuthService.validateOTP(otp, user.getEmail())
                &&  userInfoService.forgotPassword(user)) { 
                 return ResponseEntity.noContent().build();
            }
            else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Count not validate the OTP and change password");
            }
        }
    }

    @GetMapping
    public UserInfoDto getUserInformation(Authentication auth)
    {
       var user =  userInfoService.getUser(auth.getName());
       if(user !=null)
       {
        user.setPassword(null);
       }
       return user;
    }

    @DeleteMapping
    public ResponseEntity<?> delUser(Authentication auth) {

        String jwt = auth.getCredentials().toString();
        uAuthService.logout(jwt);
        if (userInfoService.deleteUser(auth.getName())) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
