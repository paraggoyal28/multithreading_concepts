import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool {
    private final int nThreads;
    private final PoolWorker[] threads;
    private final LinkedBlockingQueue queue;

    public ThreadPool(int nThreads) {
        this.nThreads = nThreads;
        queue = new LinkedBlockingQueue<>();
        threads = new PoolWorker[nThreads];
        for (int i = 0; i < nThreads; ++i) {
            threads[i] = new PoolWorker();
            threads[i].start();
        }
    }

    public void execute(Runnable task) {
        synchronized (queue) {
            queue.add(task);
            queue.notify();
        }
    }

    private class PoolWorker extends Thread {
        @Override
        public void run() {
            Runnable task;

            while (true) {
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (Exception e) {
                            System.out.println("Exception occured while queue is waiting");
                        }
                    }
                    task = (Runnable) queue.poll();
                }
                task.run();
            }

        }
    }

    public static void main(String args[]) {
        ThreadPool pool = new ThreadPool(5);
        for (int i = 0; i < 100; ++i) {
            Task task = new Task(i);
            pool.execute(task);
        }
    }
}

class Task implements Runnable {
    private int num;

    public Task(int n) {
        num = n;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Task " + num + " executing under thread " + Thread.currentThread().getName());
    }
}
