package overlay.camera.com.slobtodo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues





class InputDataDBService(val context: Context,val version:Int){
    val SQL_INSERT_INPUT_DATA = "insert into input_data values"
    val SQL_INSERT_LINE_DATA = "insert into line_data values"

    private var dbHelper:InputDataDBHelper
    private var db: SQLiteDatabase? = null
    init {
        dbHelper = InputDataDBHelper(context,version)
    }

    fun open(): InputDataDBService {
        db = dbHelper.writableDatabase
        return this
    }
    fun close():Unit{
        dbHelper.close()
    }
    fun insertLineData(lineData:InputData.LineData):Unit{
        val values = ContentValues()
        values.put("ischecked",lineData.isChecked)
        values.put("todo",lineData.todo)
        db?.insert("line_data",null,values)
    }

}