package com.ensi.elearning.services;

import com.ensi.elearning.models.ConfirmationToken;
import com.ensi.elearning.models.User;
import com.ensi.elearning.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final String USER_NOT_FOUND = "cannot find user with email %s";
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));
    }

    public String signUp(User user) {
        Boolean userExists = userRepository.findByEmail(user.getEmail())
                .isPresent();

        if(userExists){
            throw new IllegalStateException("email already exists!");
        }
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        //TOKEN Confirmation
        String token = UUID.randomUUID().toString();
        ConfirmationToken conformationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10),
                user

        );
        confirmationTokenService.saveConfirmationToken(
                conformationToken
        );

        //JMS

        return token;
    }

    public int enableUser(String email) {
        return userRepository.enableUser(email);
    }
}
