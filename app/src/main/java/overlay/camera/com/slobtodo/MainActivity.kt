package overlay.camera.com.slobtodo

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toolbar

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
//    var inputDataList:List<InputData> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)
        setSupportActionBar(toolbar_input)
        setTitle("")

        if(savedInstanceState == null){
            val fragmentManager:FragmentManager = this.supportFragmentManager
            val fragmentTransaction:FragmentTransaction = fragmentManager.beginTransaction()

            fragmentTransaction.add(R.id.content_fragment,ScheduleListFlagment.newInstance(),"ScheduleFlagment")
            fragmentTransaction.commit()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        Log.d("dispatchKey",event.toString())
        return super.dispatchKeyEvent(event)
    }



}
