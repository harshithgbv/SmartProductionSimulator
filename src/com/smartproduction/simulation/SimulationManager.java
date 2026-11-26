package com.smartproduction.simulation;
import com.smartproduction.buffer.SharedBuffer;
import com.smartproduction.consumer.Consumer;
import com.smartproduction.producer.Producer;
import java.util.concurrent.BlockingQueue;
public class SimulationManager {

    private final int capacity;

    private final SharedBuffer sharedBuffer;

    private final int totalItems;

    private final PauseController pauseController;

    private final BlockingQueue<String> commands;

    public SimulationManager(
            int capacity,
            int totalItems,
            BlockingQueue<String> commands) {

        this.capacity = capacity;
        this.sharedBuffer = new SharedBuffer(capacity);
        this.totalItems = totalItems;
        this.pauseController = new PauseController();
        this.commands = commands;
    }

    public void startSimulation() {

        Producer producerTask =
                new Producer(
                        sharedBuffer,
                        totalItems,
                        500,
                        pauseController
                );

        Consumer consumerTask =
                new Consumer(sharedBuffer, totalItems, 800, pauseController);

        Thread producerThread =
                new Thread(producerTask, "Producer");

        Thread consumerThread =
                new Thread(consumerTask, "Consumer");

        System.out.println("Starting simulation...\n");

        producerThread.start();
        consumerThread.start();
        
        Thread controlThread = new Thread(() -> {

        System.out.println("\nCONTROLS:");
        System.out.println("P - Pause");
        System.out.println("R - Resume");
        System.out.println("S - Stop");

        while (true) {

                try {

                String command = commands.take();

                switch (command) {

                        case "P" -> pauseController.pause();

                        case "R" -> pauseController.resume();

                        case "S" -> {

                        pauseController.stop();

                        producerThread.interrupt();
                        consumerThread.interrupt();

                        return;
                        }

                        default -> {
                        // X, Q, or any non-simulation command:
                        // preserve exactly once for the main loop,
                        // then exit cleanly -- never loop back to take().
                        commands.offer(command);
                        return;
                        }

                }

                } catch (InterruptedException e) {

                // Main thread interrupted us after workers finished.
                Thread.currentThread().interrupt();
                return;
                }
        }

        }, "ControlThread");


        controlThread.start();

        try {

            producerThread.join();
            consumerThread.join();
            controlThread.interrupt();
            controlThread.join();

        } catch (InterruptedException e) {

            producerThread.interrupt();
            consumerThread.interrupt();

            Thread.currentThread().interrupt();

            System.out.println("Simulation interrupted.");
            return;
        }

        System.out.println("\n================================");
        System.out.println("       SIMULATION REPORT");
        System.out.println("================================");

        System.out.println("Total Items Produced: " +
                sharedBuffer.getProducedCount());

        System.out.println("Total Items Consumed: " +
                sharedBuffer.getConsumedCount());

        System.out.println("Remaining Items: " +
                sharedBuffer.getRemainingItems());

        System.out.println("Buffer Capacity: " + capacity);

        System.out.println("Empty Slots: " +
                sharedBuffer.getEmptySlots());

        System.out.println("Full Slots: " +
                sharedBuffer.getFullSlots());

        System.out.println("================================");

        if (pauseController.isStopped()) {

        System.out.println("       SIMULATION STOPPED");

        } else {

        System.out.println("      SIMULATION COMPLETED");

        }

        System.out.println("================================");
        }
}
