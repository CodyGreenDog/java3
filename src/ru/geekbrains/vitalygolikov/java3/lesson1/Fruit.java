package ru.geekbrains.vitalygolikov.java3.lesson1;

public class Fruit {
    private float weight;

    Fruit(float weight)
    {
        this.weight = weight;
    }

    public float getWeight()
    {
        return weight;
    }
}

class Apple extends Fruit {

    public static final float WEIGHT = 1f;

    Apple()
    {
        super(WEIGHT);
    }

}


class Orange extends Fruit {

    public static final float WEIGHT = 1.5f;

    Orange()
    {
        super(WEIGHT);
    }

}