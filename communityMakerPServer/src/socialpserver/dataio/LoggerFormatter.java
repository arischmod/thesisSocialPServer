/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socialpserver.dataio;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * It determines the format of the output of the Loggers. 
 * Tow Loggers are used, one for debugging info
 * and one for the algorithm output info
 * Our formatter creates an output of a single line with a single msg
 * the Date-Time default features are removed
 */
public class LoggerFormatter extends Formatter {

    @Override
    public String format(final LogRecord rec) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatMessage(rec)).append(System.getProperty("line.separator"));
        if (null != rec.getThrown()) {
            sb.append("Throwable occurred: "); //$NON-NLS-1$
            Throwable t = rec.getThrown();
            PrintWriter pw = null;
            try {
                StringWriter sw = new StringWriter();
                pw = new PrintWriter(sw);
                t.printStackTrace(pw);
                sb.append(sw.toString());
            } finally {
                if (pw != null) {
                    try {
                        pw.close();
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        }
        return sb.toString();
    }
}
