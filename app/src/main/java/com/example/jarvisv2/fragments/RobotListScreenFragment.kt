package com.example.jarvisv2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jarvisv2.R
import com.example.jarvisv2.models.Robot
import com.example.jarvisv2.utils.longToastShow
import com.example.jarvisv2.view_models.RobotViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.material.textfield.TextInputLayout
import java.util.UUID

class RobotListScreenFragment : Fragment() {

    private val robotViewModel:RobotViewModel by lazy {
        ViewModelProvider(this)[RobotViewModel::class.java]
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_robot_list_screen, container, false)

        val moveCharBtn =view.findViewById<Button>(R.id.moveChatBtn)

        moveCharBtn.setOnClickListener {
            val action =
                RobotListScreenFragmentDirections
                    .actionRobotListScreenFragmentToChatScreenFragment()
            findNavController().navigate(action)
        }

        return view
    }

    private fun addRobotDialog(view:View){
        val edRobotName = EditText(view.context)
        edRobotName.hint = "Enter Robot Name"
        edRobotName.maxLines = 3

        val textInputLayout = TextInputLayout(view.context)
        val container = FrameLayout(view.context)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(50,30,50,30)
        textInputLayout.layoutParams = params

        textInputLayout.addView(edRobotName)
        container.addView(textInputLayout)

        MaterialAlertDialogBuilder(view.context)
            .setTitle("Add a new Robot")
            .setView(container)
            .setCancelable(false)
            .setPositiveButton("Add"){dialog,which ->
                val robotName = edRobotName.text.toString().trim()
                if (robotName.isNotEmpty()){
                    robotViewModel.insertRobot(
                        Robot(
                            UUID.randomUUID().toString(),
                            robotName,

                        )
                    )
                }else{
                    view.context.longToastShow("Required")
                }
            }
            .setNegativeButton("Cancel",null)
            .create()
            .show()
    }

}