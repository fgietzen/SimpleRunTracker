package fgietzen.simpleruntracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import fgietzen.simpleruntracker.persistence.Run
import fgietzen.simpleruntracker.persistence.RunsPersistence
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.util.*
import java.util.stream.Stream
import kotlin.streams.toList

class MainActivity : AppCompatActivity(), LocationListener {

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private lateinit var map : MapView;
    private lateinit var traveled : TextView;
    private lateinit var time : TextView;
    private lateinit var startStop : Button;

    private lateinit var currentMarker: Marker;
    private lateinit var pathLine: Polyline;

    private var running: Boolean = false;
    private var startTime: Long = 0;

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        startLocationUpdates();

        Configuration.getInstance().userAgentValue = applicationContext.packageName;

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        currentMarker = Marker(map);
        currentMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        currentMarker.icon = ContextCompat.getDrawable(applicationContext,
            org.osmdroid.library.R.drawable.marker_default_focused_base)

        initMapOverlay();
        traveled = findViewById(R.id.text_traveled);

        time = findViewById(R.id.text_time);
        Timer().schedule(TimerUpdater {
            if (running) {
                runOnUiThread { time.text = String.format("%d s", elapsedTime()) };
            }
        }, 0, 100);

        startStop = findViewById(R.id.button_start_stop);
        startStop.setOnClickListener {
            running = !running;

            if (running) {
                startStop.setText(R.string.stop);

                startTime = System.currentTimeMillis();
                pathLine.addPoint(currentMarker.position);
            } else {
                startStop.setText(R.string.start);

                RunsPersistence.getInstance(applicationContext).getRunDao()
                        .insert(Run(pathLine.distance, elapsedTime(), pathLine.actualPoints));

                map.overlays.clear();
                initMapOverlay();
            }
        };
    }

    private fun initMapOverlay() {
        pathLine = Polyline(map);
        map.overlays.add(currentMarker);
        map.overlays.add(pathLine);
    }

    private fun elapsedTime(): Long {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    override fun onResume() {
        super.onResume();

        map.onResume();
    }

    override fun onPause() {
        super.onPause();

        map.onPause();
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.default_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_overview -> {
                val intent = Intent(applicationContext, RunOverviewActivity::class.java);
                startActivity(intent);
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startLocationUpdates() {
        val permissions = setOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
        val hasPermissions = permissions.stream()
            .allMatch { p -> ActivityCompat.checkSelfPermission(this, p) == PackageManager.PERMISSION_GRANTED };
        if (!hasPermissions) {
            requestPermissionsIfNecessary(permissions.stream());
            return;
        }

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5.0f, this);
    }

    override fun onLocationChanged(loc: Location) {
        val pos = GeoPoint(loc);
        map.controller.setCenter(pos);
        map.controller.setZoom(17.5);

        currentMarker.position = pos;

        if (running) {
            pathLine.addPoint(pos);

            traveled.text = String.format("%.2f km", pathLine.distance/1000.0);
            time.text = String.format("%d s", (System.currentTimeMillis() - startTime) / 1000);
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (permissions.isNotEmpty()) {
            requestPermissionsIfNecessary(Arrays.stream(permissions));
        }

        startLocationUpdates();
    }

    private fun requestPermissionsIfNecessary(permissions: Stream<out String>) {
        val permsToRequest = permissions.filter { perm ->
            ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED
        }.toList();

        if (permsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permsToRequest.toTypedArray(), REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}
