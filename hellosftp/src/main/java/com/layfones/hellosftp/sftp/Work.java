package com.layfones.hellosftp.sftp;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public interface Work {

    Request request();

    void execute() throws JSchException, SftpException;

    void enqueue(Callback callback);

//    void cancel();

    boolean isExecuted();

//    boolean isCanceled();

}
