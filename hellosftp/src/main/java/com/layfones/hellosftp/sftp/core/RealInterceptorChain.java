package com.layfones.hellosftp.sftp.core;


import androidx.annotation.Nullable;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.layfones.hellosftp.sftp.Interceptor;
import com.layfones.hellosftp.sftp.Request;
import com.layfones.hellosftp.sftp.Work;

import java.util.List;



public final class RealInterceptorChain implements Interceptor.Chain {

    private final List<Interceptor> interceptors;
    private final int index;
    private final Request request;
    private final Work work;
    @Nullable
    private final ChannelSftp channelSftp;

    public RealInterceptorChain(List<Interceptor> interceptors, int index, Request request, Work work, @Nullable ChannelSftp channelSftp) {
        this.interceptors = interceptors;
        this.index = index;
        this.request = request;
        this.work = work;
        this.channelSftp = channelSftp;
    }

    @Override
    public Request request() {
        return request;
    }

    @Override
    public ChannelSftp channelSftp() {
        return channelSftp;
    }

    @Override
    public void proceed(Request request) throws JSchException, SftpException {
        this.proceed(request, channelSftp);
    }

    public void proceed(Request request, ChannelSftp channelSftp) throws JSchException, SftpException {
        if (index >= interceptors.size()) throw new AssertionError();
        RealInterceptorChain next = new RealInterceptorChain(interceptors, index + 1, request, work, channelSftp);
        Interceptor interceptor = interceptors.get(index);
        interceptor.interceptor(next);
    }
}
