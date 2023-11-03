package fgietzen.simpleruntracker.persistence

import android.content.Context
import androidx.room.Room

object RunsPersistence {

    private lateinit var db: RunsDatabase;

    fun getInstance(context: Context): RunsDatabase {
        if (!this::db.isInitialized) {
            db = Room.databaseBuilder(context, RunsDatabase::class.java, "runs")
                .allowMainThreadQueries()
                .build();
        }

        return db;
    }
}