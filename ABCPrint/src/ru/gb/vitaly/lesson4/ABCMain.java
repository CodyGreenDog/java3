package ru.gb.vitaly.lesson4;

public class ABCMain {


    public static void main(String[] args){

        new Thread( new ABCPrint(0) ).start();
        new Thread( new ABCPrint(1) ).start();
        new Thread( new ABCPrint(2) ).start();

    }
}

class ABCPrint implements Runnable {
    private static final int ITERATION_VAL = 5;
    private static final Object monitor = new Object();
    private static int curr_idx = 0;
    private final int idx;
    private int counter;
    private char[] abc = {'A', 'B', 'C'};

    ABCPrint(int idx) {
        this.idx = idx;
        counter = 0;
    }

    private static void idxUpdate() {
        curr_idx = ++curr_idx <= 2 ? curr_idx : 0;
    }

    @Override
    public void run() {

        synchronized (monitor) {
            while (counter < ITERATION_VAL) {

                while (curr_idx != idx) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                System.out.print(abc[idx]);
                counter++;
                idxUpdate();
                monitor.notifyAll();
            }

        }
    }
}

