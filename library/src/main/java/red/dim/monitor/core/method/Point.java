package red.dim.monitor.core.method;

import java.util.Arrays;
/**
 * Created by dim on 18/03/12.
 */
public class Point {
    private Object thisObject;
    private Object[] arg;
    private Object returnObject;

    public Object getThisObject() {
        return thisObject;
    }

    public void setThisObject(Object thisObject) {
        this.thisObject = thisObject;
    }

    public Object[] getArg() {
        return arg;
    }

    public void setArg(Object[] arg) {
        this.arg = arg;
    }

    public Object getReturnObject() {
        return returnObject;
    }

    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public String toString() {
        return "Point{" +
                "thisObject=" + thisObject +
                ", arg=" + Arrays.toString(arg) +
                ", returnObject=" + returnObject +
                '}';
    }
}
