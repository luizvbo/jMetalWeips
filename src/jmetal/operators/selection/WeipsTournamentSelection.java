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
package jmetal.operators.selection;

import java.util.Comparator;
import java.util.List;
import jmetal.util.JMException;

/**
 *
 * @author luiz
 */
public class WeipsTournamentSelection extends Selection {
    private Comparator<S> comparator;

    private final int numberOfTournaments;

    /** Constructor */
    public WeipsTournamentSelection(double[][] weightMatrix, int numberOfTournaments) {
        this.numberOfTournaments = numberOfTournaments;
        this.comparator = new WeipsComparator<S>(weightMatrix);
    }

    @Override
    /** Execute() method */
    public S execute(List<S> solutionList) {
        if (null == solutionList) {
            throw new JMetalException("The solution list is null") ;
        } else if (solutionList.isEmpty()) {
            throw new JMetalException("The solution list is empty") ;
        }

        S result;
        if (solutionList.size() == 1) {
            result = solutionList.get(0);
        } else {
            result = SolutionListUtils.selectNRandomDifferentSolutions(1, solutionList).get(0);
            int count = 1; // at least 2 solutions are compared
            do {
                S candidate = SolutionListUtils.selectNRandomDifferentSolutions(1, solutionList).get(0);
                result = SolutionUtils.getBestSolution(result, candidate, comparator) ;
            } while (++count < this.numberOfTournaments);
        }

        return result;
    }

    @Override
    public Object execute(Object object) throws JMException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
