package org.relaxedbase.web.rest

import javax.persistence.EntityManager
import kotlin.test.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.relaxedbase.RelaxedbaseApp
import org.relaxedbase.domain.Employee
import org.relaxedbase.repository.EmployeeRepository
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
 * Integration tests for the [EmployeeResource] REST controller.
 *
 * @see EmployeeResource
 */
@SpringBootTest(classes = [RelaxedbaseApp::class])
@AutoConfigureMockMvc
@WithMockUser
class EmployeeResourceIT {

    @Autowired
    private lateinit var employeeRepository: EmployeeRepository

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

    private lateinit var restEmployeeMockMvc: MockMvc

    private lateinit var employee: Employee

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val employeeResource = EmployeeResource(employeeRepository)
         this.restEmployeeMockMvc = MockMvcBuilders.standaloneSetup(employeeResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        employee = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createEmployee() {
        val databaseSizeBeforeCreate = employeeRepository.findAll().size

        // Create the Employee
        restEmployeeMockMvc.perform(
            post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(employee))
        ).andExpect(status().isCreated)

        // Validate the Employee in the database
        val employeeList = employeeRepository.findAll()
        assertThat(employeeList).hasSize(databaseSizeBeforeCreate + 1)
        val testEmployee = employeeList[employeeList.size - 1]
        assertThat(testEmployee.firstName).isEqualTo(DEFAULT_FIRST_NAME)
        assertThat(testEmployee.lastName).isEqualTo(DEFAULT_LAST_NAME)
        assertThat(testEmployee.email).isEqualTo(DEFAULT_EMAIL)
        assertThat(testEmployee.phoneNumber).isEqualTo(DEFAULT_PHONE_NUMBER)
        assertThat(testEmployee.team).isEqualTo(DEFAULT_TEAM)
    }

    @Test
    @Transactional
    fun createEmployeeWithExistingId() {
        val databaseSizeBeforeCreate = employeeRepository.findAll().size

        // Create the Employee with an existing ID
        employee.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmployeeMockMvc.perform(
            post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(employee))
        ).andExpect(status().isBadRequest)

        // Validate the Employee in the database
        val employeeList = employeeRepository.findAll()
        assertThat(employeeList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllEmployees() {
        // Initialize the database
        employeeRepository.saveAndFlush(employee)

        // Get all the employeeList
        restEmployeeMockMvc.perform(get("/api/employees?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employee.id?.toInt())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].team").value(hasItem(DEFAULT_TEAM))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getEmployee() {
        // Initialize the database
        employeeRepository.saveAndFlush(employee)

        val id = employee.id
        assertNotNull(id)

        // Get the employee
        restEmployeeMockMvc.perform(get("/api/employees/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(employee.id?.toInt()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.team").value(DEFAULT_TEAM)) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingEmployee() {
        // Get the employee
        restEmployeeMockMvc.perform(get("/api/employees/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateEmployee() {
        // Initialize the database
        employeeRepository.saveAndFlush(employee)

        val databaseSizeBeforeUpdate = employeeRepository.findAll().size

        // Update the employee
        val id = employee.id
        assertNotNull(id)
        val updatedEmployee = employeeRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedEmployee are not directly saved in db
        em.detach(updatedEmployee)
        updatedEmployee.firstName = UPDATED_FIRST_NAME
        updatedEmployee.lastName = UPDATED_LAST_NAME
        updatedEmployee.email = UPDATED_EMAIL
        updatedEmployee.phoneNumber = UPDATED_PHONE_NUMBER
        updatedEmployee.team = UPDATED_TEAM

        restEmployeeMockMvc.perform(
            put("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedEmployee))
        ).andExpect(status().isOk)

        // Validate the Employee in the database
        val employeeList = employeeRepository.findAll()
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate)
        val testEmployee = employeeList[employeeList.size - 1]
        assertThat(testEmployee.firstName).isEqualTo(UPDATED_FIRST_NAME)
        assertThat(testEmployee.lastName).isEqualTo(UPDATED_LAST_NAME)
        assertThat(testEmployee.email).isEqualTo(UPDATED_EMAIL)
        assertThat(testEmployee.phoneNumber).isEqualTo(UPDATED_PHONE_NUMBER)
        assertThat(testEmployee.team).isEqualTo(UPDATED_TEAM)
    }

    @Test
    @Transactional
    fun updateNonExistingEmployee() {
        val databaseSizeBeforeUpdate = employeeRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeeMockMvc.perform(
            put("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(employee))
        ).andExpect(status().isBadRequest)

        // Validate the Employee in the database
        val employeeList = employeeRepository.findAll()
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteEmployee() {
        // Initialize the database
        employeeRepository.saveAndFlush(employee)

        val databaseSizeBeforeDelete = employeeRepository.findAll().size

        // Delete the employee
        restEmployeeMockMvc.perform(
            delete("/api/employees/{id}", employee.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val employeeList = employeeRepository.findAll()
        assertThat(employeeList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_FIRST_NAME = "AAAAAAAAAA"
        private const val UPDATED_FIRST_NAME = "BBBBBBBBBB"

        private const val DEFAULT_LAST_NAME = "AAAAAAAAAA"
        private const val UPDATED_LAST_NAME = "BBBBBBBBBB"

        private const val DEFAULT_EMAIL = "AAAAAAAAAA"
        private const val UPDATED_EMAIL = "BBBBBBBBBB"

        private const val DEFAULT_PHONE_NUMBER = "AAAAAAAAAA"
        private const val UPDATED_PHONE_NUMBER = "BBBBBBBBBB"

        private const val DEFAULT_TEAM = "AAAAAAAAAA"
        private const val UPDATED_TEAM = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Employee {
            val employee = Employee(
                firstName = DEFAULT_FIRST_NAME,
                lastName = DEFAULT_LAST_NAME,
                email = DEFAULT_EMAIL,
                phoneNumber = DEFAULT_PHONE_NUMBER,
                team = DEFAULT_TEAM
            )

            return employee
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Employee {
            val employee = Employee(
                firstName = UPDATED_FIRST_NAME,
                lastName = UPDATED_LAST_NAME,
                email = UPDATED_EMAIL,
                phoneNumber = UPDATED_PHONE_NUMBER,
                team = UPDATED_TEAM
            )

            return employee
        }
    }
}
