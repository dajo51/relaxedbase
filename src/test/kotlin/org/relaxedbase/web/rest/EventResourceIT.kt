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
import org.relaxedbase.domain.Event
import org.relaxedbase.repository.EventRepository
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
 * Integration tests for the [EventResource] REST controller.
 *
 * @see EventResource
 */
@SpringBootTest(classes = [RelaxedbaseApp::class])
@AutoConfigureMockMvc
@WithMockUser
class EventResourceIT {

    @Autowired
    private lateinit var eventRepository: EventRepository

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

    private lateinit var restEventMockMvc: MockMvc

    private lateinit var event: Event

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val eventResource = EventResource(eventRepository)
         this.restEventMockMvc = MockMvcBuilders.standaloneSetup(eventResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        event = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createEvent() {
        val databaseSizeBeforeCreate = eventRepository.findAll().size

        // Create the Event
        restEventMockMvc.perform(
            post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(event))
        ).andExpect(status().isCreated)

        // Validate the Event in the database
        val eventList = eventRepository.findAll()
        assertThat(eventList).hasSize(databaseSizeBeforeCreate + 1)
        val testEvent = eventList[eventList.size - 1]
        assertThat(testEvent.title).isEqualTo(DEFAULT_TITLE)
        assertThat(testEvent.description).isEqualTo(DEFAULT_DESCRIPTION)
        assertThat(testEvent.location).isEqualTo(DEFAULT_LOCATION)
        assertThat(testEvent.startDate).isEqualTo(DEFAULT_START_DATE)
        assertThat(testEvent.endDate).isEqualTo(DEFAULT_END_DATE)
        assertThat(testEvent.inviteOnly).isEqualTo(DEFAULT_INVITE_ONLY)
    }

    @Test
    @Transactional
    fun createEventWithExistingId() {
        val databaseSizeBeforeCreate = eventRepository.findAll().size

        // Create the Event with an existing ID
        event.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventMockMvc.perform(
            post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(event))
        ).andExpect(status().isBadRequest)

        // Validate the Event in the database
        val eventList = eventRepository.findAll()
        assertThat(eventList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllEvents() {
        // Initialize the database
        eventRepository.saveAndFlush(event)

        // Get all the eventList
        restEventMockMvc.perform(get("/api/events?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.id?.toInt())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(sameInstant(DEFAULT_START_DATE))))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(sameInstant(DEFAULT_END_DATE))))
            .andExpect(jsonPath("$.[*].inviteOnly").value(hasItem(DEFAULT_INVITE_ONLY))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getEvent() {
        // Initialize the database
        eventRepository.saveAndFlush(event)

        val id = event.id
        assertNotNull(id)

        // Get the event
        restEventMockMvc.perform(get("/api/events/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(event.id?.toInt()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION))
            .andExpect(jsonPath("$.startDate").value(sameInstant(DEFAULT_START_DATE)))
            .andExpect(jsonPath("$.endDate").value(sameInstant(DEFAULT_END_DATE)))
            .andExpect(jsonPath("$.inviteOnly").value(DEFAULT_INVITE_ONLY)) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingEvent() {
        // Get the event
        restEventMockMvc.perform(get("/api/events/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateEvent() {
        // Initialize the database
        eventRepository.saveAndFlush(event)

        val databaseSizeBeforeUpdate = eventRepository.findAll().size

        // Update the event
        val id = event.id
        assertNotNull(id)
        val updatedEvent = eventRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedEvent are not directly saved in db
        em.detach(updatedEvent)
        updatedEvent.title = UPDATED_TITLE
        updatedEvent.description = UPDATED_DESCRIPTION
        updatedEvent.location = UPDATED_LOCATION
        updatedEvent.startDate = UPDATED_START_DATE
        updatedEvent.endDate = UPDATED_END_DATE
        updatedEvent.inviteOnly = UPDATED_INVITE_ONLY

        restEventMockMvc.perform(
            put("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedEvent))
        ).andExpect(status().isOk)

        // Validate the Event in the database
        val eventList = eventRepository.findAll()
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate)
        val testEvent = eventList[eventList.size - 1]
        assertThat(testEvent.title).isEqualTo(UPDATED_TITLE)
        assertThat(testEvent.description).isEqualTo(UPDATED_DESCRIPTION)
        assertThat(testEvent.location).isEqualTo(UPDATED_LOCATION)
        assertThat(testEvent.startDate).isEqualTo(UPDATED_START_DATE)
        assertThat(testEvent.endDate).isEqualTo(UPDATED_END_DATE)
        assertThat(testEvent.inviteOnly).isEqualTo(UPDATED_INVITE_ONLY)
    }

    @Test
    @Transactional
    fun updateNonExistingEvent() {
        val databaseSizeBeforeUpdate = eventRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventMockMvc.perform(
            put("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(event))
        ).andExpect(status().isBadRequest)

        // Validate the Event in the database
        val eventList = eventRepository.findAll()
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteEvent() {
        // Initialize the database
        eventRepository.saveAndFlush(event)

        val databaseSizeBeforeDelete = eventRepository.findAll().size

        // Delete the event
        restEventMockMvc.perform(
            delete("/api/events/{id}", event.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val eventList = eventRepository.findAll()
        assertThat(eventList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_TITLE = "AAAAAAAAAA"
        private const val UPDATED_TITLE = "BBBBBBBBBB"

        private const val DEFAULT_DESCRIPTION = "AAAAAAAAAA"
        private const val UPDATED_DESCRIPTION = "BBBBBBBBBB"

        private const val DEFAULT_LOCATION = "AAAAAAAAAA"
        private const val UPDATED_LOCATION = "BBBBBBBBBB"

        private val DEFAULT_START_DATE: ZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC)
        private val UPDATED_START_DATE: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0)

        private val DEFAULT_END_DATE: ZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC)
        private val UPDATED_END_DATE: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0)

        private const val DEFAULT_INVITE_ONLY: Boolean = false
        private const val UPDATED_INVITE_ONLY: Boolean = true

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Event {
            val event = Event(
                title = DEFAULT_TITLE,
                description = DEFAULT_DESCRIPTION,
                location = DEFAULT_LOCATION,
                startDate = DEFAULT_START_DATE,
                endDate = DEFAULT_END_DATE,
                inviteOnly = DEFAULT_INVITE_ONLY
            )

            return event
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Event {
            val event = Event(
                title = UPDATED_TITLE,
                description = UPDATED_DESCRIPTION,
                location = UPDATED_LOCATION,
                startDate = UPDATED_START_DATE,
                endDate = UPDATED_END_DATE,
                inviteOnly = UPDATED_INVITE_ONLY
            )

            return event
        }
    }
}
