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
package org.uma.jmetal.algorithm.multiobjective.weips;

import java.util.List;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.WeipsTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 *
 * @author luiz
 */
public class Unpas <S extends Solution<?>> extends Weips<S> {

    public Unpas(int maxEvaluations, 
                 int populationSize, 
                 int tournamentSize,
                 int numberWeights,
                 Problem<S> problem, 
                 CrossoverOperator<S> crossoverOperator, 
                 MutationOperator<S> mutationOperator,
                 SolutionListEvaluator<S> evaluator){
        super(maxEvaluations, populationSize, tournamentSize, numberWeights, 
              problem, crossoverOperator, mutationOperator, evaluator);
    }

    @Override
    protected SelectionOperator<List<S>, S> getSelectionOperator(int numObjectives, 
                                                                 int numWeights, 
                                                                 int tournamentSize) {
        return new WeipsTournamentSelection(buildWeightMatrix(numObjectives, numWeights), tournamentSize);
    }

    private double[][] buildWeightMatrix(int numObjectives, int numWeights) {
        double [][] weightMatrix = new double[numWeights][numObjectives];
        JMetalRandom random = JMetalRandom.getInstance();

        for(int i = 0; i < weightMatrix.length; i++){
            double sum = 0;
            
            for(int j = 0; j < numObjectives; j++){
                weightMatrix[i][j] = random.nextDouble();
                sum += weightMatrix[i][j];
            }
            // Normalize the weights to sum to one
            for(int j = 0; j < numObjectives; j++){
                weightMatrix[i][j] /= sum;
            }
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
