package com.jacim3.miseforyou.threads

import android.os.Handler
import android.util.Log
import com.jacim3.miseforyou.MainActivity
import com.jacim3.miseforyou.addons.AtmosphericAssembler
import com.jacim3.miseforyou.fragments.ATMOSPHERE_ASSEMBLER_CODE
import com.jacim3.miseforyou.fragments.FirstFragment
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

/*
    Service 를 상속한 Google GPS 를 통하여 받아온 위경도 좌표를 카카오 Api 에 전송하여 좌표계변환을 통한 TM 좌표를 수신받는 쓰레드.

    GPS 에서 얻은 위/경도를 기반으로 address(latitude,longitude) 를 통하여 얻은 주소로 공공데이터 포털에서 tm 좌표를 가져오는 방법이 훨씬 간편하나,
    오차에 따라 주소가 약간씩 변하거나, 주소 표현이 달라지는 문제 때문에 이렇게 처리하는 방식이 훨씬 정확하다.
*/
val API_KEY = MainActivity.RESTAPI_KEY

class GetCoordinateSystemThread(latitude: String, longitude:String) : Thread() {

    // 카카오 rest_api 키

    private var tmX = ""
    private var tmY = ""

    var handler = Handler()

    // 보통은 위도 -> 경도 순서로 보내지나, 카카오는 경도 -> 위도 순서로 보내야 한다.
    private val REQUEST_URL = "https://dapi.kakao.com/v2/local/geo/transcoord.json?output_coord=TM&x=${longitude}&y=${latitude}&input_coord=WGS84"
    private var parseEnd = false

    override fun run() {

        if(active){
            val ins: InputStream
            try{
                parseEnd = false
                val urlConn = URL(REQUEST_URL)
                Log.e("debug", REQUEST_URL)
                val httpConn : HttpURLConnection = urlConn.openConnection() as HttpURLConnection

                var json = ""

                val jsonObject = JSONObject()       // Json 객체 생성
                json = jsonObject.toString()        // json 객체를 String으로

                // 첫 헤더를 설정함으로서 서버에 알리기 위함. 여기선 카카오 인증 작업
                httpConn.setRequestProperty("Authorization", "KakaoAK $API_KEY")

                httpConn.setRequestProperty("Accept", "application/json")
                httpConn.setRequestProperty("Content-type", "application/json")

                httpConn.requestMethod = "GET"

                // OutputStream 으로 POSt 데이터를 넘겨주겠다는 설정
                httpConn.doOutput = true
                // InputStream 으로 서버로부터 응답을 받겠다는 옵션
                httpConn.doInput = true

                val ous : OutputStream = httpConn.outputStream

                ous.write(json.toByteArray(charset("euc-kr")))

                ous.flush()

                // InputStream 으로부터 응답을 받도록..
                var str : String?
                val receiveMsg : String

                if (httpConn.responseCode == HttpURLConnection.HTTP_OK){
                    ins = httpConn.inputStream
                    val responseBodyReader = InputStreamReader(ins, "UTF-8")
                    Log.e("debug", "responseBodyReader : ${responseBodyReader.toString().length}")
                    val reader = BufferedReader(responseBodyReader)
                    val buffer = StringBuilder()

                    // ((str = reader.readLine()) != null)
                    while (reader.readLine().also { str = it } != null) {
                        buffer.append(str)
                    }
                    receiveMsg = buffer.toString()
                    reader.close()

                    var jObject = JSONObject(receiveMsg)

                    val filter:String = jObject.getString("documents")
                    Log.e("debug", "filter : $filter")

                    val jsonArray = JSONArray(filter)
                    jObject = jsonArray.getJSONObject(0)

                    tmX = jObject.getString("x")
                    tmY = jObject.getString("y")
                    parseEnd = true
                    Log.e("debug","tmX = $tmX, tmY = $tmY")

                }else{
                    Log.e("debug","${httpConn.responseCode} 에러")
                }
            } catch (e: Exception){
                FirstFragment.encounterError(ATMOSPHERE_ASSEMBLER_CODE)
            }
            if(parseEnd) {
                active = false
                handler.post {
                    AtmosphericAssembler.getCoordThreadResponse(tmX, tmY)
                }
            }
        }
    }
    companion object{
        var active = false
    }
}