
package com.smartproduction.simulation;

public class PauseController {

    private boolean paused = false;
    private boolean stopped = false;

    public synchronized void pause() {

        if (stopped) {
            return;
        }

        paused = true;

        System.out.println("\nSIMULATION PAUSED");
    }

    public synchronized void resume() {

        if (stopped) {
            return;
        }

        paused = false;

        notifyAll();

        System.out.println("\nSIMULATION RESUMED");
    }

    public synchronized void stop() {

        stopped = true;
        paused = false;

        notifyAll();

        System.out.println("\nSIMULATION STOPPED");
    }

    public synchronized void awaitIfPaused()
            throws InterruptedException {

        while (paused && !stopped) {
            wait();
        }

        if (stopped) {
            throw new InterruptedException(
                    "Simulation stopped"
            );
        }
    }

    public synchronized boolean isStopped() {
        return stopped;
    }
}
