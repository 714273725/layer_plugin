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

/**
 * Created by GeJianYe on 2018/3/13 0013.
 * Description:
 * Function:
 */

public class PathUtils {
    //首字母转大写
    public static String toUpperCaseFirstOne(String s){
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
    /**
     * 将包名转化为路径 com.lm.chaoshi.xx->com/lm/chaoshi/xx
     *
     * @param packageName 包名（com.lm.chaoshi.xx）
     * @return
     */
    public static String packageToPath(String packageName) {
        String[] packageNames = packageName.split("\\.");
        String rootName = "";
        for (int i = 0; i < packageNames.length; i++) {
            if (i == 0) {
                rootName += packageNames[i];
            } else {
                rootName += (File.separator + packageNames[i]);
            }
        }
        return rootName;
    }


    /**
     * 分割模块
     *
     * @param modelName
     * @return
     */
    public static String getModelName(String modelName) {
        String[] modelNames = modelName.split("\\.");
        return modelNames[modelNames.length - 1];
    }


    /**
     * 获取模块到包的路径
     *
     * @param modelName
     * @return
     */
    public static String getModelPath(String modelName) {
        String[] packageNames = modelName.split("\\.");
        String rootName = "";
        //如果路径数组长度只有1，说明没有路径，直接可达
        if (packageNames.length == 1) return "";
        //否则0->length-1的内容为路径
        for (int i = 0; i < packageNames.length - 1; i++) {
            if (i == 0) {
                rootName += packageNames[i];
            } else {
                rootName += (File.separator + packageNames[i]);
            }
        }
        return rootName;
    }
}
