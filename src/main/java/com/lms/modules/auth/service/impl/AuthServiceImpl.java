package com.lms.modules.auth.service.impl;

import com.lms.modules.auth.dto.AuthResponse;
import com.lms.modules.auth.dto.LoginRequest;
import com.lms.modules.auth.dto.RegisterRequest;
import com.lms.modules.auth.entity.Role;
import com.lms.modules.auth.entity.User;
import com.lms.modules.auth.enums.UserStatus;
import com.lms.modules.auth.repository.RoleRepository;
import com.lms.modules.auth.repository.UserRepository;
import com.lms.modules.auth.security.CustomUserDetails;
import com.lms.modules.auth.security.JwtTokenProvider;
import com.lms.modules.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private String generateToken(User user){
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        return jwtTokenProvider.generateAccessToken(auth);
    }
    private AuthResponse buildAuthResponse(User user, String token){
        return AuthResponse.builder().accessToken(token).email(user.getEmail()).fullName(user.getFullName()).build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request){
        boolean existsByEmail = userRepository.existsByEmail(request.getEmail());
        if(existsByEmail){
            throw new IllegalArgumentException("This email is already used!");
        }
        Role studentRole = roleRepository.findByName("STUDENT").orElseThrow(() -> new IllegalStateException("No STUDENT role found in system!"));
        User newUser = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .status(UserStatus.ACTIVE)
                .roles(Set.of(studentRole))
                .build();
        User savedUser = userRepository.save(newUser);
        String token = generateToken(savedUser);
        return buildAuthResponse(savedUser, token);
    }
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("Invalid email or password!"));
        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())){
            throw new BadCredentialsException("Invalid email or password!");
        }
        if(user.getStatus() != UserStatus.ACTIVE){
            throw new RuntimeException("Account is not active!");
        }
        String token = generateToken(user);
        return buildAuthResponse(user, token);
    }
}
