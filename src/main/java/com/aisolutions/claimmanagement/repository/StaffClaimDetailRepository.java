package com.aisolutions.claimmanagement.repository;

import com.aisolutions.claimmanagement.entity.StaffClaimDetail;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
@WithSession
public class StaffClaimDetailRepository implements PanacheRepositoryBase<StaffClaimDetail, Long> {

    public Uni<List<StaffClaimDetail>> findByStaffAndDateRange(
            String staffId, LocalDateTime from, LocalDateTime to) {
        return getSession().flatMap(session ->
            session.createQuery(
                "FROM StaffClaimDetail WHERE staffId = :staffId " +
                "AND claimDate >= :from AND claimDate < :to " +
                "ORDER BY claimDate DESC",
                StaffClaimDetail.class)
                .setParameter("staffId", staffId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList()
        );
    }

    public Uni<List<StaffClaimDetail>> findByStaff(String staffId) {
        return getSession().flatMap(session ->
            session.createQuery(
                "FROM StaffClaimDetail WHERE staffId = :staffId ORDER BY claimDate DESC",
                StaffClaimDetail.class)
                .setParameter("staffId", staffId)
                .getResultList()
        );
    }

    public Uni<StaffClaimDetail> save(StaffClaimDetail entity) {
        return getSession().flatMap(session ->
            session.persist(entity).replaceWith(entity));
    }

    public Uni<StaffClaimDetail> update(StaffClaimDetail entity) {
        return getSession().flatMap(session -> session.merge(entity));
    }
}
