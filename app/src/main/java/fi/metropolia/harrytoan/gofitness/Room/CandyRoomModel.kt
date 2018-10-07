package fi.metropolia.harrytoan.gofitness.Room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "CandyRoomModel")
data class CandyRoomModel(
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "des") val des: String,
        @ColumnInfo(name = "amount") val amount: Double,
        @ColumnInfo(name = "isCatch") var isCatch: Boolean,
        @ColumnInfo(name = "latitude") val latitude: Double,
        @ColumnInfo(name = "longitude") val longitude: Double,
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id") val id: Long = 0
) {
    override fun toString(): String {
        return name
    }
}