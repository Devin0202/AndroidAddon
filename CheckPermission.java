package com.devin.mylayout;

import android.Manifest;
import android.app.Activity;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CheckPermission {
    private final String TAG = this.getClass().toString() + "-DY";
    public boolean permissionStatus = false;

    private final int myRequestCode = 456;
    private final String[] needPermission = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private List<String> requestPermissions = new ArrayList<String>();

    public CheckPermission() {
        ;
    }

    public void doPermissions(Activity checkActivity) {
        requestPermissions.clear();
        for (String forCheck : needPermission) {
            int permissionCheck = ContextCompat.checkSelfPermission(checkActivity, forCheck);
            if (PackageManager.PERMISSION_GRANTED == permissionCheck) {
                ;
            }else{
                requestPermissions.add(forCheck);
            }
        }

        if (0 < requestPermissions.size()) {
            String[] toRequest = requestPermissions.toArray(new String[requestPermissions.size()]);
            boolean needReasons = false;
            for (String forCheck : requestPermissions) {
                needReasons = needReasons || ActivityCompat.shouldShowRequestPermissionRationale(checkActivity, forCheck);
            }

            if (needReasons) {
                // 向用户显示一个解释，要以异步非阻塞的方式
                // 该线程将等待用户响应！等用户看完解释后再继续尝试请求权限
                ActivityCompat.requestPermissions(checkActivity, toRequest, myRequestCode);
                Log.d(TAG, "NeedReasons");
            } else {
                // 不需要向用户解释了，我们可以直接请求该权限
                ActivityCompat.requestPermissions(checkActivity, toRequest, myRequestCode);
                Log.d(TAG, "NoNeedReasons");
            }
        }else{
            permissionStatus = true;
        }
    }

    public void RequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: " + requestCode + ", "
                + Arrays.toString(permissions) + ", " + Arrays.toString(grantResults));

        boolean tmpStatus = true;
        switch (requestCode) {
            case myRequestCode: {
                // 如果请求被取消了，那么结果数组就是空的
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (PackageManager.PERMISSION_GRANTED == result) {
                            // 权限被授予了
                            Log.d(TAG, "Permission Done");
                            tmpStatus = tmpStatus && true;
                        }else{
                            tmpStatus = tmpStatus && false;
                        }
                    }

                    permissionStatus = tmpStatus;
                }else{
                    permissionStatus = false;
                }
                return;
            }
            default: {
                permissionStatus = false;
                Log.d(TAG, "RequestCode Error");
            }
        }
    }
}