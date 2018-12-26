package overlay.camera.com.slobtodo

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_main.*

class ScheduleListFlagment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_main, container, false)
    }

    companion object {
        fun newInstance(): ScheduleListFlagment {
            val fragment = ScheduleListFlagment()
            return fragment
        }

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.setOnClickListener { view ->
            if(savedInstanceState == null){
                val fragmentManager: FragmentManager = activity!!.supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

//                val inputData = InputData()
//                inputData.lineDataArray += InputData.LineData(false,"test")
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.content_fragment,InputScheduleFlagment.newInstance(null),"inputFlagment")
                fragmentTransaction.commit()

                val manage: InputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manage.toggleSoftInput(1, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }
}