package com.jacim3.miseforyou.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.jacim3.miseforyou.MainActivity
import com.jacim3.miseforyou.R
import com.jacim3.miseforyou.addons.AtmosphericAssembler
import com.jacim3.miseforyou.addons.ForeCastAssembler
import com.jacim3.miseforyou.addons.SubPagerAdapter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

const val TAG = 0
const val DATA = 1
private const val DELAY_TIME = 10 * 1000L
const val ATMOSPHERE_ASSEMBLER_CODE = 0
const val FORECAST_ASSEMBLER_CODE = 1
/*
private const val MAX_SIZE = 8  // 저장할 데이터의 개수
private const val POP = 0       // 강수 확률
private const val PTY = 1       // 강수 형태
private const val REH = 2       // 습도
private const val SKY = 3       // 하늘 상태
private const val T3H = 4       // 3시간 기온
private const val TMN = 5       // 아침 최저기온
private const val TMX = 6       // 낮 최고기온
private const val UUU = 7       // 풍속 동서
private const val VVV = 8       // 풍속 남북
private const val S06 = 9       // 신적설(하루마다 새롭게 쌓이는 눈의 양
*/
class FirstFragment : Fragment() {

    var atmoAssembler: AtmosphericAssembler? = null
    var foreAssembler: ForeCastAssembler? = null
    var onCreateBool = false        // GetAtmoThread로 부터 데이터 사용 여부를 검사하여, 가능할 시 true를 반환
    val wStatus = arrayOf(arrayOf(R.drawable.ic_sun,R.drawable.ic_cloudy,R.drawable.ic_cloudy,R.drawable.ic_cloud_sun),
        arrayOf(R.drawable.ic_night, R.drawable.ic_cloudy, R.drawable.ic_cloudy, R.drawable.ic_cloud_moon))
    val wDesc = arrayOf("맑음","구름많음","구름많음","흐림")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ctx = context!!
        getAct = activity!!
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_first, container, false)
        rotateButton = view.findViewById(R.id.rotateButton)
        spinner = view.findViewById(R.id.spinner)
        val tvTime = view.findViewById<TextView>(R.id.tvTime)       // 시간 출력
        val tvDate = view.findViewById<TextView>(R.id.tvDate)       // 날짜 출력
        val tvLocation = view.findViewById<TextView>(R.id.tvLocation)       // 장소 출력
        val tvTemp = view.findViewById<TextView>(R.id.tvTemp)       // 온도 출력
        val ivWeather = view.findViewById<ImageView>(R.id.ivWeather)        // 날씨 출력(이미지)
        val tvWeDesc = view.findViewById<TextView>(R.id.tvWeatherDesc)      // 날씨 설명 출력

        val scrollPager = view.findViewById<ViewPager2>(R.id.scrollPager)
        val rotate = AnimationUtils.loadAnimation(context, R.anim.rotate_anim)
        rotateButton.isSoundEffectsEnabled = false

        scrollPager.adapter = SubPagerAdapter(activity!!.supportFragmentManager,lifecycle)

        basicTemp(tvTemp, ivWeather, tvWeDesc)
        basicLocation(tvLocation)
        Handler().postDelayed(object: Runnable {
            override fun run() {
                basicDate(tvTime, tvDate)
                Handler().postDelayed(this, DELAY_TIME)
            }
        },0)

        rotateButton.setOnClickListener {

                if (!proceedAtmo && !AtmosphericAssembler.isAtmoReady) {   // 로딩애니메이션 재생
                    rotateButton.startAnimation(rotate)
                    proceedAtmo = true
                    atmoAssembler = AtmosphericAssembler(MainActivity.mLatitude, MainActivity.mLongitude)
                }
                if (!proceedFore && !ForeCastAssembler.isForeReady){
                    rotateButton.startAnimation(rotate)
                    proceedFore = true
                    foreAssembler = ForeCastAssembler()
                }

                else {
                    Toast.makeText(context,"현재 데이터를 읽어오는 중 입니다.",Toast.LENGTH_SHORT).show()
                }
            }
        onCreateBool = true
        rotateButton.performClick()
        return view
    }

    private fun basicLocation(tvLocation: TextView) {

        val addressSet = MainActivity.address.split(" ")[0]+" "+MainActivity.address.split(" ")[1]+" "+MainActivity.address.split(" ")[2]
        tvLocation.text = addressSet
    }

    private fun basicDate(tvTime: TextView, tvDate: TextView) {
        val date = Date(System.currentTimeMillis())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.KOREA)
        val dateFormat = SimpleDateFormat("M월 d일 E요일",Locale.KOREA)

        val getTime = timeFormat.format(date)
        val getDate = dateFormat.format(date)


        tvTime.text = getTime.toString()
        tvDate.text = getDate.toString()
    }

    private fun basicTemp(tvTemp: TextView, ivWeather: ImageView, tvDesc: TextView) {
        Handler().postDelayed(object: Runnable {
            override fun run() {
                if(ForeCastAssembler.isForeReady) {

                    val compareTime = SimpleDateFormat("HH",Locale.KOREA).format(Date(System.currentTimeMillis())).toInt()

                    foreCasts[TAG].forEachIndexed { index: Int, s: String ->
                        if (s == "T3H") {
                            tvTemp.text = foreCasts[DATA][index]
                        }
                        if (s == "SKY") {
                            val idx = foreCasts[DATA][index].toInt() -1
                            var tdx = 0
                            tdx = if (compareTime >= 18 || compareTime < 6) 1 else 0
                            ivWeather.setImageResource(wStatus[tdx][idx])
                            tvDesc.text = wDesc[idx]
                        }
                    }
                } else {
                    Handler().postDelayed(this, 500)
                }
            }
        },0)
        var tmp = 0
    }

    override fun onPause() {
        super.onPause()
        Log.d("이거", "이거")
    }

    companion object {

        var stations = ArrayList<String>()
        var atmospheres: ArrayList<ArrayList<String>> = ArrayList()
        var foreCasts: ArrayList<ArrayList<String>> = ArrayList()

        var proceedAtmo = false
        var proceedFore = false
        lateinit var rotateButton: ImageView
        lateinit var ctx: Context
        lateinit var spinner: Spinner
        lateinit var getAct: FragmentActivity

        fun newInstance(): FirstFragment {

            return FirstFragment()
        }

        fun getAtmosphere(station: ArrayList<String>?, atmosphere: ArrayList<ArrayList<String>>?) {
            stations = station!!
            atmospheres = atmosphere!!
        }
        fun getForeCast(foreCast: ArrayList<ArrayList<String>>?){
            foreCasts = foreCast!!
        }

        fun encounterError(requestCode: Int) {
            if (requestCode == ATMOSPHERE_ASSEMBLER_CODE)
                proceedAtmo = false
            else
                proceedFore = false

            rotateButton.clearAnimation()
            Snackbar.make(getAct.findViewById(R.id.snackView),"정보를 받아오는데 실패하였습니다 !\n나중에 다시 시도해 주세요",Snackbar.LENGTH_INDEFINITE).setAction("확인",
                {}).show()
        }
        fun receptionOK(requestCode: Int){

            if (requestCode == ATMOSPHERE_ASSEMBLER_CODE) {
                proceedAtmo = false
                spinnerSetting(spinner)
            }
            else
                proceedFore = false



            if(AtmosphericAssembler.isAtmoReady && ForeCastAssembler.isForeReady) {
                rotateButton.clearAnimation()
                rotateButton.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.fade_out))
                Handler().postDelayed({ rotateButton.isVisible = false }, 1000)
            }
        }

        private fun spinnerSetting(spinner: Spinner) {
            var tmp = 0
            val arrayAdapter =
                ArrayAdapter(ctx, R.layout.custom_spinner, stations)
            arrayAdapter.setDropDownViewResource(R.layout.custom_spinner)
            spinner.adapter = arrayAdapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    try {
                        for (i in stations.indices) {           // 추가 삽입한 측정소 위치 데이터를 통하여 거기에 맞는 정보를 검색
                            if (atmospheres[i][atmospheres[i].size - 1] == stations[position]) {
                                tmp = i
                                break
                            }
                        }
                    }catch (e: IndexOutOfBoundsException) {
                        //encounterError()
                    }
                    SubFragment1.setDust(tmp)
                    if (SubFragment2.onCreate)
                        SubFragment2.setView(tmp)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

        }
    }
}