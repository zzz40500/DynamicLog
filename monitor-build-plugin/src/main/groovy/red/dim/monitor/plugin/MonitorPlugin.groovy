package red.dim.monitor.plugin

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.AppliedPlugin
import red.dim.monitor.plugin.transform.MonitorTransform;

/**
 * Created by dim on 17/10/14.
 */

class MonitorPlugin implements Plugin<Project> {
    @Override
    void apply(final Project project) {
        AppliedPlugin plugin = project.getPluginManager().findPlugin("com.android.application");
        if (plugin == null) {
            throw new GradleException("application plugin required");
        }

        project.getExtensions().create(MonitorExtension.NAME, MonitorExtension.class);
        project.android.registerTransform(new MonitorTransform(project, project.extensions.getByName(MonitorExtension.NAME)));
    }
}
