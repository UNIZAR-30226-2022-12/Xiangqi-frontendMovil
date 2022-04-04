package eina.unizar.xiangqi_frontendmovil

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class SignIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        // Add listeners to remove editText error messages after editing them
        val email: TextInputLayout = findViewById(R.id.editTextEmail)
        email.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                email.error = ""
            }
        })

        val password: TextInputLayout = findViewById(R.id.editTextPassword)
        password.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                password.error = ""
            }
        })
    }

    private fun checkFormData(): Boolean {
        val email: TextInputLayout = findViewById(R.id.editTextEmail)
        val password: TextInputLayout = findViewById(R.id.editTextPassword)
        val emailText: String = email.editText?.text.toString()
        val passwordText: String = password.editText?.text.toString()

        var ok = true
        if (emailText == "") {
            email.error = "Por favor, indique su correo"
            ok = false
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.error = "El correo introducido no es válido"
            ok = false
        }
        if (passwordText == "") {
            password.error = "Por favor, especifique una contraseña"
            ok = false
        }

        return ok
    }

    fun onClickLogin(view: View) {
        // Validate data
        if (!checkFormData()) return

        // Disable interactivity while making the request
        val email: TextInputLayout = findViewById(R.id.editTextEmail)
        val password: TextInputLayout = findViewById(R.id.editTextPassword)
        val login: Button = findViewById(R.id.buttonLogin)
        val register: Button = findViewById(R.id.buttonRegister)
        val forgottenPass: TextView = findViewById(R.id.textViewForgottenPass)
        password.error = ""
        email.isEnabled = false
        password.isEnabled = false
        login.isEnabled = false
        register.isEnabled = false
        forgottenPass.isEnabled = false

        val context = this  // Save activity context to launch intents

        // Send HTTP login request
        val request = HttpHandler.LoginRequest(email.editText?.text.toString(),
            password.editText?.text.toString())
        MainScope().launch {
            val response = HttpHandler.makeLoginRequest(request)
            email.isEnabled = true
            password.isEnabled = true
            login.isEnabled = true
            register.isEnabled = true
            forgottenPass.isEnabled = true
            if (response.exist and response.ok and response.validacion) {
                // If successful, launch main menu
                val i = Intent(context, Home::class.java)
                startActivity(i)
                finish()
            }
            else if (response.error) {
                Toast.makeText(context, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
            }
            else if (!response.exist) {
                email.error = "E-mail no registrado"
            }
            else if (!response.validacion) {
                password.error = "Cuenta no validada, revise su correo electrónico"
            }
            else if (!response.ok) {
                password.error = "Contraseña incorrecta"
            }
        }
    }

    fun onClickRegister(view: View) {
        val i = Intent(this, SignUp::class.java)
        startActivity(i)
    }

    fun onClickForgottenPass(view: View) {
        Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show()
    }
}