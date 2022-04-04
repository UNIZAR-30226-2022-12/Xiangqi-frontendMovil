package eina.unizar.xiangqi_frontendmovil

import androidx.test.ext.junit.runners.AndroidJUnit4
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
            "sosem84191@nuesond.com",
            "1234")) }
        assert(!response.error)
        assert(response.exist)
        assert(response.validacion)
        assert(!response.ok)

        // Test unvalidated account
        response = runBlocking { HttpHandler.makeLoginRequest(HttpHandler.LoginRequest(
            "sosem84191@nuesond.com",
            "ABcdef12")) }
        assert(!response.error)
        assert(response.exist)
        assert(response.validacion)
        assert(response.ok)
    }

    @Test
    fun makeRegisterTest() {
        val response = runBlocking { HttpHandler.makeRegisterRequest(HttpHandler.RegisterRequest(
            "android",
            "android",
            "android@test.com",
            "ABcdef12",
            "03/04/2000",
            "Spain",
            "ES",
            null
        )) }
        assertEquals("Test", response)
    }

    @Test
    fun makeProfileTest() {
        val ret = runBlocking { HttpHandler.makeProfileRequest("22") }
        assertEquals("Test", ret)
    }

    @Test
    fun makeCountriesTest() {
        val ret = runBlocking { HttpHandler.makeCountriesRequest("22") }
        assertEquals("Test", ret)
    }

    @Test
    fun makeImageTest() {
        val ret = runBlocking { HttpHandler.makeImageRequest("23") }
        assertEquals("Test", ret)
    }
}