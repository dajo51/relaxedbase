package org.relaxedbase.web.rest

import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import javax.persistence.EntityManager
import kotlin.test.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.relaxedbase.RelaxedbaseApp
import org.relaxedbase.domain.SickLeave
import org.relaxedbase.repository.SickLeaveRepository
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
 * Integration tests for the [SickLeaveResource] REST controller.
 *
 * @see SickLeaveResource
 */
@SpringBootTest(classes = [RelaxedbaseApp::class])
@AutoConfigureMockMvc
@WithMockUser
class SickLeaveResourceIT {

    @Autowired
    private lateinit var sickLeaveRepository: SickLeaveRepository

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

    private lateinit var restSickLeaveMockMvc: MockMvc

    private lateinit var sickLeave: SickLeave

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val sickLeaveResource = SickLeaveResource(sickLeaveRepository)
         this.restSickLeaveMockMvc = MockMvcBuilders.standaloneSetup(sickLeaveResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        sickLeave = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createSickLeave() {
        val databaseSizeBeforeCreate = sickLeaveRepository.findAll().size

        // Create the SickLeave
        restSickLeaveMockMvc.perform(
            post("/api/sick-leaves")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(sickLeave))
        ).andExpect(status().isCreated)

        // Validate the SickLeave in the database
        val sickLeaveList = sickLeaveRepository.findAll()
        assertThat(sickLeaveList).hasSize(databaseSizeBeforeCreate + 1)
        val testSickLeave = sickLeaveList[sickLeaveList.size - 1]
        assertThat(testSickLeave.startDate).isEqualTo(DEFAULT_START_DATE)
        assertThat(testSickLeave.endDate).isEqualTo(DEFAULT_END_DATE)
    }

    @Test
    @Transactional
    fun createSickLeaveWithExistingId() {
        val databaseSizeBeforeCreate = sickLeaveRepository.findAll().size

        // Create the SickLeave with an existing ID
        sickLeave.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restSickLeaveMockMvc.perform(
            post("/api/sick-leaves")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(sickLeave))
        ).andExpect(status().isBadRequest)

        // Validate the SickLeave in the database
        val sickLeaveList = sickLeaveRepository.findAll()
        assertThat(sickLeaveList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllSickLeaves() {
        // Initialize the database
        sickLeaveRepository.saveAndFlush(sickLeave)

        // Get all the sickLeaveList
        restSickLeaveMockMvc.perform(get("/api/sick-leaves?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sickLeave.id?.toInt())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(sameInstant(DEFAULT_START_DATE))))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(sameInstant(DEFAULT_END_DATE)))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getSickLeave() {
        // Initialize the database
        sickLeaveRepository.saveAndFlush(sickLeave)

        val id = sickLeave.id
        assertNotNull(id)

        // Get the sickLeave
        restSickLeaveMockMvc.perform(get("/api/sick-leaves/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sickLeave.id?.toInt()))
            .andExpect(jsonPath("$.startDate").value(sameInstant(DEFAULT_START_DATE)))
            .andExpect(jsonPath("$.endDate").value(sameInstant(DEFAULT_END_DATE))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingSickLeave() {
        // Get the sickLeave
        restSickLeaveMockMvc.perform(get("/api/sick-leaves/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateSickLeave() {
        // Initialize the database
        sickLeaveRepository.saveAndFlush(sickLeave)

        val databaseSizeBeforeUpdate = sickLeaveRepository.findAll().size

        // Update the sickLeave
        val id = sickLeave.id
        assertNotNull(id)
        val updatedSickLeave = sickLeaveRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedSickLeave are not directly saved in db
        em.detach(updatedSickLeave)
        updatedSickLeave.startDate = UPDATED_START_DATE
        updatedSickLeave.endDate = UPDATED_END_DATE

        restSickLeaveMockMvc.perform(
            put("/api/sick-leaves")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedSickLeave))
        ).andExpect(status().isOk)

        // Validate the SickLeave in the database
        val sickLeaveList = sickLeaveRepository.findAll()
        assertThat(sickLeaveList).hasSize(databaseSizeBeforeUpdate)
        val testSickLeave = sickLeaveList[sickLeaveList.size - 1]
        assertThat(testSickLeave.startDate).isEqualTo(UPDATED_START_DATE)
        assertThat(testSickLeave.endDate).isEqualTo(UPDATED_END_DATE)
    }

    @Test
    @Transactional
    fun updateNonExistingSickLeave() {
        val databaseSizeBeforeUpdate = sickLeaveRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSickLeaveMockMvc.perform(
            put("/api/sick-leaves")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(sickLeave))
        ).andExpect(status().isBadRequest)

        // Validate the SickLeave in the database
        val sickLeaveList = sickLeaveRepository.findAll()
        assertThat(sickLeaveList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteSickLeave() {
        // Initialize the database
        sickLeaveRepository.saveAndFlush(sickLeave)

        val databaseSizeBeforeDelete = sickLeaveRepository.findAll().size

        // Delete the sickLeave
        restSickLeaveMockMvc.perform(
            delete("/api/sick-leaves/{id}", sickLeave.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val sickLeaveList = sickLeaveRepository.findAll()
        assertThat(sickLeaveList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

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
        fun createEntity(em: EntityManager): SickLeave {
            val sickLeave = SickLeave(
                startDate = DEFAULT_START_DATE,
                endDate = DEFAULT_END_DATE
            )

            return sickLeave
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): SickLeave {
            val sickLeave = SickLeave(
                startDate = UPDATED_START_DATE,
                endDate = UPDATED_END_DATE
            )

            return sickLeave
        }
    }
}
