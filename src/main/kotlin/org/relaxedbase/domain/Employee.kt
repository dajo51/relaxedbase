package org.relaxedbase.domain

import java.io.Serializable
import javax.persistence.*

/**
 * A Employee.
 */
@Entity
@Table(name = "employee")
data class Employee(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "first_name")
    var firstName: String? = null,

    @Column(name = "last_name")
    var lastName: String? = null,

    @Column(name = "email")
    var email: String? = null,

    @Column(name = "phone_number")
    var phoneNumber: String? = null,

    @Column(name = "team")
    var team: String? = null,

    @OneToOne @JoinColumn(unique = true)
    var position: Employee? = null,

    @OneToMany(mappedBy = "standIn")
    var vacationRequests: MutableSet<VacationRequest> = mutableSetOf(),

    @OneToMany(mappedBy = "employee")
    var sickLeaves: MutableSet<SickLeave> = mutableSetOf(),

    @OneToMany(mappedBy = "participant")
    var events: MutableSet<Event> = mutableSetOf()

    // @OneToMany(mappedBy = "host")
    // var events: MutableSet<Event> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addVacationRequest(vacationRequest: VacationRequest): Employee {
        this.vacationRequests.add(vacationRequest)
        vacationRequest.applicant = this
        return this
    }

    fun removeVacationRequest(vacationRequest: VacationRequest): Employee {
        this.vacationRequests.remove(vacationRequest)
        vacationRequest.applicant = null
        return this
    }

    fun addSickLeave(sickLeave: SickLeave): Employee {
        this.sickLeaves.add(sickLeave)
        sickLeave.employee = this
        return this
    }

    fun removeSickLeave(sickLeave: SickLeave): Employee {
        this.sickLeaves.remove(sickLeave)
        sickLeave.employee = null
        return this
    }

    /*fun addEvent(event: Event): Employee {
        this.events.add(event)
        event.participant = this
        return this
    }

    fun removeEvent(event: Event): Employee {
        this.events.remove(event)
        event.participant = null
        return this
    } */

    fun addEvent(event: Event): Employee {
        this.events.add(event)
        event.host = this
        return this
    }

    fun removeEvent(event: Event): Employee {
        this.events.remove(event)
        event.host = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Employee) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Employee{" +
        "id=$id" +
        ", firstName='$firstName'" +
        ", lastName='$lastName'" +
        ", email='$email'" +
        ", phoneNumber='$phoneNumber'" +
        ", team='$team'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
