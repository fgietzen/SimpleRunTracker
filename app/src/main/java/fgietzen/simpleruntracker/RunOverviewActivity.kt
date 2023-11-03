package fgietzen.simpleruntracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fgietzen.simpleruntracker.persistence.RunsPersistence

class RunOverviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.run_overview);

        val db = RunsPersistence.getInstance(applicationContext);
        val dataset = db.getRunDao().getAll().toMutableList();

        val list: RecyclerView = findViewById(R.id.run_entry_list);
        list.layoutManager = LinearLayoutManager(applicationContext);
        list.adapter = RunEntryAdapter(dataset,
            { runId ->
                if (runId == null) {
                    return@RunEntryAdapter;
                }

                db.getRunDao().remove(runId);
                dataset.removeAll { r -> r.id == runId };
            },
            { runId ->
                if (runId == null) {
                    return@RunEntryAdapter;
                }

                val intent = Intent(applicationContext, RunDetailActivity::class.java)
                    .apply { putExtra("runId", runId); };
                startActivity(intent);
            }
        );
    }
}