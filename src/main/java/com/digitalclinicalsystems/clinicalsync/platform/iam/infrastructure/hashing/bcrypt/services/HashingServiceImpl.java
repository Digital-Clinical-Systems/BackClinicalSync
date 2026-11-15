package com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.hashing.bcrypt.services;

import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.hashing.bcrypt.IBCryptHashingService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class HashingServiceImpl implements IBCryptHashingService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
