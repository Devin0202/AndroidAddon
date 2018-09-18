package com.devin.mylayout;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StillTest {
    private final String TAG = this.getClass().toString() + "-DY";
    public List<String> newList;
    public List<String> resultList;
    public String picRoot;
    public String fileName;
    public int frameCount = 0;
    public float totalTime = 0;

    public StillTest()
    {
        resultList = new ArrayList();
        picRoot = "/sdcard/Download/EF/";
        newList = Txt();
    }

    public void Wxt(List<String> msg) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-HH-mm");
        String filePath = "/sdcard/Download/" + "face++33pxFDDB" + "_1280720_" + dateFormat.format(now)
                + ".txt";

        FileOutputStream outputStream;
        try {
            File outFile = new File(filePath);
            outputStream = new FileOutputStream(outFile);
            for (String i : msg){
                outputStream.write(i.getBytes());
                outputStream.write("\n".getBytes());
            }
            outputStream.flush();
            outputStream.close();
            Log.d(TAG, "rectFile: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> Txt() {
        //将读出来的一行行数据使用List存储
        String filePath = picRoot + "hisceneFace.txt";

        List newList = new ArrayList<String>();
        try {
            File file = new File(filePath);
            int count = 0;//初始化 key值
            if (file.isFile() && file.exists()) {//文件存在
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
                BufferedReader br = new BufferedReader(isr);
                String lineTxt = null;
                while ((lineTxt = br.readLine()) != null) {
                    if (!"".equals(lineTxt)) {
                        String reds = lineTxt.split("\\+")[0];  //java 正则表达式
                        newList.add(count, reds);
                        count++;
                    }
                }
                isr.close();
                br.close();
            } else {
                Log.d(TAG, filePath + " can not find file");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newList;
    }

    public byte[] getNV21(int inputWidth, int inputHeight, Bitmap scaled) {
        int [] argb = new int[inputWidth * inputHeight];

        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);

        byte [] yuv = new byte[inputWidth*inputHeight * 3 / 2];
        encodeYUV420SP(yuv, argb, inputWidth, inputHeight);

        scaled.recycle();

        return yuv;
    }

    public void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        final int frameSize = width * height;

        int yIndex = 0;
        int uvIndex = frameSize;

        int a, R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                a = (argb[index] & 0xff000000) >> 24; // a is not used obviously
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff) >> 0;

                // well known RGB to YUV algorithm
                Y = ( (  66 * R + 129 * G +  25 * B + 128) >> 8) +  16;
                U = ( ( -38 * R -  74 * G + 112 * B + 128) >> 8) + 128;
                V = ( ( 112 * R -  94 * G -  18 * B + 128) >> 8) + 128;

                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                //    pixel AND every other scanline.
                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] = (byte)((V<0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[uvIndex++] = (byte)((U<0) ? 0 : ((U > 255) ? 255 : U));
                }

                index ++;
            }
        }
    }
}