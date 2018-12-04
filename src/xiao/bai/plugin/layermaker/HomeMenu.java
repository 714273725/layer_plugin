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

import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static xiao.bai.plugin.layermaker.MakerLayer.Activity;
import static xiao.bai.plugin.layermaker.MakerLayer.Fragment;
import static xiao.bai.plugin.layermaker.MakerLayer.SRC;

/**
 * Created by GeJianYe on 2018/1/31 0031.
 * Description:
 * Function:
 */

public class HomeMenu extends JFrame {
    //显示module根目录
    private JLabel rootLabel;
    //输入模块
    private JTextField layerInput;
    private JLabel moduleRootTips;
    //输入想要生成layer的目录(eg:输入com.xiao.bai.view,则在view下创建layer目录)
    private JTextField layerRootInput;
    public JRadioButton activity = new JRadioButton("View Activity实现");
    public JRadioButton fragment = new JRadioButton("View Fragment实现");
    //该module的java路径（E:\pro\anPro\fastframework\module_user\src\main\java）
    private String ROOT;
    //该module的Name(module_user)
    private String Module;
    //AndroidManifest中获取的包名
    private String packageName;
    //显示包名的控件
    private JLabel packageLabel;

    private int windowWidth = 800;
    private int borderWidth = 10;
    private int innerWidth = windowWidth - borderWidth * 2;
    private int nameWidth = 100;
    private int panelHeight;

    public HomeMenu(String root) throws Exception {
        //不允许调整窗口大小
        setResizable(false);
        //E:\pro\anPro\fastframework\app\src\main\java
        ROOT = root;
        //app
        Module = ROOT.replace(SRC, "")
                .replace(MakerLayer.rootPath, "");
        readModulePackage();
        String history = readLayerRootHistory();
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(borderWidth, borderWidth, borderWidth, borderWidth));
        JPanel moduleNamePanel = addModuleName();
        contentPanel.add(moduleNamePanel);
        panelHeight += moduleNamePanel.getHeight();
        JPanel packageNamePanel = addPackageName();
        contentPanel.add(packageNamePanel);
        panelHeight += packageNamePanel.getHeight();
        JPanel moduleSrcPanel = addModuleSrcPath();
        contentPanel.add(moduleSrcPanel);
        panelHeight += moduleSrcPanel.getHeight();
        JPanel addLayerRootInputPanel = addLayerRootInput(history);
        contentPanel.add(addLayerRootInputPanel);
        panelHeight += addLayerRootInputPanel.getHeight();
        JPanel addLayoutRootTipsPanel = addLayoutRootTips();
        contentPanel.add(addLayoutRootTipsPanel);
        panelHeight += addLayoutRootTipsPanel.getHeight();
        JPanel addLayerInputPanel = addLayerInput();
        contentPanel.add(addLayerInputPanel);
        panelHeight += addLayerInputPanel.getHeight();
        JPanel addViewTypeBoxPanel = addViewTypeBox();
        contentPanel.add(addViewTypeBoxPanel);
        panelHeight += addViewTypeBoxPanel.getHeight();
        JPanel confirmMakePanel = addConfirmMake();
        contentPanel.add(confirmMakePanel);
        panelHeight += confirmMakePanel.getHeight();
        panelHeight += borderWidth * 2;
       /* readModulePackage();
        String history = readLayerRootHistory();
        //不允许调整窗口大小
        setResizable(false);
        //创建一个面板
        JPanel parent = new JPanel();
        //创建一个垂直的box，类似LinearLayout
        Box box = Box.createVerticalBox();
        //show module name
        addModuleName(box);
        //show package name
        addPackageName(box);
        //show java src
        addModuleSrcPath(box);
        //show module root
        addLayerRootInput(box, history);
        box.add(Box.createVerticalStrut(10));
        //show layer input
        addLayerInput(box);
        box.add(Box.createVerticalStrut(10));
        //show view type
        addViewTypeBox(box);
        box.add(Box.createVerticalStrut(10));
        //show confirm
        addConfirmMake(box);
        box.add(Box.createVerticalStrut(10));
        //把视图加到面板上
        parent.add(box);
        //将面板显示在JFrame中
        this.add(parent);
        setSize(1200, 400);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        parent.requestFocusInWindow();*/
        contentPanel.setPreferredSize(new Dimension(windowWidth, 320));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().add(contentPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel addLayoutRootTips() {
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JTextArea comp = new JTextArea("例: 输入com.xiao.bai.view,则在" + ROOT + "/com/xiao/bai/view下创建layer的目录");
        comp.setPreferredSize(new Dimension(innerWidth, 52));
        comp.setLineWrap(true);        //激活自动换行功能
        innerPanel.add(comp);
        return innerPanel;
    }

    private JPanel addConfirmMake() {
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));
        JButton allButton = new JButton("一键生成");
        allButton.setPreferredSize(new Dimension(nameWidth, 38));
        allButton.addActionListener(e -> {
            try {
                makeAll(layerRootInput.getText(), layerInput.getText(), packageName);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        innerPanel.add(allButton);
        return innerPanel;
    }

    private JPanel addViewTypeBox() {
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));
        initViewType();
        Box viewBox = Box.createVerticalBox();
        viewBox.add(activity);
        viewBox.add(fragment);
        innerPanel.add(activity);
        innerPanel.add(fragment);
        return innerPanel;
    }

