/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.util;

import java.util.Collection;
import java.util.HashSet;
import jmetal.core.SolutionSet;

/**
 *
 * @author luiz
 */
public class SolutionListUtils {
    /**
     * Select <code>numSolutionsReturn</code> from the input solution set without
     * replacement
     * @param numSolutionsReturn Number of solutions to select
     * @param solutionSet Input solution set
     * @return A set with the selected solutions
     * @throws JMException Triggered when there is an error with the size of the 
     * input solution set
     */
    public static SolutionSet selectNRandomDifferentSolutions(int numSolutionsReturn, 
                                                             SolutionSet solutionSet) throws JMException{
        if (null == solutionSet) {
            throw new JMException("The solution list is null") ;
        } else if (solutionSet.size() == 0) {
            throw new JMException("The solution list is empty") ;
        } else if (solutionSet.size() < numSolutionsReturn) {
            throw new JMException("The solution list size (" + solutionSet.size() +") is less than "
                                + "the number of requested solutions ("+numSolutionsReturn+")") ;
        }
        
        Collection<Integer> positions = new HashSet<>(numSolutionsReturn);
        SolutionSet resultList = new SolutionSet(numSolutionsReturn);
        
        while (positions.size() < numSolutionsReturn) {
            int nextPosition = PseudoRandom.randInt(0, solutionSet.size()-1);
            if (!positions.contains(nextPosition)) {
                positions.add(nextPosition);
                resultList.add(solutionSet.get(nextPosition));
            }
        }
        return resultList;
    }
}
