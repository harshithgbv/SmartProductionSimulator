
package com.smartproduction.main;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CommandInput {

    private final BlockingQueue<String> commands =
            new LinkedBlockingQueue<>();

    public CommandInput() {

        Thread inputThread = new Thread(() -> {

            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNextLine()) {

                try {

                    String command = scanner.nextLine()
                            .trim()
                            .toUpperCase();

                    commands.put(command);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    break;

                }
            }

        }, "KeyboardInputThread");

        inputThread.setDaemon(true);
        inputThread.start();
    }

    public BlockingQueue<String> getCommands() {
        return commands;
    }
}
