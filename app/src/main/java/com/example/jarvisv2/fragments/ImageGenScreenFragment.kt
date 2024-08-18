package com.example.jarvisv2.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.jarvisv2.MainActivity
import com.example.jarvisv2.R
import com.example.jarvisv2.utils.EncryptSharedPreferenceManager
import com.example.jarvisv2.utils.gone
import com.example.jarvisv2.utils.hideKeyBoard
import com.example.jarvisv2.utils.longToastShow
import com.example.jarvisv2.utils.visible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ImageGenScreenFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingImg = view.findViewById<ImageView>(R.id.setting_img)

        settingImg.visible()
        val encryptedPreferenceManager = EncryptSharedPreferenceManager(view.context)
        settingImg.setOnClickListener {
            apiKeyDialog(it,encryptedPreferenceManager)

        }
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
}
