package ru.geekbrains.vitalygolikov.java3.lesson1;

import java.util.ArrayList;

public class Box<T extends Fruit> {

    private ArrayList<T> fruits;

    Box()
    {
        fruits = new ArrayList<>();
    }

    Box(ArrayList<T> fruits)
    {
        this.fruits = fruits;
    }

    public boolean add(ArrayList<T> fruits)
    {
        return this.fruits.addAll(fruits);
    }

    public boolean add(T fruit)
    {
        return fruits.add(fruit);
    }

    public float getWeight()
    {
        float weight = fruits.size();
        if(weight > 0)
        {
            weight *= fruits.get(0).getWeight();
        }

        return weight;
    }

    public boolean compare(Box<? extends Fruit> box)
    {
        return Math.abs( getWeight() - box.getWeight() ) < 0.00001f;
    }



    public boolean replace(Box<T> box)
    {
        boolean status = box.add(fruits);
        fruits.clear();

        return status;
    }


}
