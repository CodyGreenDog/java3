package ru.gb.vitaly.java3.lesson5;

import java.util.concurrent.*;

public class Car implements Runnable {
    private static int CARS_COUNT;
    static {
        CARS_COUNT = 0;
    }
    private CyclicBarrier cb;
    public Semaphore rideThroughTunnel;
    private ArrayBlockingQueue<String> abq;
    private Race race;
    private int speed;
    private String name;
    public String getName() {
        return name;
    }
    public int getSpeed() {
        return speed;
    }
    public Car(Race race, int speed, CyclicBarrier cb, Semaphore rideThroughTunnel, ArrayBlockingQueue<String> abq) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
        this.cb = cb;
        this.rideThroughTunnel = rideThroughTunnel;
        this.abq = abq;
    }
    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int)(Math.random() * 800));
            System.out.println(this.name + " готов");
            cb.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }

        abq.offer(getName() + " - WINS!");
        try {
            cb.await();
        }catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }


    }
}
