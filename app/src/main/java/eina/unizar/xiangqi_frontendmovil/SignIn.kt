package eina.unizar.xiangqi_frontendmovil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout

class SignIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
    }

    fun onClickLogin(view: View) {
        // Disable interactivity while making the request
        val email: TextInputLayout = findViewById(R.id.editTextEmail)
        val password: TextInputLayout = findViewById(R.id.editTextPassword)
        val login: Button = findViewById(R.id.buttonLogin)
        val register: Button = findViewById(R.id.buttonRegister)
        val forgottenPass: TextView = findViewById(R.id.textViewForgottenPass)
        email.error = ""
        email.isEnabled = false
        password.isEnabled = false
        login.isEnabled = false
        register.isEnabled = false
        //forgottenPass.isEnabled = false

        // Send HTTP login request

        // If successful, launch main menu

        // If errored, re-enable interactivity and highlight editText boxes
        /*email.isEnabled = true
        password.isEnabled = true
        login.isEnabled = true
        register.isEnabled = true
        forgottenPass.isEnabled = true*/
    }

    fun onClickRegister(view: View) {
        val i = Intent(this, SignUp::class.java)
        startActivity(i)
    }

    fun onClickForgottenPass(view: View) {
        Toast.makeText(this, "TODO", Toast.LENGTH_LONG).show()

        // FOR DEBUGGING PURPOSE ONLY
        val email: TextInputLayout = findViewById(R.id.editTextEmail)
        val password: TextInputLayout = findViewById(R.id.editTextPassword)
        val login: Button = findViewById(R.id.buttonLogin)
        val register: Button = findViewById(R.id.buttonRegister)
        val forgottenPass: TextView = findViewById(R.id.textViewForgottenPass)
        email.isEnabled = true
        password.isEnabled = true
        login.isEnabled = true
        register.isEnabled = true
        //forgottenPass.isEnabled = true
        password.error = "E-mail o contraseña incorrectos"
    }
}