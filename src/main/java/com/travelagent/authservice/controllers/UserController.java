package com.travelagent.authservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelagent.authservice.dto.UserInfoDto;
import com.travelagent.authservice.services.UserInfoService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

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
    public void updateUser() {

    }

    @GetMapping
    public void getUser() {

    }

    @DeleteMapping
    public void delUser() {

    }

}
