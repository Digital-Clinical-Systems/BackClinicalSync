package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services;

/**
 * Port for password hashing. The domain states that passwords are stored hashed
 * and compared without being read; which algorithm does it belongs to
 * infrastructure, so it can be replaced without touching this context.
 */
public interface IHashingService {
    String encode(CharSequence rawPassword);
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
