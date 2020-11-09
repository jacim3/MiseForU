package com.jacim3.miseforyou.threads

import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import com.jacim3.miseforyou.MainActivity
import com.jacim3.miseforyou.addons.AtmosphericAssembler
import com.jacim3.miseforyou.fragments.ATMOSPHERE_ASSEMBLER_CODE
import com.jacim3.miseforyou.fragments.FirstFragment
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.lang.Exception
import java.net.ConnectException
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/*
    받아온 인접 측정소 정보를 통하여, 대기오염 정보를 받아오는 쓰레드
 */
val SERVICE_KEY = MainActivity.SERVICE_KEY

class GetAtmoInfoThread(var stationName: String) : Thread() {


    private val atmoInfoUrl = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?serviceKey=${SERVICE_KEY}&numOfRows=1&pageNo=1&stationName=${stationName}&dataTerm=DAILY&ver=1.3&"

    var handler = Handler()


    // api로 부터 받아온 데이터를 저장할 변수 관련
    var isChecked = false
    private var atmoStatusInfo = ArrayList<String>()

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun run() {
        if (active) {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            Log.d("debug", "GetAtmoInfothread : $atmoInfoUrl")
            val url = URL(atmoInfoUrl)
            try {
                val ins = url.openStream()
                xpp.setInput(ins, "utf-8")

                var eventType = xpp.eventType

                while (eventType != XmlPullParser.END_DOCUMENT) {

                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            if (xpp.name == "dataTime")
                                isChecked = true
                        }
                        XmlPullParser.TEXT -> {
                            if (isChecked) {
                                if (xpp.text.trim() != "")
                                    atmoStatusInfo.add(xpp.text.trim())
                            }
                        }
                        XmlPullParser.END_TAG -> {
                            if (xpp.name == "response") {
                                Log.d("debug", "${URLDecoder.decode(stationName, StandardCharsets.UTF_8.name())} : $atmoStatusInfo")
                                handler.post {
                                    atmoStatusInfo.add(URLDecoder.decode(stationName, StandardCharsets.UTF_8.name()))
                                    AtmosphericAssembler.getAtmoThreadResponse(atmoStatusInfo)
                                }
                            }
                            active = false
                        }
                    }
                    eventType = xpp.next()
                }

            } catch (e: ConnectException) {
                FirstFragment.encounterError(ATMOSPHERE_ASSEMBLER_CODE)
            }
        }
    }

    init {
        try {
            stationName = URLEncoder.encode(stationName, "utf-8")
        } catch (e: Exception) {

        }
    }

    companion object {
        var active = false
    }
}