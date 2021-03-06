package shaya.com.slobtodo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.*
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import kotlinx.android.synthetic.main.input_layout.*
import kotlinx.android.synthetic.main.input_line.view.*
import java.util.*

class InputScheduleFlagment: Fragment() {
    val ARG:String = "INPUT_DATA"
    var data:InputData = InputData(InputData.Alarm.OFF,InputData.AlarmTime.MORNIG)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        var _activity = activity
        if(_activity is MainActivity){
           _activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        return inflater.inflate(R.layout.input_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventOnEditText(title,object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                data.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        arguments?.let {
            val inputData = it.getSerializable(InputScheduleFlagment.ARG) as? InputData
            setInputDataOnInputLine(inputData)
            inputData?.let {
                title.setText(it.title)
                this.data = it
            }
        }
        if(this.data.id == null){
            insertNewInputData(this.data)
        }
        InputData.LineData(this.data.lineDataArray.size).let {
            addInputLineOnView(it)
        }

    }
    private fun setInputDataOnInputLine(inputData: InputData?){
        inputData?.let {
            it.lineDataArray.forEach{
                val view = this.addInputLineOnView(it)
                view.input_text.setText(it.todo)
            }
        }
    }
    private fun addInputLineOnView(lineData: InputData.LineData):View{
        return layoutInflater.inflate(R.layout.input_line, null).apply{
            if(lineData.isChecked){
                checked_list.addView(this)
            } else {
                input_list.addView(this)
            }
            setEventOnEditText(this.input_text,object :TextWatcher{
                override fun beforeTextChanged(s:CharSequence,  start:Int, count:Int, after:Int) {
                }

                override fun onTextChanged(s:CharSequence,  start:Int, count:Int, after:Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    lineData.todo = s.toString()
                }
            })
            this.input_text.setOnFocusChangeListener{_, hasFocus ->
                if(hasFocus){
                    this.findViewById<ImageButton>(R.id.clearButton).visibility = View.VISIBLE
                } else {
                    this.findViewById<ImageButton>(R.id.clearButton).visibility = View.INVISIBLE
                }
            }
            this@InputScheduleFlagment.data.lineDataArray.add(lineData)
            this.findViewById<ImageButton>(R.id.clearButton).setOnClickListener { _ ->
                data.lineDataArray.filter { !it.isDelete && !it.isChecked }.forEachIndexed { index, lineData ->
                    lineData.index = index
                }
                if(input_list.childCount == 0){
                    return@setOnClickListener
                }
                if(lineData.index == input_list.childCount-1){
                    input_list.getChildAt(lineData.index-1).requestFocus()
                } else {
                    input_list.getChildAt(lineData.index+1).requestFocus()
                }
                input_list.removeView(this)
                lineData.isDelete = true
            }
            setEventOnCheckbox(this,this.findViewById<CheckBox>(R.id.checkBox),lineData)
        }
    }
    private fun setEventOnCheckbox(view:View,checkBox: CheckBox,lineData: InputData.LineData){
        checkBox.isChecked = lineData.isChecked
        checkBox.jumpDrawablesToCurrentState()
        checkBox.setOnClickListener { _->

            when(checkBox.isChecked){
                true -> {
                    input_list.removeView(view)
                    checked_list.addView(view)
                    lineData.isChecked = true
                    lineData.doneTime =  DateFormat.format("yyyy-MM-ddTHH:mm:ss+09:00", Date()).toString()
                }
                false -> {
                    checked_list.removeView(view)
                    input_list.addView(view)
                    lineData.isChecked = false
                    lineData.doneTime = null
                }
            }
        }
    }
    private fun setEventOnEditText(text:EditText,textWatcher: TextWatcher):Unit{
        text.setOnKeyListener { _, keyCode, event ->
            if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER){
                var lineData = InputData.LineData(this.data.lineDataArray.size)
                addInputLineOnView(lineData)
                text.setText(text.text.replace("\n".toRegex(),""))
                true
            }
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                context?.let {
                    operateSQLInputData(it)
                }
                true
            }
            false
        }
        text.addTextChangedListener(textWatcher)
    }

