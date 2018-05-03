package red.dim.dynamiclog;

import android.util.Log;
/**
 * Created by dim on 18/03/12.
 */
public class TestMethod {

    private static final String TAG = "TestMethod";

    public void method1(long time) {
        Log.d(TAG, "method1: " + time);
    }

    public int method2(int count) {
        Log.d(TAG, "method2: ");
        return count + 1;
    }

    public long method3(Integer integer) {
        Log.d(TAG, "method3 ");
        return integer * 1000;
    }

}
