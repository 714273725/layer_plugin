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

import java.io.File;
import java.io.IOException;

/**
 * Created by GeJianYe on 2018/2/1 0001.
 * Description:
 * Function:
 */

public class FileUtils {
    private String rootName;

    public synchronized static FileUtils getInstance(String rootName) {
        return new FileUtils(rootName);
    }

    private FileUtils(String rootName) {
        this.rootName = rootName;
        checkRoot();
    }

    private synchronized void checkRoot() {
        File file = new File("src" +
                File.separator + rootName);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * @param rootName
     * @param moduleName
     * @param layerName
     * @return
     */
    public static String createLayerFolder(String rootName, String moduleName, String layerName) {
        String fileName = "";
        createFolder(fileName = "autosrc" + File.separator + rootName
                + File.separator + moduleName + File.separator + layerName);
        System.out.println(fileName);
        return fileName;
    }

    public static String createFolder(String folderName) {
        File file = new File(folderName);
        if (file.exists()) {
            file.delete();
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        return folderName;
    }

    public static File createFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    public static void makeDirectories(String... path) {
        if (path == null) return;
        for (int i = 0; i < path.length; i++) {
            File file = new File(path[i]);
            if (!file.exists()) file.mkdirs();
        }
    }
}
