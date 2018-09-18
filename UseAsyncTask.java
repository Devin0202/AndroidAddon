package com.devin.mylayout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.devin.mylayout.ArcSoft.FaceDB;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class useAsyncTask extends AsyncTask<String, Void, String> {
    private final String TAG = this.getClass().toString() + "-DY";

    public useAsyncTask(boolean isParallelled) {
        if (isParallelled) {
            try {
                Method setDefaultExecutor = AsyncTask.class.getMethod("setDefaultExecutor", Executor.class);
                try {
                    setDefaultExecutor.invoke(null, AsyncTask.THREAD_POOL_EXECUTOR);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }else{
            ;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public final static String[] functions = asyncJobs.functions;
    @Override
    protected String doInBackground(String... params) {
        if (2 > params.length) {
            Log.d(TAG, "Invalid Params!!!");
            return "Invalid Params!!!";
        }
        String info = params[0];
        String rt = "" + info;
//        for (int i = 0; i < 10; i++) {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            Log.d(TAG, "Get: " + info);
//        }

        switch (params[1]) {
            case "FaceAdd":
                {
                    asyncJobs.arcSoftFaceAdd(info);
                    break;
                }
            case "FaceDelete":
                {
                    asyncJobs.arcSoftFaceSingleDelete(info);
                    break;
                }
            case "GetIDs":
                {
                    asyncJobs.arcSoftGetIDs(info);
                    break;
                }
            case "SetClear":
                {
                    asyncJobs.arcSoftSetClear(info);
                    break;
                }
            default:
                {
                    Log.d(TAG, "NoThing to do!!!");
                }
        }
        return rt;
    }

    @Override
    protected void onPostExecute(String output) {
        Log.d(TAG, "onPostExecute: " + output);
    }

    //进度条版本
    //onPreExecute用于异步处理前的操作
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//        //此处将progressBar设置为可见.
//        progressBar.setVisibility(View.VISIBLE);
//    }
//
//    //在doInBackground方法中进行异步任务的处理.
//    @Override
//    protected Bitmap doInBackground(String... params) {
//        //获取传进来的参数
//        String url = params[0];
//        Bitmap bitmap = null;
//        URLConnection connection ;
//        InputStream is ;
//        try {
//            connection = new URL(url).openConnection();
//            is = connection.getInputStream();
//            //为了更清楚的看到加载图片的等待操作,将线程休眠3秒钟.
//            Thread.sleep(3000);
//            BufferedInputStream bis = new BufferedInputStream(is);
//            //通过decodeStream方法解析输入流
//            bitmap = BitmapFactory.decodeStream(bis);
//            is.close();
//            bis.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return bitmap;
//    }
//
//    //onPostExecute用于UI的更新.此方法的参数为doInBackground方法返回的值.
//    @Override
//    protected void onPostExecute(Bitmap bitmap) {
//        super.onPostExecute(bitmap);
//        //隐藏progressBar
//        progressBar.setVisibility(View.GONE);
//        //更新imageView
//        imageView.setImageBitmap(bitmap);
//    }
}
class asyncJobs {
    private final static String TAG = asyncJobs.class.toString() + "-DY";
    public final static String[] functions = {"FaceAdd", "FaceDelete", "GetIDs", "SetClear",
            "UpLoadServer", "DownLoadServer"};

    private static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
        } else if (file.exists()) {
            file.delete();
        }
    }

    public synchronized static void arcSoftSetClear(String mDBPath) {
        File file = new File(mDBPath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
        } else if (file.exists()) {
            file.delete();
        }

        Log.d(TAG, "The FaceSet is gone ");
        return;
    }

    public synchronized static void arcSoftGetIDs(String mDBPath) {
        FaceDB mFaceDB;
        mFaceDB = new FaceDB(mDBPath);
        mFaceDB.loadFaces();

        int totalIDs = 0;
        for (FaceDB.FaceRegist id : mFaceDB.mRegister) {
            Log.d(TAG, "Registered: " + id.mName);
            totalIDs++;
        }
        Log.d(TAG, "Registered numbers: " + totalIDs);
        return;
    }

    public synchronized static void arcSoftFaceSingleDelete(String mDBPath) {
        FaceDB mFaceDB;
        mFaceDB = new FaceDB(mDBPath);
        mFaceDB.loadFaces();

        String id = "孙海燕";
        boolean done = mFaceDB.delete(id);

        if (done) {
            Log.d(TAG, id + " Face is deleted");
        }else{
            Log.d(TAG, id + " Face not found");
        }
        return;
    }

    public synchronized static void arcSoftFaceAdd(String mDBPath) {
        FaceDB mFaceDB;
        Log.d(TAG, "ArcSoft DB route: " + mDBPath);
        mFaceDB = new FaceDB(mDBPath);
//        mFaceDB.loadFaces();

        AFD_FSDKError err = mFaceDB.engineD.AFD_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.fd_key,
                AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 1);
        Log.d(TAG, "AFD_FSDK_InitialFaceEngine = " + err.getCode());
        List<AFD_FSDKFace> resultD = new ArrayList<AFD_FSDKFace>();

        AFR_FSDKFace resultFR = null;
        resultFR = new AFR_FSDKFace();
        AFR_FSDKError errorFR = null;

        StillTest dTest = new StillTest();
        int indexf = 0;
        int width;
        int height;

        dTest.frameCount = 0;
        dTest.totalTime = 0;
        long faceDetectTime_action = 0;
        long algorithmTime = 0;
        for (String s : dTest.newList) {
            try {
                dTest.fileName = dTest.picRoot + s;
                dTest.resultList.add(s);

                Log.d(TAG, "pics mode: " + dTest.fileName);
                Bitmap bitmap = BitmapFactory.decodeFile(dTest.fileName);
                width = bitmap.getWidth();
                height = bitmap.getHeight();

                //Make the images of getNV21 have even rows and columns
                width = width >> 1 << 1;
                height = height >> 1 << 1;

                byte[] nv21Data = dTest.getNV21(width, height, bitmap);
                err  = mFaceDB.engineD.AFD_FSDK_StillImageFaceDetection(nv21Data, width, height,
                        AFD_FSDKEngine.CP_PAF_NV21, resultD);
                Log.d(TAG, "AFD_FSDK_StillImageFaceDetection = " + err.getCode() + " ~ " + resultD.size());

                if (!resultD.isEmpty()) {
                    indexf++;
                    faceDetectTime_action = System.currentTimeMillis();
                    errorFR = mFaceDB.mFREngine.AFR_FSDK_ExtractFRFeature(nv21Data, width, height,
                            AFR_FSDKEngine.CP_PAF_NV21, new Rect(resultD.get(0).getRect()),
                            resultD.get(0).getDegree(), resultFR);
                    algorithmTime = System.currentTimeMillis() - faceDetectTime_action;

                    dTest.frameCount++;
                    dTest.totalTime += algorithmTime;
                    Log.d(TAG, "Face = " + resultFR.getFeatureData()[0] + ","
                            + resultFR.getFeatureData()[1] + "," + resultFR.getFeatureData()[2]
                            + "," + errorFR.getCode());
                    mFaceDB.addFace(s.split("\\.")[0], resultFR);
                }else{
                    Log.d(TAG, "Register Failed: " + dTest.fileName);
                }

//                mLibrary.verifySearchFaceFromList()
                Log.d(TAG, "verify TimeCost: " + (dTest.totalTime / dTest.frameCount));
                Log.d(TAG, "nv21 done: " + width + ' ' + height);
                Thread.currentThread().sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mFaceDB.engineD.AFD_FSDK_UninitialFaceEngine();
        mFaceDB.destroy();
        Log.d(TAG, "AFR_FSDK_UninitialEngine : " + errorFR.getCode());
        Log.d(TAG, "pics mode end!! " + indexf);
    }
}