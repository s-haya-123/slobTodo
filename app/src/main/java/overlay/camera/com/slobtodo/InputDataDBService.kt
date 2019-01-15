package overlay.camera.com.slobtodo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.text.format.DateFormat
import java.util.*


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
    fun insertLineData(lineDataList: List<InputData.LineData>){
        val argNum = 5
        val inputId:Long = 1
        val sql = createSqlQuery(lineDataList,"line_data","(ischecked,todo,inputDataId,created_time,updated_time)",argNum)
        db?.beginTransaction();
        val statement:SQLiteStatement? = db?.compileStatement(sql.toString())
        val currentDate = getCurrentData()
        lineDataList.forEachIndexed { index, lineData ->
            statement?.let {
                val statementIsCheck:Long = if(lineData.isChecked) 0 else 1
                it.bindLong(argNum * index + 1,statementIsCheck)
                it.bindString(argNum * index + 2,lineData.todo)
                it.bindLong(argNum * index + 3,inputId)
                it.bindString(argNum * index + 4, "${currentDate}")
                it.bindString(argNum * index + 5, "${currentDate}")
            }
        }
        statement?.execute()
        db?.setTransactionSuccessful()
        db?.endTransaction()
    }
    private fun getCurrentData():String{
        val date: Date = Date()
        return DateFormat.format("yyyy-MM-ddTHH:mm:ss+09:00", date).toString()
    }
    private fun createSqlQuery(lineDataList: List<InputData.LineData>,tableName:String,argString:String, argNum:Int):StringBuilder{
        val values:String = (1..argNum).map { "?" }.joinToString(separator = "," )
        val sql:StringBuilder  = StringBuilder("INSERT INTO ${tableName} ${argString} VALUES (${values})")
        lineDataList.forEachIndexed { index, _ ->
            if(index == lineDataList.size -1) {
                return@forEachIndexed
            }
            sql.append(", ($values)")
        }
        return sql
    }
//    fun insertLineData(lineData:InputData.LineData):Unit{
//        val values = ContentValues()
//        values.put("ischecked",lineData.isChecked)
//        values.put("todo",lineData.todo)
//        db?.insert("line_data",null,values)
//    }

}