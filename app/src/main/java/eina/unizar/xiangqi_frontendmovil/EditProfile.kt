package eina.unizar.xiangqi_frontendmovil

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.regex.Pattern


class EditProfile : AppCompatActivity() {
    private var repasswordEnabled = false
    private var countryList = listOf("Australia", "Brazil", "China", "Egypt", "France", "Germany",
    "India", "Japan", "Spain", "United States")
    private var codeList = listOf("AU", "BR", "CN", "EG", "FR", "DE", "IN", "JP", "ES", "US")
    private var imageUri: Uri? = null
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
        setContentView(R.layout.activity_edit_profile)
        setTitle(R.string.edit_title)

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
        nickname.editText?.setText(intent.getStringExtra("nickname"))
        nickname.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                nickname.error = ""
            }
        })

        val realname: TextInputLayout = findViewById(R.id.editTextRealname)
        realname.editText?.setText(intent.getStringExtra("realname"))
        realname.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                realname.error = ""
            }
        })

        val birthdate: TextInputLayout = findViewById(R.id.editTextBirthdate)
        birthdate.editText?.setText(intent.getStringExtra("birthdate"))
        birthdate.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                birthdate.error = ""
            }
        })

        country.editText?.setText(intent.getStringExtra("country"))
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
                if (s != null) {
                    if (s.length > 0 && !repasswordEnabled) showRepassword()
                    else if (s.length == 0 && repasswordEnabled) hideRepassword()
                }
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
                country.editText?.setText(flagList[countryList.indexOf(intent.getStringExtra("country"))])
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

    fun showRepassword() {
        repasswordEnabled = true
        findViewById<TextInputLayout>(R.id.editTextRepassword).visibility = TextInputLayout.VISIBLE
        val constraintLayout = findViewById<ConstraintLayout>(R.id.parentLayout)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.connect(
            R.id.buttonConfirm,
            ConstraintSet.TOP,
            R.id.editTextRepassword,
            ConstraintSet.BOTTOM,
            20
        )
        constraintSet.applyTo(constraintLayout)
    }

    fun hideRepassword() {
        repasswordEnabled = false
        findViewById<TextInputLayout>(R.id.editTextRepassword).visibility = TextInputLayout.GONE
        val constraintLayout = findViewById<ConstraintLayout>(R.id.parentLayout)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.connect(
            R.id.buttonConfirm,
            ConstraintSet.TOP,
            R.id.editTextPassword,
            ConstraintSet.BOTTOM,
            20
        )
        constraintSet.applyTo(constraintLayout)
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
        if (passwordText != "") {
            if (passwordText.length < 8) {
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
        }

        return ok
    }

    fun onClickConfirm(view: View) {
        // Validate data
        if (!checkFormData()) return

        // Disable interactivity while making the request
        val nickname: TextInputLayout = findViewById(R.id.editTextNickname)
        val realname: TextInputLayout = findViewById(R.id.editTextRealname)
        val birthdate: TextInputLayout = findViewById(R.id.editTextBirthdate)
        val country: TextInputLayout = findViewById(R.id.editTextCountry)
        val image: TextInputLayout = findViewById(R.id.editTextImage)
        val password: TextInputLayout = findViewById(R.id.editTextPassword)
        val repassword: TextInputLayout = findViewById(R.id.editTextRepassword)
        val confirm: Button = findViewById(R.id.buttonConfirm)
        nickname.isEnabled = false
        realname.isEnabled = false
        birthdate.isEnabled = false
        country.isEnabled = false
        image.isEnabled = false
        password.isEnabled = false
        repassword.isEnabled = false
        confirm.isEnabled = false

        val context = this  // Save activity context to launch intents

        // Send HTTP register request
        var pwd = "a"
        if (password.editText?.text.toString() != "") pwd = password.editText?.text.toString()
        val request = HttpHandler.EditRequest(nickname.editText?.text.toString(),
            realname.editText?.text.toString(),
            pwd,
            birthdate.editText?.text.toString(),
            country.editText?.text.toString().substring(5),
            imageUri)
        MainScope().launch {
            val response = HttpHandler.makeEditRequest(request, context)
            nickname.isEnabled = true
            realname.isEnabled = true
            birthdate.isEnabled = true
            country.isEnabled = true
            image.isEnabled = true
            password.isEnabled = true
            repassword.isEnabled = true
            confirm.isEnabled = true
            if (response.success) {
                Toast.makeText(context, "Perfil modificado correctamente", Toast.LENGTH_LONG).show()
                setResult(RESULT_OK)
                finish()
            }
            else if (response.error) {
                Toast.makeText(context, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
            }
        }
    }
}