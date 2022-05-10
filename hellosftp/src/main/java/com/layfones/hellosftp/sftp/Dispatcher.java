package com.layfones.hellosftp.sftp;

import androidx.annotation.Nullable;



import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class Dispatcher {

    private ExecutorService executorService;
    private final Deque<RealWork.AsyncWork> mReadyAsyncWorks = new ArrayDeque<>();
    private final Deque<RealWork.AsyncWork> mRunningAsyncWorks = new ArrayDeque<>();
    private final Deque<RealWork> runningSyncCalls = new ArrayDeque<>();

    private int maxRequests = 64;
    @Nullable
    private Runnable idleCallback;

    public synchronized void setIdleCallback(@Nullable Runnable idleCallback) {
        this.idleCallback = idleCallback;
    }

    /**
     * Set the maximum number of requests to execute concurrently. Above this requests queue in
     * memory, waiting for the running calls to complete.
     *
     * <p>If more than {@code maxRequests} requests are in flight when this is invoked, those requests
     * will remain in flight.
     */
    public void setMaxRequests(int maxRequests) {
        if (maxRequests < 1) {
            throw new IllegalArgumentException("max < 1: " + maxRequests);
        }
        synchronized (this) {
            this.maxRequests = maxRequests;
        }
        promoteAndExecute();
    }

    public synchronized int getMaxRequests() {
        return maxRequests;
    }

    public synchronized ExecutorService executorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(
                    0,
                    Integer.MAX_VALUE,
                    60,
                    TimeUnit.SECONDS,
                    new SynchronousQueue<>(), r -> {
                Thread result = new Thread(r, "Sftp Dispatcher");
                result.setDaemon(false);
                return result;
            });
        }
        return executorService;
    }

    void enqueue(RealWork.AsyncWork callback) {
        synchronized (this) {
            mReadyAsyncWorks.add(callback);
        }
        promoteAndExecute();
    }

    synchronized void executed(RealWork call) {
        runningSyncCalls.add(call);
    }

    private boolean promoteAndExecute() {
        List<RealWork.AsyncWork> executableCalls = new ArrayList<>();
        boolean isRunning;
        synchronized (this) {
            Iterator<RealWork.AsyncWork> iterator = mReadyAsyncWorks.iterator();
            while (iterator.hasNext()) {
                RealWork.AsyncWork asyncWork = iterator.next();
                if (mRunningAsyncWorks.size() >= maxRequests) break;
                iterator.remove();
                executableCalls.add(asyncWork);
                mRunningAsyncWorks.add(asyncWork);
            }
            isRunning = runningCallsCount() > 0;
        }
        for (int i = 0, size = executableCalls.size(); i < size; i++) {
            RealWork.AsyncWork asyncWork = executableCalls.get(i);
            asyncWork.executeOn(executorService());
        }
        return isRunning;
    }

    public synchronized int runningCallsCount() {
        return mRunningAsyncWorks.size() + runningSyncCalls.size();
    }

    public void finished(RealWork.AsyncWork asyncWork) {
        finished(mRunningAsyncWorks, asyncWork);
    }

    public void finished(RealWork realCall) {
        finished(runningSyncCalls, realCall);
    }

    private <T> void finished(Deque<T> calls, T call) {
        Runnable idleCallback;
        synchronized (this) {
            if (!calls.remove(call)) throw new AssertionError("Call wasn't in-flight!");
            idleCallback = this.idleCallback;
        }

        boolean isRunning = promoteAndExecute();

        if (!isRunning && idleCallback != null) {
            idleCallback.run();
        }
    }


}
