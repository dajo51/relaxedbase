package org.relaxedbase.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.relaxedbase.web.rest.equalsVerifier

class SickLeaveTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(SickLeave::class)
        val sickLeave1 = SickLeave()
        sickLeave1.id = 1L
        val sickLeave2 = SickLeave()
        sickLeave2.id = sickLeave1.id
        assertThat(sickLeave1).isEqualTo(sickLeave2)
        sickLeave2.id = 2L
        assertThat(sickLeave1).isNotEqualTo(sickLeave2)
        sickLeave1.id = null
        assertThat(sickLeave1).isNotEqualTo(sickLeave2)
    }
}
