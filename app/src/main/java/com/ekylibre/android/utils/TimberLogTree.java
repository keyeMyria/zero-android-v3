package com.ekylibre.android.utils;

import com.ekylibre.android.BuildConfig;

import timber.log.Timber;


public class TimberLogTree extends Timber.DebugTree {

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        // Workaround for devices that doesn't show lower priority logs
        if (BuildConfig.DEBUG)
            super.log(priority, tag, message, t);

    }

    @Override
    protected String createStackElementTag(StackTraceElement element) {
        // Add log statements line number to the log
        return super.createStackElementTag(element);  // + " - " + element.getLineNumber()
    }
}
