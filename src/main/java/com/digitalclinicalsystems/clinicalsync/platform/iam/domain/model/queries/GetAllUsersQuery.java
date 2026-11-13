package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.queries;

/** Service directory, optionally narrowed to one role. A null role returns every user. */
public record GetAllUsersQuery(String role) {
}
