package com.layfones.hellosftp.sftp;

import androidx.annotation.IntDef;

import com.jcraft.jsch.ChannelSftp;

public final class SftpClient {

    public static final String TAG = "SFTP框架:: ";

    private SftpUser sftpUser;

    private int maxOpenChannelRetryCount;

    private int maxSftpRetryCount;

    public int getMaxOpenChannelRetryCount() {
        return maxOpenChannelRetryCount;
    }

    public int getMaxSftpRetryCount() {
        return maxSftpRetryCount;
    }

    /**
     * 覆盖模式(默认): 如果目标文件已存在, 传输的文件将完全覆盖已存在的文件, 产生新的文件
     * 恢复模式: 断点续传, 如果文件已传输一部分, 此时由于网络或其它原因导致传输中断, 若下一次传输相同文件, 则会从上一次中断的地方续传
     * 追加模式: 如果目标文件已存在, 传输的文件将在已存在的文件后追加
     */
    @IntDef({ChannelSftp.OVERWRITE, ChannelSftp.RESUME, ChannelSftp.APPEND})
    public @interface Mode {
    }

    private final Dispatcher dispatcher;

    private SftpClient() {
        dispatcher = new Dispatcher();
    }

    public Dispatcher dispatcher() {
        return dispatcher;
    }

    private static volatile SftpClient INSTANCE;

    protected SftpUser getSftpUser() {
        return sftpUser;
    }

    public static SftpClient with(SftpUser sftpUser, int maxOpenChannelRetryCount, int maxSftpRetryCount) {
        if (INSTANCE == null) {
            synchronized (SftpClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SftpClient();
                }
            }
        }
        INSTANCE.sftpUser = sftpUser;
        INSTANCE.maxOpenChannelRetryCount = maxOpenChannelRetryCount;
        INSTANCE.maxSftpRetryCount = maxSftpRetryCount;
        return INSTANCE;
    }

    public static SftpClient with(SftpUser sftpUser) {
        return with(sftpUser, 5, 3);
    }

    public Work download(Request request) {
        return new RealWork(this,false, request);
    }

    public Work upload(Request request) {
        return new RealWork(this,true, request);
    }

}
