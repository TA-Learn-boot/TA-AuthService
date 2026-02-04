package com.travelagent.authservice.securityConfigs;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelagent.authservice.Utils.JwtUtil;
import com.travelagent.authservice.dto.UserInfoDto;
import com.travelagent.authservice.services.BlacklistService;
import com.travelagent.authservice.services.UserInfoService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final String AUTH_HEADER_KEY = "Authorization";

    @Autowired
    private UserInfoService userInfoSvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BlacklistService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(AUTH_HEADER_KEY);
        if (authHeader == null
                || authHeader.isBlank()
                || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        } else {
            try {
                String jwt = authHeader.substring(7);
                Claims claim = jwtUtil.getClaim(jwt);
                String emailId = claim.getSubject();
                String jti = claim.getId();
                if (blacklistService.isBlackListed(jti)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"User is logged out. Please sign-in again\"}");
                    response.getWriter().flush();
                    return;
                }

                UserInfoDto userInfo = userInfoSvc.getUser(emailId);

                UserDetails user = User.builder()
                        .roles(userInfo.getRoles())
                        .username(userInfo.getEmail())
                        .password(userInfo.getPassword()).build();

                UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(user, jwt,
                        user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(userToken);
                response.setHeader("userName", userInfo.getEmail());
                response.setHeader("roles", new ObjectMapper().writeValueAsString(userInfo.getRoles()));
                filterChain.doFilter(request, response);

            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"" + ex.getMessage() + "\"}");
                response.getWriter().flush();
                return;
            }
        }

    }

}
