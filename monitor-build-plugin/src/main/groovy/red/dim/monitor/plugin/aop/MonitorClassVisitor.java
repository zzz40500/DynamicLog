package red.dim.monitor.plugin.aop;


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import red.dim.monitor.plugin.MonitorExtension;
import red.dim.monitor.plugin.entity.MonitorClass;
import red.dim.monitor.plugin.entity.MonitorMethod;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by dim on 17/10/14.
 */
public class MonitorClassVisitor extends ClassVisitor {
    private boolean handle = false;
    private String className;

    private MonitorExtension monitorExtension;
    private MonitorClass monitorClass = new MonitorClass();

    private int methodId = 0;

    public MonitorClass getMonitorClass() {
        return monitorClass;
    }

    public MonitorClassVisitor(MonitorExtension monitorExtension, ClassWriter cw) {
        super(Opcodes.ASM5, cw);
        this.monitorExtension = monitorExtension;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        for (String temp : monitorExtension.getBlackPackageMap()) {
            if (name.startsWith(temp)) {
                handle = false;
                super.visit(version, access, name, signature, superName, interfaces);
                return;
            }
        }
        for (String temp : monitorExtension.getPackageMap()) {
            if (name.startsWith(temp)) {
                handle = true;
                break;
            }
        }
        if (name.matches(".*/R(\\$.+)?")) {
            handle = false;
        }
        className = name;
        monitorClass.setClassName(className);
        super.visit(version, access, name, signature, superName, interfaces);
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        if (handle) {
            if ((access & ACC_ABSTRACT) != 0 || (access & ACC_SYNTHETIC) != 0) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
            String sb = convertSignature(name, desc);
            MonitorMethod monitorMethod = new MonitorMethod();
            monitorMethod.setMethod(sb);
            monitorMethod.setMethodId(methodId++);
            monitorClass.addMethod(monitorMethod);
            return new MonitorMethodVisitor(className, access, name, desc, monitorMethod.getMethodId(), super.visitMethod(access, name, desc, signature, exceptions));
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    private String convertSignature(String name, String desc) {
        Type method = Type.getType(desc);
        StringBuilder sb = new StringBuilder();
        sb.append(method.getReturnType().getClassName()).append(" ")
                .append(name);
        sb.append("(");
        for (int i = 0; i < method.getArgumentTypes().length; i++) {
            sb.append(method.getArgumentTypes()[i].getClassName());
            if (i != method.getArgumentTypes().length - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void visitSource(String source, String debug) {
        super.visitSource(source, debug);
        if (handle) {
            FieldVisitor fieldVisitor = visitField(ACC_PUBLIC + ACC_STATIC + ACC_VOLATILE, "s_Monitor_1", "Lred/dim/monitor/core/method/MethodMonitor;", null, null);
            fieldVisitor.visitAnnotation("Landroid/support/annotation/Keep;", false);
            fieldVisitor.visitEnd();
        }
    }

    @Override
    public void visitEnd() {

        super.visitEnd();
    }

}
