package overlay.camera.com.slobtodo

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import kotlinx.android.synthetic.main.input_layout.*
import kotlinx.android.synthetic.main.input_line.*
import kotlinx.android.synthetic.main.input_line.view.*
import kotlinx.android.synthetic.main.schedule_card.*

class InputScheduleFlagment: Fragment() {
    val ARG:String = "INPUT_DATA"
    var data:InputData = InputData()
    var isNewSchedule:Boolean = false
    private var isAlarmOn:Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.input_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val inputData = it.getSerializable(InputScheduleFlagment.ARG) as? InputData?
            this.isNewSchedule = inputData == null
            setInputDataOnInputLine(inputData)
            inputData?.let { this.data = it }
        }
        createInputLine()
    }

    private fun createInputLine():View{
        val view:View = layoutInflater.inflate(R.layout.input_line, null)
        var lineData = InputData.LineData(false,"")
        var index = this.data.lineDataArray.size
        input_list.addView(view)
        setEventOnEditText(view.input_text,view,lineData)
        view.findViewById<ImageButton>(R.id.clearButton).setOnClickListener { _ ->
            input_list.removeView(view)
            this.data.lineDataArray.removeAt(index)
        }
        return view
    }
    private fun setEventOnEditText(text:EditText,view: View,lineData:InputData.LineData):Unit{
        text.setOnKeyListener { v, keyCode, event ->
            Log.d("textKey",keyCode.toString())
            if(keyCode == KeyEvent.KEYCODE_ENTER){
                createInputLine()
                true
            }
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                val _activity = activity
                if(_activity is MainActivity && this.isNewSchedule){
                    _activity.inputDataList += data
                }
                true
            }
            false
        }
        text.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s:CharSequence,  start:Int, count:Int, after:Int) {
            }

            override fun onTextChanged(s:CharSequence,  start:Int, count:Int, after:Int) {
                lineData.todo = s.toString()
//                Log.d("textChange",lineData.todo)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        text.setOnFocusChangeListener{_, hasFocus ->
            if(hasFocus){
                view.findViewById<ImageButton>(R.id.clearButton).visibility = View.VISIBLE
            } else {
                view.findViewById<ImageButton>(R.id.clearButton).visibility = View.INVISIBLE
            }
        }
        this.data.lineDataArray.add(lineData)
    }


    override fun onCreateOptionsMenu(menu: Menu?, menuInflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, menuInflater)
        if(menuInflater != null && menu != null){
            menuInflater.inflate(R.menu.input, menu)

            when(this.isAlarmOn){
                true -> {
                    menu.findItem(R.id.action_activate).isVisible = true
                    menu.findItem(R.id.action_notactive).isVisible = false
                }
                false -> {
                    menu.findItem(R.id.action_activate).isVisible = false
                    menu.findItem(R.id.action_notactive).isVisible = true
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null){
            return when (item.itemId) {
                R.id.action_settings -> true
                R.id.action_activate ->{
                    this.isAlarmOn = false
                    activity?.apply { this.fragmentManager.invalidateOptionsMenu() }
                    true
                }
                R.id.action_notactive ->{
                    this.isAlarmOn = true
                    activity?.apply { this.fragmentManager.invalidateOptionsMenu() }
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        } else {
            return super.onOptionsItemSelected(item)
        }
    }
    private fun setInputDataOnInputLine(inputData: InputData?):Unit{
        inputData?.let {
            it.lineDataArray.forEach{
                val view = this.createInputLine()
                view.input_text.setText(it.todo)
            }
        }
    }
    companion object {
        val ARG:String = "INPUT_DATA"

        fun newInstance(inputData: InputData?): InputScheduleFlagment = InputScheduleFlagment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG,inputData)
            }
        }
    }
}