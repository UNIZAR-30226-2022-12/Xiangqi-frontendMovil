package eina.unizar.xiangqi_frontendmovil

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SignUp : AppCompatActivity() {
    private var countryList = listOf("Australia", "Brazil", "China", "Egypt", "France", "Germany",
        "India", "Japan", "Spain", "United States")
    private var codeList = listOf("AU", "BR", "CN", "EG", "FR", "DE", "IN", "JP", "ES", "US")
    private var imageUri: Uri? = null
    private lateinit var dialog: Dialog
    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Handle the returned Uri
        if (uri != null) {
            imageUri = uri

            // Retrieve file name
            val cursor = contentResolver.query(uri, null, null, null, null)
            val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor?.moveToFirst()

            // Write file name
            val image: TextInputLayout = findViewById(R.id.editTextImage)
            image.editText?.setText(nameIndex?.let { cursor.getString(it) })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        setTitle(R.string.signup_title)

        // Set default country dropdown menu items
        val country: TextInputLayout = findViewById(R.id.editTextCountry)
        val flagOffset = 0x1F1E6
        val asciiOffset = 0x41
        var flagList = mutableListOf<String>()
        for (i in countryList.indices) {
            val firstChar = Character.codePointAt(codeList[i], 0) - asciiOffset + flagOffset
            val secondChar = Character.codePointAt(codeList[i], 1) - asciiOffset + flagOffset
            flagList.add(String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
                         + " " + countryList[i])
        }
        var adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, flagList)
        (country.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        // Add listeners to remove editText error messages after editing them
        val nickname: TextInputLayout = findViewById(R.id.editTextNickname)
        nickname.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                nickname.error = ""
            }
        })

        val realname: TextInputLayout = findViewById(R.id.editTextRealname)
        realname.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                realname.error = ""
            }
        })

        val email: TextInputLayout = findViewById(R.id.editTextEmail)
        email.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                email.error = ""
            }
        })

        val birthdate: TextInputLayout = findViewById(R.id.editTextBirthdate)
        birthdate.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                birthdate.error = ""
            }
        })

        country.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                country.error = ""
            }
        })

        val image: TextInputLayout = findViewById(R.id.editTextImage)
        image.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                image.error = ""
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

        val repassword: TextInputLayout = findViewById(R.id.editTextRepassword)
        repassword.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                repassword.error = ""
            }
        })

        // Fill EULA dialog
        dialog = Dialog(this)
        dialog.setContentView(R.layout.fragment_eula)
        dialog.findViewById<TextView>(R.id.textView).text = Html.fromHtml(resources.getString(R.string.eula), 0)

        // Enable backward navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Retrieve country list
        MainScope().launch {
            val response = HttpHandler.makeCountriesRequest()
            if (!response.error) {
                countryList = response.countryList
                codeList = response.codeList
                flagList = mutableListOf()
                for (i in codeList.indices) {
                    val firstChar = Character.codePointAt(codeList[i], 0) - asciiOffset + flagOffset
                    val secondChar = Character.codePointAt(codeList[i], 1) - asciiOffset + flagOffset
                    flagList.add(String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
                            + " " + countryList[i])
                }
                adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, flagList)
                (country.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    fun onClickBirthdate(view: View) {
        val newFragment = DatePickerFragment.newInstance { _, year, month, day ->
            // +1 because January is zero
            val selectedDate = "$day/${month+1}/$year"
            findViewById<TextInputLayout>(R.id.editTextBirthdate).editText?.setText(selectedDate)
        }
        newFragment.show(supportFragmentManager, "datePicker")
    }

    fun onClickImage(view: View) {
        getImage.launch("image/*")
    }

    fun onClickEula(view: View) {
        dialog.show()
    }

    private fun checkFormData(): Boolean {
        var ok = true

        // Check nickname field
        val nickname: TextInputLayout = findViewById(R.id.editTextNickname)
        val nicknameText: String = nickname.editText?.text.toString()
        if (nicknameText == "") {
            nickname.error = "Por favor, indique un nombre de usuario"
            ok = false
        } else if (nicknameText.length > 15) {
            nickname.error = "El nombre de usuario no puede tener más de 15 caracteres"
            ok = false
        }

        // Check realname field
        val realname: TextInputLayout = findViewById(R.id.editTextRealname)
        if (realname.editText?.text.toString() == "") {
            realname.error = "Por favor, indique su nombre"
            ok = false
        }

        // Check email field
        val email: TextInputLayout = findViewById(R.id.editTextEmail)
        val emailText: String = email.editText?.text.toString()
        if (emailText == "") {
            email.error = "Por favor, indique su correo"
            ok = false
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.error = "El correo introducido no es válido"
            ok = false
        }

        // Check birthdate field
        val birthdate: TextInputLayout = findViewById(R.id.editTextBirthdate)
        if (birthdate.editText?.text.toString() == "") {
            birthdate.error = "Por favor, indique su fecha de nacimiento"
            ok = false
        }

        // Check country field
        val country: TextInputLayout = findViewById(R.id.editTextCountry)
        if (country.editText?.text.toString() == "") {
            country.error = "Por favor, indique su país de residencia"
            ok = false
        }

        // Check password field
        val password: TextInputLayout = findViewById(R.id.editTextPassword)
        val passwordText: String = password.editText?.text.toString()
        val passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")
        if (passwordText == "") {
            password.error = "Por favor, especifique una contraseña"
            ok = false
        }
        else if (passwordText.length < 8) {
            password.error = "La contraseña debe tener al menos 8 caracteres"
            ok = false
        }
        else if (!passwordPattern.matcher(passwordText).matches()) {
            password.error = "La contraseña debe contener mayusculas, minusculas y números"
            ok = false
        }

        // Check repassword field
        val repassword: TextInputLayout = findViewById(R.id.editTextRepassword)
        val repasswordText: String = repassword.editText?.text.toString()
        if (repasswordText == "") {
            repassword.error = "Por favor, especifique una contraseña"
            ok = false
        }
        else if (repasswordText != passwordText) {
            repassword.error = "Las contraseñas no coinciden"
            ok = false
        }

        // Check eula field
        val eula: CheckBox = findViewById(R.id.checkBoxEula)
        if (!eula.isChecked and ok) {
            Toast.makeText(this, "Debe aceptar los términos y condiciones para proceder",
                Toast.LENGTH_SHORT).show()
            ok = false
        }

        return ok
    }

    fun onClickRegister(view: View) {
        // Validate data
        if (!checkFormData()) return

        // Disable interactivity while making the request
        val nickname: TextInputLayout = findViewById(R.id.editTextNickname)
        val realname: TextInputLayout = findViewById(R.id.editTextRealname)
        val email: TextInputLayout = findViewById(R.id.editTextEmail)
        val birthdate: TextInputLayout = findViewById(R.id.editTextBirthdate)
        val country: TextInputLayout = findViewById(R.id.editTextCountry)
        val image: TextInputLayout = findViewById(R.id.editTextImage)
        val password: TextInputLayout = findViewById(R.id.editTextPassword)
        val repassword: TextInputLayout = findViewById(R.id.editTextRepassword)
        val eula: CheckBox = findViewById(R.id.checkBoxEula)
        val register: Button = findViewById(R.id.buttonRegister)
        email.error = ""
        nickname.isEnabled = false
        realname.isEnabled = false
        email.isEnabled = false
        birthdate.isEnabled = false
        country.isEnabled = false
        image.isEnabled = false
        password.isEnabled = false
        repassword.isEnabled = false
        eula.isEnabled = false
        register.isEnabled = false

        val context = this  // Save activity context to launch intents

        // Send HTTP register request
        val request = HttpHandler.RegisterRequest(nickname.editText?.text.toString(),
            realname.editText?.text.toString(),
            email.editText?.text.toString(),
            password.editText?.text.toString(),
            birthdate.editText?.text.toString(),
            country.editText?.text.toString().substring(5),
            codeList[countryList.indexOf(country.editText?.text.toString().substring(5))],
            imageUri)
        MainScope().launch {
            val response = HttpHandler.makeRegisterRequest(request, context)
            nickname.isEnabled = true
            realname.isEnabled = true
            email.isEnabled = true
            birthdate.isEnabled = true
            country.isEnabled = true
            image.isEnabled = true
            password.isEnabled = true
            repassword.isEnabled = true
            eula.isEnabled = true
            register.isEnabled = true
            if (response.success) {
                Toast.makeText(context, "Cuenta creada correctamente. Revise su email para verificarla.", Toast.LENGTH_LONG).show()
                finish()
            }
            else if (response.error) {
                Toast.makeText(context, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
            }
            else if (!response.success) {
                email.error = "El correo introducido ya existe"
                Toast.makeText(context, "El correo introducido ya existe", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun accept(view: View) {}
}