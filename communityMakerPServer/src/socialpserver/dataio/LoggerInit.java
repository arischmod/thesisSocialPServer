/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socialpserver.dataio;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import static socialpserver.SocialPServer.algorithmOutputLogger;
import static socialpserver.SocialPServer.socialPServerOutputLogger;

/**
 * Necessary steps for the initialization of Loggers. It determines the output
 * target of each Logger and also the output format. Tow Loggers are used, one
 * for debugging info and one for the algorithm output info
 */
public class LoggerInit {

    public static void initLogger() {
        // set Hierarchy so the algorithmic Infos are also stored in the full-SocialPServer-Logger
        algorithmOutputLogger.setParent(socialPServerOutputLogger);

        LogManager.getLogManager().reset();  // used to stop the default ConsoleHandler

        LoggerFormatter formatter = new LoggerFormatter(); // create a new Custom Formatter

        ConsoleHandler consoleHandler = new ConsoleHandler();  // create ConsoleHandler
        consoleHandler.setFormatter(formatter);  // use the Custom formatter for the output
        algorithmOutputLogger.addHandler(consoleHandler);  // add consoleHandler        
        //socialPServerOutput.addHandler(consoleHandler);  // add consoleHandler

        FileHandler fhAlgorithm, fhSocialPServer; // create FileHandler                
        try {
            // This block configure the logger with handler and formatter
            fhAlgorithm = new FileHandler("./algorithmOutput.log");
            fhAlgorithm.setFormatter(formatter);  // use the Custom formatter for the output
            algorithmOutputLogger.addHandler(fhAlgorithm);   // add FileHandler

            fhSocialPServer = new FileHandler("./socialPServerOutput.log");
            fhSocialPServer.setFormatter(formatter);  // use the Custom formatter for the output
            socialPServerOutputLogger.addHandler(fhSocialPServer);   // add FileHandler

        } catch (SecurityException | IOException ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
