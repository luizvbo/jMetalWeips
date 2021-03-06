//  NSGAII_Settings.java 
//
//  Authors:
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

package jmetal.experiments.settings;

import jmetal.core.Algorithm;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.operators.crossover.Crossover;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.Selection;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;

import java.util.HashMap;
import java.util.Properties;
import jmetal.metaheuristics.weips.Grips;
import jmetal.metaheuristics.weips.Rawps;
import jmetal.metaheuristics.weips.StratGrips;
import jmetal.metaheuristics.weips.Unpas;
import jmetal.metaheuristics.weips.Weips;

/**
 * Settings class of algorithm NSGA-II (real encoding)
 */
public class Weips_Settings extends Settings {
    public enum eWeipsMethod{ UNPAS, RAWPS, GRIPS, STRATGRIPS };
    
    public eWeipsMethod weipsMethod_            ;
    public int populationSize_                 ;
    public int maxEvaluations_                 ;
    public int tournamentSize_                 ;
    public int numWeights_                     ;
    public double mutationProbability_         ;
    public double crossoverProbability_        ;
    public double mutationDistributionIndex_   ;
    public double crossoverDistributionIndex_  ;
    
    /**
    * Constructor
    */
    public Weips_Settings(String problem, eWeipsMethod weipsMethod) {
        super(problem) ;

        Object [] problemParams = {"Real"};
        try {
            problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
        } catch (JMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // Default experiments.settings
        //    populationSize_              = 100   ;
        //    maxEvaluations_              = 25000 ;

        populationSize_              = 100   ;
        maxEvaluations_              = 25000 ;

        mutationProbability_         = 1.0/problem_.getNumberOfVariables() ;
        crossoverProbability_        = 0.9   ;
        mutationDistributionIndex_   = 20.0  ;
        crossoverDistributionIndex_  = 20.0  ;
        
        weipsMethod_ = weipsMethod;
        tournamentSize_ = 3;
        if(problem_.getNumberOfObjectives() == 3 &&
                (weipsMethod == eWeipsMethod.GRIPS || weipsMethod == eWeipsMethod.STRATGRIPS))
            // This value ensures the number of weights is 300
            numWeights_ = 24;
        numWeights_ = populationSize_; 
    } 


    /**
     * Configure Weips with default parameter experiments.settings
     * @return A Weips algorithm object
     * @throws jmetal.util.JMException
     */
    @Override
    public Algorithm configure() throws JMException {
        Algorithm algorithm ;
        Selection  selection ;
        Crossover  crossover ;
        Mutation   mutation  ;

        HashMap  parameters ; // Operator parameters
        
        switch(weipsMethod_){
            case GRIPS:
                algorithm = new Grips(problem_);
                break;
            case RAWPS:
                algorithm = new Rawps(problem_);
                break;
            case STRATGRIPS:
                algorithm = new StratGrips(problem_);
                break;
            case UNPAS:
                algorithm = new Unpas(problem_);
                break;
            default:
                algorithm = new Rawps(problem_);
                break;  
        }

        // Algorithm parameters
        algorithm.setInputParameter("populationSize", populationSize_);
        algorithm.setInputParameter("maxEvaluations", maxEvaluations_);
        algorithm.setInputParameter(Weips.p_numWeights, numWeights_);
        algorithm.setInputParameter(Weips.p_tournamentSize, tournamentSize_);
        

        // Mutation and Crossover for Real codification
        parameters = new HashMap() ;
        parameters.put("probability", crossoverProbability_) ;
        parameters.put("distributionIndex", crossoverDistributionIndex_) ;
        crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);

        parameters = new HashMap() ;
        parameters.put("probability", mutationProbability_) ;
        parameters.put("distributionIndex", mutationDistributionIndex_) ;
        mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);

        // Selection Operator
        parameters = null ;

        // Add the operators to the algorithm
        algorithm.addOperator("crossover",crossover);
        algorithm.addOperator("mutation",mutation);

        return algorithm ;
    } 

    /**
     * Configure Weips with user-defined parameter experiments.settings
     * @return A Weips algorithm object
     */
    @Override
    public Algorithm configure(Properties configuration) throws JMException {
        Algorithm algorithm ;
        Crossover  crossover ;
        Mutation   mutation  ;

        HashMap  parameters ; // Operator parameters

        switch(weipsMethod_){
            case GRIPS:
                algorithm = new Grips(problem_);
                break;
            case RAWPS:
                algorithm = new Rawps(problem_);
                break;
            case STRATGRIPS:
                algorithm = new StratGrips(problem_);
                break;
            case UNPAS:
                algorithm = new Unpas(problem_);
                break;
            default:
                algorithm = new Rawps(problem_);
                break;  
        }

        // Algorithm parameters
        populationSize_ = Integer.parseInt(configuration.getProperty("populationSize",String.valueOf(populationSize_)));
        maxEvaluations_  = Integer.parseInt(configuration.getProperty("maxEvaluations",String.valueOf(maxEvaluations_)));
        algorithm.setInputParameter("populationSize", populationSize_);
        algorithm.setInputParameter("maxEvaluations", maxEvaluations_);
        
        numWeights_ = Integer.parseInt(configuration.getProperty(Weips.p_numWeights, String.valueOf(numWeights_)));
        tournamentSize_ = Integer.parseInt(configuration.getProperty(Weips.p_tournamentSize, String.valueOf(tournamentSize_)));
        algorithm.setInputParameter(Weips.p_numWeights, numWeights_);
        algorithm.setInputParameter(Weips.p_tournamentSize, tournamentSize_);

        // Mutation and Crossover for Real codification
        crossoverProbability_ = Double.parseDouble(configuration.getProperty("crossoverProbability",String.valueOf(crossoverProbability_)));
        crossoverDistributionIndex_ = Double.parseDouble(configuration.getProperty("crossoverDistributionIndex",String.valueOf(crossoverDistributionIndex_)));
        parameters = new HashMap() ;
        parameters.put("probability", crossoverProbability_) ;
        parameters.put("distributionIndex", crossoverDistributionIndex_) ;
        crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);

        mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability",String.valueOf(mutationProbability_)));
        mutationDistributionIndex_ = Double.parseDouble(configuration.getProperty("mutationDistributionIndex",String.valueOf(mutationDistributionIndex_)));
        parameters = new HashMap() ;
        parameters.put("probability", mutationProbability_) ;
        parameters.put("distributionIndex", mutationDistributionIndex_) ;
        mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);

        // Add the operators to the algorithm
        algorithm.addOperator("crossover",crossover);
        algorithm.addOperator("mutation",mutation);

        return algorithm ;
    }
} 
