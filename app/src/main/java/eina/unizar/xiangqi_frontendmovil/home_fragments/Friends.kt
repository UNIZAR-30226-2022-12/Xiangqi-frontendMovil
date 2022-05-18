package eina.unizar.xiangqi_frontendmovil.home_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import eina.unizar.xiangqi_frontendmovil.HttpHandler
import eina.unizar.xiangqi_frontendmovil.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class Friends : Fragment(R.layout.fragment_friends) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainScope().launch {
            // Retrieve friends data
            val response = HttpHandler.makeProfileRequest(HttpHandler.ProfileRequest(null))
            if (!isAdded) return@launch

            // Hide loading bar and fill text fields
            view.findViewById<ProgressBar>(R.id.progressBar).visibility = ProgressBar.GONE
            view.findViewById<TextView>(R.id.textViewLoading).visibility = TextView.GONE

            if (response.error) return@launch
        }
    }
}