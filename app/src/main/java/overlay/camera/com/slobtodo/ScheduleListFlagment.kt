package overlay.camera.com.slobtodo

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class ScheduleListFlagment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_main, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val _activity = activity
        if(_activity is MainActivity){
            val result = _activity.inputDataList.withIndex().groupBy{ it.index / 2 }.map{ it.value.map{ it.value } }
            result.forEach {
                val list:View = createLinearList(it)
                activity_main_list.addView(list)
            }
        }
        fab.setOnClickListener { _ ->
            if(savedInstanceState == null){
                changeMainFragment(null)
            }
        }
    }

    private fun createLinearList(inputDataList:List<InputData>):View{
        val list:LinearLayout = LinearLayout(activity)
        list.orientation = LinearLayout.HORIZONTAL
        list.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        activity?.apply {
            inputDataList.forEach {
                val card = createCardView(this,it)
                card.setOnClickListener { _ ->
                    changeMainFragment(it)
                }
                list.addView(card) }
        }
        return list
    }
    private fun createCardView(activity: FragmentActivity,inputData:InputData):View{
        val card:View = layoutInflater.inflate(R.layout.schedule_card, null)
        val p = Point()
        activity.windowManager.defaultDisplay?.getSize(p)
        card.layoutParams = ViewGroup.LayoutParams(p.x / 2 ,ViewGroup.LayoutParams.WRAP_CONTENT)
        inputData.lineDataArray.forEach {
            val showLine:View = layoutInflater.inflate(R.layout.show_line, null)
            showLine.findViewById<TextView>(R.id.show_line_text).text = it.todo
            card.findViewById<LinearLayout>(R.id.schedule_card_linear).addView(showLine)
        }
        return card
    }

    private fun changeMainFragment(inputData: InputData?){
        activity?.let {
            val fragmentManager: FragmentManager = it.supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.content_fragment,InputScheduleFlagment.newInstance(inputData),"inputFlagment")
            fragmentTransaction.commit()

            val manage: InputMethodManager = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manage.toggleSoftInput(1, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    companion object {
        fun newInstance(): ScheduleListFlagment {
            val fragment = ScheduleListFlagment()
            return fragment
        }

    }
}