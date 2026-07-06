package com.memorylane.memorylane.service;


import com.memorylane.memorylane.model.User;
import com.memorylane.memorylane.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public User register(String username, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Bu email zaten kullanılıyor");
        }
        if(userRepository.existsByUsername(username)) {
            throw new RuntimeException("Bu username zaten kullanılıyor");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        if(!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Şifre yanlış");
        }

        return jwtService.generateToken(user.getUsername());
    }
}
