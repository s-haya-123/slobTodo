package overlay.camera.com.slobtodo

import java.io.Serializable

class InputData: Serializable {
    class LineData (var isChecked:Boolean,var todo:String)
    var title = ""
    var lineDataArray = mutableListOf<LineData>()
}