    @Nullable
    private String readLayerRootHistory() throws IOException {
        //该文件用于记录上一次记录的层级目录在java目录下的相对包，一般来说是包名的超集
        File file = new File(MakerLayer.rootPath + "/PluginHolder");
        String history = null;
        if (file.exists()) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            history = bufferedReader.readLine();
        }
        return history;
    }

    /**
     * 读取module的包名
     *
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private void readModulePackage() throws ParserConfigurationException, SAXException, IOException {
        //E:\pro\anPro\fastframework\app\src\main\AndroidManifest.xml
        File AndroidManifest = new File(ROOT.replace("java", "AndroidManifest.xml"));
        packageLabel = new JLabel();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            //从AndroidManifest文件中读取包名，用于import R文件
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(AndroidManifest);
            packageName = document.getDocumentElement().getAttribute("package");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加输入layer信息模块
     */
    private JPanel addLayerInput() {
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));
        JLabel layerLabel = new JLabel(" layer name:");
        layerLabel.setPreferredSize(new Dimension(nameWidth, 26));
        innerPanel.add(layerLabel);
        layerInput = new JTextField("请输入layer name");
        layerInput.setPreferredSize(new Dimension(innerWidth - nameWidth, 26));
        layerInput.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (layerInput.getText().trim().equals("请输入layer name")) {
                    layerInput.setText("");//让文本为空白
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (layerInput.getText().trim().equals("")) {
                    layerInput.setText("请输入layer name");
                }
            }
        });
        innerPanel.add(layerInput);
        return innerPanel;
    }

    /**
     * 添加显示输入layer root 的视图
     */
    private JPanel addLayerRootInput(String history) {
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));
        layerRootInput = new JTextField((history != null && history.length() > 0) ? history :
                "输入layer根目录，格式为(xx.cc.gg)(xxx.xx)等，依此类推");
        layerRootInput.setPreferredSize(new Dimension(innerWidth - nameWidth, 26));
        layerRootInput.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (layerRootInput.getText().trim().equals("输入layer根目录，格式为(xx.cc.gg)(xxx.xx)等，依此类推")) {
                    layerRootInput.setText("");//让文本为空白
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (layerRootInput.getText().trim().equals("")) {
                    layerRootInput.setText("输入layer根目录，格式为(xx.cc.gg)(xxx.xx)等，依此类推");
                }
            }
        });
        JLabel moduleInputTitle = new JLabel(" layer根目录:");
        moduleInputTitle.setPreferredSize(new Dimension(nameWidth, 26));
        innerPanel.add(moduleInputTitle);
        innerPanel.add(layerRootInput);
        return innerPanel;
    }

    /**
     * 显示module java src目录
     */
    private JPanel addModuleSrcPath() {
        JPanel moduleSrcPanel = new JPanel();
        moduleSrcPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JTextArea comp = new JTextArea("Module源码目录:" + ROOT);
        comp.setPreferredSize(new Dimension(innerWidth, 52));
        comp.setLineWrap(true);
        moduleSrcPanel.add(comp);
        return moduleSrcPanel;
    }

    /**
     * 添加一个显示Module 包名的视图
     */
    private JPanel addPackageName() {
        JPanel packageNamePanel = new JPanel();
        packageNamePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel comp = new JLabel("包名:" + packageName, JLabel.LEFT);
        comp.setPreferredSize(new Dimension(innerWidth, 26));
        packageNamePanel.add(comp);
        return packageNamePanel;
    }

    /**
     * 添加一个显示Module名的视图
     */
    private JPanel addModuleName() {
        JPanel modulePanel = new JPanel();
        modulePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel comp = new JLabel("ModuleName:" + Module, JLabel.LEFT);
        comp.setPreferredSize(new Dimension(innerWidth, 26));
        modulePanel.add(comp);
        return modulePanel;
    }

    /**
     * 初始化分组,使得activity与fragment只能选其一，
     * 并默认选中activity
     */
    private void initViewType() {
        ButtonGroup buttonGroup = new ButtonGroup();
        activity.setSelected(true);
        buttonGroup.add(activity);
        buttonGroup.add(fragment);
    }

    /**
     * @param layerName 模块名（Index）
     */
    private void makeAll(String layerRoot, String layerName, String packageName) throws IOException {
        File file = new File(MakerLayer.rootPath + File.separator + "PluginHolder");
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String history = bufferedReader.readLine();
        if (!layerRoot.equals(history)) {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.write(layerRoot);
            fileWriter.flush();
            fileWriter.close();
        }
        if ("请输入layer name".equals(layerName)) {
            JOptionPane.showConfirmDialog(null,
                    "layer name不能为空", "warning",
                    JOptionPane.DEFAULT_OPTION);
            return;
        }
        if (packageName == null || packageName.length() == 0) {
            JOptionPane.showConfirmDialog(null,
                    "无法获取包名，这可能不是一个Android Lib，" +
                            "请检查调起插件的文件位置和AndroidManifest.xml", "warning",
                    JOptionPane.DEFAULT_OPTION);
            return;
        }

        int response = JOptionPane.showConfirmDialog(null,
                "将在" + layerRoot + "下创建layer " + layerName + "，是否继续？", "warning", JOptionPane.YES_NO_OPTION);
        if (response == 0) {
            dismiss();
            int viewType = fragment.isSelected() ? Fragment : Activity;
            LayoutResMarker layoutResMarker = new LayoutResMarker(MakerLayer.rootPath, Module, layerName, viewType);
            if (layoutResMarker.resExists()) {
                int confirm = JOptionPane.showConfirmDialog(null,
                        "layout文件重复，请检查你的层级关系，继续生成将覆盖原有的模块，是否继续？", "warning", JOptionPane.YES_NO_OPTION);
                if (confirm == 0) {
                    String bingDing = layoutResMarker.create();
                    new LayerMaker(ROOT, layerRoot, layerName, viewType, packageName).make(bingDing);
                    dismiss();
                }
            } else {
                String bingDing = layoutResMarker.create();
                new LayerMaker(ROOT, layerRoot, layerName, viewType, packageName).make(bingDing);
            }
        } else {
            dismiss();
        }
    }


       /* if (makeExecutor.exists() || makeDomain.exists()) {
            int response = JOptionPane.showConfirmDialog(null,
                    "模块已存在，继续生成将覆盖原有文件，是否继续？", "warning", JOptionPane.YES_NO_OPTION);
            if (response == 0) {
                makeExecutor.make();
                makeDomain.make();
                dismiss();
            }
        } else {
            makeExecutor.make();
            makeDomain.make();
            dismiss();
        }*/


        /*if ("请输入模块名".equals(moduleName)) {
            JOptionPane.showConfirmDialog(null,
                    "模块名不能为空", "warning",
                    JOptionPane.DEFAULT_OPTION);
            return;
        }


        MakeExecutor makeExecutor = new MakeExecutor(moduleName);
        MakeDomain makeDomain = new MakeDomain(moduleName);
        MakeView makeView = null;
        if (!notCreatView.isSelected()) {
            if (activity.isSelected()) {
                makeView = new MakeView(moduleName, MakeView.Activity);
            } else if (fragment.isSelected()) {
                makeView = new MakeView(moduleName, MakeView.Fragment);
            } else {
                makeView = new MakeView(moduleName, MakeView.NONE);
            }
        }
        if (notCreatView.isSelected()) {
            if (makeExecutor.exists() || makeDomain.exists()) {
                int response = JOptionPane.showConfirmDialog(null,
                        "模块已存在，继续生成将覆盖原有文件，是否继续？", "warning", JOptionPane.YES_NO_OPTION);
                if (response == 0) {
                    makeExecutor.make();
                    makeDomain.make();
                    dismiss();
                }
            } else {
                makeExecutor.make();
                makeDomain.make();
                dismiss();
            }
        } else {
            if (makeExecutor.exists() || makeDomain.exists() || makeView.exists()) {
                int response = JOptionPane.showConfirmDialog(null,
                        "模块已存在，继续生成将覆盖原有文件，是否继续？", "warning", JOptionPane.YES_NO_OPTION);
                if (response == 0) {
                    makeExecutor.make();
                    makeDomain.make();
                    makeView.make();
                    dismiss();
                }
            } else {
                makeExecutor.make();
                makeDomain.make();
                makeView.make();
                dismiss();
            }
        }*/

    public void dismiss() {
        setVisible(false);
        dispose();
    }
}
