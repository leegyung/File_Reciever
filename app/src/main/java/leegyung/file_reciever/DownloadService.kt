package leegyung.file_reciever

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.*
import java.net.Socket

class DownloadService : Service() {
    private val CHANNEL_ID = "Down_Noti"
    private lateinit var context : Context
    private lateinit var socket : Socket
    private lateinit var inputStream : InputStream
    private lateinit var outputStream : OutputStream
    private lateinit var notification : NotificationCompat.Builder
    private lateinit var manager : NotificationManager
    private val ipAddr = "118.176.170.33"




    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        context = this

        val msg = intent?.getStringExtra("msg").toString()
        val fileName = intent?.getStringExtra("fileName").toString()

        createNotificationChannel()
        notification = createNotification(fileName)
        startForeground(1, notification.build())


        Toast.makeText(this, "$fileName 다운 시작", Toast.LENGTH_SHORT).show()

        downLoadFile(msg, fileName)


        return super.onStartCommand(intent, flags, startId)
    }



    private fun createNotification(fileName: String) : NotificationCompat.Builder{

        return NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.download)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                //.setContentIntent(pendingIntent) //누르면 실행할 activity 인탠트
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentTitle("$fileName 다운로드")
                .setContentText("다운중")
                .setSilent(true)
    }

    private fun createNotificationChannel(){
        val serviceChannel = NotificationChannel(CHANNEL_ID, "Download Notification", NotificationManager.IMPORTANCE_DEFAULT)
        serviceChannel.description = "for download service"
        manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    private fun downLoadFile(msg : String, fileName : String){
        CoroutineScope(Dispatchers.IO).launch {

            socket = Socket(ipAddr, 9000)
            outputStream = DataOutputStream(socket.getOutputStream())
            inputStream = DataInputStream(socket.getInputStream())

            outputStream.write(("size@@@$msg").toByteArray())
            val dataArr = ByteArray(1024)
            val size = inputStream.read(dataArr)
            val fileSize = String(dataArr,0, size, Charsets.UTF_8).toDouble()

            outputStream.write(("down@@@$msg").toByteArray())

            val file = File("/storage/emulated/0/test/$fileName")
            val fos = FileOutputStream(file)
            val bos = BufferedOutputStream(fos)

            while(true){
                val dataArr = ByteArray(4096)
                val size = inputStream.read(dataArr)

                bos.write(dataArr, 0, size)
                if(size < 4096){
                    break
                }
                downloadingNotification(fileSize, file)
            }

            completedDownload(fileName)
        }
    }

    private fun downloadingNotification(fileSize: Double, file : java.io.File){
        val progress = (file.length() * 100 / fileSize).toInt()

        notification.setProgress(100, progress, false)
        manager.notify(1, notification.build())
    }

    private fun completedDownload(fileName: String){
        CoroutineScope(Dispatchers.IO).launch {
            outputStream.write(("quit").toByteArray())
            socket.close()
        }

        val fileIntent = Intent(Intent.ACTION_PICK)
        fileIntent.setDataAndType(Uri.parse("/storage/emulated/0/test/$fileName"), "*/*")
        val pendingIntent = PendingIntent.getActivity(this,0,fileIntent,0)


        notification.setProgress(100, 100, false)
                .setContentTitle("$fileName 다운완료")
                .addAction(R.drawable.start, "Open File", pendingIntent)
                .setAutoCancel(true)
                .setOngoing(false)
        manager.notify(1, notification.build())


        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "$fileName 다운 완료", Toast.LENGTH_SHORT).show()
        }


        stopForeground(false)
        //stopSelf()

    }



}