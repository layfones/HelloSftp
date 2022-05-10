package com.layfones.hellosftp.sftp.core;

import android.util.Log;



import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.layfones.hellosftp.sftp.Interceptor;
import com.layfones.hellosftp.sftp.Request;
import com.layfones.hellosftp.sftp.SftpClient;
import com.layfones.hellosftp.sftp.SftpUser;

import java.util.Properties;

public final class ConnectInterceptor implements Interceptor {

    private final SftpUser sftpUser;

    public ConnectInterceptor(SftpUser sftpUser) {
        this.sftpUser = sftpUser;
    }

    @Override
    public void interceptor(Chain chain) throws JSchException, SftpException {
        Log.d(SftpClient.TAG, "连接拦截器开始工作 --" + this.getClass().getSimpleName());
        RealInterceptorChain realInterceptorChain = (RealInterceptorChain) chain;
        Request request = chain.request();
        ChannelSftp channelSftp = openChannel(sftpUser);
        realInterceptorChain.proceed(request, channelSftp);
    }

    /**
     * 开启通道
     * @param sftpUser
     * @return
     * @throws JSchException
     */
    protected ChannelSftp openChannel(SftpUser sftpUser) throws JSchException {
        String ftpHost = sftpUser.getHost();
        String ftpUsername = sftpUser.getUsername();
        String ftpPassword = sftpUser.getPassword();
        String ftpPort = sftpUser.getPort();
        if (Util.isEmptyText(ftpHost) || Util.isEmptyText(ftpUsername) || Util.isEmptyText(ftpPassword) || Util.isEmptyText(ftpPort)) {
            throw new IllegalStateException("Sftp user info is missing!");
        }
        JSch jsch = new JSch();
        Session session = jsch.getSession(ftpUsername, ftpHost, Integer.parseInt(ftpPort));
        session.setPassword(ftpPassword);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig("PreferredAuthentications", "password");
        session.setConfig(config); // 为Session对象设置properties
        session.setTimeout(5000); // 设置timeout时间
        session.connect(); // 通过Session建立链接
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp"); // 打开SFTP通道
        channelSftp.connect(1500); // 建立SFTP通道的连接
        return channelSftp;
    }

}
