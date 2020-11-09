package com.jacim3.miseforyou.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.jacim3.miseforyou.R
import com.jacim3.miseforyou.addons.AtmosphericAssembler

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SubFragment2 : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sub2, container, false)

        tvO3Status = view.findViewById(R.id.tvO3_Status)
        tvO3Figure = view.findViewById(R.id.tvO3_Figure)
        tvCO1Status = view.findViewById(R.id.tvCO_Status)
        tvCO1Figure = view.findViewById(R.id.tvCO_Figure)
        tvNO2Status = view.findViewById(R.id.tvNO2_Status)
        tvNO2Figure = view.findViewById(R.id.tvNO2_Figure)
        tvSO2Status = view.findViewById(R.id.tvSO2_Status)
        tvSO2Figure = view.findViewById(R.id.tvSO2_Figure)

        Handler().postDelayed(object: Runnable{
            override fun run() {
                if(AtmosphericAssembler.isAtmoReady)
                    setView(0)
                else
                    Handler().postDelayed(this, 1000)
            }
        },0)
        onCreate = true
        return view
    }

    companion object {

        lateinit var tvO3Status: TextView
        lateinit var tvO3Figure: TextView
        lateinit var tvCO1Status: TextView
        lateinit var tvCO1Figure: TextView
        lateinit var tvNO2Status: TextView
        lateinit var tvNO2Figure: TextView
        lateinit var tvSO2Status: TextView
        lateinit var tvSO2Figure: TextView

        var onCreate = false

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SubFragment2().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        fun prepareView() {

        }
        fun setView(tmp: Int) {
            tvO3Figure.text = FirstFragment.atmospheres[tmp][O3_V]
            tvCO1Figure.text = FirstFragment.atmospheres[tmp][CO1_V]
            tvNO2Figure.text = FirstFragment.atmospheres[tmp][NO2_V]
            tvSO2Figure.text = FirstFragment.atmospheres[tmp][SO2_V]

            atmoSymbol(FirstFragment.atmospheres[tmp][O3_V], FirstFragment.atmospheres[tmp][O3_G], tvO3Status)
            atmoSymbol(FirstFragment.atmospheres[tmp][CO1_V], FirstFragment.atmospheres[tmp][CO1_G], tvCO1Status)
            atmoSymbol(FirstFragment.atmospheres[tmp][NO2_V], FirstFragment.atmospheres[tmp][NO2_G], tvNO2Status)
            atmoSymbol(FirstFragment.atmospheres[tmp][SO2_V], FirstFragment.atmospheres[tmp][SO2_G], tvSO2Status)
        }
        private fun atmoSymbol(value: String, grade: String,textView: TextView){
            val idx = grade.toInt()-1
            val symbol = arrayOf("좋음","보통","나쁨","매우나쁨")
            val colors = arrayOf("#00B4DB","#FFFFFF","#ffc000","#ed7d31")

            if(grade == "-" || value == "-") {
                textView.text = "정보없음"
                textView.setTextColor(Color.parseColor("#FFFFFF"))
            }
            else {
                textView.text = symbol[idx]
                textView.setTextColor(Color.parseColor(colors[idx]))
            }
        }
    }
}