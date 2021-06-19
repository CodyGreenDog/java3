package ru.gb.vitaly.java3.lesson7;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestRunner {
    public static void main(String[] args) {

    }

    public static void start(Class testClass) {
        Method[] methods = testClass.getDeclaredMethods();

        Method[] beforeAfterMethods = checkCorrectDeclaration(methods); //[0] - BeforeSuite, [1] - AfterSuite

        try {

            runBeforeSuite(beforeAfterMethods[0]);
            runTests(methods);
            runAfterSuite(beforeAfterMethods[1]);

        }catch(IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }


    }

    //Returns two methods in array, where method[0] is BeforeSuite, method[1] is AfterSuite
    private static Method[] checkCorrectDeclaration(Method[] methods){

        Method[] outputMethods = new Method[2];
        int beforeSuiteCounter = 0;
        int afterSuiteCounter = 0;


        for(Method m : methods) {
            if(m.isAnnotationPresent(BeforeSuite.class)) {
                beforeSuiteCounter++;
                outputMethods[0] = m;
            }
            else if(m.isAnnotationPresent(AfterSuite.class)){
                afterSuiteCounter++;
                outputMethods[1] = m;
            }

            if(beforeSuiteCounter > 1){
                throw new RuntimeException("Multiple BeforSuite declaration");
            }

            if(afterSuiteCounter > 1){
                throw new RuntimeException("Multiple AfterSuite declaration");
            }
        }

        return outputMethods;
    }

    private static void runBeforeSuite(Method method) throws  IllegalAccessException, InvocationTargetException {
        if(method != null)
            method.invoke(null);
    }

    private static void runTests(Method[] methods) throws  IllegalAccessException, InvocationTargetException{

        for(int i = 9; i >= 0; i--) {

            for (Method m : methods) {
                if (m.isAnnotationPresent(Test.class) && m.getAnnotation(Test.class).priority() == i) {
                    m.invoke(null);
                }
            }
        }
    }

    private static void runAfterSuite(Method method) throws  IllegalAccessException, InvocationTargetException{
        if(method != null)
        method.invoke(null);
    }
}
