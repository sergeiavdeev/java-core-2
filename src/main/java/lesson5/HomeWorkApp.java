package lesson5;

import java.util.Arrays;

public class HomeWorkApp {
    public static void main(String[] args) {

        final int SIZE = 10000000;
        float[] arr = new float[SIZE];
        float[] ntArr = new float[SIZE];

        Arrays.fill(arr, 1.0f);
        oneThread(arr);
        System.arraycopy(arr, 0, ntArr, 0, SIZE);
        if (!Arrays.equals(arr, ntArr)) {
            System.out.println("Error array diff");
        }

        Arrays.fill(arr, 1.0f);
        twoThread(arr);
        if (!Arrays.equals(arr, ntArr)) {
            System.out.println("Error array diff");
        }

        Arrays.fill(arr, 1.0f);
        nThread(arr, 8);
        if (!Arrays.equals(arr, ntArr)) {
            System.out.println("Error array diff");
        }
    }

    private static void calc(float[] arr, int start, int step, int shift) {

        for (int i = start; i < arr.length; i += step) {
            arr[i] = (float)(arr[i] * Math.sin(0.2f + (i + shift) / 5) * Math.cos(0.2f + (i + shift) / 5) * Math.cos(0.4f + (i + shift) / 2));
        }
        System.out.printf("Iteration in thread %s: %d\n", Thread.currentThread().getName(), arr.length / step);
    }

    private static void oneThread(float[] arr) {

        long startTime = System.currentTimeMillis();

        calc(arr, 0, 1, 0);

        System.out.printf("oneThread time: %d\n", System.currentTimeMillis() - startTime);
    }

    private static void twoThread(float[] arr) {

        float[] arr1 = new float[arr.length / 2];
        float[] arr2 = new float[arr.length / 2];

        long startTime = System.currentTimeMillis();

        System.arraycopy(arr, 0, arr1, 0, arr.length / 2);
        System.arraycopy(arr, arr.length / 2, arr2, 0, arr.length / 2);

        Thread t1 = new Thread(() -> calc(arr1, 0, 1, 0));
        Thread t2 = new Thread(() -> calc(arr2, 0, 1, arr.length / 2));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
            System.arraycopy(arr1, 0, arr, 0, arr.length / 2);
            System.arraycopy(arr2, 0, arr, arr.length / 2, arr.length / 2);
            System.out.printf("twoThread time: %d\n", System.currentTimeMillis() - startTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void nThread(float[] arr, int threadCount) {

        long startTime = System.currentTimeMillis();

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;
            threads[i] = new Thread(() -> calc(arr, threadNum, threadCount, 0));
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("%d Thread time: %d\n", threadCount, System.currentTimeMillis() - startTime);
    }
}
