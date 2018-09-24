package fi.metropolia.harrytoan.gofitness

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_home.*

interface HomeFragmentInterface {
    fun onLetsGoButtonClicked()
}

class HomeFragment : Fragment() {

    private var listener: HomeFragmentInterface? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {



        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        goToMapBtn.setOnClickListener {
            listener?.onLetsGoButtonClicked()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is HomeFragmentInterface) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()

        listener = null
    }


    companion object {
        fun newInstance(): HomeFragment = HomeFragment()
    }
}