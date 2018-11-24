package xiao.bai.plugin.layermaker;

import java.io.*;

public class LayoutResMarker {
    private String modulePath;
    private String moduleName;
    private String layerPath;
    private int viewType;


    public LayoutResMarker(String modulePath, String moduleName, String layerPath, int viewType) throws IOException {
        this.modulePath = modulePath;
        this.moduleName = moduleName;
        this.layerPath = layerPath;
        this.viewType = viewType;
        create();

    }

    public String create() throws IOException {
        String resRoot = modulePath + moduleName +
                File.separator + "src" +
                File.separator + "main" +
                File.separator + "res" +
                File.separator + "layout";
        String resFile = resRoot + File.separator + "activity_" + layerPath.replace(".", "_") + ".xml";
        if (viewType == 1) {
            File file = new File(resFile);
            if (!file.exists())
                file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            bw.newLine();
            bw.write("<layout>");
            bw.newLine();
            bw.write("<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"");
            bw.newLine();
            bw.write("android:layout_width=\"match_parent\"");
            bw.newLine();
            bw.write("android:layout_height=\"match_parent\"");
            bw.newLine();
            bw.write("android:orientation=\"vertical\">");
            bw.newLine();
            bw.write(" </LinearLayout>");
            bw.newLine();
            bw.write("</layout>");
            bw.newLine();
            bw.flush();
            bw.close();
        }
        String[] split = layerPath.split("\\.");
        StringBuilder builder = new StringBuilder("Activity");
        for (int i = 0; i < split.length; i++) {
            if (split[i].length() == 0) continue;
            builder.append(PathUtils.toUpperCaseFirstOne(split[i]));
        }
        builder.append("Binding");
        return builder.toString();
    }
}
