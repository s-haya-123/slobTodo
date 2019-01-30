package overlay.camera.com.slobtodo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.text.format.DateFormat
import java.util.*
import android.content.ContentValues




class InputDataDBService(val context: Context,val version:Int){
    fun InputData.LineData.isChecktoLong():Long{
        return if(this.isChecked) 0 else 1
    }

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
    fun selectInputData():List<InputData>{
        val tableName = "input_data"
        val inputDataList = arrayListOf<InputData>()
        db?.beginTransaction()

        val cursor = db?.query(tableName, arrayOf("_id","title","alarm"), null, null, null, null, null)
        cursor?.let {
            var isEof = it.moveToFirst()
            while (isEof) {
                val alarmRaw = cursor.getLong(cursor.getColumnIndex("alarm")).toInt()
                var inputData = InputData(InputData.Alarm.values().filter { it.rawValue == alarmRaw }[0])
                inputData.id = cursor.getLong(cursor.getColumnIndex("_id"))
                inputData.title = cursor.getString(cursor.getColumnIndex("title"))
                inputData.lineDataArray = selectLineData(inputData.id)
                inputDataList.add(inputData)
                isEof = cursor.moveToNext()
            }
            it.close()
        }
        db?.setTransactionSuccessful()
        db?.endTransaction()
        return inputDataList
    }
    fun selectLineData(id:Long?):MutableList<InputData.LineData>{
        val tableName = "line_data"
        val lineDataList = arrayListOf<InputData.LineData>()
        val cursor = db?.query(tableName, null, "inputDataId=? and isdeleted IS NULL", arrayOf(id.toString()), null, null, null)
        cursor?.let {
            var isEof = it.moveToFirst()
            while (isEof) {

                var lineData = cursor.getLong(cursor.getColumnIndex("_index"))
                        .let { return@let InputData.LineData(it.toInt()) }
                        .apply {
                            this.id = cursor.getLong(cursor.getColumnIndex("_id"))
                            this.isChecked = if(cursor.getLong(cursor.getColumnIndex("ischecked")) > 0) false else true
                            this.todo = cursor.getString(cursor.getColumnIndex("todo"))
                        }
                lineDataList.add(lineData)
                isEof = cursor.moveToNext()
            }
            it.close()
        }
        return lineDataList
    }
    fun insertInputData(inputData:InputData):Long?{
        val tableName = "input_data"
        val argString = "(title,alarm,created_time,updated_time)"
        val sql = StringBuilder("INSERT INTO ${tableName} ${argString} VALUES (?,?,?,?)")
        db?.beginTransaction()
        val statement:SQLiteStatement? = db?.compileStatement(sql.toString())
        val currentDate = getCurrentData()
        statement?.let {
            it.bindString(1,inputData.title)
            it.bindLong(2,inputData.alarm.rawValue.toLong())
            it.bindString(3,currentDate)
            it.bindString(4,currentDate)
        }
        val id =statement?.executeInsert()
        db?.setTransactionSuccessful()
        db?.endTransaction()
        return id
    }

    fun updateLine(lineData:InputData.LineData){
        val currentDate = getCurrentData()
        val cv = ContentValues()
        cv.put("ischecked", lineData.isChecktoLong())
        cv.put("todo", lineData.todo)
        cv.put("updated_time",currentDate)
        if(lineData.isDelete){
            cv.put("isdeleted",currentDate)
        }
        db?.update("line_data", cv, "_id = ${lineData.id}", null)
    }

    fun updateInputData(inputData: InputData){
        val currentDate = getCurrentData()
        val cv = ContentValues()
        cv.put("title", inputData.title)
        cv.put("alarm",inputData.alarm.rawValue.toLong())
        cv.put("updated_time",currentDate)
        db?.update("input_data", cv, "_id = ${inputData.id}", null)
    }

    fun insertLine(lineData:InputData.LineData,inputId:Long){
        val tableName = "line_data"
        val argString = "(ischecked,todo,inputDataId,_index,doneTime,created_time,updated_time)"
        val sql = StringBuilder("INSERT INTO ${tableName} ${argString} VALUES (?,?,?,?,?,?,?)")

        db?.beginTransaction();
        val statement:SQLiteStatement? = db?.compileStatement(sql.toString())
        val currentDate = getCurrentData()
        statement?.let {
            it.bindLong(1,lineData.isChecktoLong())
            it.bindString(2,lineData.todo)
            it.bindLong(3,inputId)
            it.bindLong(4,lineData.index.toLong())
            if(lineData.doneTime == null){
                it.bindNull( 5)
            } else {
                it.bindString( 5,lineData.doneTime)
            }
            it.bindString(6,currentDate)
            it.bindString(7,currentDate)
        }
        val id =statement?.executeInsert()
        db?.setTransactionSuccessful()
        db?.endTransaction()
    }
    private fun getIsDeleted(lineData:InputData.LineData,date:String):String?{
        if(lineData.isDelete){
            return date
        } else {
            return null
        }
    }

    fun insertLineDataList(lineDataList: List<InputData.LineData>, inputId:Long): LongRange? {
        val argNum = 7
        val sql = createSqlQuery(lineDataList,"line_data","(ischecked,todo,inputDataId,_index,doneTime,created_time,updated_time)",argNum)
        db?.beginTransaction();
        val statement:SQLiteStatement? = db?.compileStatement(sql.toString())
        val currentDate = getCurrentData()
        lineDataList.forEachIndexed { index, lineData ->
            statement?.let {
                val statementIsCheck:Long = lineData.isChecktoLong()
                it.bindLong(argNum * index + 1,statementIsCheck)
                it.bindString(argNum * index + 2,lineData.todo)
                it.bindLong(argNum * index + 3,inputId)
                it.bindLong(argNum * index + 4,lineData.index.toLong())
                if(lineData.doneTime == null){
                    it.bindNull(argNum * index + 5)
                } else {
                    it.bindString(argNum * index + 5,lineData.doneTime)
                }
                it.bindString(argNum * index + 6,currentDate)
                it.bindString(argNum * index + 7,currentDate)
            }
        }
        val lastId = statement?.executeInsert()
        db?.setTransactionSuccessful()
        db?.endTransaction()
        lastId?.let {
            return (it - lineDataList.size +1 .. it)
        }
        return null
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

}