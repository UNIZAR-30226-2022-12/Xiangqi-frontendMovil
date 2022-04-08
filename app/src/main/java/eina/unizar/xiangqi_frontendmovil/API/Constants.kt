package eina.unizar.xiangqi_frontendmovil.API//rutas y urls utilizados

object Constants{
    const val URL_BASE = "http://ec2-3-82-235-243.compute-1.amazonaws.com"
    const val LOGIN_RUTE = "/do-login"
    const val REGISTER_RUTE = "/do-create"
    const val PROFILE_RUTE = "/do-getProfile/{id}"
    const val VALIDATE_RUTE = "/do-validate"
    const val FORGOT_RUTE = "/do-forgotPwd"
    const val CHANGE_RUTE = "/do-changePwd"
    const val COUNTRY_RUTE = "/do-getCountries"
    const val IMAGE_RUTE = "/do-getProfileImage/{id}"
}