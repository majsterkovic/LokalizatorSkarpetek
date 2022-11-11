package pl.mariusz.lokalizatorskarpetek

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.MinimapOverlay
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MainActivity : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map : MapView


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        setContentView(R.layout.activity_main)

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)

        val mapController = map.controller

        val mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(applicationContext), map)
        mLocationOverlay.enableMyLocation()
        mLocationOverlay.isDrawAccuracyEnabled = true
        mLocationOverlay.runOnFirstFix { runOnUiThread {
            mapController.animateTo(mLocationOverlay.myLocation)

        } }
        map.overlays.add(mLocationOverlay)

        val compassOverlay = CompassOverlay(applicationContext, InternalCompassOrientationProvider(applicationContext), map)
        compassOverlay.enableCompass()
        map.overlays.add(compassOverlay)

        val rotationGestureOverlay = RotationGestureOverlay(map)
        rotationGestureOverlay.isEnabled
        map.setMultiTouchControls(true)
        map.overlays.add(rotationGestureOverlay)


        val marker = Marker(map)
        marker.position = GeoPoint(51.70854, 17.42532)
        marker.title = "Skarpetki Mariusza"

        map.overlays.add(marker)
        mapController.setZoom(18)

        val dm : DisplayMetrics = this.resources.displayMetrics
        val scaleBarOverlay = ScaleBarOverlay(map)
        scaleBarOverlay.setCentred(true)
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10)
        map.overlays.add(scaleBarOverlay)

        val minimapOverlay = MinimapOverlay(this, map.tileRequestCompleteHandler)
        minimapOverlay.width = dm.widthPixels / 5
        minimapOverlay.height = dm.heightPixels / 5
        map.overlays.add(minimapOverlay)

        map.invalidate()

    }

    override fun onResume() {

        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }
}