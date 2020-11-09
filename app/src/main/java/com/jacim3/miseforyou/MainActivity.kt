package com.jacim3.miseforyou

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.jacim3.miseforyou.addons.GpsTracker
import com.jacim3.miseforyou.addons.MyPagerAdapter
import me.relex.circleindicator.CircleIndicator3
import java.io.IOException
import java.util.*
import kotlin.system.exitProcess

private const val GPS_ENABLE_REQUEST_CODE = 2001
private const val PERMISSION_REQUEST_CODE = 100

private var tmx = ""
private var tmy = ""
private const val DELAY_TIME = 2000L        // 핸들러의 재 요청 딜레이 시간 ex)1000L = 1초
private const val REFRESH_TIME = 5 * 60 * 1000L     // 자동 리프레쉬 시간 = 5분
private const val DELAY_COUNT = 100
private const val TAG = 0;
private const val DATA = 1

class MainActivity : AppCompatActivity() {

    // FragmentStatePagerAdapter 는 화면에 보여지지 않는 fragment 를 메모리에서 제거하지만
    // FragmentPagerAdapter 는 모든 fragment 를 메모리에 유지하는 차이점을 가짐. -> 메모리 부하가 심하다.
    private var REQUIRED_PERMISSIONS: Array<String> = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private var pressedTime: Long = 0
    var pager: ViewPager2? = null
    var pagerAdapter: MyPagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.e("@@@", "MainActivity : onCreate()")

        //StatusBarUtil.setStatusBarColor(this,StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR)

/*
        val tb = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(tb)
        val ab = supportActionBar
        ab!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
*/

        pager = findViewById(R.id.viewPager)
        val indicator = findViewById<CircleIndicator3>(R.id.indicator)

        pagerAdapter = MyPagerAdapter(supportFragmentManager, lifecycle)
        pager?.orientation = ViewPager2.ORIENTATION_VERTICAL
        pager?.adapter = pagerAdapter
        indicator?.setViewPager(pager)

        SERVICE_KEY = getString(R.string.SERVICE_KEY)
        RESTAPI_KEY = getString(R.string.RESTAPI_KEY)

        val gpsTracker = GpsTracker(this)

        if (!checkLocationServiceStatus()) {
            showDialogForLocationServiceSetting()
        } else {
            checkRunTimePermission()
        }

        val latitude = gpsTracker.get_Latitude()
        val longitude = gpsTracker.get_Longitude()
        mLatitude = latitude.toString()
        mLongitude = longitude.toString()

        address = getCurrentAddress(latitude, longitude)
        Log.d("MainActivity : ", "Latitude : $latitude, Longitude : $longitude")
        Log.d("MainActivity : ", address)

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.size == REQUIRED_PERMISSIONS.size) {
            var checkResult = true

            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    checkResult = false;
                    break;
                }
            }

            if (checkResult) {
                // 위치 값을 가져올 수 있음
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[0]
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[1]
                    )
                ) {
                    Toast.makeText(this, "퍼미션이 거부되었습니다 앱을 재 실행하여 퍼미션을 허용해 주세요.", Toast.LENGTH_LONG)
                        .show()
                    finish()
                } else {
                    Toast.makeText(this, "퍼미션이 거부되었습니다. 설정에서 퍼미션을 허용하십시오.", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun checkRunTimePermission() {
        //런타임 퍼미션 처리
        //1. 위치 퍼미션을 가지고 있는지 체크
        val hasFineLocationPermission: Int = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission: Int = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {
            // 이미 퍼미션을 가지고 있다면, 위치 값을 가져올 수 있음
        } else {
            // 사용자가 퍼미션 거부를 한 적이 있는 경우
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    REQUIRED_PERMISSIONS[0]
                )
            ) {
                // 요청을 진행하기 전 사용자에게 퍼미션이 필요한 이유를 설명해야 한다.
                Toast.makeText(this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show()
                // 사용자에게 퍼미션 요청을 한다. 결과는 onRequestPermissionResult에서 수신함
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    PERMISSION_REQUEST_CODE
                )
            } else {
                // 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 한다.
                //결과는 똑같이 onRequestPermissionResult에서 수신
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    PERMISSION_REQUEST_CODE
                )

            }
        }
    }

    private fun getCurrentAddress(latitude: Double, longtitude: Double): String {
        val geocoder: Geocoder = Geocoder(this, Locale.getDefault())

        //val addresses: List<Address>

        try {
            addresses = geocoder.getFromLocation(latitude, longtitude, 7)
        } catch (ioException: IOException) {
            Toast.makeText(this, "지오코더 서비스 사용 불가", Toast.LENGTH_LONG).show()
            return "지오코더 서비스 사용 불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show()
            return "잘못된 GPS 좌표"
        }

        if (addresses == null || addresses!!.isEmpty()) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show()
            return "주소 미 발견"
        }
        val address: Address = addresses!![0]
        return address.getAddressLine(0).toString() + "\n"
    }

    //여기부터 GPS 활성화를 위한 메소드들
    private fun showDialogForLocationServiceSetting() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("위치 서비스 비활성화").setMessage("앱을 사용하기 위해서는 위치서비스가 필요합니다.").setCancelable(true)
            .setPositiveButton("설정") { _, _ ->
                val callGPSSettingIntent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)

            }.setNegativeButton("취소") { dialog, _ ->
                dialog.cancel()
                finish()
            }.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE -> {
                if (checkLocationServiceStatus()) {
                    Log.d("MainActivity : ", "onActivityResult : GPS 활성화")
                    checkRunTimePermission()

                    finishAffinity()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    exitProcess(0)
                }
            }
        }
    }
    // 로케이션 매니저를 통하여 GPS 제공자와 네트워크 제공자가 사용가능 한지를 체크하는 매서드
    private fun checkLocationServiceStatus(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onBackPressed() {
        //super.onBackPressed();
        if (pressedTime == 0L) {
            Toast.makeText(this@MainActivity, "버튼을 한번 더 누르면 앱을 종료합니다.", Toast.LENGTH_LONG).show()
            pressedTime = System.currentTimeMillis()
        } else {
            val seconds = (System.currentTimeMillis() - pressedTime).toInt()
            if (seconds > 2000) {
                pressedTime = 0
            } else {
                exitProcess(0)
            }
        }
    }


    companion object {

        var address: String = ""
        var addresses: List<Address>? = null

        var SERVICE_KEY = ""        // R.value.string 파일에 저장한 공공데이터 서비스키를 저장하는 변수
        var RESTAPI_KEY = ""        // REST API 키를 저장하는 변수

        var mLatitude = ""          // GPS 로 측정한 위/경도값을 저장하는 변수
        var mLongitude = ""

    }
}