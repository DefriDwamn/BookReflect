import com.defri.bookreflect.data.local.BookDao
import com.defri.bookreflect.data.remote.FirestoreBookSource
import com.defri.bookreflect.data.remote.GoogleBooksSource
import com.defri.bookreflect.data.repository.BookRepositoryImpl
import com.defri.bookreflect.domain.model.Book
import com.defri.bookreflect.core.Result
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class BookRepositoryImplTest {
    private val dao = mock<BookDao>()
    private val firestore = mock<FirestoreBookSource>()
    private val google = mock<GoogleBooksSource>()
    private val repo = BookRepositoryImpl(firestore, dao, google)

    @Test
    fun `createBook Success`() = runBlocking {
        val book = Book("1",
            true,
            "t",
            "a",
            "d",
            "u",
            null)
        whenever(dao.insert(org.mockito.kotlin.any())).thenReturn(Unit)
        val result = repo.createBook(book)
        assertTrue(result is Result.Success)
    }
} 