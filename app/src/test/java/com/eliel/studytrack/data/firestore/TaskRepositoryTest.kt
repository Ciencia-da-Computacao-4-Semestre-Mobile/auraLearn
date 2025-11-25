package com.eliel.studytrack.data.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
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

class TaskRepositoryTest {

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
    lateinit var mockCollectionTasks: CollectionReference

    @MockK
    lateinit var mockDocTask: DocumentReference

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
        mockkStatic(Timestamp::class)

        every { FirebaseFirestore.getInstance() } returns mockFirestore
        every { FirebaseAuth.getInstance() } returns mockAuth
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns "testUserId"
        every { Timestamp.now() } returns mockk(relaxed = true)

        // Mock Firestore chain
        every { mockFirestore.collection("users") } returns mockCollectionUsers
        every { mockCollectionUsers.document("testUserId") } returns mockDocUser
        every { mockDocUser.collection("tasks") } returns mockCollectionTasks

        // Inject mocks into TaskRepository using reflection
        setPrivateField(TaskRepository, "db", mockFirestore)
        setPrivateField(TaskRepository, "auth", mockAuth)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `addTask should save task to firestore`() = runTest {
        val task = TaskData(id = "task1", title = "Test Task")
        
        every { mockCollectionTasks.document("task1") } returns mockDocTask
        every { mockDocTask.set(task) } returns mockTaskVoid
        coEvery { mockTaskVoid.await() } returns null

        TaskRepository.addTask(task)

        verify { mockDocTask.set(task) }
        coVerify { mockTaskVoid.await() }
    }

    @Test
    fun `getTasks should return list of tasks`() = runTest {
        val task = TaskData(id = "task1", title = "Test Task")
        
        every { mockCollectionTasks.get() } returns mockTaskQuery
        coEvery { mockTaskQuery.await() } returns mockQuerySnapshot
        every { mockQuerySnapshot.toObjects(TaskData::class.java) } returns listOf(task)

        val result = TaskRepository.getTasks()

        assertEquals(1, result.size)
        assertEquals("Test Task", result[0].title)
        
        coVerify { mockTaskQuery.await() }
    }

    @Test
    fun `updateTaskStatus should update document`() = runTest {
        val taskId = "task1"
        
        every { mockCollectionTasks.document(taskId) } returns mockDocTask
        every { mockDocTask.update(any<Map<String, Any?>>()) } returns mockTaskVoid
        coEvery { mockTaskVoid.await() } returns null

        TaskRepository.updateTaskStatus(taskId, true)

        verify { mockDocTask.update(match<Map<String, Any?>> { 
            it["completed"] == true && it.containsKey("completedAt")
        }) }
        coVerify { mockTaskVoid.await() }
    }

    @Test
    fun `deleteTask should remove document`() = runTest {
        val taskId = "task1"
        
        every { mockCollectionTasks.document(taskId) } returns mockDocTask
        every { mockDocTask.delete() } returns mockTaskVoid
        coEvery { mockTaskVoid.await() } returns null

        TaskRepository.deleteTask(taskId)

        verify { mockDocTask.delete() }
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
