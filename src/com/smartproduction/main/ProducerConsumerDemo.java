
package com.smartproduction.main;

import com.smartproduction.simulation.SimulationManager;

public class ProducerConsumerDemo {

    public static void main(String[] args) {

        int bufferCapacity = 5;
        int totalItems = 10;

        CommandInput commandInput = new CommandInput();

        System.out.println("==============================");
        System.out.println(" SMART PRODUCTION SIMULATOR");
        System.out.println("==============================");

        boolean running = true;

        while (running) {

            SimulationManager manager =
                    new SimulationManager(
                            bufferCapacity,
                            totalItems,
                            commandInput.getCommands()
                    );

            manager.startSimulation();

            boolean validAction = false;

            while (!validAction) {

                System.out.println("\nNEXT ACTION:");
                System.out.println("X - Reset / Start New Simulation");
                System.out.println("Q - Quit");

                try {

                    String command =
                            commandInput.getCommands().take();

                    switch (command) {

                        case "X" -> {

                            System.out.println(
                                    "\nRESETTING SIMULATION..."
                            );

                            System.out.println(
                                    "Creating a fresh simulation...\n"
                            );

                            validAction = true;
                        }

                        case "Q" -> {

                            running = false;

                            System.out.println(
                                    "\nExiting Smart Production Simulator."
                            );

                            validAction = true;
                        }

                        default -> System.out.println(
                                "Invalid command. Please use X or Q."
                        );
                    }

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    running = false;

                    System.out.println("Program interrupted.");
                }
            }
        }
    }
}
