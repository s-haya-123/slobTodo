package overlay.camera.com.slobtodo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.input_layout.*
import kotlinx.android.synthetic.main.input_line.view.*

class InputScheduleFlagment: Fragment() {
    val ARG:String = "INPUT_DATA"
    var data:InputData = InputData()
    private var isAlarmOn:Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.input_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val inputData = it.getSerializable(InputScheduleFlagment.ARG) as InputData?
            setInputDataOnInputLine(inputData)
            inputData?.let { this.data = it }
        }
        createInputLine()
    }

    private fun createInputLine():View{
        val view:View = layoutInflater.inflate(R.layout.input_line, null)
        input_list.addView(view)
        setEventOnEditText(view.input_text)
        return view
    }
    private fun setEventOnEditText(text:EditText):Unit{
        var lineData = InputData.LineData(false,"")
        text.setOnKeyListener { v, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_ENTER){
                createInputLine()
                true
            }
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                val _activity = activity
                if(_activity is MainActivity){
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
        this.data.lineDataArray += lineData
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