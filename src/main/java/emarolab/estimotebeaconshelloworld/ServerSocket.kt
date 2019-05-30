package emarolab.estimotebeaconshelloworld

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.util.Log
import java.io.*
import java.net.ServerSocket
import java.net.Socket

/**
 * server (blocking IO) always waiting for client so need to run in a service
 * @param name Used to name the worker thread
 */
private const val TAG = "SOCKETSERVER_SERVICE"
class SocketServer : IntentService(TAG){
    private var server: ServerSocket? = null
    private var input: InputStream? = null
    private var output: OutputStream? = null
    private var socket: Socket? = null
    var sendMsg: String? = null
    var receiveMsg: String? = null
    // initial server socket
    companion object{
        const val port = 8080
        //
    }
    init {
        try {
            server = ServerSocket(port)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    override fun onHandleIntent(intent: Intent?){
    beginListen()
    }

    // build a server socket with specific port



    //listen data from socket
    fun beginListen() {
            try {
                //try to connect with client socket
                socket = server?.accept()
                // get data from client socket
                try {
                    input = socket?.getInputStream()
                    input?.let {
                        val inputStream = DataInputStream(it)
                        receiveMsg = inputStream.readUTF()
                        Log.d("Msg from client", "$receiveMsg")

                        if (receiveMsg != null) {
                            output = socket?.getOutputStream()
                            output?.let {
                                val out = DataOutputStream(it)
                                //test text
                                sendMsg = WorkerThread().realState
                                //val testText = "proximity"
                                out.writeUTF(sendMsg)
                                out.flush()
                            }

                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                input?.close()
                output?.close()
                socket?.close()
                // can't close server yes?
                //server?.close()
            }
    }
}





