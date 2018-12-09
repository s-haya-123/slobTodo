package overlay.camera.com.slobtodo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*

class InputScheduleFlagment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.input_layout, container, false)
    }
    companion object {
        fun newInstance(): InputScheduleFlagment {
            val fragment = InputScheduleFlagment()
            return fragment
        }
    }
}