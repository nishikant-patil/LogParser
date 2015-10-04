package foo.bar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static Pattern expNamePattern = Pattern.compile(".+Exception.+");
    private static Pattern stackTracePattern = Pattern.compile("\\tat.+");
    private static Map<LoggedException, Integer> loggedExceptions = new HashMap<>();
    private static boolean printStackTrace;
    private static int stackTraceDepth;
    private static String logDirPath;

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

    public static void generateLogReport() throws FileNotFoundException {
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
            System.out.print("Exception " + entry.getKey().getName() + " found " + entry.getValue() + " times. ");
            if(printStackTrace) {
                System.out.println("Following is the stack trace:");
                for (String trace : entry.getKey().getStackTrace().subList(0, stackTraceDepth)) {
                    System.out.println(trace);
                }
            } else {
                System.out.println("");
            }
        }
        System.out.println("-------------------------------------------------------------------------");
    }

    public static void main(String... args) throws FileNotFoundException {
        if(args.length==3){
            logDirPath = args[0];
            printStackTrace = Boolean.parseBoolean(args[1]);
            stackTraceDepth = Integer.parseInt(args[2]);
        } else {
            logDirPath = "src";
            printStackTrace = true;
            stackTraceDepth = 2;
        }
        generateLogReport();
    }
}
