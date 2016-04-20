package com.tagmycode.plugin.operation;

import com.tagmycode.plugin.AbstractTaskFactory;
import com.tagmycode.plugin.gui.IOnErrorCallback;
import com.tagmycode.sdk.exception.TagMyCodeException;

public abstract class TagMyCodeAsynchronousOperation<T> {
    protected IOnErrorCallback onErrorCallback;
    private Thread thread;

    public TagMyCodeAsynchronousOperation(IOnErrorCallback onErrorCallback) {
        this.onErrorCallback = onErrorCallback;
    }

    public final void run() {
        Runnable runnable = createRunnable();
        thread = new Thread(runnable);
        thread.start();
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
        }
    }

    public void runWithTask(AbstractTaskFactory task, String title) {
        task.create(createRunnable(), title);
    }

    private Runnable createRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    beforePerformOperation();
                    final T result = performOperation();
                    onComplete();
                    onSuccess(result);
                } catch (final InterruptedException e) {
                    onInterrupted();
                    e.printStackTrace();
                } catch (final Throwable e) {
                    onComplete();
                    onFailure(e);
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
        } else {
            onErrorCallback.onError(new TagMyCodeException());
        }
    }
}
