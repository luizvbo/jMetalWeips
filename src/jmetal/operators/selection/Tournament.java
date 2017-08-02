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
import jmetal.util.PseudoRandom;
import jmetal.util.comparators.DominanceComparator;

import java.util.Comparator;
import java.util.HashMap;
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
    private int tournamentSize;

    /**
     * Constructor
     * Creates a new Binary tournament operator using a BinaryTournamentComparator
     */
    public Tournament(HashMap<String, Object> parameters){
        super(parameters) ;
        if (parameters != null){
            comparator_ = new DominanceComparator();
            tournamentSize = 2;
        }
        else{
            if(parameters.get(p_comparator) != null){
                comparator_ = (Comparator) parameters.get(p_comparator) ;
            }
            if(parameters.get(p_tournamentSize) != null){
                tournamentSize = ((Integer)parameters.get(p_tournamentSize)).intValue();
            }
        }
    } 

    /**
     * Performs the operation
     * @param object Object representing a SolutionSet
     * @return the selected solution
     */
    @Override
    public Object execute(Object object) throws JMException{
        SolutionSet solutionSet = (SolutionSet)object;
        SolutionSet candidates = SolutionListUtils.selectNRandomDifferentSolutions(tournamentSize, solutionSet);
        
        if (candidates.size() == 1) {
            return candidates.get(0);
        }
        
        return candidates.best(comparator_);
    } 
} 