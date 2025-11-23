package uk.ac.tees.mad.stridesync.data.local
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "steps")
data class StepEntity(
    @PrimaryKey val date: String,
    val steps: Int
)
