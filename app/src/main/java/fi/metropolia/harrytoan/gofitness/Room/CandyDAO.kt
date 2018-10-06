package fi.metropolia.harrytoan.gofitness.Room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface CandyDAO {
    @Query("SELECT * FROM CandyRoomModel")
    fun getAllCandies(): LiveData<List<CandyRoomModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(candy: CandyRoomModel)

    @Query("DELETE FROM CandyRoomModel")
    fun deleteAll()
}