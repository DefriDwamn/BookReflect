import com.defri.bookreflect.data.mapper.BookMapper
import com.defri.bookreflect.domain.model.Book
import com.defri.bookreflect.domain.model.BookStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class BookMapperTest {
    @Test
    fun `toEntity and fromEntity`() {
        val book = Book(
            id = "1",
            isLocal = true,
            title = "Test",
            author = "Author",
            description = "Desc",
            coverUrl = "url",
            status = BookStatus.ADDED
        )
        val entity = BookMapper.toEntity(book)
        val mapped = BookMapper.fromEntity(entity)
        assertEquals(book.copy(id = entity.id), mapped)
    }
} 