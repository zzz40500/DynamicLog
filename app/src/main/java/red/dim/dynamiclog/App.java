package red.dim.dynamiclog;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import red.dim.monitor.core.method.IMethodMonitor;
import red.dim.monitor.core.method.Point;
import red.dim.monitor.core.Monitor;

/**
 * Created by dim on 18/03/12.
 */
public class App extends Application {
    private static final String TAG = "dim";

    @Override
    public void onCreate() {
        super.onCreate();
        List<IMethodMonitor> methodListeners = new ArrayList<>();
        methodListeners.add(new IMethodMonitor() {
            @Override
            public void methodEnter(Point point) {
                Log.d(TAG, "methodEnter: " + point);
            }

            @Override
            public void methodReturn(Point point, int methodId) {
                Log.d(TAG, "methodReturn: " + point + " methodId: " + methodId);
            }
        });
        Monitor.setup(methodListeners);
    }
}
