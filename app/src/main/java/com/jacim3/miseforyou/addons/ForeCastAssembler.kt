package com.jacim3.miseforyou.addons

import android.util.Log
import com.jacim3.miseforyou.MainActivity
import com.jacim3.miseforyou.fragments.FORECAST_ASSEMBLER_CODE
import com.jacim3.miseforyou.fragments.FirstFragment
import com.jacim3.miseforyou.fragments.SecondFragment
import com.jacim3.miseforyou.threads.GetForeCastThread

class ForeCastAssembler {

    init {
        val changer = LatLngToGridxy(MainActivity.mLatitude.toDouble(), MainActivity.mLongitude.toDouble())
        Log.d("MainActivity : ", "Latitude : ${changer.locX}, Longitude : ${changer.locY}")

        val getForeCastThread = GetForeCastThread(changer.locX.toString(), changer.locY.toString())
        GetForeCastThread.active = true
        getForeCastThread.start()
    }

    companion object{
        var isForeReady = false     // ForeCastThread 로 부터 데이터를 받아와 사용이 가능함을 알려주는 변수

        fun getForeThreadResponse(data: ArrayList<ArrayList<String>>) {
            if(data[0].size != 0) {
                FirstFragment.getForeCast(data)
                isForeReady = true
                FirstFragment.receptionOK(FORECAST_ASSEMBLER_CODE)
            } else {
                FirstFragment.encounterError(FORECAST_ASSEMBLER_CODE)
            }
        }
    }
}