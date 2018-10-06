package fi.metropolia.harrytoan.gofitness

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import fi.metropolia.harrytoan.gofitness.Room.ViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), HomeFragmentInterface {

    private val MAP_FRAGMENT = "MapFragment"
    private val HOME_FRAGMENT = "MapFragment"
    private val USER_FRAGMENT = "MapFragment"
    private val CAMERA_FRAGMENT = "MapFragment"

    // ViewModal for Room

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ViewModel::class.java)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val homeFragment = HomeFragment.newInstance()
                homeFragment.listener = this
                openFragment(homeFragment, HOME_FRAGMENT)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_user -> {
                val userFragment = UserFragment.newInstance()
                openFragment(userFragment, USER_FRAGMENT)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_maps -> {
                val mapFragment = MapFragment(viewModel)
                openFragment(mapFragment, MAP_FRAGMENT)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment, tag)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
        askForPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
        askForPermission(android.Manifest.permission.INTERNET)


        supportFragmentManager.beginTransaction().replace(R.id.container, HomeFragment()).commit()

        viewModel.allCandies.observe(this, Observer {
            it?.let {
                val mapFragment = supportFragmentManager.findFragmentByTag(MAP_FRAGMENT) as MapFragment

                if (mapFragment != null && mapFragment.isVisible) {
                    mapFragment.updateMap(it)
                }
            }
        })

    }

    override fun onLetsGoButtonClicked() {
        navigationView.selectedItemId = R.id.navigation_maps
    }

    private fun askForPermission(permission: String) {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 0)
        }
    }
}
