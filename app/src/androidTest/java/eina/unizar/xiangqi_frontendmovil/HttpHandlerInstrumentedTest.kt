package eina.unizar.xiangqi_frontendmovil

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class HttpHandlerInstrumentedTest {
    @Test
    fun A_makeRegisterTest() {
        // Test unregistered email
        var response = runBlocking { HttpHandler.makeRegisterRequest(HttpHandler.RegisterRequest(
            "android",
            "android test",
            "android@unvalidated.com",
            "ABcdef12",
            "04/03/2000",
            "Spain",
            "ES",
            null
        ), InstrumentationRegistry.getInstrumentation().context) }
        assert(!response.error)
        assert(response.success)

        response = runBlocking { HttpHandler.makeRegisterRequest(HttpHandler.RegisterRequest(
            "android",
            "android test",
            "android@validated.com",
            "ABcdef12",
            "04/03/2000",
            "Spain",
            "ES",
            null
        ), InstrumentationRegistry.getInstrumentation().context) }
        assert(!response.error)
        assert(response.success)

        // Test registered email
        response = runBlocking { HttpHandler.makeRegisterRequest(HttpHandler.RegisterRequest(
            "android",
            "android test",
            "android@validated.com",
            "ABcdef12",
            "04/03/2000",
            "Spain",
            "ES",
            null
        ), InstrumentationRegistry.getInstrumentation().context) }
        assert(!response.error)
        assert(!response.success)
    }

    @Test
    fun B_makeValidationTest() {
        // Test existing account
        val response = runBlocking { HttpHandler.makeValidationRequest(HttpHandler.ValidationRequest(
            "android@validated.com")) }
        assert(!response.error)
        assert(response.success)
    }

    @Test
    fun C_makeLoginTest() {
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
            "android@validated.com",
            "1234")) }
        assert(!response.error)
        assert(response.exist)
        assert(response.validacion)
        assert(!response.ok)

        // Test validated account
        response = runBlocking { HttpHandler.makeLoginRequest(HttpHandler.LoginRequest(
            "android@validated.com",
            "ABcdef12")) }
        assert(!response.error)
        assert(response.exist)
        assert(response.validacion)
        assert(response.ok)
    }

    @Test
    fun D_makeProfileTest() {
        /*// Log into validated account
        val login = runBlocking { HttpHandler.makeLoginRequest(HttpHandler.LoginRequest(
            "android@validated.com",
            "ABcdef12")) }
        assert(!login.error)
        assert(login.exist)
        assert(login.validacion)
        assert(login.ok)*/

        // Get account profile
        val profile = runBlocking { HttpHandler.makeProfileRequest(HttpHandler.ProfileRequest(null)) }
        assert(!profile.error)
        assert(profile.nickname == "android")
        assert(profile.realname == "android test")
        assert(profile.birthdate == "04/03/2000")
        assert(profile.country == "Spain")
        assert(profile.code == "ES")
        assert(!profile.image)
    }

    @Test
    fun E_makeEditTest() {
        /*// Log into validated account
        val login = runBlocking { HttpHandler.makeLoginRequest(HttpHandler.LoginRequest(
            "android@test.com",
            "ABcdef12")) }
        assert(!login.error)
        assert(login.exist)
        assert(login.validacion)
        assert(login.ok)*/

        // Modify profile data
        val edit = runBlocking { HttpHandler.makeEditRequest(HttpHandler.EditRequest(
            "androide",
            "androide test",
            "a",
            "05/03/2000",
            "Andorra",
            null
        ), InstrumentationRegistry.getInstrumentation().context) }
        assert(!edit.error)
        assert(edit.success)

        // Check changes
        val profile = runBlocking { HttpHandler.makeProfileRequest(HttpHandler.ProfileRequest(null)) }
        assert(!profile.error)
        assert(profile.nickname == "androide")
        assert(profile.realname == "androide test")
        assert(profile.birthdate == "05/03/2000")
        assert(profile.country == "Andorra")
        assert(profile.code == "AD")
        assert(!profile.image)
    }

    @Test
    fun F_makeDeletionTest() {
        /*// Log into validated account
        val login = runBlocking { HttpHandler.makeLoginRequest(HttpHandler.LoginRequest(
            "android@test.com",
            "ABcdef12")) }
        assert(!login.error)
        assert(login.exist)
        assert(login.validacion)
        assert(login.ok)*/

        // Test deletion
        var deletion = runBlocking { HttpHandler.makeDeletionRequest() }
        assert(!deletion.error)
        assert(deletion.success)

        // Validate and delete extra accounts generated during testing
        val validation = runBlocking { HttpHandler.makeValidationRequest(HttpHandler.ValidationRequest(
            "android@unvalidated.com")) }
        assert(!validation.error)
        assert(validation.success)

        val login = runBlocking { HttpHandler.makeLoginRequest(HttpHandler.LoginRequest(
            "android@unvalidated.com",
            "ABcdef12")) }
        assert(!login.error)
        assert(login.exist)
        assert(login.validacion)
        assert(login.ok)

        deletion = runBlocking { HttpHandler.makeDeletionRequest() }
        assert(!deletion.error)
        assert(deletion.success)
    }
}