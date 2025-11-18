package com.eliel.studytrack.auth

import com.eliel.studytrack.data.firestore.UserRepository
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.mockito.MockedStatic
import com.google.android.gms.tasks.Tasks

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private lateinit var auth: FirebaseAuth
    private val dispatcher = StandardTestDispatcher()

    private var firebaseAuthStatic: MockedStatic<FirebaseAuth>? = null
    private var userRepoStatic: MockedStatic<UserRepository>? = null

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)


        auth = mock(FirebaseAuth::class.java, RETURNS_DEEP_STUBS)

        firebaseAuthStatic = mockStatic(FirebaseAuth::class.java)
        firebaseAuthStatic?.`when`<FirebaseAuth> {
            FirebaseAuth.getInstance()
        }?.thenReturn(auth)


        viewModel = AuthViewModel()
    }

    @After
    fun tearDown() {
        userRepoStatic?.close()
        firebaseAuthStatic?.close()
        Dispatchers.resetMain()
    }

    @Test
    fun `registerUser - campos vazios retorna erro`() {
        var wasSuccess = true
        var message: String? = null

        viewModel.registerUser("", "123456", "Name") { success, msg ->
            wasSuccess = success
            message = msg
        }

        assertThat(wasSuccess).isFalse()
        assertThat(message).isEqualTo("Preencha todos os campos.")
    }

    @Test
    fun `registerUser - senha curta retorna erro`() {
        var wasSuccess = true
        var message: String? = null

        viewModel.registerUser("a@a.com", "123", "Name") { success, msg ->
            wasSuccess = success
            message = msg
        }

        assertThat(wasSuccess).isFalse()
        assertThat(message).isEqualTo("A senha deve ter pelo menos 6 caracteres.")
    }

    @Test
    fun `registerUser - sucesso`() = runTest {
        val firebaseUser = mock(FirebaseUser::class.java)

        whenever(auth.createUserWithEmailAndPassword(any(), any()))
            .thenReturn(Tasks.forResult(mock(AuthResult::class.java)))

        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.updateProfile(any()))
            .thenReturn(Tasks.forResult(null))

        userRepoStatic = mockStatic(UserRepository::class.java)
        userRepoStatic!!.`when`<Any?> {
            runBlocking {
                UserRepository.createUser(
                    any<String>(),
                    any<String>(),
                    any<String>()
                )
            }
        }.thenReturn(Unit)

        var result = false

        viewModel.registerUser("a@a.com", "123456", "Eliel") { success, _ ->
            result = success
        }

        advanceUntilIdle()
        assertThat(result).isTrue()
    }

    @Test
    fun `loginUser - sucesso`() {
        whenever(auth.signInWithEmailAndPassword(any(), any()))
            .thenReturn(Tasks.forResult(mock(AuthResult::class.java)))

        var result = false
        viewModel.loginUser("a@a.com", "123456") { success, _ ->
            result = success
        }

        assertThat(result).isTrue()
    }

    @Test
    fun `loginUser - erro`() {
        whenever(auth.signInWithEmailAndPassword(any(), any()))
            .thenReturn(Tasks.forException(Exception("Senha incorreta")))

        var message: String? = null
        viewModel.loginUser("a@a.com", "123456") { _, msg ->
            message = msg
        }

        assertThat(message).isEqualTo("Senha incorreta.")
    }

    @Test
    fun `resetPassword - envia email`() {
        whenever(auth.sendPasswordResetEmail(any()))
            .thenReturn(Tasks.forResult(null))

        var result = false
        viewModel.resetPassword("a@a.com") { success, _ ->
            result = success
        }

        assertThat(result).isTrue()
    }

    @Test
    fun `logout chama signOut`() {
        viewModel.logout()
        verify(auth, times(1)).signOut()
    }

    @Test
    fun `isUserLoggedIn - retorna true se currentUser nao for null`() {
        whenever(auth.currentUser).thenReturn(mock())
        assertThat(viewModel.isUserLoggedIn()).isTrue()
    }

    @Test
    fun `isUserLoggedIn - retorna false se currentUser for null`() {
        whenever(auth.currentUser).thenReturn(null)
        assertThat(viewModel.isUserLoggedIn()).isFalse()
    }


}
