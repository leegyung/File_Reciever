package leegyung.file_reciever

import kotlinx.coroutines.*
import leegyung.file_reciever.databinding.ActivityMainBinding
import java.io.*
import java.lang.Exception
import java.net.Socket

class SocketConnection(b: ActivityMainBinding, main: MainActivity) {
    private var binding: ActivityMainBinding = b
    private val mainActivity = main
    private val ipAddr = "118.176.170.33"
    private lateinit var socket : Socket
    private lateinit var inputStream : InputStream
    private lateinit var outputStream : OutputStream
    private lateinit var mainPath : String

    private var fileList : ArrayList<File> = arrayListOf()

    //For tests
    private var type = 0


    init{
        CoroutineScope(Dispatchers.IO).launch {
            try{
                socket = Socket(ipAddr, 9000)

                outputStream = DataOutputStream(socket.getOutputStream())
                inputStream = DataInputStream(socket.getInputStream())

                outputStream.write("initial".toByteArray())


                val dataArr = ByteArray(1024)
                val size = inputStream.read(dataArr)

                CoroutineScope(Dispatchers.Main).launch {
                    mainActivity.mainPath = String(dataArr, 0, size, Charsets.UTF_8)
                    mainActivity.currentPath = mainActivity.mainPath
                    binding.location.text = mainActivity.mainPath
                    getList("list")
                }

            }catch (e : Exception){
                mainPath = "Connection failed"
            }
        }
    }


    private fun stringToList(response : String){
        fileList.clear()
        val temp = response.split("    ")
        for(str : String in temp){
            if(str == "@@file@@"){
                type = 1
            }else{
                fileList.add(File(name = str, type = type))
            }
        }
        fileList.removeLast()
        type = 0
    }

    //
    fun getList(path : String){
        CoroutineScope(Dispatchers.IO).launch {
            outputStream.write(path.toByteArray())

            //while 로 계속 받아와야함 바이트 어레이의 마지막이 0 이면 break
            var result = ""
            while(true){
                val dataArr = ByteArray(1024)
                val size = inputStream.read(dataArr)

                result += String(dataArr,0, size, Charsets.UTF_8)

                //마지막이 0
                if (dataArr[dataArr.size - 1] == 0.toByte()) {
                    if(dataArr[dataArr.size - 2] == 0.toByte()){
                        break
                    }
                }

            }
            stringToList(result)
            //메인엑티비티 updateFileList 호출
            CoroutineScope(Dispatchers.Main).launch {
               mainActivity.updateFileList(fileList)
            }
        }
    }



    fun disconnect(){
        CoroutineScope(Dispatchers.IO).launch {
            outputStream.write("quit".toByteArray())
            inputStream.close()
            outputStream.close()
            socket.close()
        }

    }






}