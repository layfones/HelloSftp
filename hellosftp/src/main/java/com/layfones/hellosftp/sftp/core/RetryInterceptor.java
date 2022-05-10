package com.layfones.hellosftp.sftp.core;

import android.util.Log;


import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.layfones.hellosftp.sftp.Interceptor;
import com.layfones.hellosftp.sftp.Request;
import com.layfones.hellosftp.sftp.SftpClient;

public final class RetryInterceptor implements Interceptor {

    private final SftpClient sftpClient;

    public RetryInterceptor(SftpClient sftpClient) {
        this.sftpClient = sftpClient;
    }

    @Override
    public void interceptor(Chain chain) throws JSchException, SftpException {
        Log.d(SftpClient.TAG, "重试拦截器开始工作 --" + this.getClass().getSimpleName());
        Request request = chain.request();
        int openChannelRetryCount = 0;
        int sftpRetryCount = 0;
        while (true) {
            try {
                chain.proceed(request);
                break;
            } catch (JSchException e) {
                e.printStackTrace();
                if (++openChannelRetryCount > sftpClient.getMaxOpenChannelRetryCount()) {
                    Log.d(SftpClient.TAG, "重试拦截器，登录异常，超过重试次数 -" + openChannelRetryCount + " --" + this.getClass().getSimpleName());
                    throw new JSchException("Too many openChannelRetryCount requests: " + openChannelRetryCount);
                }
                Log.d(SftpClient.TAG, "重试拦截器，登录异常，执行重试 -" + openChannelRetryCount + " --" + this.getClass().getSimpleName());
            } catch (SftpException e) {
                e.printStackTrace();
                if (++sftpRetryCount > sftpClient.getMaxSftpRetryCount()) {
                    Log.d(SftpClient.TAG, "重试拦截器，传输异常，超过重试次数 -" + sftpRetryCount + " --" + this.getClass().getSimpleName());
                    throw new SftpException(-99, "Too many sftpRetryCount requests: " + sftpRetryCount);
                }
                Log.d(SftpClient.TAG, "重试拦截器，传输异常，执行重试 -" + sftpRetryCount + " --" + this.getClass().getSimpleName());
            }
        }
    }

}
