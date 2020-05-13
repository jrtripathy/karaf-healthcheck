package com.healthedge.connector.healthcheck.command;

import java.io.*;
import java.util.List;

public class SystemCommandExecutor
{
  private List<String> commandInformation;
  private String adminPassword;
  private ThreadedStreamHandler inputStreamHandler;
  private ThreadedStreamHandler errorStreamHandler;
  
  /**
   * Pass in the system command you want to run as a List of Strings, as shown here:
   * 
   * List<String> commands = new ArrayList<String>();
   * commands.add("/sbin/ping");
   * commands.add("-c");
   * commands.add("5");
   * commands.add("www.google.com");
   * SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
   * commandExecutor.executeCommand();
   *
   * @param commandInformation The command you want to run.
   */
  public SystemCommandExecutor(final List<String> commandInformation)
  {
    if (commandInformation==null) throw new NullPointerException("The commandInformation is required.");
    this.commandInformation = commandInformation;
    this.adminPassword = null;
  }

  public int executeCommand()
  throws IOException, InterruptedException
  {
    int exitValue = -99;

    try
    {
      ProcessBuilder pb = new ProcessBuilder(commandInformation);
      Process process = pb.start();

      OutputStream stdOutput = process.getOutputStream();
      
      InputStream inputStream = process.getInputStream();
      InputStream errorStream = process.getErrorStream();

      inputStreamHandler = new ThreadedStreamHandler(inputStream, stdOutput, adminPassword);
      errorStreamHandler = new ThreadedStreamHandler(errorStream);

      inputStreamHandler.start();
      errorStreamHandler.start();

      exitValue = process.waitFor();
 
      inputStreamHandler.interrupt();
      errorStreamHandler.interrupt();
      inputStreamHandler.join();
      errorStreamHandler.join();
    }
    catch (IOException e)
    {
      throw e;
    }
    catch (InterruptedException e)
    {
      throw e;
    }
    finally
    {
      return exitValue;
    }
  }

  /**
   * Get the standard output (stdout) from the command you just exec'd.
   */
  public List<String> getStandardOutputFromCommand()
  {
    return inputStreamHandler.getOutputBuffer();
  }

  /**
   * Get the standard error (stderr) from the command you just exec'd.
   */
  public List<String> getStandardErrorFromCommand()
  {
    return errorStreamHandler.getOutputBuffer();
  }


}







