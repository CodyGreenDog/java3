package ru.gb.vitaly.java3.lesson7;

public class TestContainer {

    @BeforeSuite
    public static void beforeAll(){
        System.out.println("BeforeSuite run");
    }

    @Test( priority = 5)
    public static void test1(){
        System.out.println("Test1 is running");
    }

    @Test(priority = 8)
    public static void test2(){
        System.out.println("Test2 is running");
    }

    @Test(priority = 7)
    public static void test3(){
        System.out.println("Test3 is running");
    }

    @AfterSuite
    public static void afterAll(){
        System.out.println("AfterSuite run");
    }
}
