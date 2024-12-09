package com.jlizquierdo.redtaller.ui.acercade

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jlizquierdo.redtaller.R
import com.jlizquierdo.redtaller.databinding.FragmentAcercadeBinding

class AcercadeFragment : Fragment() {

    private var _binding: FragmentAcercadeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val acercadeViewModel =
            ViewModelProvider(this).get(AcercadeViewModel::class.java)

        _binding = FragmentAcercadeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textAcercade
        acercadeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = getResources().getText(R.string.acerca_de_texto)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}