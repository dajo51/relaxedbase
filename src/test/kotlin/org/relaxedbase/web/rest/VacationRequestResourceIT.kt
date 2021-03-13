package org.relaxedbase.web.rest

import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import javax.persistence.EntityManager
import kotlin.test.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.relaxedbase.RelaxedbaseApp
import org.relaxedbase.domain.VacationRequest
import org.relaxedbase.repository.VacationRequestRepository
import org.relaxedbase.service.UserService
import org.relaxedbase.web.rest.errors.ExceptionTranslator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator

/**
 * Integration tests for the [VacationRequestResource] REST controller.
 *
 * @see VacationRequestResource
 */
@SpringBootTest(classes = [RelaxedbaseApp::class])
@AutoConfigureMockMvc
@WithMockUser
class VacationRequestResourceIT {

    @Autowired
    private lateinit var vacationRequestRepository: VacationRequestRepository

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    private lateinit var restVacationRequestMockMvc: MockMvc

    private lateinit var vacationRequest: VacationRequest

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val vacationRequestResource = VacationRequestResource(vacationRequestRepository, userService)
         this.restVacationRequestMockMvc = MockMvcBuilders.standaloneSetup(vacationRequestResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        vacationRequest = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createVacationRequest() {
        val databaseSizeBeforeCreate = vacationRequestRepository.findAll().size

        // Create the VacationRequest
        restVacationRequestMockMvc.perform(
            post("/api/vacation-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(vacationRequest))
        ).andExpect(status().isCreated)

        // Validate the VacationRequest in the database
        val vacationRequestList = vacationRequestRepository.findAll()
        assertThat(vacationRequestList).hasSize(databaseSizeBeforeCreate + 1)
        val testVacationRequest = vacationRequestList[vacationRequestList.size - 1]
        assertThat(testVacationRequest.status).isEqualTo(DEFAULT_STATUS)
        assertThat(testVacationRequest.startDate).isEqualTo(DEFAULT_START_DATE)
        assertThat(testVacationRequest.endDate).isEqualTo(DEFAULT_END_DATE)
    }

    @Test
    @Transactional
    fun createVacationRequestWithExistingId() {
        val databaseSizeBeforeCreate = vacationRequestRepository.findAll().size

        // Create the VacationRequest with an existing ID
        vacationRequest.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restVacationRequestMockMvc.perform(
            post("/api/vacation-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(vacationRequest))
        ).andExpect(status().isBadRequest)

        // Validate the VacationRequest in the database
        val vacationRequestList = vacationRequestRepository.findAll()
        assertThat(vacationRequestList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getVacationRequest() {
        // Initialize the database
        vacationRequestRepository.saveAndFlush(vacationRequest)

        val id = vacationRequest.id
        assertNotNull(id)

        // Get the vacationRequest
        restVacationRequestMockMvc.perform(get("/api/vacation-requests/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(vacationRequest.id?.toInt()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.startDate").value(sameInstant(DEFAULT_START_DATE)))
            .andExpect(jsonPath("$.endDate").value(sameInstant(DEFAULT_END_DATE))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingVacationRequest() {
        // Get the vacationRequest
        restVacationRequestMockMvc.perform(get("/api/vacation-requests/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateVacationRequest() {
        // Initialize the database
        vacationRequestRepository.saveAndFlush(vacationRequest)

        val databaseSizeBeforeUpdate = vacationRequestRepository.findAll().size

        // Update the vacationRequest
        val id = vacationRequest.id
        assertNotNull(id)
        val updatedVacationRequest = vacationRequestRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedVacationRequest are not directly saved in db
        em.detach(updatedVacationRequest)
        updatedVacationRequest.status = UPDATED_STATUS
        updatedVacationRequest.startDate = UPDATED_START_DATE
        updatedVacationRequest.endDate = UPDATED_END_DATE

        restVacationRequestMockMvc.perform(
            put("/api/vacation-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedVacationRequest))
        ).andExpect(status().isOk)

        // Validate the VacationRequest in the database
        val vacationRequestList = vacationRequestRepository.findAll()
        assertThat(vacationRequestList).hasSize(databaseSizeBeforeUpdate)
        val testVacationRequest = vacationRequestList[vacationRequestList.size - 1]
        assertThat(testVacationRequest.status).isEqualTo(UPDATED_STATUS)
        assertThat(testVacationRequest.startDate).isEqualTo(UPDATED_START_DATE)
        assertThat(testVacationRequest.endDate).isEqualTo(UPDATED_END_DATE)
    }

    @Test
    @Transactional
    fun updateNonExistingVacationRequest() {
        val databaseSizeBeforeUpdate = vacationRequestRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVacationRequestMockMvc.perform(
            put("/api/vacation-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(vacationRequest))
        ).andExpect(status().isBadRequest)

        // Validate the VacationRequest in the database
        val vacationRequestList = vacationRequestRepository.findAll()
        assertThat(vacationRequestList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteVacationRequest() {
        // Initialize the database
        vacationRequestRepository.saveAndFlush(vacationRequest)

        val databaseSizeBeforeDelete = vacationRequestRepository.findAll().size

        // Delete the vacationRequest
        restVacationRequestMockMvc.perform(
            delete("/api/vacation-requests/{id}", vacationRequest.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val vacationRequestList = vacationRequestRepository.findAll()
        assertThat(vacationRequestList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_STATUS: Boolean = false
        private const val UPDATED_STATUS: Boolean = true

        private val DEFAULT_START_DATE: ZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC)
        private val UPDATED_START_DATE: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0)

        private val DEFAULT_END_DATE: ZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC)
        private val UPDATED_END_DATE: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0)

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): VacationRequest {
            val vacationRequest = VacationRequest(
                status = DEFAULT_STATUS,
                startDate = DEFAULT_START_DATE,
                endDate = DEFAULT_END_DATE
            )

            return vacationRequest
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): VacationRequest {
            val vacationRequest = VacationRequest(
                status = UPDATED_STATUS,
                startDate = UPDATED_START_DATE,
                endDate = UPDATED_END_DATE
            )

            return vacationRequest
        }
    }
}
