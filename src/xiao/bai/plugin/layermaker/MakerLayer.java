package xiao.bai.plugin.layermaker;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import org.jetbrains.annotations.SystemIndependent;

import java.io.File;
import java.util.regex.Matcher;

public class MakerLayer extends AnAction {
    public static String rootPath;
    public static final String SRC = File.separator + "src" + File.separator + "main" + File.separator + "java";
    public static final int Activity = 1;
    public static final int Fragment = 0;

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        String contextFilePath = file.getVirtualFile().getPath();
        String projectFilePath = project.getBasePath();
        String suffix = contextFilePath.replace(projectFilePath, "")
                .replace("/", File.separator);
        int index = suffix.indexOf(SRC);
        String root = projectFilePath + suffix.substring(0, index + SRC.length());
        System.out.println(root);
        if (project != null) {
            rootPath = project.getBasePath().replace("/", File.separator) + File.separator;
            try {
                HomeMenu menu = new HomeMenu(root.replace("/", File.separator));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
