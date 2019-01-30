package overlay.camera.com.slobtodo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class InputDataDBHelper(var mContext: Context?,var version:Int) : SQLiteOpenHelper(mContext, "INPUT_DATA", null, version) {
    companion object {
        val CREATE_INPUT_DATA = "create table input_data ( _id integer primary key autoincrement, title text not null,alarm integer not null, created_time text not null, updated_time text not null);"
        val CREATE_LINE_DATA = "create table line_data ( _id integer primary key autoincrement, ischecked boolean not null,todo text not null,inputDataId integer not null, isdeleted text,_index inteder not null,doneTime text,created_time text not null, updated_time text not null );"
        val DELETE_INPUT_DATA ="drop table input_data;"
        val DELETE_LINE_DATA ="drop table line_data;"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_INPUT_DATA)
        db?.execSQL(CREATE_LINE_DATA)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DELETE_INPUT_DATA)
        db?.execSQL(DELETE_LINE_DATA)
        onCreate(db)
    }
}