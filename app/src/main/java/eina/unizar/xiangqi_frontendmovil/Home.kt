package eina.unizar.xiangqi_frontendmovil

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import eina.unizar.xiangqi_frontendmovil.home_fragments.*


class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var drawerLayout: DrawerLayout
    private var selected = 0
    private var refresh = false
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Construct exit dialog
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_exit)

        // Link ActionBar to drawerLayout
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.home_open,
            R.string.home_close
        ) {
            override fun onDrawerClosed(drawerView: View) {
                if (refresh) {
                    replaceFragment()
                    refresh = false
                }
            }
        }
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Enable sidebar interaction
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Preselect first item
        val menuItem = navigationView.menu.getItem(0)
        onNavigationItemSelected(menuItem)
        menuItem.isChecked = true
        refresh = false
        replaceFragment()

        // Init socket connection
        SocketHandler.connect(this)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            dialog.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START)
                else drawerLayout.openDrawer(GravityCompat.START)
            else -> return false
        }
        return true
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId != selected) refresh = true
        selected = menuItem.itemId
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun replaceFragment() {
        var title = 0
        var fragment: Fragment? = null
        when (selected) {
            R.id.nav_games -> {
                title = R.string.games_title
                fragment = Games()
            }
            R.id.nav_profile -> {
                title = R.string.profile_title
                fragment = Profile()
            }
            R.id.nav_friends -> {
                title = R.string.friends_title
                fragment = Friends()
            }
            R.id.nav_ranking -> {
                title = R.string.ranking_title
                fragment = Ranking()
            }
            R.id.nav_store -> {
                title = R.string.store_title
                fragment = Store()
            }
            R.id.nav_history -> {
                title = R.string.history_title
                fragment = History()
            }
        }
        setTitle(getString(title))
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.home_content, fragment!!)
            .commit()
    }

    fun onClickCancel(view: View) {
        dialog.hide()
    }

    fun onClickExit(view: View) {
        SocketHandler.disconnect()
        val intent = Intent(this, SignIn::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}