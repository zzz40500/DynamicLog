package red.dim.monitor.core.method;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dim on 18/03/12.
 */
public class Advice {
    @SerializedName("before")
    public boolean before = false;
    @SerializedName("after")
    public boolean after = false;
    @SerializedName("methodId")
    public int methodId;
}
