package overlay.camera.com.slobtodo

import java.io.Serializable

class InputData: Serializable {
    class LineData(){
        var isChecked:Boolean
        var todo:String
        var id:Long?
        init {
            isChecked = false
            todo = ""
            id = null
        }
    }
    var title = ""
    var lineDataArray = mutableListOf<LineData>()
    var id:Long? = null
}