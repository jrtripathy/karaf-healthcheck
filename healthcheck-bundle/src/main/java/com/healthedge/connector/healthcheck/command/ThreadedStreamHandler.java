package com.healthedge.connector.healthcheck.command;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class ThreadedStreamHandler extends Thread
{
  private static final Logger logger = Logger.getLogger(ThreadedStreamHandler.class.getName());
  InputStream inputStream;
  String adminPassword;
  OutputStream outputStream;
  PrintWriter printWriter;
  List<String> outputBuffer = new ArrayList<>();
  private boolean sudoIsRequested = false;

  ThreadedStreamHandler(InputStream inputStream)
  {
    this.inputStream = inputStream;
  }

  ThreadedStreamHandler(InputStream inputStream, OutputStream outputStream, String adminPassword)
  {
    this.inputStream = inputStream;
    this.outputStream = outputStream;
    this.printWriter = new PrintWriter(outputStream);
    this.adminPassword = adminPassword;
    this.sudoIsRequested = true;
  }
  
  public void run()
  {
    if (sudoIsRequested)
    {
      //doSleep(500);
      printWriter.println(adminPassword);
      printWriter.flush();
    }

    BufferedReader bufferedReader = null;
    try
    {
      bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      String line = null;
      while ((line = bufferedReader.readLine()) != null)
      {
        outputBuffer.add(line.trim());
      }
    }
    catch (IOException ioe)
    {
      logger.log(Level.SEVERE, "Error in ThreadedStreamHandler: ", ioe);
    }
    catch (Throwable t)
    {
      logger.log(Level.SEVERE, "Error in ThreadedStreamHandler: ", t);
    }
    finally
    {
      try
      {
        bufferedReader.close();
      }
      catch (IOException e)
      {
        // ignore this one
      }
    }
  }
  
  private void doSleep(long millis)
  {
    try
    {
      Thread.sleep(millis);
    }
    catch (InterruptedException e)
    {
      // ignore
    }
  }
  
  public List<String> getOutputBuffer()
  {
    return outputBuffer;
  }

}









