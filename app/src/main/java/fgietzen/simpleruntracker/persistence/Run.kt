package fgietzen.simpleruntracker.persistence

import androidx.room.*
import org.osmdroid.util.GeoPoint

@Entity
data class Run (
    @PrimaryKey(autoGenerate = true)
    val id : Int? = null,
    @ColumnInfo(name = "distance")
    val distance: Double,
    @ColumnInfo(name = "time")
    val time: Long,
    @ColumnInfo(name = "path")
    val path: List<GeoPoint>
) {
    constructor(distance: Double, time: Long, path: List<GeoPoint>)
            : this(null, distance, time, path);

    override fun toString(): String {
        return "id: $id, distance: $distance, time: $time";
    }
}