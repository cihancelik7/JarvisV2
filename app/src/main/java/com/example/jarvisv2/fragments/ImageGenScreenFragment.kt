package com.example.jarvisv2.fragments

import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.jarvisv2.MainActivity
import com.example.jarvisv2.R
import com.example.jarvisv2.adapter.ImageAdapter
import com.example.jarvisv2.response.CreateImageRequest
import com.example.jarvisv2.utils.EncryptSharedPreferenceManager
import com.example.jarvisv2.utils.Status
import com.example.jarvisv2.utils.appSettingOpen
import com.example.jarvisv2.utils.gone
import com.example.jarvisv2.utils.hideKeyBoard
import com.example.jarvisv2.utils.longToastShow
import com.example.jarvisv2.utils.setupDialog
import com.example.jarvisv2.utils.visible
import com.example.jarvisv2.view_models.ChatViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ImageGenScreenFragment : Fragment() {

    private val chatViewModel: ChatViewModel by lazy {
        ViewModelProvider(this).get(ChatViewModel::class.java)
    }

    private val viewImageDialog : Dialog by lazy{
        Dialog(requireActivity(),R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.view_image_dialog)
        }
    }
    private val multiplePermissionNameList = if (Build.VERSION.SDK_INT >= 33) {
        arrayListOf()
    } else {
        arrayListOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingImg = view.findViewById<ImageView>(R.id.setting_img)

        settingImg.visible()
        val encryptedPreferenceManager = EncryptSharedPreferenceManager(view.context)
        settingImg.setOnClickListener {
            apiKeyDialog(it,encryptedPreferenceManager)

        }

        val imageRv = view.findViewById<RecyclerView>(R.id.imageRv)
        val loadingPb = view.findViewById<ProgressBar>(R.id.loadingPB)
        val downloadAllBtn = view.findViewById<Button>(R.id.downloadAllBtn)



        val loadImg = viewImageDialog.findViewById<ImageView>(R.id.loadImage)
        val cancelBtn = viewImageDialog.findViewById<Button>(R.id.cancelBtn)
        val downloadBtn = viewImageDialog.findViewById<Button>(R.id.downloadImageBtn)


        cancelBtn.setOnClickListener{
            viewImageDialog.dismiss()
        }
        val imageAdapter = ImageAdapter{pos, data ->
            viewImageDialog.show()
            Glide.with(loadImg)
                .load(data.url)
                .placeholder(R.drawable.ic_placeholder)
                .into(loadImg)
            downloadBtn.setOnClickListener{
                if (it.context.checkMultiplePermission()){
                    it.context.download(data.url)
                }else{
                    appSettingOpen(it.context)
                }
            }
        }
        imageRv.adapter =imageAdapter
        downloadAllBtn.setOnClickListener {
            if (it.context.checkMultiplePermission()){
                imageAdapter.currentList.map { list ->
                    it.context.download(list.url)
                }
            }else{
                appSettingOpen(it.context)
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            chatViewModel.imageStateFlow.collect{
                when(it.status){
                    Status.LOADING -> {
                        withContext(Dispatchers.Main){
                            loadingPb.visible()
                        }
                    }
                    Status.SUCCESS ->{
                        withContext(Dispatchers.Main){
                            loadingPb.gone()

                            imageAdapter.submitList(
                                it.data?.data
                            )
                            if (imageAdapter.currentList.isNotEmpty()){
                                downloadAllBtn.visible()
                            }else{
                                downloadAllBtn.gone()
                            }
                        }
                    }
                    Status.ERROR ->{
                        withContext(Dispatchers.Main){
                            loadingPb.gone()
                            it.message?.let { it1 -> view.context.longToastShow(it1) }
                        }
                    }
                }
            }
        }
    }

    private fun Context.checkMultiplePermission(): Boolean {
        val listPermissionNeeded = arrayListOf<String>()
        for (permission in multiplePermissionNameList) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                listPermissionNeeded.add(permission)
            }
        }
        if (listPermissionNeeded.isNotEmpty()) {
            return false
        }
        return true
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_image_gen_screen, container, false)

            val tool_bar_view = view.findViewById<View>(R.id.toolbar_layout)

            val robot_image_ll = tool_bar_view.findViewById<View>(R.id.robot_image_ll)
            robot_image_ll.gone()

            val close_image = tool_bar_view.findViewById<ImageView>(R.id.back_img)
            close_image.setOnClickListener {
                startActivity(Intent(context, MainActivity::class.java))
            }

            val title_txt = tool_bar_view.findViewById<TextView>(R.id.titleTxt)
            title_txt.text = "Jarvis Image Generator Mode"


        val numberListACT = view.findViewById<AutoCompleteTextView>(R.id.numberListACT)
        numberListACT.setAdapter(
            ArrayAdapter(
                view.context,
                android.R.layout.simple_list_item_1,
                (1..10).toList()
            )
        )

        val imageSizeRG = view.findViewById<RadioGroup>(R.id.imageSizeRG)
        val ed_prompt = view.findViewById<EditText>(R.id.ed_prompt)
        val generateImageBtn = view.findViewById<AppCompatButton>(R.id.generateImageBtn)

        generateImageBtn.setOnClickListener {
            view.context.hideKeyBoard(it)
            if (ed_prompt.text.toString().trim().isNotEmpty()){
                if (ed_prompt.text.toString().trim().length<1000){
                    Log.d("Prompt",ed_prompt.text.toString().trim())
                    Log.d("numberListACT",numberListACT.text.toString().trim())

                    val selectedSizeRB = view.findViewById<RadioButton>(imageSizeRG.checkedRadioButtonId)
                    Log.d("selectedSizeRB",selectedSizeRB.text.toString().trim())

                    chatViewModel.createImage(
                        CreateImageRequest(
                            numberListACT.text.toString().toInt(),
                            ed_prompt.text.toString().trim(),
                            selectedSizeRB.text.toString().trim()
                        )
                    )
                }else {
                    view.context.longToastShow("The maximum length is 1000 characters.")
                }
            }else{
                view.context.longToastShow("Prompt is Required")
            }

        }


        return view
    }
    private fun apiKeyDialog(view: View,encryptSharedPreferenceManager: EncryptSharedPreferenceManager) {
        val edApiKey  = TextInputEditText(view.context)
        edApiKey.hint = "Enter Api Key"
        edApiKey.maxLines = 3

        val textInputLayout = TextInputLayout(view.context)
        val container = FrameLayout(view.context)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(50, 30, 50, 30)
        textInputLayout.layoutParams = params

        textInputLayout.addView(edApiKey)
        container.addView(textInputLayout)

        MaterialAlertDialogBuilder(view.context)
            .setTitle("Open API Key")
            .setView(container)
            .setCancelable(false)
            .setPositiveButton("Update") { dialog, which ->
                val apiKey = edApiKey.text.toString().trim()
                if (apiKey.isNotEmpty()) {
                    encryptSharedPreferenceManager.openAPIKey = apiKey
                } else {
                    view.context.longToastShow("Required")
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
        if (encryptSharedPreferenceManager.openAPIKey.trim().isNotEmpty()){
            edApiKey.setText(encryptSharedPreferenceManager.openAPIKey.trim())
        }
    }

    private fun getRandomString ():String{
        val allowedChar = ('A'..'Z') + ('a'..'z') + ('0'..'9')

        return (1..7)
            .map { allowedChar.random()  }
            .joinToString ("")
    }
    private fun Context.download(url: String) {
        val folder = File(
            Environment.getExternalStorageDirectory().toString() + "/Download/Image"
        )
        if (!folder.exists()) {
            folder.mkdirs()
        }
       longToastShow("Download Started")
        val fileName = getRandomString()+".jpg"

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI or
                    DownloadManager.Request.NETWORK_MOBILE
        )
        request.setTitle(fileName)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "Image/$fileName"
        )
        downloadManager.enqueue(request)

    }
}
