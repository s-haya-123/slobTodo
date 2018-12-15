package overlay.camera.com.slobtodo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.input_layout.*
import kotlinx.android.synthetic.main.input_line.*
import kotlinx.android.synthetic.main.input_line.view.*

class InputScheduleFlagment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.input_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        input_text.setOnKeyListener(){v,keyCode,event->
            Log.d("event",keyCode.toString())
            if(keyCode == KeyEvent.KEYCODE_ENTER){
                createInpputLine()
                true
            }
            false
        }
    }
    private fun createInpputLine(){
        val view:View = layoutInflater.inflate(R.layout.input_line, null)

        view.input_text.setOnKeyListener(){v,keyCode,event->
            Log.d("event",keyCode.toString())
            if(keyCode == KeyEvent.KEYCODE_ENTER){
                createInpputLine()
                true
            }
            false
        }
        input_list.addView(view)
        return
    }
    private fun enterkeyLister(v:View,keyCode:Int,event:KeyEvent):Boolean{
        if(keyCode == KeyEvent.KEYCODE_ENTER){
            createInpputLine()
            return true
        }
        return false
    }

    companion object {
        fun newInstance(): InputScheduleFlagment {
            val fragment = InputScheduleFlagment()
            return fragment
        }
    }
}