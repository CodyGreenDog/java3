package ru.geekbrains.vitalygolikov.java3.lesson1;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args)
    {
        //1.  Написать метод, который меняет два элемента массива местами.
        //    (массив может быть любого ссылочного типа);
        Integer arr[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

        System.out.println("Array before swap\n" + Arrays.toString(arr));
        int idx1 = 3;
        int idx2 = 6;

        swapArrayElements(arr, idx1, idx2);

        System.out.println("Array after swap " + idx1 + " and " + idx2 + " elements:");
        System.out.println( Arrays.toString(arr) );
        System.out.println("================================");


        //2. Написать метод, который преобразует массив в ArrayList;
        ArrayList<Integer> list = arrayToArrayList(arr);

        System.out.println("ArrayList from arr:");
        System.out.println(list);
        System.out.println("================================");


        //3

        Box<Apple> appleBox = new Box<>( new ArrayList<Apple>() );
        Box<Orange> orangeBox = new Box<>( new ArrayList<Orange>() );
        generateFruits(appleBox, orangeBox, 10, 20);

        //    .d: Сделать метод getWeight() который высчитывает вес коробки, зная количество фруктов
        //    и вес одного фрукта(вес яблока - 1.0f, апельсина - 1.5f, не важно в каких это единицах);
        System.out.println("Apple Box weight is " + appleBox.getWeight() );
        System.out.println("Orange Box weight is " + orangeBox.getWeight() );


        //    .e:  Внутри класса коробка сделать метод compare, который позволяет сравнить текущую коробку
        //    с той, которую подадут в compare в качестве параметра, true - если их веса равны, false
        //    в противном случае(коробки с яблоками мы можем сравнивать с коробками с апельсинами);
        System.out.println( "Apple box and orange box compare result: " + appleBox.compare(orangeBox)   );


        //    .f: Написать метод, который позволяет пересыпать фрукты из текущей коробки в другую коробку
        //    (помним про сортировку фруктов, нельзя яблоки высыпать в коробку с апельсинами), соответственно
        //    в текущей коробке фруктов не остается, а в другую перекидываются объекты, которые были в этой коробке;
        Box<Apple> appleBox1 = new Box<>();
        Box<Orange> orangeBox1 = new Box<>();

        appleBox.replace(appleBox1);
        orangeBox.replace(orangeBox1);

        System.out.println("Apple box 1 weight is " + appleBox1.getWeight());
        System.out.println("Orange box 1 weight is " + orangeBox1.getWeight());

        System.out.println("Apple box weight is " + appleBox.getWeight());
        System.out.println("Orange box weight is " + orangeBox.getWeight());

    }

    //1.
    static <T> void swapArrayElements(T[] arr, int idx1, int idx2)
    {
        if(idx1 == idx2)
        {
            return;
        }

        if(idx1 > arr.length || idx2 > arr.length || idx1 < 0 || idx2 < 0)
        {
            throw new ArrayIndexOutOfBoundsException("idx1 or idx 2 is out of bound");
        }

        T tmp = arr[idx1];
        arr[idx1] = arr[idx2];
        arr[idx2] = tmp;


    }

    //2
    static <T> ArrayList<T> arrayToArrayList(T[]arr)
    {
        ArrayList<T> list = new ArrayList<>();
        for(T element : arr)
        {
            list.add(element);
        }

        return list;
    }

    //3. (utill)
    static void generateFruits(Box<Apple> appleBox, Box<Orange> orangeBox, int appleNumber, int orangeNumber )
    {
        for(int i = 0; i < appleNumber; i++)
        {
            appleBox.add( new Apple() );
        }

        for(int i = 0; i < orangeNumber; i++)
        {
            orangeBox.add( new Orange() );
        }

    }
}
