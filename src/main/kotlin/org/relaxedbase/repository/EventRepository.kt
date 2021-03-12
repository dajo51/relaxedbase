package org.relaxedbase.repository

import org.relaxedbase.domain.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Event] entity.
 */
@Suppress("unused")
@Repository
interface EventRepository : JpaRepository<Event, Long>
