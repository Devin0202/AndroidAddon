package com.facepp.demo;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy;
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.elvishew.xlog.printer.file.naming.FileNameGenerator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class customXlog{
    private LogConfiguration config;
    private Printer androidPrinter;
    private Printer filePrinter;
    private String customTag;
    private String logFolder;
    private int logEnable;

    private static volatile customXlog instance;

    private customXlog() {}

    public static customXlog getInstance(String mTag, String mFloder, boolean isOutput) {
        if (null == instance) {
            synchronized (customXlog.class) {
                if (null == instance) {
                    instance = new customXlog();
                    instance.customTag = mTag;
                    instance.logFolder = mFloder;
                    File faceFolder = new File(instance.logFolder);
                    if(!faceFolder.exists()) {
                        faceFolder.mkdir();
                    }

                    if (isOutput){
                        instance.logEnable = LogLevel.ALL;
                    }else{
                        instance.logEnable = LogLevel.NONE;
                    }
                    instance.initRef();
                }
            }
        }
        return instance;
    }

    public static customXlog useInstance() {
        if (null == instance) {
            synchronized (customXlog.class) {
                if (null == instance) {
                    instance = new customXlog();
                    instance.initNoRef();
                }
            }
        }else{
            ;
        }

        return instance;
    }

    private void initRef()
    {
        config = new LogConfiguration.Builder()
                .tag(customTag)                                         // 指定 TAG，默认为 "X-LOG"
                .logLevel(logEnable)
                .build();
        androidPrinter = new AndroidPrinter();             // 通过 android.util.Log 打印日志的打印器
        filePrinter = new FilePrinter                      // 打印日志到文件的打印器
                .Builder(logFolder)                              // 指定保存日志文件的路径
                .fileNameGenerator(new customFileNameGenerator())        // 指定日志文件名生成器，默认为 ChangelessFileNameGenerator("log")
                .backupStrategy(new FileSizeBackupStrategy(500 * 1024))     //指定单个log文件大小 500 * 1024 = 5M
                .build();

        XLog.init(config,                                                // 指定日志配置，如果不指定，会默认使用 new LogConfiguration.Builder().build()
                androidPrinter,                                        // 添加任意多的打印器。如果没有添加任何打印器，会默认使用 AndroidPrinter
                filePrinter);
    }

    private void initNoRef()
    {
        config = new LogConfiguration.Builder()
                .tag("MY_TAG")                                         // 指定 TAG，默认为 "X-LOG"
                .logLevel(LogLevel.ALL)
                .t()                                                   // 允许打印线程信息，默认禁止
                .st(2)                                                 // 允许打印深度为2的调用栈信息，默认禁止
                .b()                                                   // 允许打印日志边框，默认禁止
                .build();
        androidPrinter = new AndroidPrinter();             // 通过 android.util.Log 打印日志的打印器
        filePrinter = new FilePrinter                      // 打印日志到文件的打印器
                .Builder("/sdcard/xlog/")                              // 指定保存日志文件的路径
                .fileNameGenerator(new DateFileNameGenerator())        // 指定日志文件名生成器，默认为 ChangelessFileNameGenerator("log")
                .backupStrategy(new NeverBackupStrategy())     //指定单个log文件大小 无限制
                .build();

        XLog.init(config,                                                // 指定日志配置，如果不指定，会默认使用 new LogConfiguration.Builder().build()
                androidPrinter,                                        // 添加任意多的打印器。如果没有添加任何打印器，会默认使用 AndroidPrinter
                filePrinter);
    }

    public void cLog(String logMsg)
    {
        XLog.d(logMsg);
    }
}

/**
 * Generate file name according to the timestamp, different dates will lead to different file names.
 */
class customFileNameGenerator implements FileNameGenerator {

    ThreadLocal<SimpleDateFormat> mLocalDateFormat = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MM-dd-HH-mm", Locale.US);
        }
    };

    @Override
    public boolean isFileNameChangeable() {
        return true;
    }

    /**
     * Generate a file name which represent a specific date.
     */
    @Override
    public String generateFileName(int logLevel, long timestamp) {
        SimpleDateFormat sdf = mLocalDateFormat.get();
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(timestamp)) + ".txt";
    }
}