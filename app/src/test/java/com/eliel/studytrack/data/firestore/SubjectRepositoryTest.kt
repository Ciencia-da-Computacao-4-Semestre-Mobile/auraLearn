package com.eliel.studytrack.data.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SubjectRepositoryTest {

    @MockK
    lateinit var mockFirestore: FirebaseFirestore

    @MockK
    lateinit var mockAuth: FirebaseAuth

    @MockK
    lateinit var mockUser: FirebaseUser

    @MockK
    lateinit var mockCollectionUsers: CollectionReference

    @MockK
    lateinit var mockDocUser: DocumentReference

    @MockK
    lateinit var mockCollectionSubjects: CollectionReference

    @MockK
    lateinit var mockDocSubject: DocumentReference

    @MockK
    lateinit var mockTaskVoid: Task<Void>

    @MockK
    lateinit var mockTaskQuery: Task<QuerySnapshot>

    @MockK
    lateinit var mockQuerySnapshot: QuerySnapshot

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        mockkStatic(FirebaseFirestore::class)
        mockkStatic(FirebaseAuth::class)
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")

        every { FirebaseFirestore.getInstance() } returns mockFirestore
        every { FirebaseAuth.getInstance() } returns mockAuth
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns "testUserId"

        // Mock Firestore chain
        every { mockFirestore.collection("users") } returns mockCollectionUsers
        every { mockCollectionUsers.document("testUserId") } returns mockDocUser
        every { mockDocUser.collection("subjects") } returns mockCollectionSubjects

        // Inject mocks into SubjectRepository using reflection to handle singleton state
        setPrivateField(SubjectRepository, "db", mockFirestore)
        setPrivateField(SubjectRepository, "auth", mockAuth)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `addSubject should save subject to firestore`() = runTest {
        val subject = SubjectData(id = "sub1", name = "Math")
        
        every { mockCollectionSubjects.document("sub1") } returns mockDocSubject
        every { mockDocSubject.set(subject) } returns mockTaskVoid
        coEvery { mockTaskVoid.await() } returns null

        SubjectRepository.addSubject(subject)

        verify { mockDocSubject.set(subject) }
        coVerify { mockTaskVoid.await() }
    }

    @Test
    fun `getSubjects should return list of subjects`() = runTest {
        val subject = SubjectData(id = "sub1", name = "Math")
        
        every { mockCollectionSubjects.get() } returns mockTaskQuery
        coEvery { mockTaskQuery.await() } returns mockQuerySnapshot
        every { mockQuerySnapshot.toObjects(SubjectData::class.java) } returns listOf(subject)

        val result = SubjectRepository.getSubjects()

        assertEquals(1, result.size)
        assertEquals("Math", result[0].name)
        
        coVerify { mockTaskQuery.await() }
    }

    @Test
    fun `deleteSubject should remove document`() = runTest {
        val subjectId = "sub1"
        
        every { mockCollectionSubjects.document(subjectId) } returns mockDocSubject
        every { mockDocSubject.delete() } returns mockTaskVoid
        coEvery { mockTaskVoid.await() } returns null

        SubjectRepository.deleteSubject(subjectId)

        verify { mockDocSubject.delete() }
        coVerify { mockTaskVoid.await() }
    }

    private fun setPrivateField(target: Any, fieldName: String, value: Any) {
        try {
            val field = target::class.java.getDeclaredField(fieldName)
            field.isAccessible = true
            field.set(target, value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
