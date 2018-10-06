package fi.metropolia.harrytoan.gofitness.Room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity

@Entity(tableName = "CandyRoomModel")
data class CandyRoomModel(
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "des") val des: String,
        @ColumnInfo(name = "amount") val amount: Double,
        @ColumnInfo(name = "isCatch") val isCatch: Boolean,
        @ColumnInfo(name = "latitude") val latitude: Double,
        @ColumnInfo(name = "longitude") val longitude: Double
) {
    override fun toString(): String {
        return name
    }
}