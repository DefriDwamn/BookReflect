import com.defri.bookreflect.data.local.MoodDao
import com.defri.bookreflect.data.remote.FirestoreMoodSource
import com.defri.bookreflect.data.repository.MoodRepositoryImpl
import com.defri.bookreflect.domain.model.Mood
import com.defri.bookreflect.core.Result
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class MoodRepositoryImplTest {
    private val dao = mock<MoodDao>()
    private val firestore = mock<FirestoreMoodSource>()
    private val repo = MoodRepositoryImpl(firestore, dao)

    @Test
    fun `saveMood Success`() = runBlocking {
        val mood = Mood(
            "1",
            "b1",
            listOf("t"),
            "n",
            listOf("q"),
            true,
            1L)
        whenever(dao.insert(org.mockito.kotlin.any())).thenReturn(Unit)
        val result = repo.saveMood("u1", mood)
        assertTrue(result is Result.Success)
    }
} 