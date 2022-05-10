package com.layfones.hellosftp.sftp.core;

import android.util.Log;

import androidx.annotation.Nullable;


import com.jcraft.jsch.SftpProgressMonitor;
import com.layfones.hellosftp.sftp.Callback;
import com.layfones.hellosftp.sftp.SftpClient;

public final class Monitor implements SftpProgressMonitor {

    private long count = 0;     //当前接收的总字节数
    private long max = 0;       //最终文件大小
    private long percent = 0;   //进度
    @Nullable
    private final Callback responseCall; //回调

    public Monitor(@Nullable Callback responseCall) {
        this.responseCall = responseCall;
    }

    @Override
    public void init(int op, String src, String dest, long max) {
        this.max = max;
        this.count = 0;
        this.percent = 0;
//        Log.d(TAG, String.format("op = %s; src = %s; dest = %s; max = %s", "上传", src, dest, max));
        Log.d(SftpClient.TAG, String.format("op = %s; src = %s; dest = %s; max = %s", "上传", src, dest, max));
    }

    @Override
    public boolean count(long count) {
        this.count += count;
        if (percent >= this.count * 100 / max) {
            return true;
        }
        percent = this.count * 100 / max;
        if (responseCall != null) {
            responseCall.onProgress(percent);
        }
//        Log.d(TAG, "Completed " + this.count + "(" + percent + "%) out of " + max + ".");
        Log.d(SftpClient.TAG, "正在上传:" + "(" + percent + "%) --" + this.getClass().getSimpleName());
        return true;
    }

    @Override
    public void end() {
        if (responseCall != null) {
            responseCall.onSuccess();
        }
//        Log.d(TAG, "Transferring done.");
        Log.d(SftpClient.TAG, "上传结束 --" + this.getClass().getSimpleName());
    }
}
