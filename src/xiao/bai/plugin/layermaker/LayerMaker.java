package xiao.bai.plugin.layermaker;

import a.a.S;
import xiao.bai.plugin.JavaWriter;

import javax.lang.model.element.Modifier;
import java.io.*;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;

public class LayerMaker {
    //模块名
    private String layerName;
    private String layerRoot;
    private String moduleRoot;
    private int viewType;

    public LayerMaker(String moduleRoot, String layerRoot, String layerName, int viewType) {
        this.layerName = layerName;
        this.layerRoot = layerRoot;
        this.moduleRoot = moduleRoot;
        this.viewType = viewType;
    }

    public void make(String bindingName) throws IOException {
        String layerPath = PathUtils.getModelPath(layerName);
        String root = moduleRoot.replace("/", File.separator) + File.separator + PathUtils.packageToPath(this.layerRoot);
        if (layerPath.length() > 0) {
            root += File.separator + layerPath;
        }
        File rootFile = new File(root);
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }
        String prefix = PathUtils.toUpperCaseFirstOne(PathUtils.getModelName(layerName));
        String contact = root + File.separator + prefix + "Contact.java";
        File contactFile = new File(contact);
        String presenter = root + File.separator + prefix + "Presenter.java";
        File presenterFile = new File(presenter);
        String view = root + File.separator + prefix + (viewType == MakerLayer.Fragment ? "ViewFragment.java" : "ViewActivity.java");
        File viewFile = new File(view);
        contactFile.createNewFile();
        presenterFile.createNewFile();
        viewFile.createNewFile();

