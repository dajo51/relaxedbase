package org.relaxedbase.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable
import java.time.ZonedDateTime
import javax.persistence.*

/**
 * A SickLeave.
 */
@Entity
@Table(name = "sick_leave")
data class SickLeave(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "start_date")
    var startDate: ZonedDateTime? = null,

    @Column(name = "end_date")
    var endDate: ZonedDateTime? = null,

    @ManyToOne @JsonIgnoreProperties(value = ["sickLeaves"], allowSetters = true)
    var employee: Employee? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SickLeave) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "SickLeave{" +
        "id=$id" +
        ", startDate='$startDate'" +
        ", endDate='$endDate'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
