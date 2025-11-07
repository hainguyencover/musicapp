package com.example.musicapp.service;

import com.example.musicapp.dto.UserRegistrationDTO;

public interface IUserService {
    void registerNewUser(UserRegistrationDTO dto);
}
