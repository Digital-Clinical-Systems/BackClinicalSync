package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.application.internal.queryservices;

import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.aggregates.MedicalOrder;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.queries.GetMedicalOrderByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.queries.GetMedicalOrdersQuery;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.repositories.IMedicalOrderRepository;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.services.IMedicalOrderQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalOrderQueryServiceImpl implements IMedicalOrderQueryService {

    private final IMedicalOrderRepository medicalOrderRepository;

    public MedicalOrderQueryServiceImpl(IMedicalOrderRepository medicalOrderRepository) {
        this.medicalOrderRepository = medicalOrderRepository;
    }

    @Override
    public List<MedicalOrder> handle(GetMedicalOrdersQuery query) {
        var patientId = query.patientId() == null ? null : new PatientId(query.patientId());
        var orders = medicalOrderRepository.findAllByFilters(patientId, query.status());
        if (query.pending() == null) {
            return orders;
        }
        return orders.stream().filter(order -> order.isPending() == query.pending()).toList();
    }

    @Override
    public Optional<MedicalOrder> handle(GetMedicalOrderByIdQuery query) {
        return medicalOrderRepository.findById(query.orderId());
    }
}
