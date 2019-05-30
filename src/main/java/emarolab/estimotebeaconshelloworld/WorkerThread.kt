package emarolab.estimotebeaconshelloworld

/**
 *  Creat worker thread for  applying sliding window algorithm
 */


import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import kotlin.collections.ArrayList



class WorkerThread : Thread() {
    // all states(update every 1s) from phone of beacons
    private var stateArrayYellow = ArrayList<Int>()
    private var stateArrayPink = ArrayList<Int>()
    // states from proximity in MainActivity
    var currentStateYellow : Int = 1
    var currentStatePink : Int = 1

    //var StateArrayNullable = ArrayList<Int?>()

     var realState:String? = null

    var userTimeArray = ArrayList<Double>()
    lateinit var windowAveYellow:List<Double>
    lateinit var workerThreadHandler: Handler
    lateinit var testThread:Handler
    private var workerLooper: Looper? = null
    val mess: Message? = null

    val enterYellow: Int = 0
    val exitYellow: Int = 1
    val enterPink: Int = 2
    val exitPink: Int = 3
    val unchange  = 4

    override fun run() {
        // store data in external storage
        val storage = ExternalStorage()
        val currentState = storage.getExternalsDir("ProximityResult", "Test.txt")
        val allState = storage.getExternalsDir("ProximityResult", "AllStateYellow.txt")
        //val allStateWin = storage.getExternalsDir("Test", "AllWinAve.txt")
        Looper.prepare()
        //get looper(tied with MessageQueue) of current worker thread
        workerLooper = Looper.myLooper()

        workerThreadHandler = object : Handler(workerLooper) {
            // start time of worker thread
            val startTime = System.currentTimeMillis()
            override fun handleMessage(msg: Message?) {
                // proximity result doesn't change in 1 seconds
                when (msg?.what) {
                    unchange -> {
                        //start from outside(1) of the zone
                        currentStateYellow = if (stateArrayYellow.size == 0) 1 else stateArrayYellow.last()
                        workerThreadHandler.sendEmptyMessageDelayed(unchange, 1000)
                    }
                    enterYellow -> {
                        // make sure one state in one seconds
                        val lastIndex = stateArrayYellow.lastIndex
                        stateArrayYellow.removeAt(lastIndex)
                        currentStateYellow = msg?.obj.toString().toInt()
                        val currentTime = (System.currentTimeMillis().toDouble() - startTime.toDouble()) / 1000
                        userTimeArray.add(currentTime)
                        currentState.appendText("State:${currentStateYellow}, Time:${userTimeArray}\n")
                        //Log.d("enter ", "$currentTime")

                    }
                    exitYellow -> {
                        // make sure one state in one seconds
                        val lastIndex = stateArrayYellow.lastIndex
                        stateArrayYellow.removeAt(lastIndex)
                        currentStateYellow = msg?.obj.toString().toInt()
                        val currentTime = (System.currentTimeMillis().toDouble() - startTime.toDouble()) / 1000
                        userTimeArray.add(currentTime)
                        currentState.appendText("State:${currentStateYellow}, Time:${userTimeArray}\n")
                        //Log.d("exit ", "$currentTime")
                    }

                }

                stateArrayYellow.add(currentStateYellow)
                //Log.d("SS","$stateArrayYellow")
                //StateArrayPink.add(currentStatePink)
                //StateArraySize.add(StateArray.size)
                val windowYellow = stateArrayYellow.windowed(size = 5, step = 1)
                windowAveYellow = windowYellow.map { it.average() }
                //val show = WindowAveyellow.lastOrNull()
                Log.d("AVE","$windowAveYellow")
                // 0.4 and 0.6 are threshold for real enter and exit
                realState = RealState(windowAveYellow,"Yellow")
                Log.d("realState","$realState")

                //var windowspink= StateArrayPink.windowed(size = 10, step = 1)
                //val WindowAvepink = windowspink.map { it.average() }
                //allState.appendText("$stateArrayYellow\n")
                //allStateWin.appendText("${WindowAveyellow.lastOrNull()}\n")

                //getExternalsDir("ProximityResult", "AllStatepink.txt").appendText("$StateArraypink\n")
                //getExternalsDir("ProximityResult", "WinAve.txt").appendText("Win:${windows.lastOrNull()}\n" + "Ave:${WindowAve.lastOrNull()}\n")


            }

        }
        workerThreadHandler.sendEmptyMessageDelayed(unchange, 1000)
        //infinitive loop getting message from MessageQueue
        Looper.loop()


        /*mess?.obj = RealState(windowAveYellow,"Yellow")
        testThread = Handler(Looper.getMainLooper())
        testThread.sendMessage(mess)*/


    }
    fun RealState(winAve: List<Double>, beacon: String): String? {
        val lastAve: Double? = winAve.lastOrNull()
        var result: String? = null
        lastAve?.let {
            when (it) {
                0.4 -> {
                    val checkState = winAve.elementAt(winAve.size - 2)
                    if (checkState > 0.4) {
                        result = "Enter $beacon zone"
                    }
                }
                0.6 -> {
                    val checkState = winAve.elementAt(winAve.size - 2)
                    if (checkState < 0.6) {
                        result = "Exit $beacon zone"

                    }
                }

            }

        }
        return result
    }

}