    private fun operateSQLInputData(context:Context){
        val dbService:InputDataDBService = InputDataDBService(context,2)
        dbService.open()
        dbService.updateInputData(data)
        updateAlreadyExistInputData(dbService,data)
        dbService.close()
    }
    private fun insertNewInputData(inputData: InputData){
        context?.let {
            val dbService:InputDataDBService = InputDataDBService(it,2)
            dbService.open()
            dbService.insertInputData(inputData)?.apply {
                inputData.id  = this
            }
            dbService.close()
        }
    }
    private fun updateAlreadyExistInputData(dbService:InputDataDBService,inputData: InputData){
        inputData.lineDataArray.forEach {
            var lineData = it
            if(it.id == null && ! it.isDelete ){
                inputData.id?.let { dbService.insertLine(lineData,it) }
            } else {
                dbService.updateLine(lineData)
            }
        }
    }




    override fun onCreateOptionsMenu(menu: Menu?, menuInflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, menuInflater)
        if(menuInflater != null && menu != null){
            menuInflater.inflate(R.menu.input, menu)

            when(this.data.alarm){
                InputData.Alarm.ON -> {
                    menu.findItem(R.id.action_activate).isVisible = true
                    menu.findItem(R.id.action_notactive).isVisible = false
                    menu.findItem(R.id.action_important).isVisible = false
                }
                InputData.Alarm.OFF -> {
                    menu.findItem(R.id.action_activate).isVisible = false
                    menu.findItem(R.id.action_notactive).isVisible = true
                    menu.findItem(R.id.action_important).isVisible = false
                }
                InputData.Alarm.IMPORTANT ->{
                    menu.findItem(R.id.action_activate).isVisible = false
                    menu.findItem(R.id.action_notactive).isVisible = false
                    menu.findItem(R.id.action_important).isVisible = true
                }
            }
            when(this.data.alarmTime){
                InputData.AlarmTime.MORNIG->{
                    menu.findItem(R.id.evening).isVisible = false
                    menu.findItem(R.id.morning).isVisible = true
                }
                InputData.AlarmTime.EVENING ->{
                    menu.findItem(R.id.evening).isVisible = true
                    menu.findItem(R.id.morning).isVisible = false
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null){
            return when (item.itemId) {
                android.R.id.home ->{
                    context?.let {
                        operateSQLInputData(it)
                    }
                    backBeforeFragment()
                    true
                }
                R.id.morning ->{
                    this.data.alarmTime = InputData.AlarmTime.EVENING
                    activity?.let {
                        it.fragmentManager.invalidateOptionsMenu()
                    }
                    true
                }
                R.id.evening ->{
                    this.data.alarmTime = InputData.AlarmTime.MORNIG
                    activity?.let {
                        it.fragmentManager.invalidateOptionsMenu()
                    }
                    true
                }
                R.id.action_activate ->{
                    //set next icon
                    this.data.alarm = InputData.Alarm.IMPORTANT
                    activity?.let {
                        val amount = calcTimeAtTommorrow()+ ReminderNotification.WAKEUP_TIME * 60 * 60
                        alarmStart(it,amount,"明日実行！")
                        it.fragmentManager.invalidateOptionsMenu()
                    }
                    true
                }
                R.id.action_notactive ->{
                    this.data.alarm = InputData.Alarm.ON
                    activity?.let {
                        val amount = calcTimeAtWeekend()
                        alarmStart(it,amount,"週末にタイマーをセットしたよ～")
                        it.fragmentManager.invalidateOptionsMenu()
                    }
                    true
                }
                R.id.action_important ->{
                    this.data.alarm = InputData.Alarm.OFF
                    activity?.let {
                        alarmCancel(it)
                        it.fragmentManager.invalidateOptionsMenu()
                    }
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        } else {
            return super.onOptionsItemSelected(item)
        }
    }
    private fun calcTimeAtWeekend():Int{
        val currentCalendar:Calendar = Calendar.getInstance()
        val week = currentCalendar.get(Calendar.DAY_OF_WEEK)
        val subTimeAtTommorow = calcTimeAtTommorrow(currentCalendar)
        val wakeupSecond = ReminderNotification.WAKEUP_TIME * 60 * 60
        when(week){
            Calendar.SATURDAY ->{
                return subTimeAtTommorow + wakeupSecond
            }
            else -> {
                val subTimeAtWeekend = Calendar.SATURDAY - currentCalendar.get(Calendar.DAY_OF_WEEK) -1
                return subTimeAtTommorow + subTimeAtWeekend * 24 * 60 * 60 + wakeupSecond
            }
        }
    }
    private fun calcTimeAtTommorrow(calender:Calendar):Int{
        val hour = calender.get(Calendar.HOUR_OF_DAY)
        val minute = calender.get(Calendar.MINUTE)
        val second = calender.get(Calendar.SECOND)
        return ((23 - hour) * 60 + 60 - minute -1) * 60+ 60 -second
    }
    private fun calcTimeAtTommorrow():Int{
        val calender:Calendar = Calendar.getInstance()
        val hour = calender.get(Calendar.HOUR_OF_DAY)
        val minute = calender.get(Calendar.MINUTE)
        val second = calender.get(Calendar.SECOND)
        return ((23 - hour) * 60 + 60 - minute -1) * 60+ 60 -second
    }
    private fun getAlarmCode(inputData: InputData):Int{
        return when(inputData.id){
            null -> ReminderNotification.REMINDER_REQUESTCODE
            else -> ReminderNotification.REMINDER_REQUESTCODE +inputData.id!!.toInt()
        }
    }
    private fun alarmStart(activity: FragmentActivity,timerAmount:Int,text:String) {
        val calendar = Calendar.getInstance().apply {
            this.setTimeInMillis(System.currentTimeMillis())
            this.add(Calendar.SECOND, timerAmount)
        }
        val intent = Intent(activity.applicationContext, ReminderNotification::class.java).apply {
            this.putExtra("RequestCode", getAlarmCode(this@InputScheduleFlagment.data))
        }

        val pending = PendingIntent.getBroadcast(
                activity.applicationContext,getAlarmCode(this.data), intent, 0)

        val am = activity.getSystemService(Context.ALARM_SERVICE)

        if (am is AlarmManager) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), pending)
            Toast.makeText(activity.applicationContext,
                    text, Toast.LENGTH_SHORT).show()
        }
    }
    private fun alarmCancel(activity: FragmentActivity){
        val intent = Intent(activity.applicationContext,ReminderNotification::class.java)
        val pending = PendingIntent.getBroadcast(activity.applicationContext,ReminderNotification.REMINDER_REQUESTCODE,intent,0)
        val am = activity.getSystemService(Context.ALARM_SERVICE)
        if(am is AlarmManager){
            am.cancel(pending)
            Toast.makeText(activity.applicationContext,"alarm is cancel",Toast.LENGTH_SHORT).show()
        }
    }
    private fun backBeforeFragment(){
        activity?.let {
            val fragmentManager:FragmentManager = it.supportFragmentManager
            val fragmentTransaction:FragmentTransaction = fragmentManager.beginTransaction()

            fragmentTransaction.replace(R.id.content_fragment,ScheduleListFlagment.newInstance(),"ScheduleFlagment")
            fragmentTransaction.commit()
        }
    }


    companion object {
        val ARG:String = "INPUT_DATA"

        fun newInstance(inputData: InputData): InputScheduleFlagment = InputScheduleFlagment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG,inputData)
            }
        }
    }
}