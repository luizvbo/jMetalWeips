package jmetal.metaheuristics.weips;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import jmetal.core.*;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.Tournament;
import jmetal.problems.ProblemFactory;
import jmetal.problems.ZDT.ZDT3;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Configuration;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.util.StrictlyNonDominatedSet;
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
    public static String p_extremesElitism = "extremesElitism";
    
    public static String po_distributionIndex = "distributionIndex";
    public static String po_probability = "probability";
    
    protected Tournament tournmentSelOperator = null;
    protected boolean useExtremeElitism = false;
    
    /**
     * Constructor
     * @param problem Problem to solve
     */
    public Weips(Problem problem) {
        super (problem);
    }

    public void build(){
        int numberOfObjectives = problem_.getNumberOfObjectives();
        int numberWeights = (Integer) getInputParameter(p_numWeights);
        
        HashMap  parameters = new HashMap();
        parameters.put(Tournament.p_tournamentSize, (Integer) getInputParameter(p_tournamentSize)) ;
        parameters.put(Tournament.p_comparator, new WeipsComparator(getWeightMatrix(numberOfObjectives, numberWeights)));
        
        tournmentSelOperator = new Tournament(parameters);
        if(getInputParameter(p_extremesElitism) != null){
            useExtremeElitism = (Boolean) getInputParameter(p_extremesElitism);
        }
    }
    
    public abstract String getName();
    
    public abstract String getDescription();
    
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
            /******************************************************************
             *                        Replacement Stage                       *
             ******************************************************************/            
            
            // Create the solutionSet union of solutionSet and offSpring
            union = ((SolutionSet) population).union(offspringPopulation);

            // Ranking the union
            StrictlyNonDominatedSet stricNDS = new StrictlyNonDominatedSet(union);
            
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
                // Add the extremes of the PF (only if useExtremeElitism = true)
                if(useExtremeElitism){
                    for(int k = 0; k < remain && k < problem_.getNumberOfObjectives(); k++) {
                        Solution selected = getBestSolutionAtObjective(stricNDS.getNonDominatedSet(), k);
                        stricNDS.getNonDominatedSet().remove(selected);
                        population.add(selected);
                        remain--;
                    }
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
        ranking.getSubfront(0).printFeasibleFUN("FUN_" + getName()) ;

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
    
    /**
     * @param args Command line arguments.
     * @throws JMException 
     * @throws IOException 
     * @throws SecurityException 
     * Usage: three options
     *      - jmetal.metaheuristics.weips.Weips
     *      - jmetal.metaheuristics.weips.Weips problemName
     *      - jmetal.metaheuristics.weips.Weips problemName paretoFrontFile
     */
    public static void main(String [] args) throws JMException, SecurityException, 
                                                    IOException, ClassNotFoundException {
        Problem   problem   ; // The problem to solve
        Algorithm algorithm ; // The algorithm to use
        Operator  crossover ; // Crossover operator
        Operator  mutation  ; // Mutation operator

        HashMap  parameters ; // Operator parameters

        QualityIndicator indicators ; // Object to get quality indicators

//        public static Logger      logger_ ;      // Logger object
//        public static FileHandler fileHandler_ ; // FileHandler object
        
        String outputDir = "/tmp/weips/";

        // Logger object and file to store log messages
        Logger logger      = Configuration.logger_ ;
        FileHandler fileHandler = new FileHandler(outputDir + "Weips_main.log"); 
        logger.addHandler(fileHandler) ;

        indicators = null ;
        if (args.length == 1) {
            Object [] params = {"Real"};
            problem = (new ProblemFactory()).getProblem(args[0],params);
        } // if
        else if (args.length == 2) {
            Object [] params = {"Real"};
            problem = (new ProblemFactory()).getProblem(args[0],params);
            indicators = new QualityIndicator(problem, args[1]) ;
        } // if
        else { // Default problem
            //problem = new Kursawe("Real", 3);
            //problem = new Kursawe("BinaryReal", 3);
            //problem = new Water("Real");
            problem = new ZDT3("ArrayReal", 30);
            //problem = new ConstrEx("Real");
            //problem = new DTLZ1("Real");
            //problem = new OKA2("Real") ;
        }

        algorithm = new Rawps(problem);

        // Algorithm parameters
        algorithm.setInputParameter("populationSize",100);
        algorithm.setInputParameter("maxEvaluations",25000);
        algorithm.setInputParameter(Weips.p_numWeights, 100);

        // Mutation and Crossover for Real codification 
        parameters = new HashMap() ;
        parameters.put("probability", 0.9) ;
        parameters.put("distributionIndex", 20.0) ;
        crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   

        parameters = new HashMap() ;
        parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
        parameters.put("distributionIndex", 20.0) ;
        mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    

        // Add the operators to the algorithm
        algorithm.addOperator("crossover",crossover);
        algorithm.addOperator("mutation",mutation);

        // Add the indicator object to the algorithm
        algorithm.setInputParameter("indicators", indicators) ;

        // Execute the Algorithm
        long initTime = System.currentTimeMillis();
        SolutionSet population = algorithm.execute();
        long estimatedTime = System.currentTimeMillis() - initTime;

        // Result messages 
        logger.info("Total execution time: "+estimatedTime + "ms");
        logger.info("Variables values have been writen to file VAR");
        population.printVariablesToFile(outputDir + "VAR");    
        logger.info("Objectives values have been writen to file FUN");
        population.printObjectivesToFile(outputDir + "FUN");

        if (indicators != null) {
            logger.info("Quality indicators") ;
            logger.info("Hypervolume: " + indicators.getHypervolume(population)) ;
            logger.info("GD         : " + indicators.getGD(population)) ;
            logger.info("IGD        : " + indicators.getIGD(population)) ;
            logger.info("Spread     : " + indicators.getSpread(population)) ;
            logger.info("Epsilon    : " + indicators.getEpsilon(population)) ;  

            int evaluations = ((Integer)algorithm.getOutputParameter("evaluations")).intValue();
            logger.info("Speed      : " + evaluations + " evaluations") ;      
        } 
    } 
}
