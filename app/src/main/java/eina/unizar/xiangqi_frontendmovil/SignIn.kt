package eina.unizar.xiangqi_frontendmovil

import android.app.Dialog
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
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        setTitle(R.string.signin_title)

        // Add listeners to remove editText error messages after editing it
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

        // Construct forgotten password dialog
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_forgotten_pass)

        // Add listener to remove editText error messages after editing it
        val forgottenEmail: TextInputLayout = dialog.findViewById(R.id.editTextForgottenEmail)
        forgottenEmail.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                forgottenEmail.error = ""
            }
        })
    }

    private fun checkFormData(): Boolean {
        var ok = true

        // Check email field
        val email: TextInputLayout = findViewById(R.id.editTextEmail)
        val emailText: String = email.editText?.text.toString()
        if (emailText == "") {
            email.error = "Por favor, especifique una dirección de correo electrónico"
            ok = false
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.error = "El correo introducido no es válido"
            ok = false
        }

        // Check password field
        val password: TextInputLayout = findViewById(R.id.editTextPassword)
        if (password.editText?.text.toString() == "") {
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
        email.error = ""
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
        startActivity(Intent(this, SignUp::class.java))
    }

    private fun checkForgottenPassData(): Boolean {
        var ok = true

        // Check email field
        val email: TextInputLayout = dialog.findViewById(R.id.editTextForgottenEmail)
        val emailText: String = email.editText?.text.toString()
        if (emailText == "") {
            email.error = "Por favor, indique su correo"
            ok = false
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.error = "El correo introducido no es válido"
            ok = false
        }

        return ok
    }

    fun onClickRecovery(view: View) {
        if (!checkForgottenPassData()) return

        // Disable interactivity while making the request
        val email: TextInputLayout = dialog.findViewById(R.id.editTextForgottenEmail)
        val recovery: Button = dialog.findViewById(R.id.buttonRecovery)
        email.error = ""
        email.isEnabled = false
        recovery.isEnabled = false

        val context = this  // Save activity context to launch intents

        // Send HTTP login request
        val request = HttpHandler.ForgottenPassRequest(email.editText?.text.toString())
        MainScope().launch {
            val response = HttpHandler.makeForgottenPassRequest(request)
            email.isEnabled = true
            recovery.isEnabled = true
            if (response.success) {
                Toast.makeText(context, "¡Correo enviado correctamente!\n" +
                        "Por favor, revisa tu correo electrónico y sigue las " +
                        "instrucciones para recuperar tu contraseña",
                    Toast.LENGTH_LONG).show()
                dialog.hide()
                email.editText?.setText("")
            }
            else if (response.error) {
                Toast.makeText(context, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
            }
            else if (!response.success) {
                email.error = "E-mail no registrado"
            }
        }
    }

    fun onClickForgottenPass(view: View) {
        dialog.show()
    }
}