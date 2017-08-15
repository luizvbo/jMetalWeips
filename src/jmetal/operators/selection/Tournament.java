//  BinaryTournament.java
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

package jmetal.operators.selection;

import jmetal.core.SolutionSet;
import jmetal.util.comparators.DominanceComparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import jmetal.core.Solution;
import jmetal.util.JMException;
import jmetal.util.SolutionListUtils;

/**
 * This class implements an binary tournament selection operator
 */
public class Tournament extends Selection {
    public static String p_tournamentSize = "tournamentSize";
    public static String p_comparator = "comparator";
    
    /**
     * Stores the <code>Comparator</code> used to compare two
     * solutions
     */
    private Comparator comparator_;
    
    /**
     * Defines the size of the tournament
     */
    private int tournamentSize_;

    /**
     * a_ stores a permutation of the solutions in the solutionSet used
     */
    private int a_[];
  
    /**
     *  index_ stores the actual index for selection
     */
    private int index_ = 0;
    
    /**
     * Constructor
     * Creates a new Binary tournament operator using a BinaryTournamentComparator
     */
    public Tournament(HashMap<String, Object> parameters){
        super(parameters) ;
        if (parameters != null){
            comparator_ = new DominanceComparator();
            tournamentSize_ = 2;
        }
        else{
            if(parameters.get(p_comparator) != null){
                comparator_ = (Comparator) parameters.get(p_comparator) ;
            }
            if(parameters.get(p_tournamentSize) != null){
                tournamentSize_ = ((Integer)parameters.get(p_tournamentSize)).intValue();
            }
        }
    } 

    /**
     * Performs the operation
     * @param object Object representing a SolutionSet
     * @return the selected solution
     * @throws jmetal.util.JMException
     */
    @Override
    public Object execute(Object object) throws JMException{
        SolutionSet solutionSet = (SolutionSet)object;
//        if (index_ == 0) { //Create the permutation
//          a_= (new jmetal.util.PermutationUtility()).intPermutation(solutionSet.size());
//        }
        
        SolutionSet candidates = new SolutionSet(tournamentSize_);
        while(candidates.size() < tournamentSize_){
            candidates.add(solutionSet.get(a_[index_]));
            index_ ++;
            if(index_ >= solutionSet.size()){
                index_ = 0;
                a_= (new jmetal.util.PermutationUtility()).intPermutation(solutionSet.size());
            }
        }
        
        if (candidates.size() == 1) {
            return candidates.get(0);
        }
        
        return candidates.best(comparator_);
    } 
    
    public void resetPermutation(int solSetSize){
        index_ = 0;
        a_= (new jmetal.util.PermutationUtility()).intPermutation(solSetSize);
    }
       
    /**
     * Selects one solution from the input solution set using a tournament of size 
     * <code>tournamentSize</code> and then remove it from the set
     * @param solutionList List of solutions used as input for the tournment
     * @return The winner of the tournment
     */
    public Solution noReplacementTournament(List<Solution> solutionList) {
        int tournamentSize = Math.min(tournamentSize_, solutionList.size());
        
        if (index_ == 0) { //Create the permutation
          a_= (new jmetal.util.PermutationUtility()).intPermutation(solutionList.size());
        }
        
        SolutionSet candidates = new SolutionSet(tournamentSize);
        while(candidates.size() < tournamentSize){
            if(a_[index_] < solutionList.size())
                candidates.add(solutionList.get(a_[index_]));
            if(index_ < solutionList.size()){
                index_ ++;
            }
            else{
                index_ = 0;
                a_= (new jmetal.util.PermutationUtility()).intPermutation(solutionList.size());
            }
        }
        
        if (candidates.size() == 1) {
            solutionList.remove(candidates.get(0));
            return candidates.get(0);
        }
                
        Solution selectedSol = candidates.best(comparator_);
        solutionList.remove(selectedSol);
        
        return selectedSol;
    }
} 
