package com.jacim3.miseforyou.threads

import android.annotation.SuppressLint
import android.os.Handler
import android.util.Log
import com.jacim3.miseforyou.addons.ForeCastAssembler
import com.jacim3.miseforyou.fragments.FORECAST_ASSEMBLER_CODE
import com.jacim3.miseforyou.fragments.FirstFragment
import com.jacim3.miseforyou.fragments.SecondFragment
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.FileNotFoundException
import java.lang.Exception
import java.net.ConnectException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/*
    공공데이터 포털의 동네예보 조회서비스 Api 를 활용하여 정보를 가져오는 Thread
*/

private const val TAG = 0
private const val DATA = 1

class GetForeCastThread(gridX: String, gridY: String) : Thread() {
    var handler = Handler()

    private val date = Date(System.currentTimeMillis())
    private val simpleDate1 = SimpleDateFormat("yyyyMMdd",Locale.KOREA)
    private val simpleDate2 = SimpleDateFormat("HHmm",Locale.KOREA)
    private var getDate =  simpleDate1.format(date)
    private val getTime = simpleDate2.format(date)
    private val setDate = simpleDate1.parse(getDate)

    private var baseDate = getDate
    private var baseTime = calcTime(getTime)
    private var foreCastUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst?serviceKey=${SERVICE_KEY}&pageNo=1&numOfRows=10&dataType=XML&base_date=${baseDate}&base_time=${baseTime}&nx=${gridX}&ny=${gridY}"
    var check1 = false
    var check2 = false

    private var dataArray: ArrayList<ArrayList<String>> = ArrayList()   // 기상 정보를 저장할 배열을 선언.
    private var tmpArray1 = ArrayList<String>()
    private var tmpArray2 = ArrayList<String>()

    override fun run() {

        if (active) {
            try {
                Log.e("@@@@@@@@@@", baseTime)
                val factory = XmlPullParserFactory.newInstance()
                factory.isNamespaceAware = true
                val xpp = factory.newPullParser()
                Log.d("GetForeCastThread", foreCastUrl)
                val url = URL(foreCastUrl)

                val ins = url.openStream()
                xpp.setInput(ins, "UTF-8")

                var eventType = xpp.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            if (xpp.name == "category") {
                                check1 = true
                            }

                            if (xpp.name == "fcstValue") {
                                check2 = true
                            }

                        }
                        XmlPullParser.TEXT -> {

                            if (check1) {
                                Log.e("@@@@@@@@@@", xpp.text)
                                tmpArray1.add(xpp.text)
                                check1 = false
                            }
                            if (check2) {
                                Log.e("@@@@@@@@@@", xpp.text)
                                tmpArray2.add(xpp.text)
                                check2 = false
                            }

                        }
                        XmlPullParser.END_TAG -> {
                            if (xpp.name == "response") {
                                dataArray.add(tmpArray1)
                                dataArray.add(tmpArray2)

                                Log.e("@@@@@@@@@@", dataArray[TAG].size.toString())
                                Log.e("@@@@@@@@@@", dataArray[DATA].toString())

                                handler.post {
                                    ForeCastAssembler.getForeThreadResponse(dataArray)
                                }
                            }
                        }
                    }
                    eventType = xpp.next()
                }
            } catch (e: Exception) {
                Log.d("GetForeCastThread : ", "NetExceptionError !!!")
                FirstFragment.encounterError(FORECAST_ASSEMBLER_CODE)
            }
        }
    }

    // 3시간마다 갱신되는 예보를 가까운 시간대에 맞춰서 받을수 있도록 하는 매서드.
    // 스스로 제작
    private fun calcTime (time : String) :String{
        var calc = (time[0]+time[1].toString()).toInt()
        return when {

            calc == 24 || calc == 1 || calc == 0 -> {

                val cal = GregorianCalendar(Locale.KOREA)
                cal.time = setDate
                cal.add(Calendar.DATE,-1)
                baseDate = simpleDate1.format(cal.time)
                "2300"
            }
            calc%3 != 2  -> {
                val tmp = calc / 3
                for(i in 0..7) {
                    if (tmp == i+1) {
                        calc = tmp*2 + i
                    }
                }
                if(calc.toString().length == 2) {
                    calc.toString() + "00"
                } else
                    "0"+calc.toString() + "00"
            }
            else    -> {
                time
            }
        }
    }

    companion object {

        var active = false
    }
}