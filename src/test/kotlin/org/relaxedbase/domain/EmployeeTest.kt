package org.relaxedbase.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.relaxedbase.web.rest.equalsVerifier

class EmployeeTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Employee::class)
        val employee1 = Employee()
        employee1.id = 1L
        val employee2 = Employee()
        employee2.id = employee1.id
        assertThat(employee1).isEqualTo(employee2)
        employee2.id = 2L
        assertThat(employee1).isNotEqualTo(employee2)
        employee1.id = null
        assertThat(employee1).isNotEqualTo(employee2)
    }
}
