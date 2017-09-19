package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.AbstractTaskFactory;
import com.tagmycode.plugin.gui.IOnErrorCallback;
import com.tagmycode.sdk.exception.TagMyCodeException;

public abstract class TagMyCodeAsynchronousOperation<T> {
    protected IOnErrorCallback onErrorCallback;
    private Thread thread;
    private boolean isRunning;

    public TagMyCodeAsynchronousOperation(IOnErrorCallback onErrorCallback) {
        this.onErrorCallback = onErrorCallback;
    }

    public final Thread start() {
        Runnable runnable = createRunnable();
        thread = new Thread(runnable);
        thread.start();
        return thread;
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
        }
    }

    public void runWithTask(AbstractTaskFactory abstractTaskFactory, String title) {
        abstractTaskFactory.create(createRunnable(), title);
    }

    public void allowStopTask() throws InterruptedException {
        Thread.sleep(0);
    }

    private Runnable createRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                isRunning = true;
                try {
                    beforePerformOperation();
                    final T result = performOperation();
                    onComplete();
                    onSuccess(result);
                    isRunning = false;
                } catch (final InterruptedException e) {
                    onComplete();
                    onInterrupted();
                    isRunning = false;
                    e.printStackTrace();
                } catch (final Throwable e) {
                    onComplete();
                    onFailure(e);
                    isRunning = false;
                    e.printStackTrace();
                }
            }
        };
    }

    protected void onInterrupted() {
        onComplete();
    }

    protected void beforePerformOperation() {

    }

    protected abstract T performOperation() throws Exception;

    protected void onComplete() {

    }

    protected void onSuccess(T result) {

    }

    protected void onFailure(Throwable e) {
        if (e instanceof TagMyCodeException) {
            onErrorCallback.onError((TagMyCodeException) e);
        } else if (e instanceof Exception) {
            onErrorCallback.onError(new TagMyCodeException((Exception) e));
        } else {
            onErrorCallback.onError(new TagMyCodeException(e.getMessage()));
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}
