package com.digitalclinicalsystems.clinicalsync.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

/**
 * Bean names are fully qualified so every bounded context can keep the same
 * class names (for example {@code ExternalIamService}) without collisions.
 */
@SpringBootApplication(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
public class ClinicalSyncPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicalSyncPlatformApplication.class, args);
    }

}
