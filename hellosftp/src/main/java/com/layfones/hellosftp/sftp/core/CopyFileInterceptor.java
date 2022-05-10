package com.layfones.hellosftp.sftp.core;


import android.util.Log;

import androidx.annotation.Nullable;


import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.layfones.hellosftp.sftp.Callback;
import com.layfones.hellosftp.sftp.Interceptor;
import com.layfones.hellosftp.sftp.Request;
import com.layfones.hellosftp.sftp.SftpClient;

public final class CopyFileInterceptor implements Interceptor {

    @Nullable
    private final Callback callback;

    private final boolean isUpload;

    public CopyFileInterceptor(@Nullable Callback callback, boolean isUpload) {
        this.callback = callback;
        this.isUpload = isUpload;
    }

    @Override
    public void interceptor(Chain chain) throws SftpException {
        Log.d(SftpClient.TAG, "传输拦截器开始工作 --" + this.getClass().getSimpleName());
        Request request = chain.request();
        ChannelSftp channelSftp = chain.channelSftp();
        if (channelSftp != null) {
            String dstPath = request.getDstPath();
            if (!dirIsExist(dstPath, channelSftp)) {
                createDir(dstPath, channelSftp);
            }
            if (isUpload) {
                channelSftp.put(request.getSrcPath(), dstPath, new Monitor(callback), request.getMode());
            } else {
                channelSftp.get(request.getSrcPath(), dstPath, new Monitor(callback), request.getMode());
            }
            Log.d(SftpClient.TAG, "传输拦截器，完成传输 --" + this.getClass().getSimpleName());
            channelSftp.disconnect();
        }
    }

    /**
     * 目标文件夹是否存在
     *
     * @return true 文件夹存在
     */
    public boolean dirIsExist(String remotePath, ChannelSftp channelSftp) {
        try {
            channelSftp.ls(remotePath);
        } catch (SftpException e) {
            Log.d(SftpClient.TAG, "传输拦截器，目录不存在，创建目录 --" + this.getClass().getSimpleName());
            return false;
        }
        return true;
    }

    /**
     * 创建文件夹
     *
     * @throws SftpException e
     */
    public void createDir(String dstPath, ChannelSftp channelSftp) throws SftpException {
        String[] folders = dstPath.split("/");
        for (String folder : folders) {
            if (folder.length() > 0) {
                try {
                    channelSftp.cd(folder);
                } catch (SftpException e) {
                    Log.d(SftpClient.TAG, "传输拦截器，CD失败，创建目录 --" + this.getClass().getSimpleName());
                    channelSftp.mkdir(folder);
                    channelSftp.cd(folder);
                }
            }
        }
    }
}
