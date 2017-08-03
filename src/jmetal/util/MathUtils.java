/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.util;

/**
 *
 * @author luiz
 */
public class MathUtils {
    
    /**
     * Return evenly spaced values within a given interval.
     * Values are generated within theinterval [<code>start<\code>, <code>end<\code>].
     * @param start Start of interval.
     * @param end End of interval. 
     * @param totalCount Number of steps to take from <code>start<\code> to <code>end<\code>
     * @return Array with evenly spaced values within the interval.
     */
    public static double[] range(float start, float end, int totalCount) {
        double[] rangeArray = new double[totalCount];
        double step = (end - start) / (double) (totalCount - 1);
        for(int i = 0; i < totalCount - 1; i++){
            rangeArray[i] = start;
            start += step;
        }
        rangeArray[totalCount - 1] = end;
        return rangeArray;
    }
}
