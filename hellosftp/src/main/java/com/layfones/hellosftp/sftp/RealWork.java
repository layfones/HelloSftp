package com.layfones.hellosftp.sftp;

import android.util.Log;

import androidx.annotation.Nullable;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.layfones.hellosftp.sftp.core.CopyFileInterceptor;
import com.layfones.hellosftp.sftp.core.NamedRunnable;
import com.layfones.hellosftp.sftp.core.ConnectInterceptor;
import com.layfones.hellosftp.sftp.core.RealInterceptorChain;
import com.layfones.hellosftp.sftp.core.RetryInterceptor;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

public class RealWork implements Work {

    final SftpClient sftpClient;
    final Request request;
    // Guarded by this.
    private boolean executed;
    private final boolean isUpload;

    public RealWork(SftpClient sftpClient, boolean isUpload, Request request) {
        this.sftpClient = sftpClient;
        this.isUpload = isUpload;
        this.request = request;
    }

    @Override
    public Request request() {
        return request;
    }


    /**
     * thread
     *
     * @throws SftpException e
     */
    @Override
    public void execute() throws JSchException, SftpException {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already Executed");
            executed = true;
        }
        sftpClient.dispatcher().executed(this);
        try {
            Log.d(SftpClient.TAG, "同步传输 --" + this.getClass().getSimpleName());
            fileCopy(null);
        } catch (JSchException|SftpException e) {
            e.printStackTrace();
            throw e;
        } finally {
            sftpClient.dispatcher().finished(this);
        }
    }

    @Override
    public void enqueue(Callback callback) {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already Executed");
            executed = true;
        }
        sftpClient.dispatcher().enqueue(new AsyncWork(callback));
    }


    @Override
    public boolean isExecuted() {
        return executed;
    }


    final class AsyncWork extends NamedRunnable {

        private final Callback responseCallback;

        AsyncWork(Callback responseCallback) {
            super("Sftp Thread");
            this.responseCallback = responseCallback;
        }

        void executeOn(ExecutorService executorService) {
            boolean success = false;
            try {
                executorService.execute(this);
                success = true;
            } catch (RejectedExecutionException exception) {
                InterruptedIOException ioException = new InterruptedIOException("executor rejected");
                ioException.initCause(exception);
                responseCallback.onFailure(RealWork.this, ioException);
            } finally {
                if (!success) {
                    sftpClient.dispatcher().finished(this);
                }
            }
        }

        /**
         * 要执行的任务
         */
        @Override
        protected void execute() {
//            boolean signalledCallback = false;
            try {
                Log.d(SftpClient.TAG, "异步传输 --" + this.getClass().getSimpleName());
                fileCopy(responseCallback);
            } catch (JSchException | SftpException exception) {
                responseCallback.onFailure(RealWork.this, exception);
            } catch (Exception exception) {
                exception.printStackTrace();
                responseCallback.onFailure(RealWork.this, exception);
            } finally {
                sftpClient.dispatcher().finished(this);
            }
        }
    }

    private void fileCopy(@Nullable Callback responseCallback) throws JSchException, SftpException {
        Log.d(SftpClient.TAG, "文件传输责任链开始工作 --" + this.getClass().getSimpleName());
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.add(new RetryInterceptor(sftpClient));
        interceptors.add(new ConnectInterceptor(sftpClient.getSftpUser()));
        interceptors.add(new CopyFileInterceptor(responseCallback, isUpload));
        Interceptor.Chain chain = new RealInterceptorChain(interceptors, 0, request, this, null);
        chain.proceed(request);
    }


}
