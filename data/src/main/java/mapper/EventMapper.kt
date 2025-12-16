package mapper

import model.EventEntity
import transaction.model.Event

internal fun Event.toEntity(): EventEntity {
    return EventEntity(
        id = id,
        eventName = eventName,
        startDate = startDate,
        endDate = endDate,
        numberOfParticipants = numberOfParticipants,
        accountId = accountId,
        isActive = isActive,
        participants = participants
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
        isActive = isActive,
        participants = participants
    )
}

