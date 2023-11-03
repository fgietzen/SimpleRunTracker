package fgietzen.simpleruntracker.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RunDao {
    @Insert
    fun insert(vararg run: Run);

    @Query("SELECT * FROM run")
    fun getAll(): List<Run>;

    @Query("SELECT * FROM run WHERE id = :id")
    fun getById(vararg id: Int) : Run;

    @Delete
    fun remove(vararg run: Run);

    @Query("DELETE FROM run WHERE id = :id")
    fun remove(vararg id: Int);
}