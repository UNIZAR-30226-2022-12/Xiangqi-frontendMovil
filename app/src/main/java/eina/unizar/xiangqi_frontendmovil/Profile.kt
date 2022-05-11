package eina.unizar.xiangqi_frontendmovil

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class Profile : Fragment(R.layout.fragment_profile) {
    private lateinit var dialog: Dialog
    private lateinit var response: HttpHandler.ProfileResponse
    val callback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.home_content, Profile())
                .commit()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Construct delete account dialog
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.fragment_delete_account)
        dialog.findViewById<Button>(R.id.buttonDeleteNo).setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View) {
                dialog.hide()
            }
        })
        dialog.findViewById<Button>(R.id.buttonDeleteYes).setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View) {
                MainScope().launch {
                    if (HttpHandler.makeDeletionRequest().error){
                        Toast.makeText(activity ,"No se ha podido eliminar la cuenta", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        val intent = Intent(activity, SignIn::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        Toast.makeText(activity ,"La cuenta ha sido eliminada correctamente", Toast.LENGTH_LONG).show()
                        startActivity(intent)
                        activity?.finish()
                    }
                }
            }
        })

        MainScope().launch {
            // Retrieve profile data
            response = HttpHandler.makeProfileRequest(HttpHandler.ProfileRequest(null))
            if (response.image) {
                val image = HttpHandler.makeImageRequest(HttpHandler.ImageRequest(null)).image
                view.findViewById<ImageView>(R.id.imageViewProfile).setImageDrawable(image)
            }

            // Hide loading bar and fill text fields
            view.findViewById<ProgressBar>(R.id.progressBar).visibility = ProgressBar.GONE
            view.findViewById<TextView>(R.id.textViewLoading).visibility = TextView.GONE

            view.findViewById<ImageView>(R.id.imageViewProfile).visibility = ImageView.VISIBLE
            view.findViewById<TextView>(R.id.textViewBirthdateTitle).visibility = TextView.VISIBLE
            view.findViewById<TextView>(R.id.textViewRealname).text = response.realname
            view.findViewById<TextView>(R.id.textViewNickname).text = "#${response.nickname}"
            view.findViewById<TextView>(R.id.textViewBirthdate).text = response.birthdate
            val flagOffset = 0x1F1E6
            val asciiOffset = 0x41
            val firstChar = Character.codePointAt(response.code, 0) - asciiOffset + flagOffset
            val secondChar = Character.codePointAt(response.code, 1) - asciiOffset + flagOffset
            view.findViewById<TextView>(R.id.textViewCountry).text = String(Character.toChars(firstChar)) +
                    String(Character.toChars(secondChar)) + " " + response.country

            view.findViewById<ImageView>(R.id.imageViewPoints).visibility = ImageView.VISIBLE
            view.findViewById<TextView>(R.id.textViewPointsTitle).visibility = TextView.VISIBLE
            view.findViewById<TextView>(R.id.textViewPoints).text = "${response.points} puntos"

            view.findViewById<ImageView>(R.id.imageViewRanking).visibility = ImageView.VISIBLE
            view.findViewById<TextView>(R.id.textViewRankingTitle).visibility = TextView.VISIBLE
            view.findViewById<TextView>(R.id.textViewRanking).text = "Puesto ${response.points}"

            view.findViewById<ImageView>(R.id.imageViewCalendar).visibility = ImageView.VISIBLE
            view.findViewById<TextView>(R.id.textViewCalendarTitle).visibility = TextView.VISIBLE
            view.findViewById<TextView>(R.id.textViewCalendar).text = response.registerdate

            view.findViewById<ImageView>(R.id.imageViewFriends).visibility = ImageView.VISIBLE
            view.findViewById<TextView>(R.id.textViewFriendsTitle).visibility = TextView.VISIBLE
            view.findViewById<TextView>(R.id.textViewFriends).text = "${response.points} amigos"

            val winrate = view.findViewById<ProgressBar>(R.id.progressBarWinrate)
            winrate.visibility = ProgressBar.VISIBLE
            winrate.max = response.played
            winrate.progress = response.won
            view.findViewById<TextView>(R.id.textViewWinrate).text = "Partidas ganadas: ${response.won}" +
                    "  Partidas jugadas: ${response.played}"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val result = super.onCreateOptionsMenu(menu, inflater)
        menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, R.string.edit_title)
        menu.add(Menu.NONE, Menu.FIRST+1, Menu.NONE, R.string.profile_delete)
        return result
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            Menu.FIRST -> {
                val intent = Intent(activity, EditProfile::class.java)
                intent.putExtra("nickname", response.nickname)
                intent.putExtra("realname", response.realname)
                intent.putExtra("birthdate", response.birthdate)
                intent.putExtra("country", response.country)
                callback.launch(intent)
                return true
            }
            Menu.FIRST+1 ->  {
                dialog.show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}