package org.relaxedbase.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable
import java.time.ZonedDateTime
import javax.persistence.*

/**
 * A Event.
 */
@Entity
@Table(name = "event")
data class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "title")
    var title: String? = null,

    @Column(name = "description")
    var description: String? = null,

    @Column(name = "location")
    var location: String? = null,

    @Column(name = "start_date")
    var startDate: ZonedDateTime? = null,

    @Column(name = "end_date")
    var endDate: ZonedDateTime? = null,

    @Column(name = "invite_only")
    var inviteOnly: Boolean? = null,

    @ManyToOne @JsonIgnoreProperties(value = ["events"], allowSetters = true)
    var participant: Employee? = null,

    @ManyToOne @JsonIgnoreProperties(value = ["events"], allowSetters = true)
    var host: Employee? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Event) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Event{" +
        "id=$id" +
        ", title='$title'" +
        ", description='$description'" +
        ", location='$location'" +
        ", startDate='$startDate'" +
        ", endDate='$endDate'" +
        ", inviteOnly='$inviteOnly'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
