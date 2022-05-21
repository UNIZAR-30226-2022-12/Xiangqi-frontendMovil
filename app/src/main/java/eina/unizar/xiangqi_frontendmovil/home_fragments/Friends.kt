package eina.unizar.xiangqi_frontendmovil.home_fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import com.google.android.material.imageview.ShapeableImageView
import eina.unizar.xiangqi_frontendmovil.HttpHandler
import eina.unizar.xiangqi_frontendmovil.OtherProfile
import eina.unizar.xiangqi_frontendmovil.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class Friends : Fragment(R.layout.fragment_friends) {
    private val callback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.home_content, Games())
            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainScope().launch {
            // Retrieve friend requests data
            val requests = HttpHandler.makeFriendRequestsRequest()
            if (!isAdded) return@launch
            val friends = HttpHandler.makeFriendsRequest()
            if (!isAdded) return@launch

            // Hide loading bar and fill text fields
            view.findViewById<ProgressBar>(R.id.progressBar).visibility = ProgressBar.GONE
            view.findViewById<TextView>(R.id.textViewLoading).visibility = TextView.GONE

            if (requests.error || friends.error) return@launch
            view.findViewById<TextView>(R.id.textViewFriendRequestsHeader).visibility = TextView.VISIBLE
            view.findViewById<LinearLayout>(R.id.linearLayoutFriendRequestsHeader).visibility = LinearLayout.VISIBLE
            view.findViewById<View>(R.id.divider).visibility = View.VISIBLE

            view.findViewById<TextView>(R.id.textViewFriendsHeader).visibility = TextView.VISIBLE
            view.findViewById<LinearLayout>(R.id.linearLayoutFriendsHeader).visibility = LinearLayout.VISIBLE
            view.findViewById<View>(R.id.divider).visibility = View.VISIBLE
            
            if (requests.ids.isEmpty())
                view.findViewById<TextView>(R.id.textViewFriendsRequestsNotFound).visibility = TextView.VISIBLE

            val requestsTable = view.findViewById<LinearLayout>(R.id.linearLayoutRequestsTable)
            val rowOffset = requestsTable.childCount
            // If there are requests, fill the requestsTable
            for (i in 0 until  requests.ids.size) {
                val layout = layoutInflater.inflate(R.layout.friends_requests_row, requestsTable)
                val row = requestsTable[i+rowOffset]
                row.findViewById<TextView>(R.id.textViewName).text =  requests.realnames[i]
                row.findViewById<TextView>(R.id.textViewBirthdate).text =  requests.birthdates[i]

                val flagOffset = 0x1F1E6
                val asciiOffset = 0x41
                val firstChar = Character.codePointAt(requests.codes[i], 0) - asciiOffset + flagOffset
                val secondChar = Character.codePointAt(requests.codes[i], 1) - asciiOffset + flagOffset
                row.findViewById<TextView>(R.id.textViewCountry).text = String(Character.toChars(firstChar)) +
                        String(Character.toChars(secondChar)) + " " +  requests.countries[i]

                val nickname = row.findViewById<TextView>(R.id.textViewUser)
                nickname.text =  requests.nicknames[i]
                nickname.setOnClickListener {
                    val intent = Intent(requireContext(), OtherProfile::class.java)
                    intent.putExtra("id",  requests.ids[i])
                    intent.putExtra("nickname",  requests.nicknames[i])
                    callback.launch(intent)
                }

                row.findViewById<ImageView>(R.id.imageViewAccept).setOnClickListener {
                    // Debug, integrate when appropriate
                    Toast.makeText(requireContext(), "Accept request from ${requests.ids[i]}", Toast.LENGTH_SHORT).show()
                }

                row.findViewById<ImageView>(R.id.imageViewReject).setOnClickListener {
                    // Debug, integrate when appropriate
                    Toast.makeText(requireContext(), "Reject request from ${requests.ids[i]}", Toast.LENGTH_SHORT).show()
                }
            }

            // Load opponent images afterwards to reduce loading time
            for (i in 0 until  requests.ids.size) {
                if ( requests.hasImages[i]) {
                    val image = HttpHandler.makeImageRequest(HttpHandler.ImageRequest(requests.ids[i])).image
                    requestsTable[i+rowOffset].findViewById<ImageView>(R.id.imageViewUser).setImageDrawable(image)
                }
            }
        }
    }
}