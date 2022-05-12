package com.galtashma.parsedashboard

import android.app.Application
import com.appizona.yehiahd.fastsave.FastSave

/**
 * Created by gal on 3/16/18, rewritten by Cyb3rKo on 05/12/22.
 */

class ParseDashboardApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FastSave.init(applicationContext)
    }
}
