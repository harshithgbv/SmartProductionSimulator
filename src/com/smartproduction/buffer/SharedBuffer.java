
package com.smartproduction.buffer;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class SharedBuffer {

    private final Queue<Integer> buffer;
    private final Semaphore empty;
    private final Semaphore full;
    private final ReentrantLock mutex;
    private int producedCount = 0;
    private int consumedCount = 0;

    public SharedBuffer(int capacity) {

        buffer = new ArrayDeque<>();

        empty = new Semaphore(capacity);
        full = new Semaphore(0);

        mutex = new ReentrantLock();
    }

    // Producer inserts an item

    public void produce(int item) throws InterruptedException {

        while (!empty.tryAcquire(500, TimeUnit.MILLISECONDS)) {
            System.out.println(
                "[WAITING] Producer: Buffer is FULL. Waiting for consumer..."
            );
        }

        mutex.lock();

        try {
            buffer.add(item);
            producedCount++;

            System.out.println(
                "PRODUCER: Created Item " + item +
                " | Buffer: " + buffer
            );

        } finally {
            mutex.unlock();
        }

        full.release();
    }

    // Consumer removes an item

    public int consume() throws InterruptedException {

        while (!full.tryAcquire(500, TimeUnit.MILLISECONDS)) {
            System.out.println(
                "[WAITING] Consumer: Buffer is EMPTY. Waiting for producer..."
            );
        }

        int item;

        mutex.lock();

        try {
            item = buffer.remove();
            consumedCount++;

            System.out.println(
                "CONSUMER: Consumed Item " + item +
                " | Buffer: " + buffer
            );

        } finally {
            mutex.unlock();
        }

        empty.release();

        return item;
    }

    public int getRemainingItems() {

        mutex.lock();

        try {
            return buffer.size();

        } finally {
            mutex.unlock();
        }
    }

    public int getEmptySlots() {
        return empty.availablePermits();
    }

    public int getFullSlots() {
        return full.availablePermits();
    }
    public int getProducedCount() {
    mutex.lock();
    try {
        return producedCount;
    } finally {
        mutex.unlock();
    }
   }

    public int getConsumedCount() {
        mutex.lock();
        try {
            return consumedCount;
        } finally {
            mutex.unlock();
        }
    }
}
