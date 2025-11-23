package uk.ac.tees.mad.stridesync.data.StepRepository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.stridesync.data.local.StepDao
import uk.ac.tees.mad.stridesync.data.local.StepEntity
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepRepository @Inject constructor(
    private val stepDao: StepDao,
    private val firestore: FirebaseFirestore
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private fun todayDate(): String = dateFormat.format(Date())

    suspend fun saveSteps(steps: Int, userId: String) {
        val date = todayDate()
        val entity = StepEntity(date, steps)

        stepDao.insertOrUpdate(entity)

        firestore.collection("users")
            .document(userId)
            .collection("daily_steps")
            .document(date)
            .set(entity)
    }

    fun getTodaySteps(): Flow<StepEntity?> = stepDao.getStepsByDate(todayDate())

    fun getHistory(): Flow<List<StepEntity>> = stepDao.getAllSteps()
}
