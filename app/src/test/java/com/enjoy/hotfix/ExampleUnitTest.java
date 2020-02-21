package com.enjoy.hotfix;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    static class A {
        static {
            System.out.println("11111");
        }
    }

    @Test
    public void addition_isCorrect() throws ClassNotFoundException {
        assertEquals(4, 2 + 2);
        getClass().getClassLoader().loadClass("com.enjoy.hotfix.ExampleUnitTest$A");
    }
}