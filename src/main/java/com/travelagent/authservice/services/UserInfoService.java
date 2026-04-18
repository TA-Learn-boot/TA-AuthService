package com.travelagent.authservice.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.travelagent.authservice.dto.UserInfoDto;
import com.travelagent.authservice.entity.UserInfoEntity;
import com.travelagent.authservice.repositories.UserInfoRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserInfoService {

    @Autowired
    private PasswordEncoder PasswordEncoder;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private com.travelagent.authservice.Utils.UserMapper userMapper;

    public void createUser(UserInfoDto user) {
        user.setPassword(PasswordEncoder.encode(user.getPassword()));
        var userEntity = userMapper.toEntity(user);
        if (userEntity != null) {
            userInfoRepository.save(userEntity);
        }
    }

    public UserInfoDto getUser(String email) {
        if(email == null || email.isEmpty())
        {
            return null;
        }
        var entity = userInfoRepository.findOptionalByEmail(email);
        if (entity.isPresent())
            return userMapper.toDto(entity.get());
        else
            return null;
    }

    public boolean deleteUser(String email) {
        var deletedUser = userInfoRepository.deleteOptionalbyEmail(email);
        return deletedUser.isPresent();
    }

    public boolean forgotPassword(UserInfoDto user) {
        try {
            Optional<UserInfoEntity> entity = userInfoRepository.findOptionalByEmail(user.getEmail());
            entity.ifPresent(u -> {
                u.setPassword(user.getPassword());
                userInfoRepository.save(u);
            });
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
