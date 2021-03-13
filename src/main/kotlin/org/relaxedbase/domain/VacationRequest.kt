package org.relaxedbase.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable
import java.time.ZonedDateTime
import javax.persistence.*

/**
 * A VacationRequest.
 */
@Entity
@Table(name = "vacation_request")
data class VacationRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "status")
    var status: Boolean? = null,

    @Column(name = "start_date")
    var startDate: ZonedDateTime? = null,

    @Column(name = "end_date")
    var endDate: ZonedDateTime? = null,

    @Column(name = "owner")
    var owner: String? = null,

    @ManyToOne @JsonIgnoreProperties(value = ["vacationRequests"], allowSetters = true)
    var applicant: Employee? = null,

    @ManyToOne @JsonIgnoreProperties(value = ["vacationRequests"], allowSetters = true)
    var standIn: Employee? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VacationRequest) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "VacationRequest{" +
        "id=$id" +
        ", status='$status'" +
        ", startDate='$startDate'" +
        ", endDate='$endDate'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
