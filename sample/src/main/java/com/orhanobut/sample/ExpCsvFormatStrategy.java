package com.orhanobut.sample;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.orhanobut.sample.Utils.checkNotNull;


public class ExpCsvFormatStrategy implements FormatStrategy {

  private static final String NEW_LINE = System.getProperty("line.separator");
  private static final String NEW_LINE_REPLACEMENT = " <br> ";
  private static final String SEPARATOR = ",";

  @NonNull
  private final Date date;
  @NonNull
  private final SimpleDateFormat dateFormat;
  @NonNull
  private final LogStrategy logStrategy;
  @Nullable
  private final String tag;

  private ExpCsvFormatStrategy(@NonNull ExpCsvFormatStrategy.Builder builder) {
    checkNotNull(builder);

    date = builder.date;
    dateFormat = builder.dateFormat;
    logStrategy = builder.logStrategy;
    tag = builder.tag;
  }

  @NonNull
  public static ExpCsvFormatStrategy.Builder newBuilder() {
    return new ExpCsvFormatStrategy.Builder();
  }

  @Override
  public void log(int priority, @Nullable String onceOnlyTag, @NonNull String message) {
    checkNotNull(message);

    String tag = formatTag(onceOnlyTag);

    date.setTime(System.currentTimeMillis());

    StringBuilder builder = new StringBuilder();
//        builder.append(Long.toString(date.getTime()));

    // human-readable date/time
    builder.append("[");
    builder.append(dateFormat.format(date));
    builder.append("]");
    if (TextUtils.isEmpty(tag)) {
      builder.append(tag);
      builder.append(" ");
    }
    // message这也是换行符,功能和"\n"是一致的,但是此种写法屏蔽了 Windows和Linux的区别 ，更保险一些.
    if (message.contains(NEW_LINE)) {
      // a new line would break the CSV format, so we replace it here
      message = message.replaceAll(NEW_LINE, NEW_LINE_REPLACEMENT);
    }

    builder.append(message);

    // new line
    builder.append(NEW_LINE);
    logStrategy.log(priority, tag, builder.toString());
  }

  @Nullable
  private String formatTag(@Nullable String tag) {
    if (!Utils.isEmpty(tag) && !Utils.equals(this.tag, tag)) {
      return this.tag + "-" + tag;
    }
    return this.tag;
  }

  public static final class Builder {
    private static final int MAX_BYTES = 500 * 1024; // 500K averages to a 4000 lines per file

    Date date;
    SimpleDateFormat dateFormat;
    LogStrategy logStrategy;
    String tag = "";

    private Builder() {
    }

    @NonNull
    public ExpCsvFormatStrategy.Builder date(@Nullable Date val) {
      date = val;
      return this;
    }

    @NonNull
    public ExpCsvFormatStrategy.Builder dateFormat(@Nullable SimpleDateFormat val) {
      dateFormat = val;
      return this;
    }

    @NonNull
    public ExpCsvFormatStrategy.Builder logStrategy(@Nullable LogStrategy val) {
      logStrategy = val;
      return this;
    }

    @NonNull
    public ExpCsvFormatStrategy.Builder tag(@Nullable String tag) {
      this.tag = tag;
      return this;
    }

    @NonNull
    public ExpCsvFormatStrategy build() {
      if (date == null) {
        date = new Date();
      }
      if (dateFormat == null) {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
      }
      if (logStrategy == null) {
        String diskPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String folder = diskPath + File.separatorChar + "hsw";

        HandlerThread ht = new HandlerThread("AndroidFileLogger." + folder);
        ht.start();
        Handler handler = new ExpLogStrategy.WriteHandler(ht.getLooper(), folder, MAX_BYTES);
        logStrategy = new ExpLogStrategy(handler);
      }
      return new ExpCsvFormatStrategy(this);
    }
  }
}
