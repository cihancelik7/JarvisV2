package com.example.jarvisv2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.jarvisv2.R
import com.example.jarvisv2.utils.gone

class SelectTextScreenFragment : Fragment() {
    private val selectArgs: SelectTextScreenFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_select_text_screen, container, false)
        val tool_bar_view = view.findViewById<View>(R.id.toolbar_layout)

        val robot_image_ll = tool_bar_view.findViewById<View>(R.id.robot_image_ll)
        robot_image_ll.gone()

        val close_image = tool_bar_view.findViewById<ImageView>(R.id.back_img)
        close_image.setImageResource(R.drawable.ic_close)

        close_image.setOnClickListener {
            findNavController().navigateUp()
        }

        val title_txt = tool_bar_view.findViewById<TextView>(R.id.titleTxt)
        title_txt.text = "Select Text"

        val select_txt = view.findViewById<TextView>(R.id.select_txt)
        select_txt.text = selectArgs.selectedMessage

        return view
    }
}
