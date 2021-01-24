package com.example.sht.learningstar

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import com.example.sht.learningstar.MainSide.KeXie
import com.example.sht.learningstar.MainSide.NanFangZhouMo
import com.example.sht.learningstar.MainSide.TexntMaps
import com.example.sht.learningstar.MainSide.XinLanWeiBo

class MainActivity : AppCompatActivity() {

    // region override

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragment(Class_Environment.Environment())
        findViewById<BottomNavigationView>(R.id.navigation).setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        // Toolbar 代替 ActionBar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // DrawerLayout
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        // Toolbar 上面最左边显示三杠图标监听 DrawerLayout
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.title_dashboard)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val search = findViewById<ImageButton>(R.id.search)
        search.setOnClickListener { startActivity(Intent(this@MainActivity, ZhihuActivity::class.java)) }
        search.scaleType = ImageView.ScaleType.FIT_START

        val photo = findViewById<ImageButton>(R.id.toolbar_code)
        photo.setOnClickListener { startActivity(Intent(MediaStore.ACTION_IMAGE_CAPTURE)) }
        photo.scaleType = ImageView.ScaleType.CENTER_INSIDE

        val q = findViewById<View>(R.id.q) as Button
        q.setOnClickListener {
            val intent = Intent(this@MainActivity, KeXie::class.java)
            startActivity(intent)
        }
        val a = findViewById<View>(R.id.a) as Button
        a.setOnClickListener {
            val intent = Intent(this@MainActivity, NanFangZhouMo::class.java)
            startActivity(intent)
        }
        val z = findViewById<View>(R.id.z) as Button
        z.setOnClickListener {
            val intent = Intent(this@MainActivity, XinLanWeiBo::class.java)
            startActivity(intent)
        }
        val w = findViewById<View>(R.id.w) as Button
        w.setOnClickListener {
            val intent = Intent(this@MainActivity, TexntMaps::class.java)
            startActivity(intent)
        }
    }

    // endregion

    // region lambda

    private val onNavigationItemSelectedListener: (MenuItem) -> Boolean = {
        when (it.itemId) {
            R.id.home_page -> {
                replaceFragment(Class_Environment.Environment())
                true
            }
            R.id.knowledge -> {
                replaceFragment(Chat())
                true
            }
            R.id.mine -> {
                replaceFragment(Find())
                true
            }
            else -> false
        }
    }

    // endregion

    // region private

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment, fragment)
        transaction.commit()
    }

    // endregion
}