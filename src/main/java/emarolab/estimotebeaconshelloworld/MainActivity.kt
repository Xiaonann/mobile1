package emarolab.estimotebeaconshelloworld

import android.os.*
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials
import com.estimote.proximity_sdk.api.ProximityObserver
import com.estimote.proximity_sdk.api.ProximityObserverBuilder
import com.estimote.proximity_sdk.api.ProximityZoneBuilder
import java.util.*
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    /** Fields in this class */

    // Related to proximity methods
    private lateinit var proximityObserver: ProximityObserver
    private var proximityObservationHandler: ProximityObserver.Handler? = null
    private var userState = 0
    var startTime = 0L
    //private var userStateArray = ArrayList<Int>()

    //lateinit var textViewStateyellow: TextView
    //lateinit var textviewReal:TextView
    //lateinit var textViewStatepink : TextView
    //lateinit var textViewTime : TextView
    private lateinit var workerThread: WorkerThread
    var testThread = WorkerThread().testThread
    // storage
    val storage = ExternalStorage()
    val state = storage.getExternalsDir("ProximityResult","Real.txt")
    //val exit = storage.getExternalsDir("ProximityResult","Realexit.txt")

    // Could credentials found from https://cloud.estimote.com/
    private val cloudCredentials =
            EstimoteCloudCredentials("laboratorium-dibris-gmail--kfg", "90e1b9d8344624e9c2cd42b9f5fd6392")

    // Lambda functions for displaying errors when checking requirements
    private val displayToastAboutMissingRequirements: (List<Requirement>) -> Unit = {
        Toast.makeText(
                this,
                "Unable to start proximity observation. Requirements not fulfilled: ${it.size}",
                Toast.LENGTH_SHORT
        ).show()
    }
    private val displayToastAboutError: (Throwable) -> Unit = {
        Toast.makeText(this, "Error while trying to start proximity observation: ${it.message}", Toast.LENGTH_SHORT)
                .show()
    }

    /** Methods in this class */
    // onCreate method runs first (when an application is launched)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //textviewReal = findViewById(R.id.tv_realresult)

        // Requirements check
        RequirementsWizardFactory.createEstimoteRequirementsWizard().fulfillRequirements(
                this,
                onRequirementsFulfilled = { startProximityObservation() },
                onRequirementsMissing = displayToastAboutMissingRequirements,
                onError = displayToastAboutError
        )
        val one = testThread.obtainMessage().obj
        Log.d("main","$one")

    //test socket(client version) show message from server
        /*bt_socket.setOnClickListener {
            thread {
                val host = "130.251.13.194"
                val port = 8080
                val client = MyClient(host, port)
                client.openClient()
                tv_client.text=client.receiveMsg
            }
        }*/

        //test socket(server version) show message from client
        /*bt_server.setOnClickListener {
            thread {
                val server: SocketServer = SocketServer(8080)
                server.beginListen()
                tv_server.text = server.text
                // how to show message from client or don't need
                //val mes = SocketServer()
                //tv_server.text=SocketServer(8080).beginListen().
            }
        }*/


    }

    // The method where you implement the logic for your application
    private fun startProximityObservation() {

        proximityObserver = ProximityObserverBuilder(applicationContext, cloudCredentials)
                .onError(displayToastAboutError)
                .withTelemetryReportingDisabled() //Added this to reduce the bluetooth call back traffic which was giving an error " Closed Bluetooth Low Energy scan failed with error code: 2"
                .withAnalyticsReportingDisabled() //Similarly this
                .withBalancedPowerMode() //Similarly this
                .withEstimoteSecureMonitoringDisabled()
                .build()


        //textViewStateyellow = findViewById(R.id.textView5)
        //textViewStatepink = findViewById(R.id.textView6)

        //textViewTime = findViewById(R.id.textView6)
        // Create and start the worker thread.
        workerThread = WorkerThread()

        workerThread.start()

        startTime = System.currentTimeMillis()


        // start proximity
        val yellowBeacon = ProximityZoneBuilder()
                .forTag("purple2")
                .inCustomRange(1.0)
                .onEnter {
                    //state
                    userState = 0
                    //send message to worker thread
                    val msg = Message.obtain()
                    msg.what = 0
                    msg.obj = "0"
                    workerThread.workerThreadHandler.sendMessage(msg)
                    textViewStateyellow.text = "State: Enter"
                }
                .onExit {
                    //state
                    userState = 1
                    //userStateArray.add(userState)
                    // send message to worker thread
                    val msg = Message.obtain()
                    msg.what = 1
                    msg.obj = "1"
                    workerThread.workerThreadHandler.sendMessage(msg)
                    textViewStateyellow.text = "State: Exit"
                }
                .build()

        //proximityObservationHandler = proximityObserver.startObserving(pinkBeacon, purpleBeacon, yellowBeacon)
        proximityObservationHandler = proximityObserver.startObserving(yellowBeacon)

        //val result = WorkerThread().testThread

    }



    //button click
    fun enterClick(v: View?) {
        when (v?.id) {
            R.id.bt_enteryellow ->{
                val enterTime = (System.currentTimeMillis().toDouble()-startTime.toDouble())/1000
                //Log.d("click","$startTime")
                //val CurrentState = "0"

                state.appendText("state:0, Time:$enterTime\n")
            }
        }
    }
    fun exitClick(v: View?) {
        when (v?.id) {
            R.id.bt_exityellow->{
                val exitTime = (System.currentTimeMillis().toDouble()-startTime.toDouble())/1000
                //val CurrentState  = "1"
                state.appendText("state:1, Time:$exitTime\n")
            }
        }
    }

    // onDestroy method runs last (when an application is closed)
    // IMPORTANT (This applies for mobile app):
    // If you don't stop the scan here, the foreground service will remain active EVEN if the user kills your APP.
    // You can use it to retain scanning when app is killed, but you will need to handle actions properly.
    override fun onDestroy() {
        super.onDestroy()
        proximityObservationHandler?.stop()

    }
}
