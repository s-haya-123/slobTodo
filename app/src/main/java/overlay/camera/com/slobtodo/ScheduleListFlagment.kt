package overlay.camera.com.slobtodo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

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
}