package fi.metropolia.harrytoan.gofitness.Room

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import fi.metropolia.harrytoan.gofitness.Candy

class ViewModel(application: Application) : AndroidViewModel(application) {

    private val candyRoomDatabase: CandyDatabase = CandyDatabase.get(application)

    val allCandies = candyRoomDatabase.candyDAO().getAllCandies()

    fun insertCandy(candy: CandyRoomModel) = candyRoomDatabase.candyDAO().insert(candy)

    fun deleteAllCandies() = candyRoomDatabase.candyDAO().deleteAll()

    fun update(candy: CandyRoomModel) {
        candyRoomDatabase.candyDAO().update(candy.isCatch, candy.id)
    }

}