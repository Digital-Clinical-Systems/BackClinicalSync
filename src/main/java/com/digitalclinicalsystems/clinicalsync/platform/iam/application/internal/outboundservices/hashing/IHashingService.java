package com.digitalclinicalsystems.clinicalsync.platform.iam.application.internal.outboundservices.hashing;

public interface IHashingService {
    String encode(CharSequence rawPassword);
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
