package com.memorylane.memorylane.service;

import com.memorylane.memorylane.model.User;
import com.memorylane.memorylane.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    public User register(String username, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Bu email zaten kullanılıyor");
        }
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Bu username zaten kullanılıyor");
        }

        String code = String.format("%06d", new Random().nextInt(999999));

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setVerified(false);
        user.setVerificationCode(code);
        user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(10));

        userRepository.save(user);
        emailService.sendVerificationCode(email, code);

        return user;
    }

    public String login(String emailOrUsername, String password) {
        User user;

        if (emailOrUsername.contains("@")) {
            user = userRepository.findByEmail(emailOrUsername)
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        } else {
            user = userRepository.findByUsername(emailOrUsername)
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Şifre yanlış");
        }

        if (!user.getVerified()) {
            throw new RuntimeException("EMAIL_NOT_VERIFIED");
        }

        return jwtService.generateToken(user.getUsername());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
    }

    public User updateProfile(String username, String firstName, String lastName, String bio, String profileImageUrl, String location, String website, String favoriteDestination) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (bio != null) user.setBio(bio);
        if (profileImageUrl != null) user.setProfileImageUrl(profileImageUrl);
        if (location != null) user.setLocation(location);
        if (website != null) user.setWebsite(website);
        if (favoriteDestination != null) user.setFavoriteDestination(favoriteDestination);

        return userRepository.save(user);
    }

    public User getProfile(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
    }

    public void sendResetCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Bu e-posta ile kayıtlı hesap bulunamadı"));

        String code = String.format("%06d", new Random().nextInt(999999));
        user.setResetPasswordCode(code);
        user.setResetPasswordCodeExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        emailService.sendResetCode(email, code);
    }

    public void resetPassword(String email, String code, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        if (user.getResetPasswordCode() == null || !user.getResetPasswordCode().equals(code)) {
            throw new RuntimeException("Geçersiz kod");
        }

        if (user.getResetPasswordCodeExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Kodun süresi dolmuş");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetPasswordCode(null);
        user.setResetPasswordCodeExpiry(null);
        userRepository.save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
    public List<User> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }
}