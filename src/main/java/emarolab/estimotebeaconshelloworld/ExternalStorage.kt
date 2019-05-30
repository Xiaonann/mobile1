package emarolab.estimotebeaconshelloworld

import android.os.Environment
import java.io.File

class ExternalStorage{
    fun getExternalsDir(DirName:String, FileName:String): File {
        val fileDir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), DirName)
        if (!fileDir.mkdirs()) {
            fileDir.mkdirs()
        }
        //var file =
        return File(fileDir,FileName)

    }

}
