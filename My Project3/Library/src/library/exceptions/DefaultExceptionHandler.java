/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.exceptions;

import java.lang.Thread.UncaughtExceptionHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Taufiqun Nur Farid
 */
public class DefaultExceptionHandler implements UncaughtExceptionHandler {

    private final static Logger LOGGER = LogManager.getLogger(DefaultExceptionHandler.class.getName());

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        LOGGER.log(Level.ERROR, "Exception occurred {}", ex);
    }
}
