package red.dim.monitor.plugin.transform;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;

import org.apache.commons.io.FileUtils;
import org.gradle.api.Project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import red.dim.monitor.plugin.MonitorExtension;
import red.dim.monitor.plugin.aop.ClassProcessor;
import red.dim.monitor.plugin.entity.Write;

import static com.android.build.api.transform.QualifiedContent.DefaultContentType.CLASSES;
import static org.apache.commons.io.FileUtils.forceDelete;
/**
 * Created by dim on 17/10/14.
 */

public class MonitorTransform extends Transform {
    private Project project;
    ClassProcessor classProcessor;

    public MonitorTransform(Project project, MonitorExtension monitorExtension) {
        this.project = project;
        classProcessor = new ClassProcessor(monitorExtension);
    }

    @Override
    public String getName() {
        return "monitor";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return ImmutableSet.<QualifiedContent.ContentType>of(CLASSES);
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return Sets.immutableEnumSet(
                QualifiedContent.Scope.PROJECT,
                QualifiedContent.Scope.PROJECT_LOCAL_DEPS,
                QualifiedContent.Scope.SUB_PROJECTS,
                QualifiedContent.Scope.SUB_PROJECTS_LOCAL_DEPS,
                QualifiedContent.Scope.EXTERNAL_LIBRARIES
        );
    }

    @Override
    public boolean isIncremental() {
        //这里偷懒了, 后续可以设置为true, 并针对性优化
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        String path = transformInvocation.getContext().getPath();
        if (!transformInvocation.isIncremental()) {
            transformInvocation.getOutputProvider().deleteAll();
            for (TransformInput input : transformInvocation.getInputs()) {
                for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                    File contentLocation = transformInvocation.getOutputProvider().getContentLocation("class", directoryInput.getContentTypes(), directoryInput.getScopes(), Format.DIRECTORY);
                    handleDirectoryInput(directoryInput.getFile(), contentLocation, directoryInput.getFile().getAbsolutePath());
                }
                for (JarInput jarInput : input.getJarInputs()) {
                    File file = transformInvocation.getOutputProvider().getContentLocation(jarInput.getName(), jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
                    processJar(jarInput.getFile(), file);
                }
            }
        }
        saveMonitorMappingTxt(path);
    }

    private void saveMonitorMappingTxt(String path) {
        String mappingTxtFile = path;
        int aFor = path.indexOf("For");
        if (aFor != -1) {
            mappingTxtFile = path.substring(aFor + 3);
        }
        File mappingTxt = new File(project.getBuildDir() + File.separator + "outputs" + File.separator + mappingTxtFile, "monitorMapping.txt");
        try {
            FileUtils.touch(mappingTxt);
            forceDelete(mappingTxt);
            new Write(classProcessor.getMonitorClasses(), mappingTxt).write();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processJar(File zipFile, File destFile) throws IOException {
        FileUtils.touch(destFile);
        forceDelete(destFile);
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destFile));
        JarFile zis = new JarFile(zipFile);
        Enumeration enumeration = zis.entries();
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement();
            String entryName = jarEntry.getName();
            if (jarEntry.isDirectory()) continue;
            ZipEntry zipEntry = new ZipEntry(entryName);
            InputStream inputStream = zis.getInputStream(jarEntry);
            zos.putNextEntry(zipEntry);
            byte[] src = ByteStreams.toByteArray(inputStream);
            if (entryName.endsWith(".class")) {
                byte[] bytes = classProcessor.process(src);
                zos.write(bytes);
            } else {
                zos.write(src);
            }

            zos.closeEntry();
        }
        zis.close();
        zos.close();
    }

    void handleDirectoryInput(File file, File targetDirFile, String relativePath) {
        if (file.isDirectory()) {
            for (File file1 : file.listFiles()) {
                handleDirectoryInput(file1, targetDirFile, relativePath);
            }
        } else {
            String targetPath = file.getAbsolutePath().replaceFirst(relativePath, targetDirFile.getAbsolutePath());
            File targetFile = new File(targetPath);
            if (file.getName().endsWith(".class")) {
                processClassFile(file, targetFile);
            }
        }
    }

    void processClassFile(File file, File targetFile) {
        if (file.getName().endsWith(".class")) {
            byte[] bytes = null;
            try {
                bytes = classProcessor.process(FileUtils.readFileToByteArray(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            try {
                FileUtils.writeByteArrayToFile(targetFile, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
