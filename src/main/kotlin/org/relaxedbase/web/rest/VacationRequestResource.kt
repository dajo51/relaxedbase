package org.relaxedbase.web.rest

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.PaginationUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import org.relaxedbase.domain.VacationRequest
import org.relaxedbase.repository.VacationRequestRepository
import org.relaxedbase.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

private const val ENTITY_NAME = "vacationRequest"
/**
 * REST controller for managing [org.relaxedbase.domain.VacationRequest].
 */
@RestController
@RequestMapping("/api")
@Transactional
class VacationRequestResource(
    private val vacationRequestRepository: VacationRequestRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /vacation-requests` : Create a new vacationRequest.
     *
     * @param vacationRequest the vacationRequest to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new vacationRequest, or with status `400 (Bad Request)` if the vacationRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/vacation-requests")
    fun createVacationRequest(@RequestBody vacationRequest: VacationRequest): ResponseEntity<VacationRequest> {
        log.debug("REST request to save VacationRequest : $vacationRequest")
        if (vacationRequest.id != null) {
            throw BadRequestAlertException(
                "A new vacationRequest cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = vacationRequestRepository.save(vacationRequest)
        return ResponseEntity.created(URI("/api/vacation-requests/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /vacation-requests` : Updates an existing vacationRequest.
     *
     * @param vacationRequest the vacationRequest to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated vacationRequest,
     * or with status `400 (Bad Request)` if the vacationRequest is not valid,
     * or with status `500 (Internal Server Error)` if the vacationRequest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/vacation-requests")
    fun updateVacationRequest(@RequestBody vacationRequest: VacationRequest): ResponseEntity<VacationRequest> {
        log.debug("REST request to update VacationRequest : $vacationRequest")
        if (vacationRequest.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = vacationRequestRepository.save(vacationRequest)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     vacationRequest.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /vacation-requests` : get all the vacationRequests.
     *
     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of vacationRequests in body.
     */
    @GetMapping("/vacation-requests")
    fun getAllVacationRequests(pageable: Pageable): ResponseEntity<List<VacationRequest>> {
        log.debug("REST request to get a page of VacationRequests")
        val page = vacationRequestRepository.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /vacation-requests/:id` : get the "id" vacationRequest.
     *
     * @param id the id of the vacationRequest to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the vacationRequest, or with status `404 (Not Found)`.
     */
    @GetMapping("/vacation-requests/{id}")
    fun getVacationRequest(@PathVariable id: Long): ResponseEntity<VacationRequest> {
        log.debug("REST request to get VacationRequest : $id")
        val vacationRequest = vacationRequestRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(vacationRequest)
    }
    /**
     *  `DELETE  /vacation-requests/:id` : delete the "id" vacationRequest.
     *
     * @param id the id of the vacationRequest to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/vacation-requests/{id}")
    fun deleteVacationRequest(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete VacationRequest : $id")

        vacationRequestRepository.deleteById(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }
}
