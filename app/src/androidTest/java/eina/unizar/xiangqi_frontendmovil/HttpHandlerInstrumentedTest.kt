package eina.unizar.xiangqi_frontendmovil

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class HttpHandlerInstrumentedTest {
    @Test
    fun makeLoginTest() {
        // Test unregistered account
        var response = runBlocking { HttpHandler.makeLoginRequest(HttpHandler.LoginRequest(
            "a@b.com",
            "1234")) }
        assert(!response.error)
        assert(!response.exist)
        assert(!response.validacion)
        assert(!response.ok)

        // Test unvalidated account
        response = runBlocking { HttpHandler.makeLoginRequest(HttpHandler.LoginRequest(
            "android@unvalidated.com",
            "1234")) }
        assert(!response.error)
        assert(response.exist)
        assert(!response.validacion)
        assert(!response.ok)

        // Test incorrect password
        response = runBlocking { HttpHandler.makeLoginRequest(HttpHandler.LoginRequest(
            "android@test.com",
            "1234")) }
        assert(!response.error)
        assert(response.exist)
        assert(response.validacion)
        assert(!response.ok)

        // Test validated account
        response = runBlocking { HttpHandler.makeLoginRequest(HttpHandler.LoginRequest(
            "android@test.com",
            "ABcdef12")) }
        assert(!response.error)
        assert(response.exist)
        assert(response.validacion)
        assert(response.ok)
    }

    @Test
    fun makeRegisterTest() {
        // Test unregistered email
        var response = runBlocking { HttpHandler.makeRegisterRequest(HttpHandler.RegisterRequest(
            "android",
            "android",
            "movil@test.com",
            "ABcdef12",
            "03/04/2000",
            "Spain",
            "ES",
            null
        ), InstrumentationRegistry.getInstrumentation().context) }
        assert(!response.error)
        assert(response.success)

        // Test registered email
        response = runBlocking { HttpHandler.makeRegisterRequest(HttpHandler.RegisterRequest(
            "android",
            "android",
            "movil@test.com",
            "ABcdef12",
            "03/04/2000",
            "Spain",
            "ES",
            null
        ), InstrumentationRegistry.getInstrumentation().context) }
        assert(!response.error)
        assert(!response.success)
    }

    @Test
    fun makeForgottenPassTest() {
        // Test existing account
        var response = runBlocking { HttpHandler.makeForgottenPassRequest(HttpHandler.ForgottenPassRequest(
            "a@b.com")) }
        assert(!response.error)
        assert(!response.success)

        // Test existing account
        response = runBlocking { HttpHandler.makeForgottenPassRequest(HttpHandler.ForgottenPassRequest(
            "android@test.com")) }
        assert(!response.error)
        assert(response.success)
    }

    @Test
    fun makeValidationTest() {
        // Test existing account
        val response = runBlocking { HttpHandler.makeValidationRequest(HttpHandler.ValidationRequest(
            "android@test.com")) }
        assert(!response.error)
        assert(response.success)
    }

    @Test
    fun makeDeletionTest() {
        // Log into validated account
        val login = runBlocking { HttpHandler.makeLoginRequest(HttpHandler.LoginRequest(
            "android@test.com",
            "ABcdef12")) }
        assert(!login.error)
        assert(login.exist)
        assert(login.validacion)
        assert(login.ok)

        // Test deletion
        val response = runBlocking { HttpHandler.makeDeletionRequest() }
        assert(!response.error)
        assert(response.success)
    }

    @Test
    fun makeProfileTest() {
        val ret = runBlocking { HttpHandler.makeProfileRequest("22") }
        assertEquals("Test", ret)
    }

    @Test
    fun makeCountriesTest() {
        val ret = runBlocking { HttpHandler.makeCountriesRequest() }
        assertEquals("Test", ret)
    }

    @Test
    fun makeImageTest() {
        val ret = runBlocking { HttpHandler.makeImageRequest("23") }
        assertEquals("Test", ret)
    }
}