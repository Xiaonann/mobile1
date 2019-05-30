package emarolab.estimotebeaconshelloworld


import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import java.io.*
import java.net.Socket
import java.net.UnknownHostException


class MyClient(var host: String?, var port: Int) {
    var clientSocket: Socket? = null
    private var isClient: Boolean = false
    private var input: InputStream? = null
    private var output: OutputStream? = null
    var receiveMsg: String? = null
    private var receiveText: String? = null
    private var mHandler: Handler? = null
    lateinit var msg: Message
    lateinit var str: String

    fun openClient() {
        Thread(Runnable {
            try {
                // build a server with port
                clientSocket = Socket(host, port)
                //Log.d("Sign for Socket", "$clientSocket")
                if (clientSocket != null) {
                    isClient = true
                    sendMsg()
                    receiveMsg = receiveMsg()

                } else {
                    isClient = false

                }

            } catch (e: UnknownHostException) {
                e.printStackTrace()
                //Log.i("socket", "6")
            } finally {
                input?.close()
                output?.close()
                clientSocket?.close()

            }

        }).start()
    }


    //message to server
    private fun sendMsg() {
        try {
            output = clientSocket?.getOutputStream()
            output?.let {
                val out = DataOutputStream(it)
                //test text
                val text = "pepper wants to know:where is the book"
                out.writeUTF(text)
                out.flush()
            }

            //out = PrintWriter(clientSocket?.getOutputStream())
            //outputMsg = PrintWriter(clientSocket?.getOutputStream())
            //var writer = BufferedWriter(OutputStreamWriter(clientSocket?.getOutputStream()))

        } catch (e: Exception) {
            e.printStackTrace()

        }

    }

    // message from server
    private fun receiveMsg(): String? {
        try {
            input = clientSocket?.getInputStream()
            input?.let {
                val input: InputStream? = (it)
                input?.let {
                    val inputStream = DataInputStream(input)
                    receiveText = inputStream.readUTF()
                    Log.d("Msg from server", "$receiveText")

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            //Log.d("client","not output")
            //clientSocket?.close()
        }
        return receiveText
    }
}