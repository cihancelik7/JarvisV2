package com.example.jarvisv2.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.jarvisv2.MainActivity
import com.example.jarvisv2.R
import com.example.jarvisv2.adapter.RobotAdapter
import com.example.jarvisv2.models.Robot
import com.example.jarvisv2.utils.EncryptSharedPreferenceManager
import com.example.jarvisv2.utils.Status
import com.example.jarvisv2.utils.StatusResult
import com.example.jarvisv2.utils.gone
import com.example.jarvisv2.utils.longToastShow
import com.example.jarvisv2.utils.robotImageList
import com.example.jarvisv2.utils.visible
import com.example.jarvisv2.view_models.RobotViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

class RobotListScreenFragment : Fragment() {

    private val robotViewModel: RobotViewModel by lazy {
        ViewModelProvider(this)[RobotViewModel::class.java]
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_robot_list_screen, container, false)

        try {
            auth = FirebaseAuth.getInstance()

            val toolBarView = view.findViewById<View>(R.id.toolbarLayout)
            val robotImageLl = toolBarView.findViewById<View>(R.id.robot_image_ll)
            robotImageLl.gone()

            val closeImage = toolBarView.findViewById<ImageView>(R.id.back_img)
            closeImage.setOnClickListener {
                startActivity(Intent(context, MainActivity::class.java))
            }

            val titleTxt = toolBarView.findViewById<TextView>(R.id.titleTxt)
            titleTxt.text = "Jarvis V2"

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {

            val settingImg = view.findViewById<ImageView>(R.id.setting_img)
            settingImg.visible()
            val encryptedPreferenceManager = EncryptSharedPreferenceManager(view.context)
            settingImg.setOnClickListener {
                apiKeyDialog(it, encryptedPreferenceManager)
            }

            val addRobotFabBtn = view.findViewById<ExtendedFloatingActionButton>(R.id.addRobotFabBtn)

            addRobotFabBtn.setOnClickListener {
                addRobotDialog(it)
            }

            val robotAdapter = RobotAdapter { type, position, robot ->

                when (type) {
                    "delete" -> {
                        val userEmail = auth.currentUser?.email ?: return@RobotAdapter
                        robotViewModel.deleteRobotUsingId(robot.robotId, userEmail)
                    }

                    "update" -> {
                        updateRobotDialog(view, robot)
                    }

                    else -> {
                        if (encryptedPreferenceManager.openAPIKey.trim().isNotEmpty()) {
                            val action =
                                RobotListScreenFragmentDirections
                                    .actionRobotListScreenFragmentToChatScreenFragment(
                                        robot.robotId,
                                        robot.robotImg,
                                        robot.robotName
                                    )
                            findNavController().navigate(action)
                        } else {
                            view.context.longToastShow("Enter the Api Key Setting Icon Click")
                        }
                    }
                }
            }

            val robotRv = view.findViewById<RecyclerView>(R.id.robotRV)
            robotRv.adapter = robotAdapter
            robotAdapter.registerAdapterDataObserver(object :
                RecyclerView.AdapterDataObserver() {
                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    super.onItemRangeChanged(positionStart, itemCount)
                    robotRv.smoothScrollToPosition(positionStart)
                }
            })
            callGetRobotList(robotAdapter, view)
            val userEmail = auth.currentUser?.email ?: return
            robotViewModel.getRobotList(userEmail)
            statusCallBack(view)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callGetRobotList(robotAdapter: RobotAdapter, view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            robotViewModel.robotStateFlow.collectLatest {
                when (it.status) {
                    Status.LOADING -> {
                        // Yükleniyor göstergesi eklenebilir
                    }
                    Status.SUCCESS -> {
                        robotAdapter.submitList(it.data)
                    }
                    Status.ERROR -> {
                        it.message?.let { it1 -> view.context.longToastShow(it1) }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        robotViewModel.clearStatusLiveData()
    }

    private fun statusCallBack(view: View) {
        robotViewModel
            .statusLiveData
            .observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it.status) {
                        Status.LOADING -> {}
                        Status.SUCCESS -> {
                            when (it.data as StatusResult) {
                                StatusResult.Added -> {
                                    Log.d("StatusResult", "Added")
                                }
                                StatusResult.Updated -> {
                                    Log.d("StatusResult", "Updated")
                                }
                                StatusResult.Deleted -> {
                                    Log.d("StatusResult", "Deleted")
                                }
                            }
                            it.message?.let { it1 -> view.context.longToastShow(it1) }
                        }
                        Status.ERROR -> {
                            it.message?.let { it1 -> view.context.longToastShow(it1) }
                        }
                    }
                }
            }
    }

    private fun updateRobotDialog(view: View, robot: Robot) {
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
            .setTitle("Update Robot")
            .setView(container)
            .setCancelable(false)
            .setPositiveButton("Update") { dialog, which ->
                val robotName = edRobotName.text.toString().trim()
                if (robotName.isNotEmpty()) {
                    val userEmail = auth.currentUser?.email ?: return@setPositiveButton
                    robotViewModel.updateRobot(
                        Robot(
                            robot.robotId,
                            robotName,
                            robot.robotImg
                        ), userEmail
                    )
                } else {
                    view.context.longToastShow("Required")
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
        edRobotName.setText(robot.robotName)
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
                    val newRobot = Robot(
                        UUID.randomUUID().toString(),
                        robotName,
                        (robotImageList.indices).random()
                    )

                    val userEmail = auth.currentUser?.email ?: return@setPositiveButton
                    robotViewModel.insertRobot(newRobot, userEmail)

                } else {
                    view.context.longToastShow("Required")
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun apiKeyDialog(view: View, encryptSharedPreferenceManager: EncryptSharedPreferenceManager) {
        val edApiKey = TextInputEditText(view.context)
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
        if (encryptSharedPreferenceManager.openAPIKey.trim().isNotEmpty()) {
            edApiKey.setText(encryptSharedPreferenceManager.openAPIKey.trim())
        }
    }
}