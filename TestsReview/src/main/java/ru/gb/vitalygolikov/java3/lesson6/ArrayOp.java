package ru.gb.vitalygolikov.java3.lesson6;

public class ArrayOp {

    public int[] afterLastFourArray(int[] input) {

        int lastFour = -1;
        //check 4
        for(int i = 0; i < input.length; i++) {
            if(input[i] == 4) {
                lastFour = i;
            }
        }

        if(lastFour == -1) {
            throw new RuntimeException("There is not number 4 in this array.");
        }

        int newSize = input.length - 1 - lastFour;
        if(lastFour == 0) {
            return new int[]{};
        }

        int[] result = new int[newSize];

        for(int i = 0; i < newSize; i++) {
            result[i] = input[ lastFour + 1 + i];
        }
        return result;
    }

    public boolean anyOneAndFour(int[] input) {

        //array can't contain 1 and 4 at the same time in this case
        if(input.length <= 1) {
            return false;
        }

        boolean thereIsOne = false;
        boolean thereIsFour = false;

        for(int i = 0; i < input.length; i++) {
            if(input[i] == 1) { thereIsOne = true; }
            else if(input[i] == 4) { thereIsFour = true; }
            else { return false; }
        }

        return thereIsOne && thereIsFour;
    }

}
