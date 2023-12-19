package com.example.CustomBlog.Authentication;

import com.example.CustomBlog.JWT.JwtServices;
import com.example.CustomBlog.user.Role;
import com.example.CustomBlog.user.User;
import com.example.CustomBlog.user.User_Repository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class Authentication_Services {
    private final User_Repository userRepository;
    private final JwtServices jwtServices;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public Authentication_Services(User_Repository userRepository, JwtServices jwtServices, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtServices = jwtServices;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        User user=new User(
                registerRequest.getDisplayName(),
                registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                Role.USER,
                registerRequest.getEmail());
        userRepository.save(user);
        String JWT=jwtServices.generateToken(user);
        return new AuthenticationResponse(JWT);
    }

    public AuthenticationResponse adminRegister(RegisterRequest registerRequest)
    {
        User user=new User(
                registerRequest.getDisplayName(),
                registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                Role.ADMIN,
                registerRequest.getEmail());
        userRepository.save(user);
        String JWT=jwtServices.generateToken(user);
        return new AuthenticationResponse(JWT);
    }

    public AuthenticationResponse changeUserPassword(ChangePasswordRequest changePasswordRequest)
    {
        String jwt=authenticate(changePasswordRequest.getAuthenticationRequest()).getToken();
        User user=userRepository
                .findByUsername(jwtServices.extractUsername(jwt))
                .orElseThrow(
                        ()->new IllegalStateException("Unexpected Error!")
                );
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
        return new AuthenticationResponse(jwtServices.generateToken(user));
    }
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        User user=userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(
                        ()-> new IllegalStateException(
                                "Wrong Username or Password !"
                        )
                );
        String JWT=jwtServices.generateToken(user);
        return new AuthenticationResponse(JWT);
    };
}
