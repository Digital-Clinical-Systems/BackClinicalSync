package com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.hashing.bcrypt;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services.IHashingService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * BCrypt adapter for the hashing port. Swapping the algorithm means replacing this
 * class only: nothing in the domain or the application layer names BCrypt.
 */
@Service
public class BCryptHashingService implements IHashingService {

    private final PasswordEncoder passwordEncoder;

    public BCryptHashingService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
