package xiao.bai.plugin.layermaker;
//			          _ooOoo_  
//	           	     o8888888o  
//                   88" . "88  
//                   (| -_- |)  
//                    O\ = /O  
//                ____/`---'\____  
//              .   ' \\| |// `.  
//               / \\||| : |||// \  
//             / _||||| -:- |||||- \  
//               | | \\\ - /// | |  
//             | \_| ''\---/'' | |  
//            \ .-\__ `-` ___/-. /  
//          ___`. .' /--.--\ `. . __  
//       ."" '< `.___\_<|>_/___.' >'"".  
//      | | : `- \`.;`\ _ /`;.`/ - ` : | |  
//        \ \ `-. \_ __\ /__ _/ .-` / /  
//======`-.____`-.___\_____/___.-`____.-'======  
//                   `=---='  
//
//.............................................  
//               佛祖保佑             永无BUG 

import xiao.bai.plugin.JavaWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.EnumSet;
import java.util.HashMap;


import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Created by GeJianYe on 2018/2/1 0001.
 * Description:
 * Function:
 */

public class MakeView {
    public static final int NONE = 0;
    public static final int Activity = 1;
    public static final int Fragment = 2;


    // view/src/main/java/
    public static final String InterfacePrefix =
            "view" + File.separator + "src" + File.separator +
                    "main" + File.separator + "java" + File.separator;
    // app/src/main/java/
    public static final String ImplPrefix =
            "app" + File.separator + "src" + File.separator +
                    "main" + File.separator + "java" + File.separator;

    private String interfacePackageName;
    private String interfacePath;

    private String implPackageName;
    private String implPath;
    //模块后缀
    private String modelSuffix;
    //模块到包的路径
    private String modelPath;


    private File implFile;
    private File interfaceFile;


    private int viewType;

    public MakeView(String modelName, int viewType) {
        this.viewType = viewType;
        modelSuffix = PathUtils.getModelName(modelName);
        modelPath = PathUtils.getModelPath(modelName);
        interfacePackageName = "com.lm.chaoshi.view." + modelName.toLowerCase();
        implPackageName = "com.lm.chaoshi.view." + modelName.toLowerCase();

        interfacePath = MakerLayer.rootPath + InterfacePrefix +
                PathUtils.packageToPath("com.lm.chaoshi.view") +
                (modelPath.length() <= 0 ?
                        File.separator + modelName.toLowerCase() :
                        File.separator + modelPath + File.separator + modelSuffix.toLowerCase());

        implPath = MakerLayer.rootPath + ImplPrefix +
                PathUtils.packageToPath("com.lm.chaoshi.view") +
                (modelPath.length() <= 0 ? File.separator + modelSuffix.toLowerCase() :
                        File.separator + modelPath + File.separator + modelSuffix.toLowerCase());

        FileUtils.makeDirectories(interfacePath, implPath);


        interfaceFile = new File(interfacePath + File.separator + modelSuffix + "View.java");

        if (viewType != NONE) {
            if (viewType == Activity) {
                implFile = new File(implPath + File.separator + modelSuffix + "ViewActivity.java");
            }
            if (viewType == Fragment) {
                implFile = new File(implPath + File.separator + modelSuffix + "ViewFragment.java");
            }
        }

    }

    public boolean exists() {
        if (viewType == NONE) {
            System.out.println("View file exists:" +
                    "interfaceFile:" + interfaceFile.exists());
            return interfaceFile.exists();
        } else {
            System.out.println("View file exists:" +
                    "interfaceFile:" + interfaceFile.exists() + "        implFile:" + implFile.exists());
            return interfaceFile.exists() || implFile.exists();
        }
    }


    public void make() {
        makeInterface();
        makeImpl();
    }

    private void makeImpl() {
        if (viewType == NONE) return;
        if (!implFile.exists()) try {
            implFile.createNewFile();
            JavaWriter javaWriter = new JavaWriter(new OutputStreamWriter
                    (new FileOutputStream(implFile)));
            javaWriter.emitPackage(implPackageName)
                    .emitImports(interfacePackageName + "." + modelSuffix + "View");


            if (viewType == Activity) {
                javaWriter.emitImports("com.quicklib.base.BaseActivity")
                        .emitImports("android.os.Bundle")
                        .emitImports("android.support.annotation.Nullable")
                        .beginType(modelSuffix + "ViewActivity", "class",
                                EnumSet.of(PUBLIC), "BaseActivity",
                                modelSuffix + "View")
                        .emitAnnotation("Override", new HashMap<String, Object>())
                        .beginMethod("void", "onCreate", EnumSet.of(PROTECTED), "@Nullable Bundle", "savedInstanceState")
                        .emitSuper("onCreate", "savedInstanceState")
                        .endMethod()
                        .endType();
            } else if (viewType == Fragment) {
                javaWriter.emitImports("com.lm.chaoshi.base.BaseFragment")
                        .emitImports("android.os.Bundle")
                        .emitImports("android.support.annotation.Nullable")
                        .emitImports("android.support.annotation.NonNull")
                        .emitImports("android.view.LayoutInflater")
                        .emitImports("android.view.View")
                        .emitImports("android.view.ViewGroup")
                        .beginType(modelSuffix + "ViewFragment", "class",
                                EnumSet.of(PUBLIC), "BaseFragment",
                                modelSuffix + "View")
                        .emitAnnotation("Nullable", new HashMap<String, Object>())
                        .emitAnnotation("Override", new HashMap<String, Object>())
                        .beginMethod("View", "onCreateView", EnumSet.of(PUBLIC),
                                "@NonNull LayoutInflater", "inflater",
                                "@Nullable ViewGroup", "container",
                                "@Nullable Bundle", "savedInstanceState")
                        .emitReturn(null)
                        .endMethod()
                        .endType();
            }
            javaWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeInterface() {
        if (!interfaceFile.exists()) try {
            interfaceFile.createNewFile();
            JavaWriter javaWriter = new JavaWriter(new OutputStreamWriter
                    (new FileOutputStream(interfaceFile)));
            javaWriter.emitPackage(interfacePackageName)
                    .beginType(modelSuffix + "View",
                            "interface",
                            EnumSet.of(PUBLIC)).endType();
            javaWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
