package com.travelagent.authservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoDto {
        private String email;
        private String password;
        private String[] roles;

        public UserInfoDto() {
         roles = new String[50];
        }

        @JsonIgnore
        public Boolean isValid()
        {
            return email!=null&& !email.isBlank() && 
                password!=null&& !password.isBlank();
        }
}
