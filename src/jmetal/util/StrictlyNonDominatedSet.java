//  Ranking.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.util;

import java.util.ArrayList;
import java.util.Arrays;
import jmetal.core.SolutionSet;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.comparators.OverallConstraintViolationComparator;

import java.util.Comparator;
import java.util.List;
import jmetal.core.Solution;

/**
 * This class implements some facilities for ranking solutions.
 * Given a <code>SolutionSet</code> object, their solutions are ranked 
 * according to scheme proposed in NSGA-II; as a result, a set of subsets 
 * are obtained. The subsets are numbered starting from 0 (in NSGA-II, the 
 * numbering starts from 1); thus, subset 0 contains the non-dominated 
 * solutions, subset 1 contains the non-dominated solutions after removing those
 * belonging to subset 0, and so on.
 */
public class StrictlyNonDominatedSet {
    
    // Set of strictly non-dominated solutions
    private List<Solution> nonDominatedSet;
    
    // Set of strictly dominated solutions
    private List<Solution> dominatedSet;

    /** 
     * Constructor.
     * @param solutionSet The <code>SolutionSet</code> to be ranked.
     */       
    public StrictlyNonDominatedSet(SolutionSet solutionSet) {     
        // Comparator for dominance checking
        Comparator dominance_ = new DominanceComparator();
        
        // Comparator for Overal Constraint Violation Comparator
        Comparator constraint_ = new OverallConstraintViolationComparator();

        dominatedSet = new ArrayList<>();
        nonDominatedSet = new ArrayList<>();
        
        /**
         * The dominance array stores the dominance situation of each solution.
         * 1 for solutions non-dominated (all of them on the begining) and -1
         * for equal or dominated solution respectively.
         */
        int[] dominance = new int[solutionSet.size()];
        // All solutions are dominated on the begining.
        Arrays.fill(dominance, 1);
        
        for (int p = 0; p < (solutionSet.size()-1); p++) {
            
            // For all q individuals , calculate if p dominates q or vice versa
            for (int q = p+1; q < solutionSet.size(); q++) {
                int flagDominate = constraint_.compare(solutionSet.get(p),solutionSet.get(q));
                if (flagDominate == 0) {
                    flagDominate = dominance_.compare(solutionSet.get(p),solutionSet.get(q));
                }
                // p dominates q
                if (flagDominate == -1){
                    dominance[q] = -1;
                }
                else{
                    // q dominates p
                    if (flagDominate == 1){
                        dominance[p] = -1;
                    }
                    // No dominance relation, but p == q in the objective space.
                    else if(isSolutionEqual(solutionSet.get(p),solutionSet.get(q))){
                            dominance[p] = -1;
                    }
                }
            } // If nobody dominates p, p belongs to the first front
        }
        for (int p = 0; p < dominance.length; p++) {
            if (dominance[p] == -1) {
                dominatedSet.add(solutionSet.get(p));
            }
            else{
                nonDominatedSet.add(solutionSet.get(p));
            }
        }
    }
    
    /**
     * Check if two solutions are equal in the objective space
     * @param solution1 First input solution 
     * @param solution2 Second input solution
     * @return True if they are equal and false otherwise
     */
    private boolean isSolutionEqual(Solution solution1, Solution solution2){
        for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
            if(solution1.getObjective(i) != solution2.getObjective(i))
                return false;
        }
        return true;
    }

    /**
     * @return the nonDominatedSet
     */
    public List<Solution> getNonDominatedSet() {
        return nonDominatedSet;
    }

    /**
     * @return the dominatedSet
     */
    public List<Solution> getDominatedSet() {
        return dominatedSet;
    }
} 
