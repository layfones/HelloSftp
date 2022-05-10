package com.layfones.hellosftp.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public interface Interceptor {

    void interceptor(Chain chain) throws JSchException, SftpException;

    interface Chain {

        Request request();

        ChannelSftp channelSftp();

        void proceed(Request request) throws JSchException, SftpException;
    }

}
