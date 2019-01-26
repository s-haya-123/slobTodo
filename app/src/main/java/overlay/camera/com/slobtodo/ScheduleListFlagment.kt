package overlay.camera.com.slobtodo

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class ScheduleListFlagment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var _activity = activity
        if(_activity is MainActivity){
            _activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
        return inflater.inflate(R.layout.activity_main, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val _activity = activity
        if(_activity is MainActivity){

            val result = selectInputDataFromDB()?.let {
                return@let it.withIndex().groupBy{ it.index / 2 }.map{ it.value.map{ it.value } }
            }
            result?.forEach {
                val list:View = createLinearList(it)
                activity_main_list.addView(list)
            }
            fab.setOnClickListener { _ ->
                if(savedInstanceState == null){
                    val inputData = InputData()
                    changeMainFragment(inputData)
                }
            }

        }
    }
    private fun selectInputDataFromDB():List<InputData>?{
        var inputDataList:List<InputData>? = null
        context?.let {
            var dbService = InputDataDBService(it,2)
            dbService.open()
            inputDataList = dbService.selectInputData()
            dbService.close()
        }
        return inputDataList
    }

    private fun createLinearList(inputDataList:List<InputData>):View{
        return  LinearLayout(activity).apply {
            this.orientation = LinearLayout.HORIZONTAL
            this.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            activity?.let {
                inputDataList.forEach {inputData ->
                    val card = createCardView(it,inputData)
                    card.setOnClickListener {
                        changeMainFragment(inputData)
                    }
                    this.addView(card) }
            }
        }
    }
    private fun createCardView(activity: FragmentActivity,inputData:InputData):View{
        val card:View = layoutInflater.inflate(R.layout.schedule_card, null)
        val p = Point()
        activity.windowManager.defaultDisplay?.getSize(p)
        card.layoutParams = ViewGroup.LayoutParams(p.x / 2 ,ViewGroup.LayoutParams.WRAP_CONTENT)
        inputData.lineDataArray.forEach { lineData ->
            if(lineData.isChecked){
                return@forEach
            }
            layoutInflater.inflate(R.layout.show_line, null).apply{
                this.findViewById<CheckBox>(R.id.checkBox).isEnabled = false
                this.findViewById<TextView>(R.id.show_line_text).text = lineData.todo
                card.findViewById<LinearLayout>(R.id.schedule_card_linear).addView(this)
            }
        }
        return card
    }

    private fun changeMainFragment(inputData: InputData){
        activity?.let {
            val fragmentManager: FragmentManager = it.supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
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