package dev.alvaroherrero;

import java.time.LocalDateTime;

/**
 * Main class for the application.
 *
 * @author Alvaro Herrero
 */
public class Main {
    /**
     * Main method for the application.
     *
     * @param args arguments for the application
     * @throws InterruptedException if the thread is interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello world!");
        while (true){
            Thread.sleep(1000);
            System.out.println(LocalDateTime.now());
        }
    }

    /**
     * Returns the provided message.
     *
     * @param message the message to be returned
     * @return the input message
     */
    public static String printMessage(String message) {
        return message;
    }
}