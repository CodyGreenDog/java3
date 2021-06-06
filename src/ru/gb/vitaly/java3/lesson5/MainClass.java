package ru.gb.vitaly.java3.lesson5;


import java.util.concurrent.*;

public class MainClass {
    public static final int CARS_COUNT = 4;
    public static void main(String[] args) {
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        Car[] cars = new Car[CARS_COUNT];

        CyclicBarrier cb = new CyclicBarrier( CARS_COUNT + 1); //Заставляет стартовать машины одновременно
                                                                     //и соблюдать песледовательность стадий гонки

        Semaphore rideThroughTunnel = new Semaphore(CARS_COUNT/2); //Обеспечивает пропускную способность туннеля
                                                                          //не больше половины участвующих машин

        ArrayBlockingQueue<String> abq = new ArrayBlockingQueue<>( 1); //Добавляет в очередь сообщение
                                                                             // о пересечении финишной черты
        for (int i = 0; i < CARS_COUNT; i++) {
            cars[i] =  new Car(race, 20 + (int) (Math.random() * 10), cb, rideThroughTunnel, abq);
        }


        for (int i = 0; i < cars.length; i++) {
            new Thread(cars[i]).start();
        }



        while(cb.getNumberWaiting() < 4);

        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");

        try {
            cb.await();
        }catch (InterruptedException | BrokenBarrierException e){
            e.printStackTrace();
        }


        cb.reset();

        while(true) {
            String winMsg = null;
            try {
                winMsg = abq.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(winMsg != null) {
                System.out.println(winMsg);
                break;
            }
        }

        try{
            cb.await();
        }catch(InterruptedException | BrokenBarrierException e){
            e.printStackTrace();
        }

        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
    }
}
