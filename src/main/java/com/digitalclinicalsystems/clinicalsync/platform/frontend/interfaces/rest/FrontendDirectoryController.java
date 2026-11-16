package com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest;

import com.digitalclinicalsystems.clinicalsync.platform.frontend.application.internal.outboundservices.acl.ExternalIamService;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.resources.FrontendPatientResource;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.resources.FrontendUserResource;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.transform.FrontendTranslator;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.queries.GetAllPatientsQuery;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.services.IPatientQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.patients.interfaces.rest.transform.PatientResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

/**
 * Directory endpoints of the frontend contract: professionals and patients.
 */
@RestController
@RequestMapping(value = FrontendApi.BASE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(name = FrontendApi.ENABLED_PROPERTY, havingValue = "true", matchIfMissing = true)
@Tag(name = "Frontend API", description = FrontendApi.DESCRIPTION)
public class FrontendDirectoryController {

    private final ExternalIamService externalIamService;
    private final IPatientQueryService patientQueryService;
    private final FrontendTranslator translator;

    public FrontendDirectoryController(ExternalIamService externalIamService,
                                       IPatientQueryService patientQueryService,
                                       FrontendTranslator translator) {
        this.externalIamService = externalIamService;
        this.patientQueryService = patientQueryService;
        this.translator = translator;
    }

    @GetMapping("/users")
    @Operation(summary = "Professionals of the service, identified by username")
    public ResponseEntity<List<FrontendUserResource>> getUsers() {
        var context = translator.context();
        var users = context.directory().byId().values().stream()
                .sorted(Comparator.comparing(ExternalIamService.Professional::username))
                .map(context::toResource)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/patients")
    @Operation(summary = "Patients of the unit (active and discharged)")
    public ResponseEntity<List<FrontendPatientResource>> getPatients() {
        var context = translator.context();
        var patients = patientQueryService.handle(new GetAllPatientsQuery(null)).stream()
                .map(PatientResourceFromEntityAssembler::toResourceFromEntity)
                .map(context::toResource)
                .toList();
        return ResponseEntity.ok(patients);
    }

    /**
     * The frontend sends only the id and the name of a patient, which is not
     * enough to admit one (medical record number and bed are mandatory). The
     * request is acknowledged without changes; patients are admitted through
     * {@code POST /api/v1/patients}.
     */
    @PostMapping(value = "/patients", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Accepted without changes", description = "Admissions go through POST /api/v1/patients.")
    public ResponseEntity<FrontendPatientResource> postPatient(@RequestBody FrontendPatientResource resource) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(resource);
    }
}
