package eina.unizar.xiangqi_frontendmovil

import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

class HttpHandlerUnitTest {
    @Test
    fun makeLoginTest() {
        val ret = runBlocking { HttpHandler.makeLoginRequest("sosem84191@nuesond.com", "ABcdef12") }
        assertEquals("Test", ret)
    }

    @Test
    fun makeRegisterTest() {
        val ret = runBlocking { HttpHandler.makeRegisterRequest("jb", "Jaime",
            "csscmmbsieotmewiyq@kvhrs.com", "princess", "01/01/2000", "75",
            "España") }
        assertEquals("Test", ret)
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