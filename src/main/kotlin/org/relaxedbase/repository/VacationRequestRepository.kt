package org.relaxedbase.repository

import org.relaxedbase.domain.VacationRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [VacationRequest] entity.
 */
@Suppress("unused")
@Repository
interface VacationRequestRepository : JpaRepository<VacationRequest, Long> {

    fun findAllByOwner(var1: Pageable, owner: String): Page<VacationRequest>
}
