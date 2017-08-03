/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import jmetal.core.SolutionSet;

/**
 *
 * @author luiz
 */
public class SolutionListUtils {
    /**
     * Select <code>numPositionsReturn</code> different indexes from a set given
     * its size
     * @param numPositionsReturn Number of positions to select
     * @param setSize Size of the set
     * @return An Integer Collection with the indexes
     */
    public static Collection<Integer> selectNRandomDifferentPostions(int numPositionsReturn, int setSize){
        Collection<Integer> positions = new HashSet<>(numPositionsReturn);
        
        while (positions.size() < numPositionsReturn) {
            int nextPosition = PseudoRandom.randInt(0, setSize-1);
            if (!positions.contains(nextPosition)) {
                positions.add(nextPosition);
            }
        }
        return positions;
    }
    
    /**
     * Select <code>numSolutionsReturn</code> solutions from the input solution set without
     * replacement
     * @param numSolutionsReturn Number of solutions to select
     * @param solutionSet Input solution set
     * @return A set with the selected solutions
     * @throws JMException JMException Triggered when there is an error with the size of the 
     * input solution set
     */
    public static SolutionSet selectNRandomDifferentSolutions(int numSolutionsReturn,
                                                             SolutionSet solutionSet) throws JMException{
        if (null == solutionSet) {
            throw new JMException("The solution list is null") ;
        } else if (solutionSet.size() == 0) {
            throw new JMException("The solution list is empty") ;
        } else if (solutionSet.size() < numSolutionsReturn) {
            throw new JMException("The solution list size (" + solutionSet.size() +
                                  ") is less than the number of requested solutions (" + 
                                  numSolutionsReturn+")") ;
        }
        Collection<Integer> positions = selectNRandomDifferentPostions(numSolutionsReturn, solutionSet.size());
        SolutionSet selectedSet = new SolutionSet(numSolutionsReturn);
        Iterator<Integer> indexIterator = positions.iterator();
        while(indexIterator.hasNext()){
            selectedSet.add(solutionSet.get(indexIterator.next()));
        }
        return selectedSet;
    }
}
