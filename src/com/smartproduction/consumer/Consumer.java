package com.smartproduction.consumer;

import com.smartproduction.buffer.SharedBuffer;
import java.util.concurrent.TimeUnit;
import com.smartproduction.simulation.PauseController;

public class Consumer implements Runnable {

    private final SharedBuffer sharedBuffer;
    private final int totalItems;
    private final long delay;
    private final PauseController pauseController;

    public Consumer(
            SharedBuffer sharedBuffer,
            int totalItems,
            long delay,
            PauseController pauseController) {

        this.sharedBuffer = sharedBuffer;
        this.totalItems = totalItems;
        this.delay = delay;
        this.pauseController = pauseController;
    }

    @Override
    public void run() {

        try {

            for (int i = 1; i <= totalItems; i++) {
                pauseController.awaitIfPaused();
                sharedBuffer.consume();

                TimeUnit.MILLISECONDS.sleep(delay);
            }

            System.out.println("Consumer finished.");

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            System.out.println("Consumer interrupted.");
        }
    }
}
