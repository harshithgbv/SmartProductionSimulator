
package com.smartproduction.producer;

import com.smartproduction.buffer.SharedBuffer;
import com.smartproduction.simulation.PauseController;
import java.util.concurrent.TimeUnit;

public class Producer implements Runnable {

    private final SharedBuffer sharedBuffer;
    private final int totalItems;
    private final long delay;
    private final PauseController pauseController;

    public Producer(
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
                sharedBuffer.produce(i);

                TimeUnit.MILLISECONDS.sleep(delay);
            }

            System.out.println("Producer finished.");

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            System.out.println("Producer interrupted.");
        }
    }
}
