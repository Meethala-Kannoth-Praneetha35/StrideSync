package uk.ac.tees.mad.stridesync.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(step: StepEntity)

    @Query("SELECT * FROM steps WHERE date = :date LIMIT 1")
    fun getStepsByDate(date: String): Flow<StepEntity?>

    @Query("SELECT * FROM steps ORDER BY date DESC")
    fun getAllSteps(): Flow<List<StepEntity>>
}
