import com.defri.bookreflect.data.mapper.MoodMapper
import com.defri.bookreflect.domain.model.Mood
import org.junit.Assert.assertEquals
import org.junit.Test

class MoodMapperTest {
    @Test
    fun `toEntity and fromEntity`() {
        val mood = Mood(
            id = "1",
            bookId = "b1",
            tags = listOf("happy"),
            note = "note",
            quotes = listOf("q1"),
            isLocal = true,
            createdAt = 123L
        )
        val entity = MoodMapper.toEntity(mood)
        val mapped = MoodMapper.fromEntity(entity)
        assertEquals(mood.copy(id = entity.id), mapped)
    }
} 