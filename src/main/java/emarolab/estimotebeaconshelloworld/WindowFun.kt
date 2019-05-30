package emarolab.estimotebeaconshelloworld
// apply sliding window and return state
class WindowFun{
    /*fun getInterval(values: ArrayList<Int>): Int{
        val partialWindows =  values.windowed(size = 5, step = 1, partialWindows = true)
        for(i in partialWindows.withIndex()){}
    }
    fun getWin(){
        val sequence = sequenceOf(1..60)
        val windows = sequence.windowed(size = 5, step = 1)
        val winArray = windows.toList()*/
    // store timestamp and state
    val timeArray = ArrayList<Long>()
    var stateArr = ArrayList<Int>()
    val stateArray = ArrayList<Int>()
    val stateResult = ArrayList<Int>()
    var startTime = System.currentTimeMillis()
    var pointsNum = 0
    var indexNum = 0
    var sum = 0
    fun getWin(): ArrayList<Int>{
        while (indexNum < timeArray.size){
            //calculate within 5s(step) using index in timeArray to get corresponding stateArray
            if(timeArray[indexNum] < startTime + 5000){
                val s = stateArray[indexNum]
                stateArr.add(s)
                pointsNum ++
                continue
            }
            indexNum ++
            //
            if(pointsNum == 0){
                stateResult.add(0)
            }else{
                for (value in stateArr) {
                    sum += value
                }
              stateResult.add(sum/pointsNum)
            }
            // set to 0 for next calculation
            sum = 0
            pointsNum = 0

            while (timeArray[indexNum-1] >= startTime+1000)
                indexNum--
            // interval is 1s
            startTime += 1000
        }
        return stateResult
    }

}