package com.tagmycode.plugin.gui.operation;

import com.tagmycode.plugin.AbstractTaskFactory;
import com.tagmycode.plugin.gui.AbstractDialog;
import com.tagmycode.sdk.exception.TagMyCodeException;

import javax.swing.*;

public abstract class TagMyCodeAsynchronousOperation<T> {
    protected AbstractDialog abstractDialog;

    public TagMyCodeAsynchronousOperation(AbstractDialog abstractDialog) {
        this.abstractDialog = abstractDialog;
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
                    // Task stopped by user
                    e.printStackTrace();
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
            abstractDialog.onError((TagMyCodeException) e);
        } else {
            abstractDialog.onError(new TagMyCodeException());
        }
    }
}
