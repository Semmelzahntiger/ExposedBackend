package com.semmelzahntiger.brainrotbackend.util;

public class RandomUtil {
    public static int getRandomBetweenSize(int max) {
        return (int) (Math.random() * max);
    }
    public static int getRandomBetweenRange(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }
}
