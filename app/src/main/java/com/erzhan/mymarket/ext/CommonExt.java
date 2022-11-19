package com.erzhan.mymarket.ext;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class CommonExt {
    // check whether the package name is installed on the device
    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            Log.d("CommonExt", "PackageName: " + packageName + "\nisInstalled: true");
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("CommonExt", "PackageName: " + packageName + "\nisInstalled: false");
            return false;
        }
    }

    // check whether the file is downloaded on the device
    public static boolean isFileDownloaded(String fileName) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + fileName.substring(fileName.lastIndexOf("/"));
        File file = new File(path);
        Log.d("CommonExt", "FilePath: " + path + "\nisExist: " + file.exists());
        return file.exists();
    }

    // get app version of the app from the package manager
    public static boolean isVersionHigher(String packageName, PackageManager packageManager, String appVersion) {
        if (appVersion == null) {
            return false;
        } else if (!appVersion.matches("[0-9]+(\\.[0-9]+)*")){
            return false;
        }

        String thisVersion;

        try {
            thisVersion = packageManager.getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

        String[] thisVersionArray = thisVersion.split("\\.");
        String[] appVersionArray = appVersion.split("\\.");
        int length = Math.max(thisVersionArray.length, appVersionArray.length);

        for (int i = 0; i < length; i++) {
            int thisVersionPart = i < thisVersionArray.length ? Integer.parseInt(thisVersionArray[i]) : 0;
            int appVersionPart = i < appVersionArray.length ? Integer.parseInt(appVersionArray[i]) : 0;
            if (thisVersionPart < appVersionPart) {
                return true;
            }
        }

        return false;
    }
}
