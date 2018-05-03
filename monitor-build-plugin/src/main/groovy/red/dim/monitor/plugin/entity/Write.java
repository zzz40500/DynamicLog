package red.dim.monitor.plugin.entity;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
/**
 * Created by dim on 17/10/14.
 */
public class Write {
    private List<MonitorClass> monitorClasses;
    private File tarFile;

    public Write(List<MonitorClass> monitorClasses, File tarFile) {
        this.monitorClasses = monitorClasses;
        this.tarFile = tarFile;
    }

    public  void write() {
        OutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(tarFile);
            for (MonitorClass monitorClass : monitorClasses) {
                IOUtils.write(monitorClass.className + '\n', fileOutputStream);
                for (MonitorMethod monitorMethod : monitorClass.getMonitorMethods()) {
                    IOUtils.write(Constant.METHOD_PREFIX + monitorMethod.getMethod() + Constant.METHOD_MAPPING + monitorMethod.getMethodId() + "\n", fileOutputStream);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                IOUtils.closeQuietly(fileOutputStream);
            }
        }

    }
}
