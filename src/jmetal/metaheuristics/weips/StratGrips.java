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

import java.util.List;
import jmetal.util.PseudoRandom;

/**
 *
 * @author luiz
 */
public class StratGrips extends Grips {
    /**
     * Constructor
     * @param problem Problem to solve
     */
    public StratGrips(jmetal.core.Problem problem) {
        super (problem);
    }

    @Override
    protected List<double[]> getWeightMatrix(int numObjectives, int numWeights) {
        List<double[]> weightMatrix = super.getWeightMatrix(numObjectives, numWeights);
        
        for(double[] weightArray : weightMatrix) {
            double sumWeights = 0;
            for(int i = 0; i< weightArray.length; i++){
                weightArray[i] *= PseudoRandom.randDouble();
                sumWeights += weightArray[i];
            }
            for(int i = 0; i< weightArray.length; i++){
                weightArray[i] /= sumWeights;
            }
        }
        return weightMatrix;
    }
    
    @Override 
    public String getName() {
        return "StratGrips" ;
    }

    @Override 
    public String getDescription() {
        return "Stratified Grid Pareto Selection method" ;
    }
}
