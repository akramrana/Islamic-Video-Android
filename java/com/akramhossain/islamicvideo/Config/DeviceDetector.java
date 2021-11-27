package com.akramhossain.islamicvideo.Config;

import android.content.Context;
import android.content.res.Configuration;

public class DeviceDetector {

    private Context _context;

    public DeviceDetector(Context context) {
        this._context = context;
    }

    public boolean isTablet() {
        return (_context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)>= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
