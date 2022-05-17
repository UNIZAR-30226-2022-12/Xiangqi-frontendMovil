package eina.unizar.xiangqi_frontendmovil

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class Profile : Fragment(R.layout.fragment_profile) {
    private lateinit var dialog: Dialog
    private lateinit var response: HttpHandler.ProfileResponse
    private val callback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
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
        dialog.setContentView(R.layout.dialog_delete_account)
        dialog.findViewById<Button>(R.id.buttonDeleteNo).setOnClickListener { dialog.hide() }
        dialog.findViewById<Button>(R.id.buttonDeleteYes).setOnClickListener {
            MainScope().launch {
                if (HttpHandler.makeDeletionRequest().error) {
                    Toast.makeText(
                        activity,
                        "No se ha podido eliminar la cuenta",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val intent = Intent(activity, SignIn::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    Toast.makeText(
                        activity,
                        "La cuenta ha sido eliminada correctamente",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(intent)
                    activity?.finish()
                }
            }
        }

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

            if (response.error) return@launch
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

            // Set winrate progress bar data
            val winrate = view.findViewById<ProgressBar>(R.id.progressBarWinrate)
            winrate.visibility = ProgressBar.VISIBLE
            winrate.max = response.played
            winrate.progress = response.won
            view.findViewById<TextView>(R.id.textViewWinrate).text = "Partidas ganadas: ${response.won}" +
                    "  Partidas jugadas: ${response.played}"

            // Set daily performance chart data
            val dailyPlayed = mutableListOf<BarEntry>()
            val dailyWon = mutableListOf<BarEntry>()
            for (i in 0 until response.dailyPlayed.size) {
                dailyPlayed.add(BarEntry((i+1).toFloat(), response.dailyPlayed[i].toFloat()))
                dailyWon.add(BarEntry((i+1).toFloat(), response.dailyWon[i].toFloat()))
            }

            val playedDataset = BarDataSet(dailyPlayed, "Partidas jugadas")
            playedDataset.color = resources.getColor(R.color.blue, null)
            playedDataset.valueTextColor = resources.getColor(R.color.black, null)

            val wonDataset = BarDataSet(dailyWon, "Partidas ganadas")
            wonDataset.color = resources.getColor(R.color.orange, null)
            wonDataset.valueTextColor = resources.getColor(R.color.black, null)

            val data = BarData(playedDataset, wonDataset)
            data.setDrawValues(false)
            data.barWidth = 0.35f

            // Configure chart appearance and assign data
            val chart = view.findViewById<BarChart>(R.id.barChart)
            chart.description.isEnabled = false
            chart.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            chart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER

            chart.axisLeft.axisMinimum = 0F
            chart.axisLeft.axisMaximum = data.yMax
            chart.axisRight.setDrawGridLines(false)
            chart.axisRight.setDrawLabels(false)

            val labels = mutableListOf<String>("Hoy")
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val today = Calendar.getInstance()
            for (i in 0 until 6) {
                today.add(Calendar.DATE, -1)
                labels.add(sdf.format(today.time))
            }
            chart.xAxis.axisMinimum = 0F
            chart.xAxis.axisMaximum = 7F
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels.reversed())
            chart.xAxis.textSize = 9f
            chart.xAxis.setCenterAxisLabels(true)
            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            chart.xAxis.granularity = 1f
            chart.xAxis.isGranularityEnabled = true

            chart.data = data
            chart.groupBars(0f, 0.2f, 0.05f)
            chart.visibility = BarChart.VISIBLE
            view.findViewById<TextView>(R.id.textViewWeekly).visibility = TextView.VISIBLE
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