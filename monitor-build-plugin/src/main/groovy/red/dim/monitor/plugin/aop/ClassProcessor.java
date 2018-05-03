package red.dim.monitor.plugin.aop;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.util.ArrayList;
import java.util.List;

import red.dim.monitor.plugin.MonitorExtension;
import red.dim.monitor.plugin.entity.MonitorClass;

/**
 * Created by dim on 17/10/14.
 */
public class ClassProcessor {

    private List<MonitorClass> monitorClasses = new ArrayList<>();
    private MonitorExtension monitorExtension;

    public ClassProcessor(MonitorExtension monitorExtension) {
        this.monitorExtension = monitorExtension;
    }

    public byte[] process(byte[] src) {
        try {
            ClassReader classReader = new ClassReader(src);
            ClassWriter cw = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
            MonitorClassVisitor monitorClassVisitor = new MonitorClassVisitor(monitorExtension, cw);
            classReader.accept(monitorClassVisitor, ClassReader.EXPAND_FRAMES);
            MonitorClass monitorClass = monitorClassVisitor.getMonitorClass();
            if (!monitorClass.isEmpty()) {
                monitorClasses.add(monitorClass);
            }
            return cw.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return src;
        }
    }

    public List<MonitorClass> getMonitorClasses() {
        return monitorClasses;
    }
}
