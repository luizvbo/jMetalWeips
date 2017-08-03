package jmetal.util.comparators;

import java.util.Comparator;
import java.util.List;
import jmetal.core.Solution;
import jmetal.util.PseudoRandom;

/**
 * This class implements a solution comparator taking into account the violation constraints
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class WeipsComparator implements Comparator {
    IConstraintViolationComparator violationConstraintComparator_ ;
    private List<double[]> weightMatrix;

    /** Constructor */
    public WeipsComparator(List<double[]> weightMatrix) {
        this(new OverallConstraintViolationComparator(), weightMatrix) ;
    }

    /** Constructor */
    public WeipsComparator(IConstraintViolationComparator constraintComparator, List<double[]> weightMatrix) {
        violationConstraintComparator_ = constraintComparator ;
        this.weightMatrix = weightMatrix;
    }

    /**
     * Compares two solutions.
     *
     * @param object1 Object representing the first <code>Solution</code>.
     * @param object2 Object representing the second <code>Solution</code>.
     * @return -1, or 0, or 1 if object1 is better than object2, both are
     * equal, or object1  is better than object2, respectively.
     */
    @Override
    public int compare(Object object1, Object object2){
        if (object1 == null) {
            return 1;
        }
        if (object2 == null) {
            return -1;
        }
        
        Solution solution1 = (Solution)object1;
        Solution solution2 = (Solution)object2;
        
        // Test to determine whether at least a solution violates some constraint
        if (violationConstraintComparator_.needToCompare(solution1, solution2)){
            return violationConstraintComparator_.compare(solution1, solution2) ;
        }
    
        return weightedSumComparison(solution1, solution2) ;
    }

    /**
     * Compare two solutions based on the weighted sum of their objectives
     * @param solution1 First input solution
     * @param solution2 Second input solution
     * @return -1, or 0, or 1 if solution1 is better than solution2, both are
    *  equal, or solution1  is better than solution2, respectively.
     */
    private int weightedSumComparison(Solution solution1, Solution solution2) {
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
        // If the weight matrix is null, the method generates a weight vector
        if(weightMatrix == null){
            double[] weightVector = new double[numObjectives];
            double sum = 0;
            
            for(int i = 0; i < numObjectives; i++){
                weightVector[i] = PseudoRandom.randDouble();
                sum += weightVector[i];
            }
            // Normalize the weights to sum to one
            for(int i = 0; i < numObjectives; i++){
                weightVector[i] /= sum;
            }
            return weightVector;
        }
        // Otherwise randomly select a row from the matrix
        return weightMatrix.get(PseudoRandom.randInt(0, weightMatrix.size() - 1));
    }
}
