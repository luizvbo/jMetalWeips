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
import java.util.List;
import jmetal.core.Problem;
import jmetal.util.PseudoRandom;

/**
 *
 * @author luiz
 */
public class Unpas extends Weips {
    /**
     * Constructor
     * @param problem Problem to solve
     */
    public Unpas(Problem problem) {
        super (problem);
    }

    @Override
    protected List<double[]> getWeightMatrix(int numObjectives, int numWeights) {
        List<double[]> weightMatrix = new ArrayList<>(numWeights);

        for(int i = 0; i < numWeights; i++){
            double sum = 0;
            double[] weightArray = new double[numObjectives];
            for(int j = 0; j < numObjectives; j++){
                weightArray[j] = PseudoRandom.randDouble();
                sum += weightArray[j];
            }
            // Normalize the weights to sum to one
            for(int j = 0; j < numObjectives; j++){
                weightArray[j] /= sum;
            }
            weightMatrix.add(weightArray);
        }
        return weightMatrix;
    }
    
    @Override 
    public String getName() {
        return "Unpas" ;
    }

    @Override 
    public String getDescription() {
        return "Uniformly distributed Pareto Selection method" ;
    }
}
