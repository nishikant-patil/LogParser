package foo.bar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static Pattern expNamePattern = Pattern.compile(".+Exception.+");
    private static Pattern stackTracePattern = Pattern.compile("\\tat.+");
    private static Map<LoggedException, Integer> loggedExceptions = new HashMap<>();

    public static void parseLog(File file) throws FileNotFoundException {
        try(Scanner logFileScanner = new Scanner(file)) {
            boolean searchForName = true;
            LoggedException loggedException = null;
            while (logFileScanner.hasNext()) {
                String line = logFileScanner.nextLine();
                if (searchForName) {
                    Matcher nameMatcher = expNamePattern.matcher(line);
                    if (nameMatcher.find()) {
                        loggedException = new LoggedException(nameMatcher.group());
                        searchForName = false;
                    }
                } else {
                    Matcher stackTraceMatcher = stackTracePattern.matcher(line);
                    if (stackTraceMatcher.find()) {
                        loggedException.getStackTrace().add(stackTraceMatcher.group());
                    } else {
                        if (loggedExceptions.containsKey(loggedException)) {
                            int count = loggedExceptions.get(loggedException);
                            loggedExceptions.put(loggedException, ++count);
                        } else {
                            loggedExceptions.put(loggedException, 1);
                        }
                        searchForName = true;
                    }
                }
            }
        }
    }

    public static void generateLogReport(String logDirPath) throws FileNotFoundException {
        File logDir = new File(logDirPath);
        for(File logFile : logDir.listFiles()){
            if(logFile.isFile()){
                parseLog(logFile);
                printReport(logFile.getName());
                loggedExceptions.clear();
            }
        }
    }

    private static void printReport(String file) {
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Exception details for file: " + file);
        for(Map.Entry<LoggedException, Integer> entry : loggedExceptions.entrySet()){
            System.out.println("Exception " + entry.getKey().getName() + " found " + entry.getValue() + " times. Following is the stack trace");
            for(String trace : entry.getKey().getStackTrace()){
                System.out.println(trace);
            }
        }
        System.out.println("-------------------------------------------------------------------------");
    }

    public static void main(String... args) throws FileNotFoundException {
        generateLogReport(args.length==0 ? "src" : args[0]);
    }
}
