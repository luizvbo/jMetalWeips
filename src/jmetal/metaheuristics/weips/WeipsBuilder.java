package jmetal.metaheuristics.weips;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.problems.ProblemFactory;
import jmetal.problems.ZDT.ZDT3;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Configuration;
import jmetal.util.JMException;


/**
 *
 * @author Luiz Otavio V. B. Oliveira <luiz.vbo@gmail.com>
 */
public class WeipsBuilder{
    public enum WeipsVariant {Rawps, Unpas, Grips, StratGrips}

    public static Logger      logger_ ;      // Logger object
    public static FileHandler fileHandler_ ; // FileHandler object

    /**
    * @param args Command line arguments.
    * @throws JMException 
    * @throws IOException 
    * @throws SecurityException 
    * Usage: three options
    *      - jmetal.metaheuristics.nsgaII.NSGAII_main
    *      - jmetal.metaheuristics.nsgaII.NSGAII_main problemName
    *      - jmetal.metaheuristics.nsgaII.NSGAII_main problemName paretoFrontFile
    */
    public static void main(String [] args) throws JMException, SecurityException, 
                                                    IOException, ClassNotFoundException {
        Problem   problem   ; // The problem to solve
        Algorithm algorithm ; // The algorithm to use
        Operator  crossover ; // Crossover operator
        Operator  mutation  ; // Mutation operator

        HashMap  parameters ; // Operator parameters

        QualityIndicator indicators ; // Object to get quality indicators

        // Logger object and file to store log messages
        logger_      = Configuration.logger_ ;
        fileHandler_ = new FileHandler("NSGAII_main.log"); 
        logger_.addHandler(fileHandler_) ;

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

        algorithm = new NSGAII(problem);
        //algorithm = new ssNSGAII(problem);

        // Algorithm parameters
        algorithm.setInputParameter("populationSize",100);
        algorithm.setInputParameter("maxEvaluations",25000);

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
        logger_.info("Total execution time: "+estimatedTime + "ms");
        logger_.info("Variables values have been writen to file VAR");
        population.printVariablesToFile("VAR");    
        logger_.info("Objectives values have been writen to file FUN");
        population.printObjectivesToFile("FUN");

        if (indicators != null) {
            logger_.info("Quality indicators") ;
            logger_.info("Hypervolume: " + indicators.getHypervolume(population)) ;
            logger_.info("GD         : " + indicators.getGD(population)) ;
            logger_.info("IGD        : " + indicators.getIGD(population)) ;
            logger_.info("Spread     : " + indicators.getSpread(population)) ;
            logger_.info("Epsilon    : " + indicators.getEpsilon(population)) ;  

            int evaluations = ((Integer)algorithm.getOutputParameter("evaluations")).intValue();
            logger_.info("Speed      : " + evaluations + " evaluations") ;      
        } 
    } 
} 
