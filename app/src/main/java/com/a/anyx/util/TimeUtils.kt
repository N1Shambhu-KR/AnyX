package com.a.anyx.util

import java.util.concurrent.TimeUnit

class TimeUtils {

    companion object{

        fun milliSecondsToHHMMSS(milliSeconds:Long):String{

            return String.format("%02d:%02d:%02d",TimeUnit.MILLISECONDS.toHours(milliSeconds),TimeUnit.MILLISECONDS.toMinutes(milliSeconds) -
                                                  TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)))
        }
    }
}