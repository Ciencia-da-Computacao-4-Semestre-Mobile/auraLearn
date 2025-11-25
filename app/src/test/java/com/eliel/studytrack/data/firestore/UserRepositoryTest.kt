package com.eliel.studytrack.data.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class UserRepositoryTest {

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
    lateinit var mockTaskVoid: Task<Void>

    @MockK
    lateinit var mockTaskSnapshot: Task<DocumentSnapshot>

    @MockK
    lateinit var mockDocumentSnapshot: DocumentSnapshot

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        mockkStatic(FirebaseFirestore::class)
        mockkStatic(FirebaseAuth::class)
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")

        every { FirebaseFirestore.getInstance() } returns mockFirestore
        every { FirebaseAuth.getInstance() } returns mockAuth
        
        // Mock behavior for currentUser (default logged in)
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns "testUserId"

        // Mock Firestore chain
        every { mockFirestore.collection("users") } returns mockCollectionUsers
        every { mockCollectionUsers.document("testUserId") } returns mockDocUser

        // Inject mocks into UserRepository using reflection
        setPrivateField(UserRepository, "db", mockFirestore)
        setPrivateField(UserRepository, "usersRef", mockCollectionUsers)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `createUser should save user to firestore`() = runTest {
        val uid = "testUserId"
        val name = "Test User"
        val email = "test@example.com"
        val userData = UserData(uid, name, email)

        every { mockCollectionUsers.document(uid) } returns mockDocUser
        every { mockDocUser.set(userData) } returns mockTaskVoid
        coEvery { mockTaskVoid.await() } returns null

        UserRepository.createUser(uid, name, email)

        verify { mockDocUser.set(userData) }
        coVerify { mockTaskVoid.await() }
    }

    @Test
    fun `getCurrentUser should return user data when logged in`() = runTest {
        val userData = UserData("testUserId", "Test User", "test@example.com")

        every { mockDocUser.get() } returns mockTaskSnapshot
        coEvery { mockTaskSnapshot.await() } returns mockDocumentSnapshot
        every { mockDocumentSnapshot.toObject(UserData::class.java) } returns userData

        val result = UserRepository.getCurrentUser()

        assertEquals(userData, result)
        coVerify { mockTaskSnapshot.await() }
    }

    @Test
    fun `getCurrentUser should return null when logged out`() = runTest {
        every { mockAuth.currentUser } returns null

        val result = UserRepository.getCurrentUser()

        assertNull(result)
    }

    @Test
    fun `updatePlan should update document field`() = runTest {
        val newPlan = "Premium"

        every { mockDocUser.update("plan", newPlan) } returns mockTaskVoid
        coEvery { mockTaskVoid.await() } returns null

        UserRepository.updatePlan(newPlan)

        verify { mockDocUser.update("plan", newPlan) }
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
