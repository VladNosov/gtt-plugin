import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;
import ui.GtTPluginPanel;
import util.GtTUtil;
import static util.icons.GtTIcons.*;

public class GtTProcessesManager extends AbstractProjectComponent {
    private final GtTPluginPanel panel;

    protected GtTProcessesManager(@NotNull final Project project) {
        super(project);
        panel = new GtTPluginPanel(project);
    }

    @Override
    public void initComponent() {
        super.initComponent();
        GtTUtil.runWhenInitialized(myProject, this::initToolWindow);
    }

    private void initToolWindow() {
        final ToolWindowManagerEx manager = ToolWindowManagerEx.getInstanceEx(myProject);
        ToolWindowEx myToolWindow = (ToolWindowEx) manager.registerToolWindow(GtTPluginPanel.ID, false,
                ToolWindowAnchor.BOTTOM, myProject, true);
        myToolWindow.setIcon(ICON_GTT);
        final ContentFactory contentFactory = ServiceManager.getService(ContentFactory.class);
        final Content content = contentFactory.createContent(panel, "", false);
        ContentManager contentManager = myToolWindow.getContentManager();
        contentManager.addContent(content);
    }
}
