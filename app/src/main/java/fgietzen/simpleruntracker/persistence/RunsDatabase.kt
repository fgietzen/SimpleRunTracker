package fgietzen.simpleruntracker.persistence

import androidx.room.*

@Database(version = 1, entities = [Run::class])
@TypeConverters(GeoPointConverter::class)
abstract class RunsDatabase : RoomDatabase() {
    abstract fun getRunDao() : RunDao;
}