package emarolab.estimotebeaconshelloworld

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
class Trytimer{
    fun main(args: Array<String>) {
        Executors.newScheduledThreadPool(1)
                .schedule({
                    println("bombing")
                }, 1, TimeUnit.SECONDS)
    }
}
