package xiao.bai.plugin.holdermaker;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.XmlRecursiveElementVisitor;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.search.EverythingGlobalScope;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utils {
    public static List<PsiFile> getLayoutFileFromCaret(Project project,
                                                       PsiFile file,
                                                       Map<String, String> layoutNames) {
        ArrayList list = new ArrayList();
        if (layoutNames == null) return list;
        for (String layoutName : layoutNames.keySet()) {
            String name = String.format("%s.xml", layoutName);
            PsiFile psi = resolveLayoutResourceFile(file, project, name.replaceAll("R.layout.", ""));
            if (psi != null) {
                list.add(psi);
            }
        }
        return list;
    }

    /**
     * 从Xml文件中读取视图集合
     *
     * @param file
     * @return
     */
    public static ArrayList<LayoutItem> getIDsFromLayout(final PsiFile file) {
        final ArrayList<LayoutItem> elements = new ArrayList<LayoutItem>();

        return getIDsFromLayout(file, elements);
    }

    public static ArrayList<LayoutItem> getIDsFromLayout(final PsiFile file, final ArrayList<LayoutItem> elements) {
        file.accept(new XmlRecursiveElementVisitor() {

            @Override
            public void visitElement(final PsiElement element) {
                super.visitElement(element);
                if (element instanceof XmlTag) {
                    XmlTag tag = (XmlTag) element;
                    if (tag.getName().equalsIgnoreCase("include")) {
                        XmlAttribute layout = tag.getAttribute("layout", null);

                        if (layout != null) {
                            Project project = file.getProject();
                            PsiFile include = findLayoutResource(file, project, getLayoutName(layout.getValue()));

                            if (include != null) {
                                getIDsFromLayout(include, elements);

                                return;
                            }
                        }
                    }

                    // get element ID
                    XmlAttribute id = tag.getAttribute("android:id", null);
                    if (id == null) {
                        return; // missing android:id attribute
                    }
                    String value = id.getValue();
                    if (value == null) {
                        return; // empty value
                    }
                    // check if there is defined custom class
                    String name = tag.getName();
                    XmlAttribute clazz = tag.getAttribute("class", null);
                    if (clazz != null) {
                        name = clazz.getValue();
                    }

                    try {
                        elements.add(new LayoutItem(name, value));
                    } catch (IllegalArgumentException e) {
                        // TODO log
                    }
                }
            }
        });
        return elements;
    }

    /**
     * Try to find layout XML file by name
     *
     * @param file
     * @param project
     * @param fileName
     * @return
     */
    public static PsiFile findLayoutResource(PsiFile file, Project project, String fileName) {
        String name = String.format("%s.xml", fileName);
        // restricting the search to the module of layout that includes the layout we are seaching for
        return resolveLayoutResourceFile(file, project, name);
    }

    /**
     * Get layout name from XML identifier (@layout/....)
     *
     * @param layout
     * @return
     */
    public static String getLayoutName(String layout) {
        if (layout == null || !layout.startsWith("@") || !layout.contains("/")) {
            return null; // it's not layout identifier
        }
        String[] parts = layout.split("/");
        if (parts.length != 2) {
            return null; // not enough parts
        }

        return parts[1];
    }

    private static PsiFile resolveLayoutResourceFile(PsiElement element, Project project, String name) {
        // restricting the search to the current module - searching the whole project could return wrong layouts
        Module module = ModuleUtil.findModuleForPsiElement(element);
        PsiFile[] files = null;
        if (module != null) {
            // first omit libraries, it might cause issues like (#103)
            GlobalSearchScope moduleScope = module.getModuleWithDependenciesScope();
            files = FilenameIndex.getFilesByName(project, name, moduleScope);
            if (files == null || files.length <= 0) {
                // now let's do a fallback including the libraries
                moduleScope = module.getModuleWithDependenciesAndLibrariesScope(false);
                files = FilenameIndex.getFilesByName(project, name, moduleScope);
            }
        }
        if (files == null || files.length <= 0) {
            // fallback to search through the whole project
            // useful when the project is not properly configured - when the resource directory is not configured
            files = FilenameIndex.getFilesByName(project, name, new EverythingGlobalScope(project));
            if (files.length <= 0) {
                System.out.println("no matching files");
                return null; //no matching files
            }
        }
        // TODO - we have a problem here - we still can have multiple layouts (some coming from a dependency)
        // we need to resolve R class properly and find the proper layout for the R class
        for (PsiFile file : files) {

        }
        return files[0];
    }


    /**
     * Load field name prefix from code style
     *
     * @return
     */
    public static String getPrefix() {
        CodeStyleSettingsManager manager = CodeStyleSettingsManager.getInstance();
        CodeStyleSettings settings = manager.getCurrentSettings();
        return settings.FIELD_NAME_PREFIX;
    }

    public static boolean isEmptyString(String text) {
        return (text == null || text.trim().length() == 0);
    }
}
