package com.travelagent.authservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelagent.authservice.dto.LoginResponse;
import com.travelagent.authservice.dto.UserInfoDto;
import com.travelagent.authservice.services.AuthService;

import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<?> Login(@RequestBody UserInfoDto userInfoDto) {

    LoginResponse loginResponse = authService.ValidateAndGenerateToken(userInfoDto);

    if (loginResponse == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
    }

    return ResponseEntity.ok(loginResponse);

  }

  @GetMapping("/refresh/{refreshToken}")
  public ResponseEntity<?> RefreshAuth(@PathVariable String refreshToken) {
    var loginResp = authService.RotateAndRefresh(refreshToken);
    if (loginResp == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is expired or does not exist.");
    }

    return ResponseEntity.ok(loginResp);
  }

  @PostMapping("/logout")
  public void Logout(Authentication auth) {

    authService.logout(auth.getCredentials().toString());
    String body = "User %s has been logged out successfully".formatted(auth.getName());
  }

  @GetMapping("/validate")
  public ResponseEntity<String> validate(Authentication auth) {

    return ResponseEntity.ok("The user %s is authenticated".formatted(auth.getName()));
    // to be designed.
  }

}
