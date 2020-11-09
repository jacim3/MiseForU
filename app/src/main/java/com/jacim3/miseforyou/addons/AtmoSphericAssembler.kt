package com.jacim3.miseforyou.addons

import android.util.Log
import com.jacim3.miseforyou.MainActivity
import com.jacim3.miseforyou.fragments.ATMOSPHERE_ASSEMBLER_CODE
import com.jacim3.miseforyou.fragments.FirstFragment
import com.jacim3.miseforyou.threads.GetAtmoInfoThread
import com.jacim3.miseforyou.threads.GetCoordinateSystemThread
import com.jacim3.miseforyou.threads.GetStationsThread


class AtmosphericAssembler(latitude: String, longitude: String) {

    init {
        val getCoordinateThread = GetCoordinateSystemThread(latitude, longitude)
        GetCoordinateSystemThread.active = true
        getCoordinateThread.start()
    }

    companion object {

        var isTmReady = false
        var isStationReady = false
        var isAtmoReady = false
        private var stations = ArrayList<String>()
        private var atmoInfoArray = ArrayList<ArrayList<String>>()
        var count = 0

        // 좌표계 쓰레드에서 호출되어 결과값을 전송받아온다.
        fun getCoordThreadResponse(getTmX: String?, getTmY: String?) {

            val getStations = GetStationsThread(getTmX.toString(), getTmY.toString())
            GetStationsThread.active = true
            getStations.start()
            isTmReady = true

            Log.d("MainActivity", "GetCoordThread : 완료")
        }

        // 인접 측정소 쓰레드에서 호출되어 결과값을 전송받아온다.
        fun getStationThreadResponse(
            station: ArrayList<String>,
            addrs: ArrayList<String>,
            tms: ArrayList<String>
        ) {
            stations = station
            for (i in stations.indices) {
                val getAtmoInfo = GetAtmoInfoThread(stations[i])
                GetAtmoInfoThread.active = true
                getAtmoInfo.start()

                isStationReady = true
            }
            Log.d("MainActivity", "GetStationThread : 완료")
        }

        // 대기정보 쓰레드에서 호출되어 결과값을 받아온다.
        fun getAtmoThreadResponse(atmoStatusInfo: ArrayList<String>) {
            count++

            atmoInfoArray.add(atmoStatusInfo)
            Log.e("aaaa",MainActivity.addresses.toString())
            if(MainActivity.addresses!!.size != 0) {
                if (count == stations.size - 1) {
                    isAtmoReady = true
                    FirstFragment.getAtmosphere(stations, atmoInfoArray)
                    count = 0
                    Log.d("MainActivity", "GetAtmoInfoThread : 완료")
                    FirstFragment.receptionOK(ATMOSPHERE_ASSEMBLER_CODE)
                    Log.e("bbbb",MainActivity.addresses.toString())
                }
            } else {
                FirstFragment.encounterError(ATMOSPHERE_ASSEMBLER_CODE)
                Log.e("cccc",MainActivity.addresses.toString())
                return
            }
        }
    }
}