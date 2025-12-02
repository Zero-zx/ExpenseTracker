package mapper

import model.EventEntity
import model.EventParticipantEntity
import model.EventWithParticipantsEntity
import transaction.model.Event
import transaction.model.EventParticipant
import transaction.model.EventWithParticipants

internal fun Event.toEntity(): EventEntity {
    return EventEntity(
        id = id,
        eventName = eventName,
        startDate = startDate,
        endDate = endDate,
        numberOfParticipants = numberOfParticipants,
        accountId = accountId,
        isActive = isActive
    )
}

internal fun EventEntity.toDomain(): Event {
    return Event(
        id = id,
        eventName = eventName,
        startDate = startDate,
        endDate = endDate,
        numberOfParticipants = numberOfParticipants,
        accountId = accountId,
        isActive = isActive
    )
}

internal fun EventParticipant.toEntity(): EventParticipantEntity {
    return EventParticipantEntity(
        id = id,
        eventId = eventId,
        participantName = participantName
    )
}

internal fun EventParticipantEntity.toDomain(): EventParticipant {
    return EventParticipant(
        id = id,
        eventId = eventId,
        participantName = participantName
    )
}

internal fun EventWithParticipantsEntity.toDomain(): EventWithParticipants {
    return EventWithParticipants(
        event = event.toDomain(),
        participants = participants.map { it.toDomain() }
    )
}

