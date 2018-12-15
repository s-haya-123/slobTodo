package overlay.camera.com.slobtodo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.input_layout.*
import kotlinx.android.synthetic.main.input_line.*

class InputScheduleFlagment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.input_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    private fun createInpputLine(){
        layoutInflater.inflate(R.layout.input_line, input_list)
        return
    }

    companion object {
        fun newInstance(): InputScheduleFlagment {
            val fragment = InputScheduleFlagment()
            return fragment
        }
    }
}