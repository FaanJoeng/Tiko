package top.middleware.tiko.quick.bytecode;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.TraceClassVisitor;
import top.middleware.tiko.quick.QuickDefinition;

import java.io.PrintWriter;

public class Generator implements Opcodes {

    public static byte[] genRepository(QuickDefinition definition) throws Exception {
        String sourceFileName = definition.getEntitySimpleName() + "Repository.java";
        String className = definition.getRepositoryName().replace(".", "/");
        String entityName = definition.getEntityName().replace(".", "/");

        // "Ljava/lang/Object;Lorg/springframework/data/jpa/repository/JpaRepository<Ltop/middleware/tiko/quick/template/EntityTemplate;Ljava/lang/Long;>;";
        String signature = "Ljava/lang/Object;Lorg/springframework/data/jpa/repository/JpaRepository<L"  + entityName + ";Ljava/lang/Long;>;";

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(System.out));

        tcv.visit(V1_8, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, className, signature, "java/lang/Object", new String[]{"org/springframework/data/jpa/repository/JpaRepository"});

        tcv.visitSource(sourceFileName, null);

        {
            av0 = tcv.visitAnnotation("Lorg/springframework/stereotype/Repository;", true);
            av0.visitEnd();
        }
        tcv.visitEnd();

        return cw.toByteArray();
    }

    public static byte[] genController(QuickDefinition definition) throws Exception {
        String sourceFileName = definition.getEntitySimpleName() + "Controller.java";
        String controllerName = definition.getControllerName().replace(".", "/");
        String entityName = definition.getEntityName().replace(".", "/");
        String repositoryName = definition.getRepositoryName().replace(".", "/");
        // Ltop/middleware/tiko/quick/template/RepositoryTemplate;
        String repositoryDescriptor = "L" +  definition.getRepositoryName().replace(".", "/") + ";";
        // Ltop/middleware/tiko/quick/template/ControllerTemplate;
        String controllerDescriptor = "L" + controllerName + ";";
        // ()Ljava/util/List<Ltop/middleware/tiko/quick/template/EntityTemplate;>;
        String selectSignature = "()Ljava/util/List<L" + entityName + ";>;";

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, controllerName, null, "java/lang/Object", null);

        cw.visitSource(sourceFileName, null);

        {
            av0 = cw.visitAnnotation("Lorg/springframework/web/bind/annotation/RestController;", true);
            av0.visitEnd();
        }
        {
            av0 = cw.visitAnnotation("Lorg/springframework/web/bind/annotation/RequestMapping;", true);
            {
                AnnotationVisitor av1 = av0.visitArray("value");
                av1.visit(null, definition.getRequestMapping()[0]);
                av1.visitEnd();
            }
            av0.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE, "repository", repositoryDescriptor, null, null);
            {
                av0 = fv.visitAnnotation("Lorg/springframework/beans/factory/annotation/Autowired;", true);
                av0.visitEnd();
            }
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(17, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", controllerDescriptor, null, l0, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "select", "()Ljava/util/List;", selectSignature, null);
            {
                av0 = mv.visitAnnotation("Lorg/springframework/web/bind/annotation/GetMapping;", true);
                av0.visitEnd();
            }
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(23, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, controllerName, "repository", repositoryDescriptor);
            mv.visitMethodInsn(INVOKEINTERFACE, repositoryName, "findAll", "()Ljava/util/List;", true);
            mv.visitInsn(ARETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", controllerDescriptor, null, l0, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}
