package org.relaxedbase.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.relaxedbase.web.rest.equalsVerifier

class VacationRequestTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(VacationRequest::class)
        val vacationRequest1 = VacationRequest()
        vacationRequest1.id = 1L
        val vacationRequest2 = VacationRequest()
        vacationRequest2.id = vacationRequest1.id
        assertThat(vacationRequest1).isEqualTo(vacationRequest2)
        vacationRequest2.id = 2L
        assertThat(vacationRequest1).isNotEqualTo(vacationRequest2)
        vacationRequest1.id = null
        assertThat(vacationRequest1).isNotEqualTo(vacationRequest2)
    }
}
