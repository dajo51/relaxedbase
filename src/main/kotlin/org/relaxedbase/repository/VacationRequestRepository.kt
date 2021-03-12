package org.relaxedbase.repository

import org.relaxedbase.domain.VacationRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [VacationRequest] entity.
 */
@Suppress("unused")
@Repository
interface VacationRequestRepository : JpaRepository<VacationRequest, Long>
