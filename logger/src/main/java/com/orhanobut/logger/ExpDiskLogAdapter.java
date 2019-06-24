package com.orhanobut.logger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ExpDiskLogAdapter implements LogAdapter {
  @NonNull private final FormatStrategy formatStrategy;

  @Override public boolean isLoggable(int priority, @Nullable String tag) {
    return true;
  }

  @Override public void log(int priority, @Nullable String tag, @NonNull String message) {
    formatStrategy.log(priority, tag, message);
  }

  public ExpDiskLogAdapter() {
    formatStrategy = ExpCsvFormatStrategy.newBuilder().build();
  }
}
