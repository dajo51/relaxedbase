package org.relaxedbase.repository

import org.relaxedbase.domain.SickLeave
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [SickLeave] entity.
 */
@Suppress("unused")
@Repository
interface SickLeaveRepository : JpaRepository<SickLeave, Long>
