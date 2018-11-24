package xiao.bai.plugin.holdermaker;

import com.intellij.openapi.project.Project;
import xiao.bai.plugin.JavaWriter;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

import static javax.lang.model.element.Modifier.PUBLIC;

public class Generator {
    private List<LayoutItem> items;
    private String adapterName;
    private String beanName;
    private String layoutName;
    private String rootPath;
    private File root;
    private File file;

    public Generator(Project project, List<LayoutItem> items, String layoutName, String beanName) {
        rootPath = project.getBasePath();
        this.items = items;
        this.layoutName = layoutName;
        this.beanName = beanName;
        this.adapterName = beanName + "Adapter";
        root = new File(rootPath +
                File.separator
                + "app"
                + File.separator + "src"
                + File.separator + "main"
                + File.separator
                + "java" + File.separator + "com"
                + File.separator + "xiao" + File.separator + "bai" + File.separator + "holder");
        file = new File(root.getAbsolutePath() + File.separator + adapterName + ".java");
    }

    public boolean exists() {
        return file.exists();
    }

    public void generate() {
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
        JavaWriter javaWriter;
        try {
            javaWriter = new JavaWriter(new OutputStreamWriter
                    (new FileOutputStream(file)));
            //写入包名
            javaWriter.emitPackage("com.xiao.bai.holder");
            //循环写入import
            for (int i = 0; i < items.size(); i++) {
                LayoutItem item = items.get(i);
                if (!Utils.isEmptyString(item.nameFull)) {
                    javaWriter.emitImports(item.nameFull);
                } else if (Definitions.paths.containsKey(item.name)) {
                    javaWriter.emitImports(Definitions.paths.get(item.name));
                } else {
                    javaWriter.emitImports("android.widget." + item.name);
                }
            }
            //写入一些固定的import R除外
            javaWriter.emitImports("com.quicklib.adapter.TAdapter");
            javaWriter.emitImports("android.view.View");
            javaWriter.emitImports("com.lm.chaoshi.R");
            HashSet<Modifier> modifiers = new HashSet<>();
            modifiers.add(Modifier.PUBLIC);
            //开始写类-构造函数中初始化fineView
            javaWriter.beginType(adapterName, "class",
                    EnumSet.of(PUBLIC), "THolder<" + beanName + "," + layoutName + beanName + "Holder" + ">")
                    .beginConstructor(modifiers, "View", "itemView")
                    .emitSuperConstructor("itemView")
                    .emitStatement("fineViews()")
                    .endConstructor();

            HashSet<Modifier> fieldModifiers = new HashSet<>();
            fieldModifiers.add(Modifier.PUBLIC);
            //循环写成员变量
            for (int i = 0; i < items.size(); i++) {
                LayoutItem item = items.get(i);
                javaWriter.emitField(item.name, item.fieldName, fieldModifiers);
            }
            //循环fineView
            javaWriter.beginMethod("void", "fineViews", modifiers);
            for (int i = 0; i < items.size(); i++) {
                LayoutItem item = items.get(i);
                javaWriter.emitStatement(item.fieldName + " = getView(R.id." + item.id + ")");
            }
            javaWriter.endMethod();
            //写holder
            javaWriter.beginType(layoutName + beanName + "Holder", "class", EnumSet.of(PUBLIC))
                    .endType();

            javaWriter.endType();
            javaWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }


    }
}
