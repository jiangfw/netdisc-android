package com.fuwei.android.libcommon.logger;

/**
 * @author Orhan Obut
 */
public enum LogLevel {

  /**
   * No log will be printed
   */
  NONE(0),


  /**
   * Release log
   */
  RELEASE(1),


  /**
   * Prints all logs
   */
  FULL(2);

  LogLevel(int level){
    loglevel = level;
  }

  public boolean canLog(LogLevel loglevel){
    return this.loglevel >= loglevel.loglevel;
  }

  int loglevel;
}
