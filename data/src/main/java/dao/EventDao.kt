package dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import model.EventEntity
import model.EventParticipantEntity
import model.EventWithParticipantsEntity

@Dao
internal interface EventDao {
    @Query("SELECT * FROM tb_event WHERE accountId = :accountId ORDER BY startDate DESC")
    fun getAllEventsByAccount(accountId: Long): Flow<List<EventEntity>>

    @Transaction
    @Query("SELECT * FROM tb_event WHERE id = :eventId")
    fun getEventWithParticipants(eventId: Long): Flow<EventWithParticipantsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(participants: List<EventParticipantEntity>)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("SELECT * FROM tb_event_participant WHERE eventId = :eventId")
    fun getParticipantsByEvent(eventId: Long): Flow<List<EventParticipantEntity>>

    @Query("SELECT * FROM tb_event WHERE id = :eventId")
    fun getEventById(eventId: Long): EventEntity?
}

