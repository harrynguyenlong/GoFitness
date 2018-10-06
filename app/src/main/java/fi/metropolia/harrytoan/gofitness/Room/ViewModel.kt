package fi.metropolia.harrytoan.gofitness.Room

import android.app.Application
import android.arch.lifecycle.AndroidViewModel

class ViewModel(application: Application) : AndroidViewModel(application) {

    private val candyRoomDatabase: CandyDatabase = CandyDatabase.get(application)

    val allCandies = candyRoomDatabase.candyDAO().getAllCandies()

    fun insertCandy(candy: CandyRoomModel) = candyRoomDatabase.candyDAO().insert(candy)

    fun deleteAllCandies() = candyRoomDatabase.candyDAO().deleteAll()


}