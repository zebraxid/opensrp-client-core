package org.smartregister.multitenant.executor;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * <p>
 * Global executor pools for plug-and-play CoreLibrary operations.
 * <p>
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 *
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 09-04-2020.
 */
public class CoreLibraryExecutors {

    private static final int THREAD_COUNT = 3;

    private final Executor diskIO;

    private final Executor networkIO;

    private final Executor mainThread;

    public CoreLibraryExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    public CoreLibraryExecutors() {
        this(new DiskIOThreadExecutor(), Executors.newFixedThreadPool(THREAD_COUNT),
                new MainThreadExecutor());
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor networkIO() {
        return networkIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

    /**
     * Executor that runs a task on a new background thread.
     */
    private static class DiskIOThreadExecutor implements Executor {

        private final Executor mDiskIO;

        public DiskIOThreadExecutor() {
            mDiskIO = Executors.newSingleThreadExecutor();
        }

        @Override
        public void execute(@NonNull Runnable command) {
            mDiskIO.execute(command);
        }
    }
}