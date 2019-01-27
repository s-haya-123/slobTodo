package overlay.camera.com.slobtodo

import java.io.Serializable

class InputData: Serializable {
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