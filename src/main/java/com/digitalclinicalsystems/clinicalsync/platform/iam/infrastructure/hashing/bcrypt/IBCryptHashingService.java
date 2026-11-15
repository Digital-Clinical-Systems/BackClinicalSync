package com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.hashing.bcrypt;

import com.digitalclinicalsystems.clinicalsync.platform.iam.application.internal.outboundservices.hashing.IHashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface IBCryptHashingService extends IHashingService, PasswordEncoder {
}
