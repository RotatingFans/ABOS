import javax.swing.*;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * LogToFile class
 * This class is intended to be use with the default logging class of java
 * It save the log in an XML file  and display a friendly message to the user
 *
 * @author Ibrabel <ibrabel@gmail.com>
 */
public class LogToFile {

    protected static final Logger logger = Logger.getLogger("MYLOG");
    /* Severities
      SEVERE
      WARNING
      INFO
      CONFIG
      FINE
      FINER
      FINEST
     */
    /**
     * log Method
     * enable to log all exceptions to a file and display user message on demand
     *
     * @param ex
     * @param level
     * @param msg
     */
    public static void log(Exception ex, Severity level, String msg) {

        FileHandler fh = null;
        try {
            // Create a file with append set to  true. Also limit the file to 10000 bytes
            fh = new FileHandler("log.txt", 10000, 1, true);

            // Set the file format to a simple text file.
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);

            switch (level) {
                case SEVERE:
                    logger.log(Level.SEVERE, msg, ex);
                    if (!msg.isEmpty()) {
                        JOptionPane.showMessageDialog(null, msg,
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case WARNING:
                    logger.log(Level.WARNING, msg, ex);
                    if (!msg.isEmpty()) {
                        JOptionPane.showMessageDialog(null, msg,
                                "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                    break;
                case INFO:
                    logger.log(Level.INFO, msg, ex);
                    if (!msg.isEmpty()) {
                        JOptionPane.showMessageDialog(null, msg,
                                "Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                    break;
                case CONFIG:
                    logger.log(Level.CONFIG, msg, ex);
                    break;
                case FINE:
                    logger.log(Level.FINE, msg, ex);
                    break;
                case FINER:
                    logger.log(Level.FINER, msg, ex);
                    break;
                case FINEST:
                    logger.log(Level.FINEST, msg, ex);
                    break;
                //default:
                //    logger.log(Level.CONFIG, msg, ex);
                //    break;
            }
        } catch (IOException | SecurityException ex1) {
            logger.log(Level.SEVERE, null, ex1);
        } finally {
            if (fh != null) {
                fh.close();
            }
        }
    }
}