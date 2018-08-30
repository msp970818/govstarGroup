package com.kaituocn.govstar;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void test_1(){
        int[] points=new int[3*4];
        int height=30;
        int step=10;
        int index=0;
        for (int i = 0; i <3 ; i++) {
                points[index++]=i*step;
                points[index++]=0;
                points[index++]=i*step;
                points[index++]=height;
        }
        for (int i = 0; i < points.length; i++) {
            System.out.println("======="+points[i]);
        }
    }

}