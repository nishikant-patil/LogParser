package foo.bar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static Pattern expNamePattern = Pattern.compile(".+Exception.+");
    private static Pattern stackTracePattern = Pattern.compile("\\tat.+");
    private static Set<LoggedException> loggedExceptions = new HashSet<>();


    public static void generateExceptionLog(){
        try {
            System.out.println("Hello world-start");
            System.out.println(1 / 0);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Hello world-end");
    }

    public static void parseLog(File file) throws FileNotFoundException {
        Scanner logFileScanner = new Scanner(file);
        boolean searchForName = true;
        LoggedException loggedException = null;
        while(logFileScanner.hasNext()){
            String line = logFileScanner.nextLine();
            if(searchForName){
                Matcher nameMatcher = expNamePattern.matcher(line);
                if(nameMatcher.find()){
                    loggedException = new LoggedException(nameMatcher.group());
                    searchForName = false;
                }
            } else {
                Matcher stackTraceMatcher = stackTracePattern.matcher(line);
                if(stackTraceMatcher.find()){
                    loggedException.getStackTrace().add(stackTraceMatcher.group());
                } else {
                    loggedExceptions.add(loggedException);
                    searchForName=true;
                }
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        parseLog(new File("src\\sample-log-file.log"));
        System.out.println(loggedExceptions.size());
    }
}
