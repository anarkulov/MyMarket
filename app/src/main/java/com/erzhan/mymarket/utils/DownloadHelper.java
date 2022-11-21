package com.erzhan.mymarket.utils;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.erzhan.mymarket.BuildConfig;
import com.erzhan.mymarket.services.MainService;

import java.io.File;

public class DownloadHelper {
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

    public static boolean isFileDownloaded(String fileName, Context context) {
        String path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fileName.substring(fileName.lastIndexOf("/") + 1);
        File file = new File(path);
        Log.d("CommonExt", "FilePath: " + path + "\nisExist: " + file.exists() + "\nFile: " + file);
        return file.exists();
    }

    public static boolean isVersionHigher(String packageName, PackageManager packageManager, String appVersion) {
        if (appVersion == null) {
            return false;
        } else if (!appVersion.matches("[0-9]+(\\.[0-9]+)*")) {
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

    public static void uninstallApp(String type, Context requireContext) {
        Uri packageURI = Uri.parse("package:"+type);
        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageURI);
        try {
            requireContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void installApp(String link, Context ctxt) {
        String fileDestinationPath = ctxt
                .getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + link.substring(link.lastIndexOf("/") + 1);
        Uri uri = Uri.parse("file://" + fileDestinationPath);

        Intent install;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(
                    ctxt,
                    BuildConfig.APPLICATION_ID + ".provider",
                    new File(fileDestinationPath));

            install = new Intent(Intent.ACTION_VIEW);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            install.setData(contentUri);
            try {
                ctxt.startActivity(install);
            } catch (Exception e) {
                Log.d("TAG", "Error to Install: " + e.getMessage());
            }
        } else {
            install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            install.setDataAndType(uri, "application/vnd.android.package-archive");
            try {
                ctxt.startActivity(install);
            } catch (Exception e) {
                Log.d("TAG", "Error to Install: " + e.getMessage());
            }
        }
    }

    public static void downloadFile(String link, String type, Context requireContext) {
        String fileDestinationPath = requireContext
                .getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + link.substring(link.lastIndexOf("/") + 1);

        Intent intent = new Intent(requireContext, MainService.class);
        intent.setAction(MainService.ACTION_ADD_TO_DOWNLOAD);
        intent.putExtra("downloadUrl", link);
        intent.putExtra("type", type);
        intent.putExtra("destinationPath", fileDestinationPath);
        try {
            Uri uri = Uri.parse("file://" + fileDestinationPath);
            requireContext.startService(intent);
            showInstallOption(fileDestinationPath, uri, requireContext);
        } catch (Exception e) {
            Log.d("TAG", "Error to Download: " + e.getMessage());
        }
    }

    public static void stopDownloadService(Context requireContext) {
        Intent intent = new Intent(requireContext, MainService.class);
        intent.setAction(MainService.ACTION_STOP);
        try {
            requireContext.startService(intent);
        } catch (Exception e) {
            Log.d("DownloadHelper", "Error to Stop Download Service: " + e.getMessage());
        }
    }

//    public static void downloadFile(String link, Context requireContext, Handler mainHandler) {
//        String fileDestinationPath = requireContext
//                .getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + link.substring(link.lastIndexOf("/") + 1);
//
//        Uri uri = Uri.parse("file://" + fileDestinationPath);
//
//        DownloadManager downloadManager = (DownloadManager) requireContext.getSystemService(Context.DOWNLOAD_SERVICE);
//        Uri downloadUri = Uri.parse(link);
//
//        Log.d("DownloadHelper", "FileDestinationPath: " + fileDestinationPath + "\nUri: " + uri + "\nFile: " + downloadUri);
//
//        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
//        request.setMimeType("application/vnd.android.package-archive");
//        request.setTitle("MyMarket. " + link.substring(link.lastIndexOf("/") + 1));
//        request.setDescription("Скачивается...");
//        request.setDestinationUri(uri);
//        long downloadId = downloadManager.enqueue(request);
//        showInstallOption(fileDestinationPath, uri, requireContext);
//        Toast.makeText(requireContext, "Загрузка началась!", Toast.LENGTH_SHORT).show();
//
//        final ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(() -> {
//            int progress = 0;
//            boolean isDownloadFinished = false;
//            while (!isDownloadFinished) {
//                DownloadManager.Query query = new DownloadManager.Query();
//                query.setFilterById(downloadId);
//                Cursor cursor = downloadManager.query(query);
//                if (cursor.moveToFirst()) {
//                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS) >= 0 ? cursor.getColumnIndex(DownloadManager.COLUMN_STATUS) : 0;
//                    int downloadStatus = cursor.getInt(columnIndex);
//                    switch (downloadStatus) {
//                        case DownloadManager.STATUS_FAILED:
//                        case DownloadManager.STATUS_SUCCESSFUL:
//                            isDownloadFinished = true;
//                            Message message = mainHandler.obtainMessage();
//                            message.what = 2;
//                            mainHandler.sendMessage(message);
//                            break;
//                        case DownloadManager.STATUS_PAUSED:
//                        case DownloadManager.STATUS_PENDING:
//                            break;
//                        case DownloadManager.STATUS_RUNNING:
//                            int columnIndexOfBytes = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR) >= 0 ? cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR) : 0;
//                            int bytesDownloaded = cursor.getInt(columnIndexOfBytes);
//                            int columnIndexOfBytesTotal = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES) >= 0 ? cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES) : 0;
//                            int bytesTotal = cursor.getInt(columnIndexOfBytesTotal);
//                            progress = (int) ((bytesDownloaded * 100l) / bytesTotal);
//                            break;
//                    }
//
//                    Message message = mainHandler.obtainMessage();
//                    message.what = 1;
//                    message.arg1 = progress;
//                    mainHandler.sendMessage(message);
//
//                }
//                cursor.close();
//            }
//        });
//    }

    private static void showInstallOption(String fileDestinationPath, Uri uri, Context context) {
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                Intent install;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri contentUri = FileProvider.getUriForFile(
                            ctxt,
                            BuildConfig.APPLICATION_ID + ".provider",
                            new File(fileDestinationPath));

                    install = new Intent(Intent.ACTION_VIEW);
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    install.setData(contentUri);
                    try {
                        ctxt.startActivity(install);
                        File file = new File(fileDestinationPath);
                        if (file.exists()) {
                            file.delete();
                        }
                    } catch (Exception e) {
                        Log.d("TAG", "Error to Install: " + e.getMessage());
                    } finally {
                        ctxt.unregisterReceiver(this);
                    }
                } else {
                    install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.setDataAndType(uri, "application/vnd.android.package-archive");
                    try {
                        ctxt.startActivity(install);
                    } catch (Exception e) {
                        Log.d("TAG", "Error to Install: " + e.getMessage());
                    } finally {
                        ctxt.unregisterReceiver(this);
                    }
                }
            }
        };

        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public static void openApp(String type, Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(type);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Приложение не установлено!", Toast.LENGTH_SHORT).show();
        }
    }
}
