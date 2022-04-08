package eina.unizar.xiangqi_frontendmovil.API

import android.provider.ContactsContract
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST(Constants.LOGIN_RUTE)
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST(Constants.REGISTER_RUTE)
    fun register(@Body request: RegisterRequest) : Call<Boolean>

    @GET(Constants.PROFILE_RUTE)
    fun getProfile(@Path("id") id: Int) : Call<ProfileResponse>

    @POST(Constants.VALIDATE_RUTE)
    fun validate(@Body request : ValidateRequest): Call<Boolean>

    @POST(Constants.FORGOT_RUTE)
    fun forgotPwd(@Body request: ValidateRequest): Call<Boolean>

    @GET(Constants.COUNTRY_RUTE)
    fun getCountries(): Call<CountriesResponse>
}