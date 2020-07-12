package common;

import org.junit.Assert;
import org.junit.Test;

public class CircularCounterTest {

    private static final double DELTA = 1e-4;

    @Test
    public void testPlainCircularCounter(){
        CircularCounter c = new CircularCounter(5);

        int[] target = new int[5];

        c.push(5);
        c.push(4);
        target[0] = 5;
        target[1] = 4;
        assertArrayEquals(target, c);
        Assert.assertEquals(-1, c.getTotal());
        Assert.assertEquals(Double.NaN, c.getAverage(), DELTA);

        c.push(3);
        c.push(2);
        c.push(1);
        target[2] = 3;
        target[3] = 2;
        target[4] = 1;
        assertArrayEquals(target, c);
        Assert.assertEquals(15, c.getTotal());
        Assert.assertEquals(3., c.getAverage(), DELTA);

        c.push(0);
        c.push(-1);
        c.push(-2);
        target[0] = 0;
        target[1] = -1;
        target[2] = -2;
        assertArrayEquals(target, c);
        Assert.assertEquals(0, c.getTotal());
        Assert.assertEquals(0., c.getAverage(), DELTA);

        c.push(-3);
        c.push(-4);
        target[3] = -3;
        target[4] = -4;
        assertArrayEquals(target, c);
        Assert.assertEquals(-10, c.getTotal());
        Assert.assertEquals(-2., c.getAverage(), DELTA);
    }

    @Test
    public void testTimeseriesCircularCounter(){
        int period = 60;
        int interval = 20;
        int[] target = new int[3];
        TimeseriesCircularCounter c = new TimeseriesCircularCounter(period, interval);
        c.increment(1000);
        c.increment(1001);
        c.increment(1002, 5);
        c.increment(1018);

        assertArrayEquals(target, c);

        // now first interval is complete. value will populate
        // [1000, 1019] -> (1,1,5,1)
        c.increment(1020, 3);
        target[0] = 1 + 1 + 5 + 1;
        assertArrayEquals(target, c);

        // [1020, 1039] -> (3)
        c.increment(1040, 4);
        target[1] = 3;
        assertArrayEquals(target, c);

        // [1040, 1059] -> (4)
        c.increment(1060, 5);
        c.increment(1079, 5);
        target[2] = 4;
        assertArrayEquals(target, c);

        // [1060, 1079] -> (5, 5)
        c.increment(1080, 6);
        c.increment(1099, 6);
        target[0] = 10;
        assertArrayEquals(target, c);

        // [1080, 1099] -> (6, 6)
        // [1100, 1119] -> ()
        c.increment(1120, 6);
        c.increment(1121, 7);
        target[1] = 12;
        target[2] = 0;
        assertArrayEquals(target, c);

        // [1120, 1139] -> (6, 7)
        c.increment(1140, 6);
        target[0] = 13;
        assertArrayEquals(target, c);

        c.increment(1240, 2);
        target[0] = 0;
        target[1] = 0;
        target[2] = 0;
        assertArrayEquals(target, c);
    }

    private static void assertArrayEquals(int[] target, CircularCounter counter) {
        for(int i=0; i<target.length; i++) {
            Assert.assertEquals(target[i], counter.get(i));
        }
    }

    private static void assertArrayEquals(int[] target, TimeseriesCircularCounter counter) {
        for(int i=0; i<target.length; i++) {
            Assert.assertEquals(target[i], counter.get(i));
        }
    }
}
