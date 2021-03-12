package org.relaxedbase.web.rest

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.PaginationUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import org.relaxedbase.domain.SickLeave
import org.relaxedbase.repository.SickLeaveRepository
import org.relaxedbase.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

private const val ENTITY_NAME = "sickLeave"
/**
 * REST controller for managing [org.relaxedbase.domain.SickLeave].
 */
@RestController
@RequestMapping("/api")
@Transactional
class SickLeaveResource(
    private val sickLeaveRepository: SickLeaveRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /sick-leaves` : Create a new sickLeave.
     *
     * @param sickLeave the sickLeave to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new sickLeave, or with status `400 (Bad Request)` if the sickLeave has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sick-leaves")
    fun createSickLeave(@RequestBody sickLeave: SickLeave): ResponseEntity<SickLeave> {
        log.debug("REST request to save SickLeave : $sickLeave")
        if (sickLeave.id != null) {
            throw BadRequestAlertException(
                "A new sickLeave cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = sickLeaveRepository.save(sickLeave)
        return ResponseEntity.created(URI("/api/sick-leaves/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /sick-leaves` : Updates an existing sickLeave.
     *
     * @param sickLeave the sickLeave to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated sickLeave,
     * or with status `400 (Bad Request)` if the sickLeave is not valid,
     * or with status `500 (Internal Server Error)` if the sickLeave couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sick-leaves")
    fun updateSickLeave(@RequestBody sickLeave: SickLeave): ResponseEntity<SickLeave> {
        log.debug("REST request to update SickLeave : $sickLeave")
        if (sickLeave.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = sickLeaveRepository.save(sickLeave)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     sickLeave.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /sick-leaves` : get all the sickLeaves.
     *
     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of sickLeaves in body.
     */
    @GetMapping("/sick-leaves")
    fun getAllSickLeaves(pageable: Pageable): ResponseEntity<List<SickLeave>> {
        log.debug("REST request to get a page of SickLeaves")
        val page = sickLeaveRepository.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /sick-leaves/:id` : get the "id" sickLeave.
     *
     * @param id the id of the sickLeave to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the sickLeave, or with status `404 (Not Found)`.
     */
    @GetMapping("/sick-leaves/{id}")
    fun getSickLeave(@PathVariable id: Long): ResponseEntity<SickLeave> {
        log.debug("REST request to get SickLeave : $id")
        val sickLeave = sickLeaveRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(sickLeave)
    }
    /**
     *  `DELETE  /sick-leaves/:id` : delete the "id" sickLeave.
     *
     * @param id the id of the sickLeave to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/sick-leaves/{id}")
    fun deleteSickLeave(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete SickLeave : $id")

        sickLeaveRepository.deleteById(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }
}
