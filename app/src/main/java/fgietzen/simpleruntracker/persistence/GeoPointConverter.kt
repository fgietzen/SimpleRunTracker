package fgietzen.simpleruntracker.persistence

import androidx.room.TypeConverter
import org.osmdroid.util.GeoPoint

class GeoPointConverter {

    private val separator = ";";

    @TypeConverter
    fun convertFromString(doubleString: String): List<GeoPoint> {
        if (doubleString.isBlank()) {
            return emptyList();
        }

        return doubleString
            .split(separator)
            .map { s -> GeoPoint.fromDoubleString(s, ',') }
            .toList();
    }

    @TypeConverter
    fun convertToString(points: List<GeoPoint>): String {
        val s =  points.joinToString(separator = separator, transform = { p -> p.toDoubleString() });
        return s;
    }
}