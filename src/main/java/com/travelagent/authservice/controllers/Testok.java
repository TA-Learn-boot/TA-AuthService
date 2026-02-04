package com.travelagent.authservice.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testok")
public class Testok {

    @GetMapping
    public ResponseEntity<?> testOkay() {
        return ResponseEntity.ok().body("done");
    }

}
