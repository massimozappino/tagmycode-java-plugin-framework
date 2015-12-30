package com.tagmycode.plugin.gui.operation;

import com.tagmycode.plugin.AbstractTaskFactory;
import com.tagmycode.plugin.gui.IonErrorCallback;
import com.tagmycode.sdk.exception.TagMyCodeException;

import javax.swing.*;

public abstract class TagMyCodeAsynchronousOperation<T> {
    protected IonErrorCallback ionErrorCallback;

    public TagMyCodeAsynchronousOperation(IonErrorCallback ionErrorCallback) {
        this.ionErrorCallback = ionErrorCallback;
    }

    public final void run() {
        Runnable runnable = getRunnable();
        new Thread(runnable).start();
    }

    public void runWithTask(AbstractTaskFactory task, String title) {
        task.create(getRunnable(), title);
    }

    private Runnable getRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            beforePerformOperation();
                        }
                    });
                    final T result = performOperation();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            onComplete();
                            onSuccess(result);
                        }
                    });
                } catch (InterruptedException e) {
                    onInterrupted();
                } catch (final Throwable e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            onComplete();
                            onFailure(e);
                            e.printStackTrace();
                        }
                    });
                }
            }
        };
    }

    protected void onInterrupted() {

    }

    protected void beforePerformOperation() {

    }

    protected abstract T performOperation()
            throws Exception;

    protected void onComplete() {
    }

    protected void onSuccess(T result) {

    }

    protected void onFailure(Throwable e) {
        if (e instanceof TagMyCodeException) {
            ionErrorCallback.onError((TagMyCodeException) e);
        } else {
            ionErrorCallback.onError(new TagMyCodeException());
        }
    }
}
