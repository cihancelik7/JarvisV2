package com.example.jarvisv2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.jarvisv2.R

class RobotListScreenFragment : Fragment() {



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


}