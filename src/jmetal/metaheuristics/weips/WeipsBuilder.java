package org.uma.jmetal.algorithm.multiobjective.weips;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.List;


/**
 *
 * @author Luiz Otavio V. B. Oliveira <luiz.vbo@gmail.com>
 */
public class WeipsBuilder<S extends Solution<?>> implements AlgorithmBuilder<Weips<S>> {
    public enum WeipsVariant {Rawps, Unpas, Grips, StratGrips}

    /**
    * WeipsBuilder class
    */
    private final Problem<S> problem;
    private int maxEvaluations;
    private int populationSize;
    private int tournamentSize;
    private int numberWeights;
    private CrossoverOperator<S>  crossoverOperator;
    private MutationOperator<S> mutationOperator;
    private SelectionOperator<List<S>, S> selectionOperator;
    private SolutionListEvaluator<S> evaluator;

    private WeipsVariant variant;

    /**
    * WeipsBuilder constructor
    */
    public WeipsBuilder(Problem<S> problem, CrossoverOperator<S> crossoverOperator,
                        MutationOperator<S> mutationOperator) {
        this.problem = problem;
        maxEvaluations = 25000;
        populationSize = 100;
        tournamentSize = 5;
        numberWeights = populationSize;
        this.crossoverOperator = crossoverOperator ;
        this.mutationOperator = mutationOperator ;
        evaluator = new SequentialSolutionListEvaluator<S>();

        this.variant = WeipsVariant.Rawps ;
    }

    public WeipsBuilder<S> setMaxEvaluations(int maxEvaluations) {
        if (maxEvaluations < 0) {
            throw new JMetalException("maxEvaluations is negative: " + maxEvaluations);
        }
        this.maxEvaluations = maxEvaluations;

        return this;
    }

    public WeipsBuilder<S> setPopulationSize(int populationSize) {
        if (populationSize < 0) {
            throw new JMetalException("Population size is negative: " + populationSize);
        }

        this.populationSize = populationSize;

        return this;
    }
    
    public WeipsBuilder<S> setTournamentSize(int tournamentSize) {
        if (tournamentSize < 2) {
            throw new JMetalException("Tournament size should be greater than 1. Value " +
                                      tournamentSize + " was found instead.");
        }

        this.tournamentSize = tournamentSize;

        return this;
    }
    
    public WeipsBuilder<S> setNumberWeights(int numberWeights) {
        if (numberWeights < 1) {
            throw new JMetalException("Number of weights is smaller than 1: " + numberWeights);
        }

        this.numberWeights = numberWeights;

        return this;
    }

    public WeipsBuilder<S> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
        if (evaluator == null) {
            throw new JMetalException("evaluator is null");
        }
        this.evaluator = evaluator;

        return this;
    }


    public WeipsBuilder<S> setVariant(WeipsVariant variant) {
        this.variant = variant;

        return this;
    }

    @Override
    public Weips<S> build() {
        Weips<S> algorithm = null ;

        // Equivalent to the Rawps initialization
        double[][] weightMatrix = null;
        switch (variant) {
            case Rawps:
                algorithm = new Rawps(maxEvaluations, populationSize, tournamentSize, numberWeights,
                        problem, crossoverOperator, mutationOperator, evaluator);
                break;
            case Unpas:
                algorithm = new Unpas(maxEvaluations, populationSize, tournamentSize, numberWeights,
                        problem, crossoverOperator, mutationOperator, evaluator);
                break;
            case Grips:
                algorithm = new Grips(maxEvaluations, populationSize, tournamentSize, numberWeights,
                        problem, crossoverOperator, mutationOperator, evaluator);
                break;
            case StratGrips:
                algorithm = new StratGrips(maxEvaluations, populationSize, tournamentSize, numberWeights,
                        problem, crossoverOperator, mutationOperator, evaluator);
                break;
            default:
                break;
        }

        return algorithm;
    }
    
    /* Getters */
    public Problem<S> getProblem() {
        return problem;
    }

    public int getMaxIterations() {
        return maxEvaluations;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public CrossoverOperator<S> getCrossoverOperator() {
        return crossoverOperator;
    }

    public MutationOperator<S> getMutationOperator() {
        return mutationOperator;
    }

    public SelectionOperator<List<S>, S> getSelectionOperator() {
        return selectionOperator;
    }

    public SolutionListEvaluator<S> getSolutionListEvaluator() {
        return evaluator;
    }
}