        makeContact(contactFile, layerName, layerRoot, prefix);
        makeView(viewFile, layerName, layerRoot, prefix, bindingName);
        makePresenter(presenterFile, layerName, layerRoot, prefix);
    }

    private void makePresenter(File presenterFile, String layerName, String packageName, String className) throws FileNotFoundException {
        JavaWriter presenterWriter = new JavaWriter(new OutputStreamWriter
                (new FileOutputStream(presenterFile)));
        int i = layerName.lastIndexOf(".");
        try {
            presenterWriter.emitPackage(i == -1 ? packageName : packageName + "." + layerName.substring(0, i))
                    .emitImports("com.fast.architect.annotation.PMap")
                    .emitImports("javax.inject.Inject")
                    .emitAnnotation("PMap")
                    .beginType(className + "Presenter", "class",
                            EnumSet.of(PUBLIC), null, className + "Contact.Presenter")
                    .emitField(className + "Contact.View", "view")
                    .emitAnnotation("Inject")
                    .beginConstructor(EnumSet.of(PUBLIC), className + "Contact.View", "view")
                    .emitStatement("this.view = view")
                    .endConstructor()
                    .endType();
            presenterWriter.close();
        } catch (Exception e) {
        }
    }

    private void makeView(File viewFile, String layerName, String packageName, String className, String bindingName) throws FileNotFoundException {
        JavaWriter viewWriter = new JavaWriter(new OutputStreamWriter
                (new FileOutputStream(viewFile)));
        int i = layerName.lastIndexOf(".");
        try {
            try {
                String baseImport;
                String baseClassName;
                String viewMap;
                if (viewType == MakerLayer.Fragment) {
                    viewMap = "VMap(VMap.Fragment)";
                    baseImport = "com.quicklib.base.BaseFragment";
                    baseClassName = "BaseFragment";
                    makeFragment(layerName, packageName, className, bindingName, viewWriter, i, baseImport, baseClassName, viewMap);
                } else {
                    viewMap = "VMap";
                    baseImport = "com.quicklib.base.BaseActivity";
                    baseClassName = "BaseActivity";
                    makeActivity(layerName, packageName, className, bindingName, viewWriter, i, baseImport, baseClassName, viewMap);
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
    }

    private void makeFragment(String layerName, String packageName, String className, String bindingName, JavaWriter viewWriter, int i, String baseImport, String baseClassName, String viewMap) throws IOException {
        viewWriter.emitPackage(i == -1 ? packageName : packageName + "." + layerName.substring(0, i))
                .emitImports("com.fast.architect.annotation.VMap")
                .emitImports(baseImport)
                .emitImports("android.os.Bundle")
                .emitImports("android.support.annotation.Nullable")
                .emitImports("android.support.annotation.NonNull")
                .emitImports("javax.inject.Inject")
                .emitImports("android.databinding.DataBindingUtil")
                .emitImports("android.view.LayoutInflater")
                .emitImports("android.view.View")
                .emitImports("android.view.ViewGroup")
                .emitImports("wan.hui.zhen.bai.xiao.R")
                .emitImports("wan.hui.zhen.bai.xiao.databinding." + bindingName)
                .emitAnnotation(viewMap)
                .beginType(className + "ViewFragment", "class",
                        EnumSet.of(PUBLIC), baseClassName, className + "Contact.View")
                .emitAnnotation("Inject")
                .emitField(className + "Contact.Presenter", "presenter")
                .emitField(bindingName, "mBinding")
                .emitAnnotation("Override", new HashMap<String, Object>())
                .emitAnnotation("Nullable", new HashMap<String, Object>())
                .beginMethod("View", "onCreateView", EnumSet.of(PUBLIC),
                        "@NonNull LayoutInflater", "inflater",
                        "@Nullable ViewGroup", "container",
                        "@Nullable Bundle", "savedInstanceState")
                .emitStatement("mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_" + layerName.replace(".", "_") + ", container, false)")
                .emitReturn("mBinding.getRoot()")
                .endMethod()
                .endType();
        viewWriter.close();
    }

    private void makeActivity(String layerName, String packageName, String className, String bindingName, JavaWriter viewWriter, int i, String baseImport, String baseClassName, String viewMap) throws IOException {
        viewWriter.emitPackage(i == -1 ? packageName : packageName + "." + layerName.substring(0, i))
                .emitImports("com.fast.architect.annotation.VMap")
                .emitImports(baseImport)
                .emitImports("android.os.Bundle")
                .emitImports("android.support.annotation.Nullable")
                .emitImports("javax.inject.Inject")
                .emitImports("android.databinding.DataBindingUtil")
                .emitImports("wan.hui.zhen.bai.xiao.R")
                .emitImports("wan.hui.zhen.bai.xiao.databinding." + bindingName)
                .emitAnnotation(viewMap)
                .beginType(className + "ViewActivity", "class",
                        EnumSet.of(PUBLIC), baseClassName, className + "Contact.View")
                .emitAnnotation("Inject")
                .emitField(className + "Contact.Presenter", "presenter")
                .emitField(bindingName, "mBinding")
                .emitAnnotation("Override", new HashMap<String, Object>())
                .beginMethod("void", "onCreate", EnumSet.of(PROTECTED), "@Nullable Bundle", "savedInstanceState")
                .emitSuper("onCreate", "savedInstanceState")
                .emitStatement("mBinding = DataBindingUtil.setContentView(this, R.layout.activity_" + layerName.replace(".", "_") + ")")
                .endMethod()
                .endType();
        viewWriter.close();
    }

    private void makeContact(File contactFile, String layerName, String packageName, String className) throws FileNotFoundException {
        JavaWriter contactWriter = new JavaWriter(new OutputStreamWriter
                (new FileOutputStream(contactFile)));
        try {
            int i = layerName.lastIndexOf(".");
            contactWriter.emitPackage(i == -1 ? packageName : packageName + "." + layerName.substring(0, i))
                    .emitImports("com.fast.architect.annotation.Contact",
                            "com.fast.architect.annotation.ContactV",
                            "com.fast.architect.annotation.ContactP")
                    .emitAnnotation("Contact")
                    .beginType(className + "Contact", "interface",
                            EnumSet.of(PUBLIC), null)
                    .emitAnnotation("ContactV" + (viewType == MakerLayer.Fragment ? "(ContactV.Fragment)" : ""))
                    .beginType("View", "interface", new HashSet<>(), null)
                    .endType()
                    .emitAnnotation("ContactP")
                    .beginType("Presenter", "interface", new HashSet<>(), null)
                    .endType()
                    .endType();
            contactWriter.close();
        } catch (Exception E) {

        }

    }
}
