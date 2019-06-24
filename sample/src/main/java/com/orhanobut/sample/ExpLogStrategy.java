package com.orhanobut.sample;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.orhanobut.logger.LogStrategy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class ExpLogStrategy implements LogStrategy {
  @NonNull
  private final Handler handler;

  public ExpLogStrategy(@NonNull Handler handler) {
    this.handler = Utils.checkNotNull(handler);
  }

  @Override
  public void log(int level, @Nullable String tag, @NonNull String message) {
    Utils.checkNotNull(message);

    // do nothing on the calling thread, simply pass the tag/msg to the background thread
    handler.sendMessage(handler.obtainMessage(level, message));
  }

  static class WriteHandler extends Handler {

    @NonNull
    private final String folder;
    private final int maxFileSize;

    WriteHandler(@NonNull Looper looper, @NonNull String folder, int maxFileSize) {
      super(Utils.checkNotNull(looper));
      this.folder = Utils.checkNotNull(folder);
      this.maxFileSize = maxFileSize;
    }

    @SuppressWarnings("checkstyle:emptyblock")
    @Override
    public void handleMessage(@NonNull Message msg) {
      String content = (String) msg.obj;

      FileWriter fileWriter = null;
      File logFile = getLogFile(folder);

      try {
        fileWriter = new FileWriter(logFile, true);

        writeLog(fileWriter, content);

        fileWriter.flush();
        fileWriter.close();
      } catch (IOException e) {
        if (fileWriter != null) {
          try {
            fileWriter.flush();
            fileWriter.close();
          } catch (IOException e1) { /* fail silently */ }
        }
      }
    }

    /**
     * This is always called on a single background thread.
     * Implementing classes must ONLY write to the fileWriter and nothing more.
     * The abstract class takes care of everything else including close the stream and catching IOException
     *
     * @param fileWriter an instance of FileWriter already initialised to the correct file
     */
    private void writeLog(@NonNull FileWriter fileWriter, @NonNull String content) throws IOException {
      Utils.checkNotNull(fileWriter);
      Utils.checkNotNull(content);

      fileWriter.append(content);
    }

    /**
     * 获取UUID
     *
     * @return
     */
    public static String getUUID32() {
      String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
      return uuid;
    }

    /**
     * 更改为文件名为UUID,文件扩展名为txt.这样处理的话，每次打印日志都会生成新的文件名。导致的后果是每个文件只记录一行日志。
     *
     * @param folderName
     * @return
     */
    private File getLogFile(@NonNull String folderName) {
      String fileName = "hsw" + getUUID32();
      Utils.checkNotNull(folderName);
      Utils.checkNotNull(fileName);

      File folder = new File(folderName);
      if (!folder.exists()) {
        //TODO: What if folder is not created, what happens then?
        folder.mkdirs();
      }

      File file = new File(folder, String.format("%s.txt", fileName));
      return file;
    }
  }
}
