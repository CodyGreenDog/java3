package ru.arraytests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.gb.vitalygolikov.java3.lesson6.ArrayOp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ArrayTests {

    private static ArrayOp arOp;

    @BeforeAll
    public static void init() {
        arOp = new ArrayOp();
    }

    public static Stream<Arguments> dataForTestLastFour(){

        List<Arguments> args = new ArrayList<>();
        args.add( Arguments.arguments( new int[] {1, 7}, new int[]{1, 2, 4, 4, 2, 3, 4, 1, 7} ) );
        args.add( Arguments.arguments( new int[]{}, new int[]{1, 2, 4, 4, 2, 3, 4} ) );

        return args.stream();
    }

    @ParameterizedTest
    @MethodSource("dataForTestLastFour")
    public void massTestLastFour(int[] result, int[] input) {
        Assertions.assertArrayEquals( result, arOp.afterLastFourArray(input) );
    }

    public static Stream<Arguments> dataForTestOneAndFour(){

        List<Arguments> args = new ArrayList<>();
        args.add( Arguments.arguments( true, new int[]{1, 1, 1, 4, 4, 1, 4, 4} ) );
        args.add( Arguments.arguments( false, new int[]{ 1, 1, 1, 1, 1, 1} ) );
        args.add( Arguments.arguments( false, new int[]{4, 4, 4, 4} ) );
        args.add( Arguments.arguments( false, new int[]{ 1, 4, 4, 1, 1, 4, 3} ) );

        return args.stream();
    }

    @ParameterizedTest
    @MethodSource("dataForTestOneAndFour")
    public void massTestOneAndFour(boolean result, int[] input) {
        Assertions.assertEquals( result, arOp.anyOneAndFour(input) );
    }


    @Test
    public void assertTest0(){
        Assertions.assertThrows(RuntimeException.class, () -> arOp.afterLastFourArray( new int[]{1, 2, 44, 2, 34, 1, 2} ));
    }

    @Test
    public void assertTest1(){
        Assertions.assertThrows(RuntimeException.class, () -> arOp.afterLastFourArray( new int[]{1, 2, 1, 7} ));
    }

}
