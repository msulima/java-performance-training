package pl.msulima.logger;

import java.util.concurrent.*;

public class Benchmark {

    private static final int LOOPS = 2_000_000;
    private static final int N_THREADS = 20;

    public static void main(String... args) {
        long disabled = benchmark(new PrintlnLogger(true));
        long buffered = benchmark(new PrintlnLogger(false));

        System.out.printf("Disabled: %.1fmsg/s. Buffered: %.1fmsg/s.%n", getRate(disabled), getRate(buffered));
    }

    private static double getRate(double disabled) {
        return LOOPS / disabled * TimeUnit.SECONDS.toNanos(1);
    }

    private static long benchmark(PrintlnLogger logger) {
        System.out.println("Warmup");
        for (int i = 0; i < LOOPS / 10; i++) {
            logger.log("i" + i);
        }
        System.out.println("Testing");
        long start = System.nanoTime();
        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        int loopsPerThread = LOOPS / N_THREADS;
        CyclicBarrier startBarrier = new CyclicBarrier(N_THREADS);
        for (int threadId = 0; threadId < N_THREADS; threadId++) {
            final int localThreadId = threadId;
            executorService.submit(() -> {
                try {
                    startBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }

                for (int i = 0; i < loopsPerThread; i++) {
                    logger.log("Oct 12, 2008 9:45:18 AM myClassInfoHere - INFO: MyLogMessageHere " + i + " - " + localThreadId);
                }
            });
        }
        try {
            executorService.shutdown();
            executorService.awaitTermination(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        long duration = System.nanoTime() - start;
        System.out.println("Done in " + TimeUnit.NANOSECONDS.toMillis(duration) + "ms.");
        return duration;
    }
}
