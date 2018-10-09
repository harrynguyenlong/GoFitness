package fi.metropolia.harrytoan.gofitness

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity(), HomeFragmentInterface, MapFragmentDelegate {

    private val MAP_FRAGMENT = "MapFragment"
    private val HOME_FRAGMENT = "MapFragment"
    private val USER_FRAGMENT = "MapFragment"
    private val CAMERA_FRAGMENT = "MapFragment"
    private val CHANNEL_ID = "ChannelID"
    private val NotificationId = 1

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
                val mapFragment = MapFragment()
                mapFragment.listener = this
                openFragment(mapFragment, MAP_FRAGMENT)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("MainActivity", "Got the message")

            var mBuilder = NotificationCompat.Builder(context!!, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_candy)
                    .setContentTitle(getString(R.string.New_achievement))
                    .setContentText(getString(R.string.title))
                    .setStyle(NotificationCompat.BigTextStyle().bigText(getString(R.string.title)))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(context)) {

                notify(NotificationId, mBuilder.build())
            }

        }
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

        createNotificationChannel()

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, IntentFilter("complete-first-100m"))

        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
        askForPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
        askForPermission(android.Manifest.permission.INTERNET)


        supportFragmentManager.beginTransaction().replace(R.id.container, HomeFragment()).commit()

    }

    override fun onDestroy() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)

        super.onDestroy()

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

    override fun didCatchCandy() {
        // toast("You just catch a candy!")
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
