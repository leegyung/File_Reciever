package leegyung.file_reciever

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycle_layout.view.*
import java.util.*

class FilesAdapter(private val context: Context, main: MainActivity) : RecyclerView.Adapter<FilesAdapter.ViewHolder>() {

    var files : ArrayList<File> = arrayListOf()
    private val mainActivity = main

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycle_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(files[position])

    }

    override fun getItemCount(): Int {
        return files.size
    }

    inner class ViewHolder(view : View): RecyclerView.ViewHolder(view){
        fun bind(item : File){

            if(item.type == 1){
                itemView.image.setImageResource(R.drawable.file)
                itemView.download_btn.visibility = View.VISIBLE
            }else{
                itemView.image.setImageResource(R.drawable.folder)
                itemView.download_btn.visibility = View.INVISIBLE
            }

            //itemView.file_name.isSelected = true
            itemView.file_name.text = item.name

            if(item.type == 1){
                itemView.download_btn.setOnClickListener {
                    mainActivity.fileDownload(item.name)
                }
            }
            else{
                itemView.file_name.setOnClickListener{
                    mainActivity.folderSelected(item.name, false)
                }
            }

        }
    }

}