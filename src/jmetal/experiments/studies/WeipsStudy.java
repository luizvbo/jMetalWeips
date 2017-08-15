//  StandardStudy.java
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

package jmetal.experiments.studies;

import jmetal.core.Algorithm;
import jmetal.experiments.Experiment;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.*;
import jmetal.experiments.util.Friedman;
import jmetal.util.JMException;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.metaheuristics.weips.Weips;
import jmetal.util.Configuration;

/**
 * Class implementing a typical experimental study. Five algorithms are 
 * compared when solving the ZDT, DTLZ, and WFG benchmarks, and the hypervolume,
 * spread and additive epsilon indicators are used for performance assessment.
 */
public class WeipsStudy extends Experiment {

    /**
     * Configures the algorithms in each independent run
     * @param problemName The problem to solve
     * @param problemIndex
     * @throws ClassNotFoundException 
     */
    public void algorithmSettings(String problemName, 
                                  int problemIndex, 
                                  Algorithm[] algorithm) throws ClassNotFoundException {
        try {
            int numberOfAlgorithms = algorithmNameList_.length;

            HashMap[] parameters = new HashMap[numberOfAlgorithms];

            for (int i = 0; i < numberOfAlgorithms; i++) {
                parameters[i] = new HashMap();
            } // for

            if (!paretoFrontFile_[problemIndex].equals("")) {
                for (int i = 0; i < numberOfAlgorithms; i++)
                    parameters[i].put("paretoFrontFile_", paretoFrontFile_[problemIndex]);
            } // if

            int index = 0;
            
//            algorithm[index++] = new Weips_Settings(problemName, Weips_Settings.eWeipsMethod.RAWPS).configure(parameters[0]);
//            algorithm[index++] = new Weips_Settings(problemName, Weips_Settings.eWeipsMethod.RAWPS).configure(parameters[0]);
//            algorithm[index-1].setInputParameter(Weips.p_extremesElitism, true);
            algorithm[index++] = new Weips_Settings(problemName, Weips_Settings.eWeipsMethod.RAWPS).configure(parameters[0]);
            algorithm[index-1].setInputParameter(Weips.p_tournamentSize, 2);
            algorithm[index++] = new Weips_Settings(problemName, Weips_Settings.eWeipsMethod.RAWPS).configure(parameters[0]);
            algorithm[index-1].setInputParameter(Weips.p_tournamentSize, 2);
            algorithm[index-1].setInputParameter(Weips.p_extremesElitism, true);
            
//            algorithm[index++] = new Weips_Settings(problemName, Weips_Settings.eWeipsMethod.UNPAS).configure(parameters[0]);
//            algorithm[index++] = new Weips_Settings(problemName, Weips_Settings.eWeipsMethod.UNPAS).configure(parameters[0]);
//            algorithm[index-1].setInputParameter(Weips.p_extremesElitism, true);
            algorithm[index++] = new Weips_Settings(problemName, Weips_Settings.eWeipsMethod.UNPAS).configure(parameters[0]);
            algorithm[index-1].setInputParameter(Weips.p_tournamentSize, 2);
            algorithm[index++] = new Weips_Settings(problemName, Weips_Settings.eWeipsMethod.UNPAS).configure(parameters[0]);
            algorithm[index-1].setInputParameter(Weips.p_tournamentSize, 2);
            algorithm[index-1].setInputParameter(Weips.p_extremesElitism, true);
                        
//            algorithm[index++] = new Weips_Settings(problemName, Weips_Settings.eWeipsMethod.GRIPS).configure(parameters[0]);
//            algorithm[index++] = new Weips_Settings(problemName, Weips_Settings.eWeipsMethod.GRIPS).configure(parameters[0]);
//            algorithm[index-1].setInputParameter(Weips.p_extremesElitism, true);
            algorithm[index++] = new Weips_Settings(problemName, Weips_Settings.eWeipsMethod.GRIPS).configure(parameters[0]);
            algorithm[index-1].setInputParameter(Weips.p_tournamentSize, 2);
            algorithm[index++] = new Weips_Settings(problemName, Weips_Settings.eWeipsMethod.GRIPS).configure(parameters[0]);
            algorithm[index-1].setInputParameter(Weips.p_tournamentSize, 2);
            algorithm[index-1].setInputParameter(Weips.p_extremesElitism, true);
            
//            algorithm[index++] = new Weips_Settings(problemName, Weips_Settings.eWeipsMethod.STRATGRIPS).configure(parameters[0]);
//            algorithm[index++] = new Weips_Settings(problemName, Weips_Settings.eWeipsMethod.STRATGRIPS).configure(parameters[0]);
//            algorithm[index-1].setInputParameter(Weips.p_extremesElitism, true);
            algorithm[index++] = new Weips_Settings(problemName, Weips_Settings.eWeipsMethod.STRATGRIPS).configure(parameters[0]);
            algorithm[index-1].setInputParameter(Weips.p_tournamentSize, 2);
            algorithm[index++] = new Weips_Settings(problemName, Weips_Settings.eWeipsMethod.STRATGRIPS).configure(parameters[0]);
            algorithm[index-1].setInputParameter(Weips.p_tournamentSize, 2);
            algorithm[index-1].setInputParameter(Weips.p_extremesElitism, true);
            
//            algorithm[index++] = new NSGAII_Settings(problemName).configure(parameters[0]);
//            algorithm[index++] = new SPEA2_Settings(problemName).configure(parameters[1]);
//            algorithm[index++] = new MOCell_Settings(problemName).configure(parameters[2]);
//            algorithm[index++] = new SMPSO_Settings(problemName).configure(parameters[3]);
//            algorithm[index++] = new GDE3_Settings(problemName).configure(parameters[4]);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(WeipsStudy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(WeipsStudy.class.getName()).log(Level.SEVERE, null, ex);
        } catch  (JMException ex) {
            Logger.getLogger(WeipsStudy.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 

    /**
     * Main method
     * @param args
     * @throws JMException
     * @throws IOException
     */
    public static void main(String[] args) throws JMException, IOException {
        String ouputDir;
        
        if(args.length == 1){
            ouputDir = args[0];
        }
        else
            ouputDir = "/home/luiz/Dados/Trabalho/Pesquisa/Publicacoes/2017/MOGP/results/jmetal_weips/";
        
        // Logger object and file to store log messages
        Logger logger      = Configuration.logger_ ;
        FileHandler fileHandler = new FileHandler(ouputDir + "Weips2DStudy.log"); 
        logger.addHandler(fileHandler) ;

        WeipsStudy exp = new WeipsStudy();

        exp.experimentName_ = "WeipsStudy";
        exp.algorithmNameList_ = new String[]{"rawpsT2", "rawpsT2el", "unpasT2", "unpasT2el", "gripsT2", "gripsT2el", "sgripsT2", "sgripsT2el",
//                                              "RawPS-t3", "RawPS-t3-elit", 
//                                              "UnPaS-t3", "UnPaS-t3-elit",
//                                              "GriPS-t3", "GriPS-t3-elit",
//                                              "StratGriPS-t3", "StratGriPS-t3",  
//                                              "RawPS-t5", "UnPaS-t5", "GriPS-t5", "StratGriPS-t5",
//                                              "SMPSO",
                                              "RawPS-t5-elit", "UnPaS-t5-elit",
                                               "GriPS-t5-elit", "StratGriPS-t5-elit"};
//                                              "NSGAII", "SPEA2", "MOCell", "GDE3"};
        exp.problemList_ = new String[]{
//                                        "DTLZ3", "DTLZ6",
                                        "ZDT1", "ZDT2","ZDT3", "ZDT4","ZDT6",
                                        "WFG1","WFG2","WFG3","WFG4", "WFG5", "WFG6",
                                        "WFG7","WFG8","WFG9", "DTLZ1","DTLZ2",
                                        "DTLZ4","DTLZ5", "DTLZ7"};
        exp.paretoFrontFile_ = new String[]{
//                                            "DTLZ3.2D.pf","DTLZ6.2D.pf",
                                            "ZDT1.pf", "ZDT2.pf","ZDT3.pf",
                                            "ZDT4.pf","ZDT6.pf", "WFG1.2D.pf",
                                            "WFG2.2D.pf","WFG3.2D.pf", "WFG4.2D.pf",
                                            "WFG5.2D.pf","WFG6.2D.pf", "WFG7.2D.pf",
                                            "WFG8.2D.pf","WFG9.2D.pf", "DTLZ1.2D.pf",
                                            "DTLZ2.2D.pf", "DTLZ4.2D.pf",
                                            "DTLZ5.2D.pf", "DTLZ7.2D.pf"};

        exp.indicatorList_ = new String[]{"HV", "SPREAD", "EPSILON", "TIME"};

        int numberOfAlgorithms = exp.algorithmNameList_.length;

        exp.experimentBaseDirectory_ =  ouputDir + exp.experimentName_;
        exp.paretoFrontDirectory_ = "";

        exp.algorithmSettings_ = new Settings[numberOfAlgorithms];

        exp.independentRuns_ = 30;

        exp.initExperiment();

        // Run the experiments
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
//        int numberOfThreads = 1;
//        exp.runExperiment(numberOfThreads) ;

//        exp.generateQualityIndicators() ;

        // Generate latex tables
        exp.generateLatexTables() ;

        // Configure the R scripts to be generated
        int rows  ;
        int columns  ;
        String prefix ;
        String [] problems ;

        // Configuring scripts for ZDT
        rows = 3 ;
        columns = 2 ;
        prefix = new String("ZDT");
        problems = new String[]{"ZDT1", "ZDT2","ZDT3", "ZDT4","ZDT6"} ;

        exp.generateRBoxplotScripts(rows, columns, problems, prefix, false, exp) ;
        exp.generateRWilcoxonScripts(problems, prefix, exp) ;

        // Configure scripts for DTLZ
        rows = 3 ;
        columns = 3 ;
        prefix = new String("DTLZ");
        problems = new String[]{"DTLZ1","DTLZ2","DTLZ4","DTLZ5",
                                "DTLZ7"} ; // "DTLZ3", "DTLZ6",

        exp.generateRBoxplotScripts(rows, columns, problems, prefix, false, exp) ;
        exp.generateRWilcoxonScripts(problems, prefix, exp) ;

        // Configure scripts for WFG
        rows = 3 ;
        columns = 3 ;
        prefix = new String("WFG");
        problems = new String[]{"WFG1","WFG2","WFG3","WFG4","WFG5","WFG6",
        "WFG7","WFG8","WFG9"} ;

        exp.generateRBoxplotScripts(rows, columns, problems, prefix, false, exp) ;
        exp.generateRWilcoxonScripts(problems, prefix, exp) ;

        // Applying Friedman test
        Friedman test = new Friedman(exp);
        test.executeTest("TIME");
        test.executeTest("EPSILON");
        test.executeTest("HV");
        test.executeTest("SPREAD");
    } 
} 


