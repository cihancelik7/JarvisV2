package com.example.jarvisv2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.jarvisv2.R
import com.example.jarvisv2.utils.EncryptSharedPreferenceManager
import com.example.jarvisv2.utils.longToastShow

class WelcomeScreenFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val  view = inflater.inflate(R.layout.fragment_welcome_screen, container, false)

        val chatBtn = view.findViewById<Button>(R.id.chatBtn).apply {
            startAnimation(
                AnimationUtils.loadAnimation(view.context,R.anim.zoom_in_cut)
            )
        }
        val generateImageBtn = view.findViewById<Button>(R.id.generateImageBtn).apply {
            startAnimation(
                AnimationUtils.loadAnimation(view.context,R.anim.zoom_in_cut)
            )
        }
        chatBtn.setOnClickListener {
            val action =
                WelcomeScreenFragmentDirections
                    .actionWelcomeScreenFragmentToRobotListScreenFragment()
            findNavController().navigate(action)
        }

    val encryptSharedPreferenceManager = EncryptSharedPreferenceManager(view.context)
        generateImageBtn.setOnClickListener {
            if (encryptSharedPreferenceManager.openAPIKey.trim().isNotEmpty()) {
                val action =
                    WelcomeScreenFragmentDirections
                        .actionWelcomeScreenFragmentToImageGenScreenFragment()
                findNavController().navigate(action)
            }else{
                view.context.longToastShow("Enter the Api Key Setting Icom Click")
            }
        }

        return view
    }


}