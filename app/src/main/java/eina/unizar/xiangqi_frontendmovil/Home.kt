package eina.unizar.xiangqi_frontendmovil

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView


class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var drawerLayout: DrawerLayout
    private var selected = 0
    private var refresh = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
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
        }
        setTitle(getString(title))
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.home_content, fragment!!)
            .commit()
    }
}