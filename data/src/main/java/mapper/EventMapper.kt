package mapper

import model.EventEntity
import model.PayeeEntity
import model.EventWithPayeesEntity
import transaction.model.Event
import transaction.model.Payee
import transaction.model.EventWithPayees

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

internal fun Payee.toEntity(): PayeeEntity {
    return PayeeEntity(
        id = id,
        eventId = eventId,
        participantName = participantName
    )
}

internal fun PayeeEntity.toDomain(): Payee {
    return Payee(
        id = id,
        eventId = eventId,
        participantName = participantName,
        account = null
    )
}

internal fun EventWithPayeesEntity.toDomain(): EventWithPayees {
    return EventWithPayees(
        event = event.toDomain(),
        participants = participants.map { it.toDomain() }
    )
}

