package foo.bar;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain model for a logged exception.
 */
public class LoggedException {
    private String name;
    private List<String> stackTrace = new ArrayList<>();

    public List<String> getStackTrace(){
        return stackTrace;
    }

    public LoggedException(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return "LoggedException{" +
                "name='" + name + '\'' +
                ", stackTrace=" + stackTrace +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoggedException that = (LoggedException) o;
        return name.equals(that.name) && stackTrace.equals(that.stackTrace);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + stackTrace.hashCode();
        return result;
    }
}
