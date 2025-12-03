package model

import androidx.room.Embedded
import androidx.room.Relation

internal data class EventWithParticipantsEntity(
    @Embedded val event: EventEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "eventId"
    )
    val participants: List<EventParticipantEntity>
)

