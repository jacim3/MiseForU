package com.jacim3.miseforyou.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.jacim3.miseforyou.R
import com.jacim3.miseforyou.addons.AtmosphericAssembler
import com.jacim3.miseforyou.addons.ForeCastAssembler

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

// 받아올 atmospheres(대기오염정보 배열) 에서 각 데이터에 해당하는 인덱스 값
const val DATE = 0      // 측정 날짜 시간
//private const val MT = 1      // 측정망 정보
const val SO2_V = 2      // 아황산가스 농도
const val CO1_V = 3      // 일산화탄소 농도
const val O3_V = 4      // 오존 농도
const val NO2_V = 5      // 이산화질소 농도
const val P10_V = 6     // 미세먼지 pm10 농도
//private const val p10_V_24 = 7  // pm10 24시간 예측 농도
const val P25_V = 8     // 미세먼지 pm2.5 농도
//private const val p25_V_24 = 9  // pm2.5 24시간 예측 농도
const val K_V = 10      // 통합 대기환경 수치
//private const val K_G = 11    // 통합 대기환경 지수
const val SO2_G = 12     // 아황산가스 지수
const val CO1_G = 13     // 일산화탄소 지수
const val O3_G = 14     // 오존 지수
const val NO2_G = 15     // 이산화질소 지수
//const val P_10_G = 16   // 미세먼지 pm10 24시간 등급
//const val P_25_G = 17   // 미세먼지 pm2.5 24시간 등급
const val P10G_1H = 18         // 미세먼지 pm10 1시간 등급
const val P25G_1H = 19         // 미세먼지 pm2.5 1시간 등급
const val STATION_NAME = 23 // 추가로 삽입한 측정소 이름

class SubFragment1 : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_sub1, container, false)
        tvDStatus = view.findViewById(R.id.tvDust_Status)
        tvDFigure = view.findViewById(R.id.tvDust_Figure)

        val ivRain = view.findViewById<ImageView>(R.id.ivRain)
        val tvRPercent = view.findViewById<TextView>(R.id.tvRain_Precent)
        val tvRDesc = view.findViewById<TextView>(R.id.tvRain_Desc)

        val tvHFigure = view.findViewById<TextView>(R.id.tvHumidity_Percent)

        val ivWind = view.findViewById<ImageView>(R.id.ivWind)
        val tvWDetail = view.findViewById<TextView>(R.id.tvWind_Detail)

        Handler().postDelayed(object: Runnable{
            override fun run() {
                if(ForeCastAssembler.isForeReady){
                    setForeCast(tvRPercent,tvHFigure,tvWDetail)
                } else
                    Handler().postDelayed(this,1000)
            }
        },0)
        return view
    }

    fun setForeCast(tvRPercent: TextView, tvHFigure: TextView, tvWdetail: TextView) {
        var isPOP = true
        var detailInfo = ""
        FirstFragment.foreCasts[TAG].forEachIndexed { index, s ->
            if(s == "POP" && isPOP) {
                tvRPercent.text = FirstFragment.foreCasts[DATA][index] + "%"
                isPOP = false
            }
            if(s == "REH")
                tvHFigure.text = FirstFragment.foreCasts[DATA][index]+"%"

            if (s == "WSD")
                tvWdetail.text = FirstFragment.foreCasts[DATA][index]+"m/s"
        }
    }

    fun windCalc() {

    }

    companion object {
        lateinit var tvDStatus: TextView
        lateinit var tvDFigure: TextView

        fun newInstance(param1: String, param2: String) =
            SubFragment1().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        fun setDust(tmp: Int) {
            if(AtmosphericAssembler.isAtmoReady){
                val fineDusts = FirstFragment.atmospheres[tmp][P10_V]+"/"+FirstFragment.atmospheres[tmp][P25_V]
                var max = 0
                var str = ""
                var colorStr =""
                val pm10 = FirstFragment.atmospheres[tmp][P10G_1H].toInt()
                val pm25 = FirstFragment.atmospheres[tmp][P25G_1H].toInt()

                max = if(pm10 < pm25) pm25 else pm10

                when(max){
                    1 -> {str= "좋음"; colorStr = "#00B4DB"}
                    2 -> {str= "보통"; colorStr = "#FFFFFF"}
                    3 -> {str= "나쁨"; colorStr = "#ffc000"}
                    4 -> {str= "매우나쁨"; colorStr = "#ed7d31"}
                }

                tvDFigure.text = fineDusts
                tvDStatus.text = str
                tvDStatus.setTextColor(Color.parseColor(colorStr))
            }
        }
    }
}