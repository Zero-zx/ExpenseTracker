package mapper

import model.LocationEntity
import transaction.model.Location

internal fun Location.toEntity(): LocationEntity {
    return LocationEntity(
        id = id,
        name = name,
        accountId = accountId,
        latitude = latitude,
        longitude = longitude
    )
}

internal fun LocationEntity.toDomain(): Location {
    return Location(
        id = id,
        name = name,
        accountId = accountId,
        latitude = latitude,
        longitude = longitude
    )
}

