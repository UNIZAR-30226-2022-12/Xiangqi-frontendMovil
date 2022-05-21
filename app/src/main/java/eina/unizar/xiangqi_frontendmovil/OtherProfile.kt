package eina.unizar.xiangqi_frontendmovil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
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

class OtherProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile)
        title = "Perfil de ${intent.getStringExtra("nickname")}"
        
        // Enable backward navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        MainScope().launch {
            // Retrieve profile data
            val response = HttpHandler.makeProfileRequest(HttpHandler.ProfileRequest(intent.getIntExtra("id", 0)))
            if (response.image) {
                val image = HttpHandler.makeImageRequest(HttpHandler.ImageRequest(intent.getIntExtra("id", 0))).image
                findViewById<ImageView>(R.id.imageViewProfile).setImageDrawable(image)
            }

            // Hide loading bar and fill text fields
            findViewById<ProgressBar>(R.id.progressBar).visibility = ProgressBar.GONE
            findViewById<TextView>(R.id.textViewLoading).visibility = TextView.GONE

            if (response.error) return@launch
            findViewById<ImageView>(R.id.imageViewProfile).visibility = ImageView.VISIBLE
            findViewById<TextView>(R.id.textViewBirthdateTitle).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textViewRealname).text = response.realname
            findViewById<TextView>(R.id.textViewNickname).text = "#${response.nickname}"
            findViewById<TextView>(R.id.textViewBirthdate).text = response.birthdate
            val flagOffset = 0x1F1E6
            val asciiOffset = 0x41
            val firstChar = Character.codePointAt(response.code, 0) - asciiOffset + flagOffset
            val secondChar = Character.codePointAt(response.code, 1) - asciiOffset + flagOffset
            findViewById<TextView>(R.id.textViewCountry).text = String(Character.toChars(firstChar)) +
                    String(Character.toChars(secondChar)) + " " + response.country

            findViewById<ImageView>(R.id.imageViewPoints).visibility = ImageView.VISIBLE
            findViewById<TextView>(R.id.textViewPointsTitle).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textViewPoints).text = "${response.points} puntos"

            findViewById<ImageView>(R.id.imageViewRanking).visibility = ImageView.VISIBLE
            findViewById<TextView>(R.id.textViewRankingTitle).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textViewRanking).text = "Puesto ${response.ranking+1}"

            findViewById<ImageView>(R.id.imageViewCalendar).visibility = ImageView.VISIBLE
            findViewById<TextView>(R.id.textViewCalendarTitle).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textViewCalendar).text = response.registerdate

            findViewById<ImageView>(R.id.imageViewFriends).visibility = ImageView.VISIBLE
            findViewById<TextView>(R.id.textViewFriendsTitle).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textViewFriends).text = "${response.friends} amigos"

            // Set winrate progress bar data
            val winrate = findViewById<ProgressBar>(R.id.progressBarWinrate)
            winrate.visibility = ProgressBar.VISIBLE
            winrate.max = response.played
            winrate.progress = response.won
            findViewById<TextView>(R.id.textViewWinrate).text = "Partidas ganadas: ${response.won}" +
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
            val chart = findViewById<BarChart>(R.id.barChart)
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
            chart.setTouchEnabled(false)
            findViewById<TextView>(R.id.textViewWeekly).visibility = TextView.VISIBLE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}