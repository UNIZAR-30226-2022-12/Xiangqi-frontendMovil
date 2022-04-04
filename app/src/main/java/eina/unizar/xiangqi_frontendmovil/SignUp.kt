package eina.unizar.xiangqi_frontendmovil

import android.app.Dialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Html
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout

class SignUp : AppCompatActivity() {
    private var image: Bitmap? = null
    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Handle the returned Uri
        if (uri != null) {
            // Retrieve file name
            val cursor = contentResolver.query(uri, null, null, null, null)
            val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor?.moveToFirst()

            // Write file name
            val image: TextInputLayout = findViewById(R.id.editTextImage)
            image.editText?.setText(nameIndex?.let { cursor.getString(it) })
        }
        //val options = BitmapFactory.Options()
        //options.inPreferredConfig = Bitmap.Config.ARGB_8888
        //image = BitmapFactory.decodeFile(uri.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Set country dropdown menu items
        val menu: TextInputLayout = findViewById(R.id.editTextCountry)
        val countryList = listOf("Australia", "Brazil", "China", "Egypt", "France", "Germany",
            "India", "Japan", "Spain", "United States")
        val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, countryList)
        (menu.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    fun onClickBirthdate(view: View) {
        val newFragment = DatePickerFragment.newInstance { _, year, month, day ->
            // +1 because January is zero
            val selectedDate = "${month+1}/$day/$year"
            findViewById<TextInputLayout>(R.id.editTextBirthdate).editText?.setText(selectedDate)
        }
        newFragment.show(supportFragmentManager, "datePicker")
    }

    fun onClickImage(view: View) {
        getImage.launch("image/*")
    }

    fun onClickEula(view: View) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.fragment_eula)
        dialog.findViewById<TextView>(R.id.textView).text = Html.fromHtml(resources.getString(R.string.eula), 0)
        dialog.show();
    }
}