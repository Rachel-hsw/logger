package com.orhanobut.sample;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogAdapter;
import com.orhanobut.logger.Logger;

public class ExpDiskLogAdapter implements LogAdapter {
    @NonNull
    private final FormatStrategy formatStrategy;

    @Override
    public boolean isLoggable(int priority, @Nullable String tag) {
        return true;
    }

    @Override
    public void log(int priority, @Nullable String tag, @NonNull String message) {
        if (priority != Logger.WARN) {
            return;
        }
        formatStrategy.log(priority, tag, message);
    }

    public ExpDiskLogAdapter() {
        formatStrategy = ExpCsvFormatStrategy.newBuilder().build();
    }


}
