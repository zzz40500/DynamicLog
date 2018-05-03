package red.dim.monitor.core.method;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dim on 18/03/12.
 */
public class Aspect {
    @SerializedName("target")
    public String target;
    @SerializedName("advices")
    public List<Advice> advices;
}
