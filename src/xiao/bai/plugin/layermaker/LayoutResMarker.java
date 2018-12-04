package xiao.bai.plugin.layermaker;

import java.io.*;

public class LayoutResMarker {
    private String modulePath;
    private String moduleName;
    private String layerPath;
    private int viewType;


    public LayoutResMarker(String modulePath, String moduleName, String layerPath, int viewType) {
        this.modulePath = modulePath;
        this.moduleName = moduleName;
        this.layerPath = layerPath;
        this.viewType = viewType;
    }

    public boolean resExists() {
        String resRoot = modulePath + moduleName +
                File.separator + "src" +
                File.separator + "main" +
                File.separator + "res" +
                File.separator + "layout";
        //activity_issue_UserIssueList => activity_issue_user_issue_list
        String layoutName = getLayoutName();
        String resFile;
        if (viewType == MakerLayer.Activity) {
            resFile = resRoot + File.separator + "activity_" + layoutName + ".xml";
        } else {
            resFile = resRoot + File.separator + "fragment_" + layoutName + ".xml";
        }
        File file = new File(resFile);
        return file.exists();
    }

    public String create() throws IOException {
        String resRoot = modulePath + moduleName +
                File.separator + "src" +
                File.separator + "main" +
                File.separator + "res" +
                File.separator + "layout";
        File layoutRoot = new File(resRoot);
        if (!layoutRoot.exists()) {
            layoutRoot.mkdirs();
        }

        //activity_issue_UserIssueList => activity_issue_user_issue_list
        String layoutName = getLayoutName();
        String resFile;
        if (viewType == MakerLayer.Activity) {
            resFile = resRoot + File.separator + "activity_" + layoutName + ".xml";
        } else {
            resFile = resRoot + File.separator + "fragment_" + layoutName + ".xml";
        }
        File file = new File(resFile);
        if (!file.exists())
            file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        bw.newLine();
        bw.write("<layout>");
        bw.newLine();
        bw.write("\t<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"");
        bw.write("\n");
        bw.write("\t\tandroid:layout_width=\"match_parent\"");
        bw.write("\n");
        bw.write("\t\tandroid:id=\"@+id/view_root\"");
        bw.write("\n");
        bw.write("\t\tandroid:layout_height=\"match_parent\"");
        bw.write("\n");
        bw.write("\t\tandroid:orientation=\"vertical\">");
        bw.newLine();
        bw.write("\n");
        bw.write(" \t</LinearLayout>");
        bw.newLine();
        bw.write("</layout>");
        bw.newLine();
        bw.flush();
        bw.close();
        String[] split = layerPath.split("\\.");
        StringBuilder builder = new StringBuilder(viewType == MakerLayer.Activity ? "Activity" : "Fragment");
        for (int i = 0; i < split.length; i++) {
            if (split[i].length() == 0) continue;
            builder.append(PathUtils.toUpperCaseFirstOne(split[i]));
        }
        builder.append("Binding");
        return builder.toString();
    }

    private String getLayoutName() {
        char[] chars = layerPath.replace(".", "_").toCharArray();
        StringBuffer buffer = new StringBuffer();
        boolean beforeIs_ = false;
        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            if (aChar >= 'A' && aChar <= 'Z') {
                if (!beforeIs_) {
                    buffer.append("_");
                }
                beforeIs_ = false;
                buffer.append((char) (aChar + 32));
            } else {
                if (aChar == '_') {
                    beforeIs_ = true;
                } else {
                    beforeIs_ = false;
                }
                buffer.append(aChar);
            }
        }
        String s = buffer.toString();
        System.out.println(s);
        return s;
    }
}
