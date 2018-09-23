package fi.metropolia.harrytoan.gofitness

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class SongsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_songs, container, false)

    companion object {
        fun newInstance(): SongsFragment = SongsFragment()
    }
}