package com.library.bookmarker.service;

import com.library.bookmarker.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public void deleteUser(Long id) throws IOException {
        userMapper.deleteUser(id);
    }
}
