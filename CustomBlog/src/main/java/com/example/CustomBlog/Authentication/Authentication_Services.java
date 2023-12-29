package com.example.CustomBlog.Authentication;

import com.example.CustomBlog.JWT.JwtServices;
import com.example.CustomBlog.Mail_Services;
import com.example.CustomBlog.user.Role;
import com.example.CustomBlog.user.User;
import com.example.CustomBlog.user.User_Repository;
import com.example.CustomBlog.user.User_Services;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class Authentication_Services {
    private final User_Repository userRepository;
    private final JwtServices jwtServices;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final Mail_Services mailServices;
    private final User_Services userServices;

    public Authentication_Services(User_Repository userRepository, JwtServices jwtServices, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, Mail_Services mailServices, User_Services userServices) {
        this.userRepository = userRepository;
        this.jwtServices = jwtServices;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.mailServices = mailServices;
        this.userServices = userServices;
    }

    public AuthenticationResponse register(RegisterRequest registerRequest) throws Exception{
        User user=new User(
                registerRequest.getDisplayName(),
                registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                Role.USER,
                registerRequest.getEmail());
        userRepository.save(user);
        String JWT=jwtServices.generateToken(user);
        mailServices.sendWelcomeEmail(user);
        return new AuthenticationResponse(JWT);
    }

    public AuthenticationResponse adminRegister(RegisterRequest registerRequest) throws Exception
    {
        User user=new User(
                registerRequest.getDisplayName(),
                registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                Role.ADMIN,
                registerRequest.getEmail());
        userRepository.save(user);
        String JWT=jwtServices.generateToken(user);
        mailServices.sendWelcomeEmail(user);
        return new AuthenticationResponse(JWT);
    }

    public AuthenticationResponse changeUserPassword(ChangePasswordRequest changePasswordRequest) throws Exception
    {
        String jwt=authenticate(changePasswordRequest.getAuthenticationRequest()).getToken();
        User user=userRepository
                .findByUsername(jwtServices.extractUsername(jwt))
                .orElseThrow(
                        ()->new IllegalStateException("Unexpected Error!")
                );
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
        mailServices.notifyPasswordChanged(user);
        return new AuthenticationResponse(jwtServices.generateToken(user));
    }
    public void resetForgottenPassword(ChangePasswordRequest changePasswordRequest) throws Exception
    {
        User user=userServices.GetUserByEmail(changePasswordRequest.getEmail());
        if(
                user.getResetPassCode()!=null
                &&
                user.getResetPassCode().equals(changePasswordRequest.getResetPasscode()))
        {
            user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            userServices.nullUserResetPassCode(user.getId());
            userRepository.save(user);
            mailServices.notifyPasswordChanged(user);
        }
        else throw new IllegalStateException(
                "The provided code is not correct or user did not request to reset password !"
        );
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
    }

    public String forgotPasswordRequest(User user) throws Exception
    {
        String resetPasscode=generateRandomString(8);
        userServices.setUserResetPassCode(resetPasscode,user.getId());
        mailServices.sendForgotPasswordEmail(userServices.GetUserByID(user.getId()));
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            userServices.nullUserResetPassCode(user.getId());
        };

        executor.schedule(task, 5, TimeUnit.MINUTES);
        executor.shutdown();
        return  resetPasscode;
    }

    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            stringBuilder.append(characters.charAt(index));
        }

        return stringBuilder.toString();
    }
}
