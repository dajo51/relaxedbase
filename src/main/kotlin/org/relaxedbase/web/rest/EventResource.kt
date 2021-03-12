package org.relaxedbase.web.rest

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.PaginationUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import org.relaxedbase.domain.Event
import org.relaxedbase.repository.EventRepository
import org.relaxedbase.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

private const val ENTITY_NAME = "event"
/**
 * REST controller for managing [org.relaxedbase.domain.Event].
 */
@RestController
@RequestMapping("/api")
@Transactional
class EventResource(
    private val eventRepository: EventRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /events` : Create a new event.
     *
     * @param event the event to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new event, or with status `400 (Bad Request)` if the event has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/events")
    fun createEvent(@RequestBody event: Event): ResponseEntity<Event> {
        log.debug("REST request to save Event : $event")
        if (event.id != null) {
            throw BadRequestAlertException(
                "A new event cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = eventRepository.save(event)
        return ResponseEntity.created(URI("/api/events/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /events` : Updates an existing event.
     *
     * @param event the event to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated event,
     * or with status `400 (Bad Request)` if the event is not valid,
     * or with status `500 (Internal Server Error)` if the event couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/events")
    fun updateEvent(@RequestBody event: Event): ResponseEntity<Event> {
        log.debug("REST request to update Event : $event")
        if (event.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = eventRepository.save(event)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     event.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /events` : get all the events.
     *
     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of events in body.
     */
    @GetMapping("/events")
    fun getAllEvents(pageable: Pageable): ResponseEntity<List<Event>> {
        log.debug("REST request to get a page of Events")
        val page = eventRepository.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /events/:id` : get the "id" event.
     *
     * @param id the id of the event to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the event, or with status `404 (Not Found)`.
     */
    @GetMapping("/events/{id}")
    fun getEvent(@PathVariable id: Long): ResponseEntity<Event> {
        log.debug("REST request to get Event : $id")
        val event = eventRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(event)
    }
    /**
     *  `DELETE  /events/:id` : delete the "id" event.
     *
     * @param id the id of the event to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/events/{id}")
    fun deleteEvent(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Event : $id")

        eventRepository.deleteById(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }
}
