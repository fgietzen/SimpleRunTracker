package fgietzen.simpleruntracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import fgietzen.simpleruntracker.persistence.RunsPersistence
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.lang.IllegalArgumentException

class RunDetailActivity : AppCompatActivity() {

    private lateinit var map : MapView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.run_detail);

        val db = RunsPersistence.getInstance(applicationContext);

        val runId = intent.getIntExtra("runId", -1);
        if (runId < 0) {
            throw IllegalArgumentException("runId was not provided!");
        }

        val run = db.getRunDao().getById(runId);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.controller.setCenter(run.path.last());
        map.controller.setZoom(17.5);

        val pathLine = Polyline(map);
        pathLine.setPoints(run.path);
        map.overlays.add(pathLine);

        val finishMarker = Marker(map);
        finishMarker.icon = ContextCompat.getDrawable(applicationContext, org.osmdroid.library.R.drawable.person);
        finishMarker.position = run.path.last();
        map.overlays.add(finishMarker);
    }

    override fun onResume() {
        super.onResume();

        map.onResume();
    }

    override fun onPause() {
        super.onPause();

        map.onPause();
    }
}