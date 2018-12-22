package overlay.camera.com.slobtodo

import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.input_layout.*
import kotlinx.android.synthetic.main.input_line.*
import kotlinx.android.synthetic.main.input_line.view.*

class InputScheduleFlagment: Fragment() {
    private var isAlarmOn:Boolean = true
    private val enterEvent = object:View.OnKeyListener{
        override fun onKey(v:View,keyCode:Int,event:KeyEvent): Boolean {
            if(keyCode == KeyEvent.KEYCODE_ENTER){
                createInpputLine()
                return true
            }
            return false
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.input_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        input_text.setOnKeyListener(enterEvent)
    }
    private fun createInpputLine(){
        val view:View = layoutInflater.inflate(R.layout.input_line, null)

        view.input_text.setOnKeyListener(enterEvent)
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

    override fun onCreateOptionsMenu(menu: Menu?, menuInflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, menuInflater)
        if(menuInflater != null && menu != null){
            menuInflater.inflate(R.menu.input, menu)

            when(this.isAlarmOn){
                true -> {
                    menu.findItem(R.id.action_favorite).isVisible = true
                    menu.findItem(R.id.action_notactive).isVisible = false
                }
                false -> {
                    menu.findItem(R.id.action_favorite).isVisible = false
                    menu.findItem(R.id.action_notactive).isVisible = true
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null){
            return when (item.itemId) {
                R.id.action_settings -> true
                R.id.action_favorite ->{
                    this.isAlarmOn = false
                    activity!!.fragmentManager.invalidateOptionsMenu()
                    true
                }
                R.id.action_notactive ->{
                    this.isAlarmOn = true
                    activity!!.fragmentManager.invalidateOptionsMenu()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

}