package shaya.com.slobtodo

import java.io.Serializable

class InputData(var alarm:Alarm,var alarmTime: AlarmTime): Serializable {
    enum class Alarm(val rawValue :Int) {
        ON(0),
        OFF(1),
        IMPORTANT(2)
    }
    enum class AlarmTime(val rawValue: Int){
        MORNIG(0),
        EVENING(1)
    }

    class LineData(var index:Int):Serializable{
        var isChecked:Boolean
        var todo:String
        var id:Long?
        var isDelete:Boolean
        var doneTime:String?
        init {
            isChecked = false
            todo = ""
            id = null
            isDelete = false
            doneTime = null
        }
    }
    var title = ""
    var lineDataArray = mutableListOf<LineData>()
    var id:Long? = null
}