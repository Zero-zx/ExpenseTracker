package dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import model.EventEntity

@Dao
internal interface EventDao {
    @Query("SELECT * FROM tb_event WHERE accountId = :accountId ORDER BY startDate DESC")
    fun getAllEventsByAccount(accountId: Long): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity): Long

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("SELECT * FROM tb_event WHERE id = :eventId")
    suspend fun getEventById(eventId: Long): EventEntity?
}

