package leegyung.file_reciever

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import kotlinx.android.synthetic.main.activity_main.*
import leegyung.file_reciever.databinding.ActivityMainBinding
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyRep
import kotlin.collections.ArrayList
import kotlin.reflect.typeOf


class MainActivity : AppCompatActivity() {
    private var MY_PERMISSION_ACCESS_ALL = 100

    private lateinit var binding : ActivityMainBinding
    private lateinit var fileAdapter : FilesAdapter
    private lateinit var socketConnection : SocketConnection
    private lateinit var fileList : ArrayList<File>

    var mainPath = ""
    var currentPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val permissions = arrayOf(
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.FOREGROUND_SERVICE

        )
        requestPermissions(permissions, MY_PERMISSION_ACCESS_ALL)

        //퍼미션 추가하기 존나 귀찮....
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSION_ACCESS_ALL)
        }

        location.isSelected = true

        val folder = java.io.File("/storage/emulated/0/test")
        if(!folder.exists()) { folder.mkdir() }


        initBinding()
        initRecyclerView()
        initSocketConnection()
        initBackBtn()

    }



    private fun initBackBtn(){
        binding.back.visibility = View.VISIBLE
        binding.back.setOnClickListener {
            val temp = currentPath.substring(0,  currentPath.lastIndexOf("\\"))
            folderSelected(temp, true)
        }
    }

    private fun initBinding(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initRecyclerView(){
        fileAdapter = FilesAdapter(this, this)

        binding.FileList.adapter = fileAdapter

        binding.FileList.addItemDecoration(RecyclerDecorator(5))
    }

    private fun initSocketConnection(){
        socketConnection = SocketConnection(binding, this)
    }

    fun updateFileList(list : ArrayList<File>){
        fileList = list
        fileAdapter.files = fileList
        fileAdapter.notifyDataSetChanged()
        binding.progressBar.visibility = View.GONE
        binding.FileList.visibility = View.VISIBLE

    }

    fun folderSelected(name : String, back : Boolean){
        binding.progressBar.visibility = View.VISIBLE
        binding.FileList.visibility = View.GONE

        if(!back){
            currentPath += "\\$name"
            binding.location.text = currentPath
            binding.back.visibility = View.VISIBLE
            socketConnection.getList("folder@@@$currentPath")
        }else{
            //여기서 커렌트바꾸고, 메인이랑 같으면 인비지블로
            currentPath = name
            binding.location.text = currentPath
            if(name == mainPath){
                binding.back.visibility = View.INVISIBLE
                socketConnection.getList("folder@@@$currentPath")
            }
            else{
                binding.back.visibility = View.VISIBLE
                socketConnection.getList("folder@@@$currentPath")
            }
        }
    }

    fun fileDownload(fileName : String){
        val serviceInt = Intent(this, DownloadService::class.java)
        serviceInt.putExtra("msg",currentPath + "\\$fileName")
        serviceInt.putExtra("fileName",fileName)
        startService(serviceInt)

    }

    override fun onStop() {
        super.onStop()
        socketConnection.disconnect()

    }











}