package red.dim.monitor.plugin.aop;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import static org.objectweb.asm.Type.getType;
/**
 * Created by dim on 17/10/14.
 */
public class MonitorMethodVisitor extends AdviceAdapter {
    private final String className;
    private final Integer methodId;
    boolean isStaticMethod = false;
    private int pointLocalIndex;

    public MonitorMethodVisitor(String className, int access, String name, String desc, Integer methodId, MethodVisitor mv) {
        super(ASM5, mv, access, name, desc);
        isStaticMethod = (access & ACC_STATIC) != 0;
        this.className = className;
        this.methodId = methodId;
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        createLocalPoint();

        Label label = startIf();
        MonitorIsNull(label);
        methodMonitorIsHotMethodEnter(label);
        {
            newPoint();
            pointSetThisObject();
            pointSetArg();
            pointMethodEnter();
        }

        endIf(label);
    }

    private void MonitorIsNull(Label l2) {
        mv.visitFieldInsn(GETSTATIC, className, "s_Monitor_1", "Lred/dim/monitor/core/method/MethodMonitor;");
        mv.visitJumpInsn(IFNULL, l2);
    }

    private void createLocalPoint() {
        visitInsn(ACONST_NULL);
        pointLocalIndex = newLocal(getType("Lred/dim/monitor/core/method/Point;"));
        mv.visitVarInsn(Opcodes.ASTORE, pointLocalIndex);
    }

    private void methodMonitorIsHotMethodEnter(Label label) {
        mv.visitFieldInsn(GETSTATIC, className, "s_Monitor_1", "Lred/dim/monitor/core/method/MethodMonitor;");
        mv.visitLdcInsn(methodId);
        mv.visitMethodInsn(INVOKEVIRTUAL, "red/dim/monitor/core/method/MethodMonitor", "hotMethodEnter", "(I)Z", false);
        mv.visitJumpInsn(IFEQ, label);

    }

    private void pointMethodEnter() {
        mv.visitFieldInsn(GETSTATIC, className, "s_Monitor_1", "Lred/dim/monitor/core/method/MethodMonitor;");
        mv.visitVarInsn(ALOAD, pointLocalIndex);
        mv.visitMethodInsn(INVOKEVIRTUAL, "red/dim/monitor/core/method/MethodMonitor", "methodEnter", "(Lred/dim/monitor/core/method/Point;)V", false);
    }


    private void pointSetArg() {
        Type[] params = Type.getArgumentTypes(methodDesc);
        mv.visitLdcInsn(new Integer(params.length));
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        int paramsIndex = newLocal(getType("[Ljava/lang/Object;"));
        mv.visitVarInsn(ASTORE, paramsIndex);
        for (int i = 0; i < params.length; i++) {
            mv.visitVarInsn(ALOAD, paramsIndex);
            mv.visitLdcInsn(i);
            loadArg(i);
            mayBoxArg(params[i]);
            mv.visitInsn(AASTORE);
        }
        mv.visitVarInsn(ALOAD, pointLocalIndex);
        mv.visitVarInsn(ALOAD, paramsIndex);
        mv.visitMethodInsn(INVOKEVIRTUAL, "red/dim/monitor/core/method/Point", "setArg", "([Ljava/lang/Object;)V", false);
    }

    private void pointSetThisObject() {
        if (!isStaticMethod) {
            mv.visitVarInsn(ALOAD, pointLocalIndex);
            loadThis();
            mv.visitMethodInsn(INVOKEVIRTUAL, "red/dim/monitor/core/method/Point", "setThisObject", "(Ljava/lang/Object;)V", false);
        }
    }

    private void newPoint() {
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitTypeInsn(NEW, "red/dim/monitor/core/method/Point");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "red/dim/monitor/core/method/Point", "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, pointLocalIndex);
    }

    private void mayBoxArg(Type param) {
        if (param == Type.BOOLEAN_TYPE) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        } else if (param == Type.CHAR_TYPE) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
        } else if (param == Type.BYTE_TYPE) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
        } else if (param == Type.SHORT_TYPE) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
        } else if (param == Type.INT_TYPE) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        } else if (param == Type.FLOAT_TYPE) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
        } else if (param == Type.LONG_TYPE) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        } else if (param == Type.DOUBLE_TYPE) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
        }
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == RETURN) {
            monitorMethodExit(0);
        } else if (opcode == ARETURN) {
            monitorMethodExit(1);
        } else if (opcode == FRETURN) {
            monitorMethodExit(1);
        } else if (opcode == DRETURN) {
            monitorMethodExit(2);
        } else if (opcode == LRETURN) {
            monitorMethodExit(2);
        } else if (opcode == IRETURN) {
            monitorMethodExit(1);
        }
        super.visitInsn(opcode);
    }

    private void monitorMethodExit(int i) {
        if (i < 0 || i > 2) {
            return;
        }

        Label l1 = startIf();
        MonitorIsNull(l1);
        methodMonitorIsHotMethodRetrun(l1);
        {

            Label l2 = startIf();
            pointIfNull(l2);
            {
                newPoint();
                pointSetThisObject();
                pointSetArg();
            }
            endIf(l2);

            adjustStack(i);
            pointSetReturnObject();
            pointMethodReturn();

        }
        endIf(l1);
    }


    public Label startIf() {
        return new Label();
    }

    public void endIf(Label label) {
        mv.visitLabel(label);
    }

    private void pointMethodReturn() {
        mv.visitFieldInsn(GETSTATIC, className, "s_Monitor_1", "Lred/dim/monitor/core/method/MethodMonitor;");
        mv.visitVarInsn(ALOAD, pointLocalIndex);
        mv.visitLdcInsn(methodId);
        mv.visitMethodInsn(INVOKEVIRTUAL, "red/dim/monitor/core/method/MethodMonitor", "methodReturn", "(Lred/dim/monitor/core/method/Point;I)V", false);
    }

    private void pointSetReturnObject() {
        Type returnType = Type.getReturnType(methodDesc);
        mayBoxArg(returnType);
        mv.visitMethodInsn(INVOKEVIRTUAL, "red/dim/monitor/core/method/Point", "setReturnObject", "(Ljava/lang/Object;)V", false);
    }

    private void adjustStack(int i) {
        if (i == 1) {
            mv.visitInsn(DUP);
        } else if (i == 2) {
            mv.visitInsn(DUP2);
        }
        mv.visitVarInsn(ALOAD, pointLocalIndex);
        if (i == 0) {
            mv.visitInsn(ACONST_NULL);
        } else if (i == 1) {
            mv.visitInsn(SWAP);
        } else if (i == 2) {
            mv.visitInsn(DUP_X2);
            mv.visitInsn(POP);
        }
    }

    private void pointIfNull(Label l22) {
        mv.visitVarInsn(ALOAD, pointLocalIndex);
        mv.visitJumpInsn(IFNONNULL, l22);
    }

    private void methodMonitorIsHotMethodRetrun(Label l11) {
        mv.visitFieldInsn(GETSTATIC, className, "s_Monitor_1", "Lred/dim/monitor/core/method/MethodMonitor;");
        mv.visitLdcInsn(methodId);
        mv.visitMethodInsn(INVOKEVIRTUAL, "red/dim/monitor/core/method/MethodMonitor", "hotMethodReturn", "(I)Z", false);
        mv.visitJumpInsn(IFEQ, l11);
    }

}
