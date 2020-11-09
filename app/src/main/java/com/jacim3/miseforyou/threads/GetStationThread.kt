package com.jacim3.miseforyou.threads

import android.os.Handler
import android.util.Log
import com.jacim3.miseforyou.addons.AtmosphericAssembler
import com.jacim3.miseforyou.fragments.ATMOSPHERE_ASSEMBLER_CODE
import com.jacim3.miseforyou.fragments.FirstFragment
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.URL

//val SERVICE_kEY = MainActivity.SERVICE_KEY

class GetStationsThread(tmX: String, tmY: String) : Thread() {

    private val nearStationsUrl =
        "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList?serviceKey=${SERVICE_KEY}&tmX=${tmX}&tmY=${tmY}&"

    var handler = Handler()

    var l_StationName = false;
    var l_Addr = false;
    var l_TM = false

    // 측정소 정보를 저장한 배열
    var stations = ArrayList<String>()
    var addrs = ArrayList<String>()
    var tms = ArrayList<String>()

    override fun run() {

        if (active) {
            try {
                val factory = XmlPullParserFactory.newInstance()
                factory.isNamespaceAware = true
                val xpp = factory.newPullParser()
                Log.d("debug", "GetStationThread : $nearStationsUrl")
                val url = URL(nearStationsUrl)
                val ins = url.openStream()
                xpp.setInput(ins, "utf-8")

                var eventType = xpp.eventType

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            if (xpp.name == "stationName") {
                                l_StationName = true
                            }
                            if (xpp.name == "addr")
                                l_Addr = true
                            if (xpp.name == "tm")
                                l_TM = true
                        }
                        XmlPullParser.TEXT -> {
                            if (l_StationName) {
                                stations.add(xpp.text)
                                l_StationName = false
                            }
                            if (l_Addr) {
                                addrs.add(xpp.text)
                                l_Addr = false
                            }
                            if (l_TM) {
                                tms.add(xpp.text)
                                l_TM = false
                            }
                        }
                        XmlPullParser.END_TAG -> {
                            if (xpp.name == "response") {   // 문서 끝의 종료 태그를 만났을 때,
                                Log.d("debug", "$stations")
                                Log.d("debug", "$addrs")
                                Log.d("debug", "$tms")

                                handler.post {
                                    AtmosphericAssembler.getStationThreadResponse(stations, addrs, tms)
                                }
                                active = false
                            }
                        }
                    }
                    eventType = xpp.next()
                }
            } catch (e : Exception) {
                FirstFragment.encounterError(ATMOSPHERE_ASSEMBLER_CODE)
            }
        }

    }

    companion object {
        var active = false
    }
}