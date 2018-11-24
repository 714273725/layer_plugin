package xiao.bai.plugin.holdermaker;

import com.intellij.openapi.project.Project;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AdapterGenerator {
    private File root;
    private String holderName;
    private String rootPath;
    private File file;

    public AdapterGenerator(Project project, String holderName) {
        rootPath = project.getBasePath();
        this.holderName = holderName;
        root = new File(rootPath +
                File.separator
                + "app"
                + File.separator + "src"
                + File.separator + "main"
                + File.separator
                + "java" + File.separator + "com"
                + File.separator + "xiao" + File.separator + "bai" + File.separator + "adapter");
        file = new File(root.getAbsolutePath() +
                File.separator + holderName + "Adapter" + ".java");
    }

    public boolean exists() {
        return file.exists();
    }

    public void generate(){
        if (!root.exists()) {
            root.mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
