package eina.unizar.xiangqi_frontendmovil

import android.content.Context
import android.util.Log
import androidx.test.InstrumentationRegistry.getTargetContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import eina.unizar.xiangqi_frontendmovil.API.ApiClient
import eina.unizar.xiangqi_frontendmovil.API.LoginRequest
import eina.unizar.xiangqi_frontendmovil.API.LoginResponse
import eina.unizar.xiangqi_frontendmovil.API.SessionManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.robolectric.RobolectricTestRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
class APITest {
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient
    lateinit var instrumentationContext: Context



    @Test
    fun makeLoginTest() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
        apiClient = ApiClient()
        sessionManager = SessionManager(instrumentationContext)

        apiClient.getApiService(instrumentationContext).login(LoginRequest(email = "s@sample.com", password = "mypassword"))
            .enqueue(object : Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // Error logging in
                }

                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    val loginResponse = response.body()

                    if (loginResponse != null){
                        if (loginResponse.ex ) {
                            sessionManager.saveAuthToken(loginResponse.token)
                            println(loginResponse.id)
                        }
                        else {
                            println("f en el chat")
                        }
                    }
                }
            })
    }
}