package emarolab.estimotebeaconshelloworld


import java.text.SimpleDateFormat
import java.util.*


class TimeFun{
    //get time function
    var data: Date = Date()
    // var calendar: Calendar = Calendar.getInstance()


    fun Date.getNowTime(): String {
        val sdf = SimpleDateFormat("HH:mm:ss")
        return sdf.format(this)
    }

    fun timeDiff (str1: String, str2: String): Int{

        var sdf: SimpleDateFormat = SimpleDateFormat("HH:mm:ss")
        var diff:Long = sdf.parse(str2).getTime() -  sdf.parse(str1).getTime()
        var second = (diff/1000).toInt()
        return second

    }

    /*fun timearray(enter:Int, start:Int, array: ArrayList<Int>): ArrayList<Int> {
       if (enter <= start + 60)
           array.add(enter)
       else
            array
    }*/



}