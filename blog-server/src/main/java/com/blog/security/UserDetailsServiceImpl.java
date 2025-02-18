package com.blog.security;

import com.blog.common.response.ResultCode;
import com.blog.exception.Asserts;
import com.blog.model.User;
import com.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.selectByUsername(username);
        if (user == null) {
            Asserts.fail(ResultCode.USER_NOT_FOUND);
        }
        return new UserDetailsImpl(user);
    }
} 