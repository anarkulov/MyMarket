package com.erzhan.mymarket.services;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.erzhan.mymarket.MainActivity;
import com.erzhan.mymarket.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


public class MainService extends Service {

    public static final int UPDATE_PROGRESS = 6661;
    public static final String ACTION_ADD_TO_DOWNLOAD = "download";
    public static final String ACTION_STOP = "stop";
    Notification notification;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(
                    "channelId",
                    "channel_name",
                    NotificationManager.IMPORTANCE_LOW
            );
        }
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_ADD_TO_DOWNLOAD)) {
            String downloadUrl = intent.getStringExtra("downloadUrl");
            String destinationPath = intent.getStringExtra("destinationPath");
            String title = destinationPath.substring(destinationPath.lastIndexOf("/") + 1);
            String type = intent.getStringExtra("type");
            download(downloadUrl, destinationPath, type);
            start(title, type);
        } else {
            stopSelf();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void download(String downloadUrl, String destinationPath, String type) {
        Uri uri = Uri.parse("file://" + destinationPath);

        DownloadManager downloadManager = (DownloadManager) getApplication().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(downloadUrl);

        Log.d("TAG", "FileDestinationPath: " + destinationPath + "\nUri: " + uri + "\nFile: " + downloadUri);

        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationUri(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        long downloadId = downloadManager.enqueue(request);
        Toast.makeText(getApplicationContext(), "Загрузка началась!", Toast.LENGTH_SHORT).show();

        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        AtomicInteger lastProgress = new AtomicInteger();
        executorService.execute(() -> {
            int progress = 0;
            boolean isDownloadFinished = false;
            while (!isDownloadFinished) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor cursor = downloadManager.query(query);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS) >= 0 ? cursor.getColumnIndex(DownloadManager.COLUMN_STATUS) : 0;
                    int downloadStatus = cursor.getInt(columnIndex);
                    switch (downloadStatus) {
                        case DownloadManager.STATUS_FAILED:
                        case DownloadManager.STATUS_SUCCESSFUL:
                            isDownloadFinished = true;
                            Intent intent = new Intent("download_progress");
                            intent.putExtra("progress", 100);
                            intent.putExtra("type", type);
                            sendBroadcast(intent);
                            notification.contentView.setProgressBar(R.id.progressBar, 100, 100, false);
                            notification.contentView.setTextViewText(R.id.tv_body, "Загрузка... 100%");
                            notificationManager.notify(1, notification);
                            notificationManager.cancelAll();
                            stopSelf();
                            break;
                        case DownloadManager.STATUS_PAUSED:
                        case DownloadManager.STATUS_PENDING:
                            break;
                        case DownloadManager.STATUS_RUNNING:
                            int columnIndexOfBytes = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR) >= 0 ? cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR) : 0;
                            int bytesDownloaded = cursor.getInt(columnIndexOfBytes);
                            int columnIndexOfBytesTotal = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES) >= 0 ? cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES) : 0;
                            int bytesTotal = cursor.getInt(columnIndexOfBytesTotal);
                            progress = (int) ((bytesDownloaded * 100l) / bytesTotal);
                            break;
                    }

                    if (progress > lastProgress.get()) {
                        Intent intent = new Intent("download_progress");
                        intent.putExtra("progress", progress);
                        intent.putExtra("type", type);
                        sendBroadcast(intent);
                        notification.contentView.setProgressBar(R.id.progressBar, 100, progress, false);
                        notification.contentView.setTextViewText(R.id.tv_body, "Загрузка... " + progress + "%");
                        notificationManager.notify(1, notification);
                        lastProgress.set(progress);
                    }

                }
                cursor.close();
            }
        });
    }

    private void start(String title, String type) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("type", type);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_custom);
        notificationLayout.setTextViewText(R.id.tv_title, title);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, "channelId")
                    .setContentTitle("MyMarket. " + title)
                    .setContentText("Загрузка...")
                    .setCustomContentView(notificationLayout)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .build();
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle("MyMarket. " + title)
                    .setContentText("Загрузка...")
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .build();
        }

        notificationManager.notify(1, notification);
    }
}