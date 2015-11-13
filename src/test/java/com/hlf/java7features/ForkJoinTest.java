package com.hlf.java7features;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by howard.fackrell on 11/11/15.
 */
public class ForkJoinTest {
    static BigDecimal FOUR = new BigDecimal(4);
    static BigDecimal PI = new BigDecimal(Math.PI);

    @Test
    public void dostuff() {
        ForkJoinPool pool = new ForkJoinPool();

        PiCalculator calculator = new PiCalculator(0, 100_000);
        pool.execute(calculator);

        BigDecimal result = null;
        try {
            result = calculator.get().multiply(FOUR);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            fail("couldn't calculate PI");
        }

        System.out.println("Final result = " + result);
        assertTrue(equals(PI, result , 0.000_01));
    }


    boolean equals(BigDecimal expected, BigDecimal actual, double tollerance) {
        return expected.subtract(actual).abs().compareTo(new BigDecimal(tollerance)) < 0;
    }

}

/**
 * calculates PI/4 using sum(  (-1)^n / 2n+1 ) ) for n = 0 to ???
 */
class PiCalculator extends RecursiveTask<BigDecimal> {
    static BigDecimal TWO = new BigDecimal(2);

    final int min;
    final int maxInclusive;
    final static int THRESHHOLD = 100;

    PiCalculator(int min, int maxInclusive) {
        this.min = min;
        this.maxInclusive = maxInclusive;
    }

    protected BigDecimal compute() {
//        System.out.println("Computing pi with terms " + min + "-" + maxInclusive);
        BigDecimal sum = BigDecimal.ZERO;
        if((maxInclusive - min) > THRESHHOLD) {
            List<PiCalculator> subTasks = creatSubtasks(split(min, maxInclusive));
            for(PiCalculator task: subTasks) {
                task.fork();
            }
            for(PiCalculator task: subTasks) {
                sum = sum.add(task.join());
            }
        } else {
            for (int n : range()) {
                sum = sum.add(createTerm(n));
            }
        }
        return sum;
    }

    int [] split(int min, int maxInclusive) {
        int[] bounds = new int[4];
        int middle = (min + maxInclusive) / 2;
        bounds[0] = min;
        bounds[1] = middle;
        bounds[2] = middle+1;
        bounds[3] = maxInclusive;
        return bounds;
    }

    List<PiCalculator> creatSubtasks(int[] bounds) {
        List<PiCalculator> tasks = new ArrayList<>();
        tasks.add(new PiCalculator(bounds[0], bounds[1]));
        tasks.add(new PiCalculator(bounds[2], bounds[3]));
        return tasks;
    }

    int[] range() {
        int[] rng = new int[(maxInclusive - min)+1];
        for (int i = min; i <= maxInclusive; i++) {
            rng[i-min] = i;
        }
        return rng;
    }

    BigDecimal createTerm(int n) {
        BigDecimal denominator = BigDecimal.ONE.add(TWO.multiply(new BigDecimal(n)));
        BigDecimal term = BigDecimal.ONE.divide(denominator,30, BigDecimal.ROUND_HALF_DOWN);

        if (n % 2 == 1)
            term = term.negate();
        return term;
    }
}