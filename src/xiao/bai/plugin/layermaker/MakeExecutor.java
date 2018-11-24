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


import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Created by GeJianYe on 2018/3/13 0013.
 * Description:
 * Function:
 */

public class MakeExecutor {
    // data/src/main/java/
    public static final String InterfacePrefix =
            "data" + File.separator + "src" + File.separator +
                    "main" + File.separator + "java" + File.separator;
    // data_service/src/main/java/
    public static final String ImplPrefix =
            "data_service" + File.separator + "src" + File.separator +
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


    public MakeExecutor(String modelName) {
        modelSuffix = PathUtils.getModelName(modelName);
        modelPath = PathUtils.getModelPath(modelName);
        interfacePackageName = "com.lm.chaoshi.data." + modelName.toLowerCase();
        implPackageName = "com.lm.chaoshi.dataservice." + modelName.toLowerCase();


        interfacePath = MakerLayer.rootPath +InterfacePrefix +
                PathUtils.packageToPath("com.lm.chaoshi.data") +
                (modelPath.length() <= 0 ?
                        File.separator + modelName.toLowerCase() :
                        File.separator + modelPath + File.separator + modelSuffix.toLowerCase());

        implPath = MakerLayer.rootPath +ImplPrefix +
                PathUtils.packageToPath("com.lm.chaoshi.dataservice") +
                (modelPath.length() <= 0 ? File.separator + modelSuffix.toLowerCase() :
                        File.separator + modelPath + File.separator + modelSuffix.toLowerCase());
        FileUtils.makeDirectories(interfacePath, implPath);


        interfaceFile = new File(interfacePath + File.separator + modelSuffix + "Executor.java");
        implFile = new File(implPath + File.separator + modelSuffix + "ExecutorImpl.java");
    }

    public boolean exists() {
        System.out.println("Executor file exists:"+
        "interfaceFile:"+interfaceFile.exists()+"        implFile:"+implFile.exists());
        return interfaceFile.exists() || implFile.exists();
    }

    public void make() {
        makeInterface();
        makeImpl();
    }

    private void makeImpl() {
        if (!implFile.exists()) try {
            implFile.createNewFile();
            JavaWriter javaWriter = new JavaWriter(new OutputStreamWriter
                    (new FileOutputStream(implFile)));

            javaWriter.emitPackage(implPackageName)
                    .emitImports(interfacePackageName + "." + modelSuffix + "Executor")
                    .beginType(modelSuffix + "ExecutorImpl", "class",
                            EnumSet.of(PUBLIC), null,
                            modelSuffix + "Executor")
                    .endType();
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
                    .beginType(modelSuffix + "Executor",
                            "interface",
                            EnumSet.of(PUBLIC)).endType();
            javaWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
