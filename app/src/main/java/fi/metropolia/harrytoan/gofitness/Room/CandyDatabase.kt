package fi.metropolia.harrytoan.gofitness.Room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [(CandyRoomModel::class)], version = 1)
abstract class CandyDatabase: RoomDatabase() {
    abstract fun candyDAO(): CandyDAO

    // Create instance

    companion object {
        private var sInstance: CandyDatabase? = null

        @Synchronized
        fun get(context: Context): CandyDatabase {

            if (sInstance == null) {
                sInstance = Room.databaseBuilder(context.applicationContext, CandyDatabase::class.java, "app.db")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries().build()
            }

            return  sInstance!!
        }
    }
}