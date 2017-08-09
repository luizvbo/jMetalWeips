package jmetal.metaheuristics.weips;

import java.util.HashMap;
import java.util.List;
import jmetal.core.*;
import jmetal.operators.selection.Tournament;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.util.StrictNonDominatedSet;
import jmetal.util.comparators.WeipsComparator;

/**
 *
 * @author Luiz Otavio V. B. Oliveira <luiz.vbo@gmail.com>
 */
public abstract class Weips extends Algorithm {
    public static String p_populationSize = "populationSize";
    public static String p_maxEvaluations = "maxEvaluations";
    public static String p_indicators = "indicators";
    public static String p_mutation = "mutation";
    public static String p_crossover = "crossover";
    public static String p_numWeights = "numWeights";
    public static String p_tournamentSize = Tournament.p_tournamentSize;
    
    public static String po_distributionIndex = "distributionIndex";
    public static String po_probability = "probability";
//    public static String p_ = "";
    
    protected Tournament tournmentSelOperator = null;
    
    /**
     * Constructor
     * @param problem Problem to solve
     */
    public Weips(Problem problem) {
        super (problem);
    }

    public void build(){
        tournmentSelOperator = getTournmentSelOperator(problem_.getNumberOfObjectives(),
                                             ((Integer) getInputParameter(p_numWeights)).intValue());
    }
    
    public abstract String getName();
    
    public abstract String getDescription();

    private Tournament getTournmentSelOperator(int numberOfObjectives, 
                                            int numberWeights) {
        HashMap  parameters = new HashMap();
        parameters.put(Tournament.p_tournamentSize, (Integer) getInputParameter(p_tournamentSize)) ;
        parameters.put(Tournament.p_comparator, new WeipsComparator(getWeightMatrix(numberOfObjectives, numberWeights)));
        
        return new Tournament(parameters);
    }
    
    protected abstract List<double[]> getWeightMatrix(int numberOfObjectives, int numberWeights);
    
    
    
    /**   
     * Runs the WeiPS algorithm.
     * @return a <code>SolutionSet</code> that is a set of non dominated solutions
     * as a result of the algorithm execution
     * @throws JMException 
     */
    public SolutionSet execute() throws JMException, ClassNotFoundException {
        build();
        
        int populationSize;
        int maxEvaluations;
        int evaluations;

        QualityIndicator indicators; // QualityIndicator object
        int requiredEvaluations; // Use in the example of use of the
        // indicators object (see below)
        
        SolutionSet population;
        SolutionSet offspringPopulation;
        SolutionSet union;

        Operator mutationOperator;
        Operator crossoverOperator;
        
        Distance distance = new Distance();

        //Read the parameters
        populationSize = ((Integer) getInputParameter(p_populationSize)).intValue();
        maxEvaluations = ((Integer) getInputParameter(p_maxEvaluations)).intValue();
        indicators = (QualityIndicator) getInputParameter(p_indicators);

        //Initialize the variables
        population = new SolutionSet(populationSize);
        evaluations = 0;

        requiredEvaluations = 0;

        //Read the operators
        mutationOperator = operators_.get(p_mutation);
        crossoverOperator = operators_.get(p_crossover);

        // Create the initial solutionSet
        Solution newSolution;
        for (int i = 0; i < populationSize; i++) {
            newSolution = new Solution(problem_);
            problem_.evaluate(newSolution);
            problem_.evaluateConstraints(newSolution);
            evaluations++;
            population.add(newSolution);
        } 

        // Generations 
        while (evaluations < maxEvaluations) {

            // Create the offSpring solutionSet      
            offspringPopulation = new SolutionSet(populationSize);
            Solution[] parents = new Solution[2];
            for (int i = 0; i < (populationSize / 2); i++) {
                if (evaluations < maxEvaluations) {
                    //obtain parents
                    parents[0] = (Solution) tournmentSelOperator.execute(population);
                    parents[1] = (Solution) tournmentSelOperator.execute(population);
                    Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);
                    mutationOperator.execute(offSpring[0]);
                    mutationOperator.execute(offSpring[1]);
                    problem_.evaluate(offSpring[0]);
                    problem_.evaluateConstraints(offSpring[0]);
                    problem_.evaluate(offSpring[1]);
                    problem_.evaluateConstraints(offSpring[1]);
                    offspringPopulation.add(offSpring[0]);
                    offspringPopulation.add(offSpring[1]);
                    evaluations += 2;
                }                            
            } 
            
            // Create the solutionSet union of solutionSet and offSpring
            union = ((SolutionSet) population).union(offspringPopulation);

            // Ranking the union
            StrictNonDominatedSet stricNDS = new StrictNonDominatedSet(union);
            
            int remain = populationSize;
            population.clear();

            if(stricNDS.getNonDominatedSet().size() < remain){
                for(int k = 0; k < stricNDS.getNonDominatedSet().size(); k++) {
                    population.add(stricNDS.getNonDominatedSet().get(k));
                }
                remain -= stricNDS.getNonDominatedSet().size();
                for(int k = 0; k < remain; k++) {
                    Solution selected = tournmentSelOperator.noReplacementTournament(stricNDS.getDominatedSet());
                    population.add(selected);
                }
            }
            else if(stricNDS.getNonDominatedSet().size() > populationSize){
                // Add the extremes of the PF                
                for(int k = 0; k < remain && k < problem_.getNumberOfObjectives(); k++) {
                    Solution selected = getBestSolutionAtObjective(stricNDS.getNonDominatedSet(), k);
                    stricNDS.getNonDominatedSet().remove(selected);
                    population.add(selected);
                    remain--;
                }
                for(int k = 0; k < remain; k++){
                    Solution selected = tournmentSelOperator.noReplacementTournament(stricNDS.getNonDominatedSet());
                    population.add(selected);
                }
            }
            else{
                for(int k = 0; k < stricNDS.getNonDominatedSet().size(); k++) {
                    population.add(stricNDS.getNonDominatedSet().get(k));
                }
            }
        }

        // Return as output parameter the required evaluations
        setOutputParameter("evaluations", requiredEvaluations);

        // Return the first non-dominated front
        Ranking ranking = new Ranking(population);
        ranking.getSubfront(0).printFeasibleFUN("FUN" + getName()) ;

        return ranking.getSubfront(0);
    } 

    private Solution getBestSolutionAtObjective(List<Solution> solutionList, int objDim) {
        Solution bestSolution = null;
        double min = Double.MAX_VALUE;
        for(Solution solution : solutionList){
            if(solution.getObjective(objDim) < min){
                min = solution.getObjective(objDim);
                bestSolution = solution;
            }
        }
        return bestSolution;
    }
}
