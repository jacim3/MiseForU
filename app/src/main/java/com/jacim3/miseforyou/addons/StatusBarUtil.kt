package com.jacim3.miseforyou.addons

import android.R
import android.app.Activity
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
/*

object StatusBarUtil {
    fun setStatusBarColor(activity: Activity, colorType: StatusBarColorType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 상태바 텍스트 색 지정
            if (colorType.backgroundColorId == R.color.background_dark) {
                // 흰색
                activity.window.decorView.systemUiVisibility = 0
            } else {
                // 검은색
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }

            // 상태바 배경 색 지정
            activity.window.statusBarColor =
                ContextCompat.getColor(activity, colorType.backgroundColorId)
        }
    }

    enum class StatusBarColorType(val backgroundColorId: Int) {
        // 색 지정
        WHITE_STATUS_BAR(R.color.background_light), DEFAULT_STATUS_BAR(R.color.background_light);

    }
}

*/