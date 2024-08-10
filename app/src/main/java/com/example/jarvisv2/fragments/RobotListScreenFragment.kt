package com.example.jarvisv2.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jarvisv2.R
import com.example.jarvisv2.models.Robot
import com.example.jarvisv2.utils.Status
import com.example.jarvisv2.utils.StatusResult
import com.example.jarvisv2.utils.gone
import com.example.jarvisv2.utils.longToastShow
import com.example.jarvisv2.utils.robotImageList
import com.example.jarvisv2.view_models.RobotViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.UUID

class RobotListScreenFragment : Fragment() {

    private val robotViewModel: RobotViewModel by lazy {
        ViewModelProvider(this)[RobotViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_robot_list_screen, container, false)

        val tool_bar_view = view.findViewById<View>(R.id.toolbarLayout)

        val robot_image_ll = tool_bar_view.findViewById<View>(R.id.robot_image_ll)
        robot_image_ll.gone()

        val close_image = tool_bar_view.findViewById<ImageView>(R.id.back_img)
        close_image.setOnClickListener {
            findNavController().navigateUp()
        }

        val title_txt = tool_bar_view.findViewById<TextView>(R.id.titleTxt)
        title_txt.text = "ChatGPT App"
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addRobotFabBtn = view.findViewById<ExtendedFloatingActionButton>(R.id.addRobotFabBtn)

        addRobotFabBtn.setOnClickListener {
            addRobotDialog(it)

        }
//        val action =
//            RobotListScreenFragmentDirections
//                .actionRobotListScreenFragmentToChatScreenFragment()
//        findNavController().navigate(action)
        statusCallBack(view)
    }

    private fun statusCallBack(view: View) {
        robotViewModel
            .statusLiveData
            .observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {}
                    Status.ERROR -> {
                        it.message?.let { it1 -> view.context.longToastShow(it1) }
                    }

                    Status.SUCCESS -> {
                        when (it.data as StatusResult) {
                            StatusResult.Added -> {
                                Log.d("StatysResult", "Added")
                            }
                        }
                        it.message?.let { it1 -> view.context.longToastShow(it1) }
                    }
                }
            }

    }

    private fun addRobotDialog(view: View) {
        val edRobotName = TextInputEditText(view.context)
        edRobotName.hint = "Enter Robot Name"
        edRobotName.maxLines = 3

        val textInputLayout = TextInputLayout(view.context)
        val container = FrameLayout(view.context)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(50, 30, 50, 30)
        textInputLayout.layoutParams = params

        textInputLayout.addView(edRobotName)
        container.addView(textInputLayout)

        MaterialAlertDialogBuilder(view.context)
            .setTitle("Add a new Robot")
            .setView(container)
            .setCancelable(false)
            .setPositiveButton("Add") { dialog, which ->
                val robotName = edRobotName.text.toString().trim()
                if (robotName.isNotEmpty()) {
                    robotViewModel.insertRobot(
                        Robot(
                            UUID.randomUUID().toString(),
                            robotName,
                            robotImageList.random()
                        )
                    )
                } else {
                    view.context.longToastShow("Required")
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

}