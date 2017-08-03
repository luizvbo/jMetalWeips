/*
 * The MIT License
 *
 * Copyright 2017 luiz.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jmetal.metaheuristics.weips;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jmetal.core.Problem;
import jmetal.util.MathUtils;

/**
 *
 * @author luiz
 */
public class Grips extends Weips {
    /**
     * Constructor
     * @param problem Problem to solve
     */
    public Grips(Problem problem) {
        super (problem);
    }

    @Override
    protected List<double[]> getWeightMatrix(int numObjectives, int numWeights) {
        List<double[]> weightMatrix = new ArrayList<>();
        double[] coordinates = MathUtils.range(0, 1, numWeights);
        // Index currently adjusted
        int[] currentIndex = new int[numObjectives];
        currentIndex[numObjectives - 1] = numWeights - 1;
        // Maximum index allowed per dimension (ensures the weights sum to one)
        int[] maxIndex = new int[numObjectives - 1];
        Arrays.fill(maxIndex, numWeights - 1);
        
        while(currentIndex[0] <= maxIndex[0]){
            double[] tmpWeight = new double[numObjectives];
            // Concatenate the weights and append to the matrix
            for(int i = 0; i < numObjectives; i++){
                tmpWeight[i] = coordinates[currentIndex[i]];
            }
            weightMatrix.add(tmpWeight);
            // Increment the last but one index
            currentIndex[numObjectives-2] += 1;
            // Check if we need to adjust the index of other coordinates
            for(int i = numObjectives - 2; i > 0; i--){
                // If we get the maxIndex, we recursively add one to the next dimension
                if(currentIndex[i] > maxIndex[i]){
                    currentIndex[i] = 0;
                    currentIndex[i-1] += 1;
                    if(currentIndex[i-1] <= maxIndex[i-1]){
                        int summation = 0;
                        for(int j = 0; j < i; j++){
                            summation += currentIndex[j];
                        }
                        for(int j = i; j < numObjectives -1; j++){
                            maxIndex[j] = numWeights - summation - 1;
                        }
                    }
                }
                else{
                    break;
                }
            }
            // The index of the last coordinate is simetric to the last but one
            currentIndex[numObjectives - 1] = maxIndex[numObjectives - 2] - 
                                               currentIndex[numObjectives - 2];
        }
        
        return weightMatrix;
    }
    
    @Override 
    public String getName() {
        return "Grips" ;
    }

    @Override 
    public String getDescription() {
        return "Grid Pareto Selection method" ;
    }
}
