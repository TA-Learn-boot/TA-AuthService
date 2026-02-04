package com.travelagent.authservice.services;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.travelagent.authservice.Utils.JwtUtil;
import com.travelagent.authservice.dto.LoginResponse;
import com.travelagent.authservice.dto.UserInfoDto;
import io.jsonwebtoken.Claims;

@Service
public class AuthService {

    @Value("${spring.application.tokenExpiryTime}")
    private long tokenExpiryTime;

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private UserDetailServiceImpl userDetailServiceImp;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    private String Bearer = "Bearer ";

    public LoginResponse ValidateAndGenerateToken(UserInfoDto userInfoDto) {
        var isCredentialValid = userDetailServiceImp.ValidateCred(userInfoDto.getEmail(), userInfoDto.getPassword());
        if (!isCredentialValid) {
            return null;
        }

        var user = userDetailServiceImp.loadUserByUsername(userInfoDto.getEmail());
        String jwt = Bearer + jwtUtil.getToken(user);
        String rt = refreshTokenService.getRefreshToken(userInfoDto.getEmail());
        Date expiryTime = new Date(System.currentTimeMillis() + tokenExpiryTime);
        return new LoginResponse(jwt, expiryTime.toString(), rt);
    }

    public LoginResponse RotateAndRefresh(String refreshToken) {
        String email = refreshTokenService.getEmailId(refreshToken);
        if (email == null || email.isBlank()) {
            return null;
        }

        var user = userDetailServiceImp.loadUserByUsername(email);
        String jwt = Bearer + jwtUtil.getToken(user);
        var newRefreshToken = refreshTokenService.getRefreshToken(email);
        Date expiryTime = new Date(System.currentTimeMillis() + tokenExpiryTime);
        return new LoginResponse(jwt, expiryTime.toString(), newRefreshToken);
    }

    public void logout(String jwt) {
        Claims claims = jwtUtil.getClaim(jwt);
        String emailId = claims.getSubject();

        BlacklistToken(claims);
        refreshTokenService.deleteAllByEmail(emailId);
    }

    private void BlacklistToken(Claims jtwClaim) {
        String jti = jtwClaim.getId();
        Date expiryTime = jtwClaim.getExpiration();
        long ttl = expiryTime.getTime() - System.currentTimeMillis();
        blacklistService.Blacklist(jti, ttl); 
    }

}
