package jmetal.util.comparators;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;

import java.io.Serializable;
import java.util.Comparator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * This class implements a solution comparator taking into account the violation constraints
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class WeipsComparator<S extends Solution<?>> implements Comparator<S>, Serializable {
    private ConstraintViolationComparator<S> constraintViolationComparator;
    private double[][] weightMatrix;

    /** Constructor */
    public WeipsComparator(double[][] weightMatrix) {
        this(new OverallConstraintViolationComparator<S>(), weightMatrix) ;
    }

    /** Constructor */
    public WeipsComparator(ConstraintViolationComparator<S> constraintComparator, double[][] weightMatrix) {
        constraintViolationComparator = constraintComparator ;
        this.weightMatrix = weightMatrix;
    }

    /**
    * Compares two solutions.
    *
    * @param solution1 Object representing the first <code>Solution</code>.
    * @param solution2 Object representing the second <code>Solution</code>.
    * @return -1, or 0, or 1 if solution1 is better than solution2, both are
    * equal, or solution1  is better than solution2, respectively.
    */
    @Override
    public int compare(S solution1, S solution2) {
        if (solution1 == null) {
            throw new JMetalException("Solution1 is null") ;
        } else if (solution2 == null) {
            throw new JMetalException("Solution2 is null") ;
        } else if (solution1.getNumberOfObjectives() != solution2.getNumberOfObjectives()) {
            throw new JMetalException("Cannot compare because solution1 has " +
            solution1.getNumberOfObjectives()+ " objectives and solution2 has " +
            solution2.getNumberOfObjectives()) ;
        }
        int result ;
        result = constraintViolationComparator.compare(solution1, solution2) ;
        if (result == 0) {
            result = weightedSumComparison(solution1, solution2) ;
        }

        return result ;
    }

    /**
     * Compare two solutions based on the weighted sum of their objectives
     * @param solution1 First input solution
     * @param solution2 Second input solution
     * @return -1, or 0, or 1 if solution1 is better than solution2, both are
    *  equal, or solution1  is better than solution2, respectively.
     */
    private int weightedSumComparison(S solution1, S solution2) {
        double sumSolution1 = 0 ;
        double sumSolution2 = 0 ;
        
        double[] weightVector = getWeightVector(solution1.getNumberOfObjectives());
        
        for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
            sumSolution1 += weightVector[i] * solution1.getObjective(i);
            sumSolution2 += weightVector[i] * solution2.getObjective(i);
        }
        
        if (sumSolution1 < sumSolution2) {
            return -1;
        } else if (sumSolution1 > sumSolution2) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Select a random weight vector from the weight matrix (if it is not null)
     * or randomly generates the weight vector
     * @param numObjectives Number of objectives defined by the problem
     * @return The weight vector
     */
    private double[] getWeightVector(int numObjectives) {
        JMetalRandom random = JMetalRandom.getInstance();
        
        // If the weight matrix is null, the method generates a weight vector
        if(weightMatrix == null){
            double[] weightVector = new double[numObjectives];
            double sum = 0;
            
            for(int i = 0; i < numObjectives; i++){
                weightVector[i] = random.nextDouble();
                sum += weightVector[i];
            }
            // Normalize the weights to sum to one
            for(int i = 0; i < numObjectives; i++){
                weightVector[i] /= sum;
            }
            return weightVector;
        }
        // Otherwise randomly select a row from the matrix
        return weightMatrix[random.nextInt(0, weightMatrix.length - 1)];
    }
}
