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

import com.intellij.ui.treeStructure.treetable.TreeTableTree;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;

import javax.swing.*;

import static xiao.bai.plugin.layermaker.MakerLayer.SRC;

/**
 * Created by GeJianYe on 2018/1/31 0031.
 * Description:
 * Function:
 */

public class HomeMenu extends JFrame {
    //显示module根目录
    private JLabel rootLable;
    //输入模块
    private JTextField moduleInput;
    private JLabel moduleRootTips;
    //输入想要生成layer的目录(eg:输入com.xiao.bai.view,则在view下创建layer目录)
    private JTextField moduleRootInput;
    public JRadioButton activity = new JRadioButton("View Activity实现");
    public JRadioButton fragment = new JRadioButton("View Fragment实现");
    public ButtonGroup g = new ButtonGroup();
    private JCheckBox notCreatView = new JCheckBox("不创建view层");
    private String ROOT;
    private String Module;

    public HomeMenu(String root) throws Exception {
        ROOT = root;
        Module = ROOT.replace(SRC, "")
                .replace(MakerLayer.rootPath, "");
        moduleRootTips = new JLabel("例: 输入com.xiao.bai.view,则在" + ROOT + "/com/xiao/bai/view下创建layer的目录");
        rootLable = new JLabel("Module源码目录:" + ROOT);
        File file = new File(MakerLayer.rootPath + "/PluginHolder");
        String history = null;
        if (file.exists()) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            history = bufferedReader.readLine();
        }
        if (history != null && history.length() > 0) {

        }
        moduleRootInput = new JTextField((history != null && history.length() > 0) ? history :
                "输入layer根目录，格式为(xx.cc.gg)(xxx.xx)等，依此类推");
        //不允许调整窗口大小
        setResizable(false);
        JPanel parent = new JPanel();
        Box box = Box.createVerticalBox();
        box.add(new JLabel("Module:           " + Module));
        Box moduleBox = Box.createHorizontalBox();
        Box moduleInputBox = Box.createHorizontalBox();
        JLabel label = new JLabel("layer name:");
        moduleInput = new JTextField("请输入layer name");

        moduleInput.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (moduleInput.getText().trim().equals("请输入layer name")) {
                    moduleInput.setText("");//让文本为空白
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (moduleInput.getText().trim().equals("")) {
                    moduleInput.setText("请输入layer name");
                }
            }
        });
        moduleRootInput.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (moduleRootInput.getText().trim().equals("输入layer根目录，格式为(xx.cc.gg)(xxx.xx)等，依此类推")) {
                    moduleRootInput.setText("");//让文本为空白
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (moduleRootInput.getText().trim().equals("")) {
                    moduleRootInput.setText("输入layer根目录，格式为(xx.cc.gg)(xxx.xx)等，依此类推");
                }
            }
        });
        box.add(rootLable);
        JLabel moduleInputTitle = new JLabel("layer根目录:");
        moduleInputBox.add(moduleInputTitle);
        moduleInputBox.add(Box.createHorizontalStrut(10));
        moduleInputBox.add(moduleRootInput);
        box.add(moduleInputBox);
        box.add(moduleRootTips);
        box.add(Box.createVerticalStrut(10));
        moduleBox.add(label);
        moduleBox.add(Box.createHorizontalStrut(10));
        moduleBox.add(moduleInput);
        box.add(moduleBox);
        box.add(Box.createVerticalStrut(10));
        box.add(Box.createVerticalStrut(10));
        Box viewBox = Box.createVerticalBox();
        g.add(activity);
        g.add(fragment);
        viewBox.add(activity);
        viewBox.add(fragment);
        box.add(viewBox);
        box.add(Box.createVerticalStrut(10));
        /*box.add(notCreatView);*/
        box.add(Box.createVerticalStrut(10));
        Box allBox = Box.createHorizontalBox();
        JButton allButton = new JButton("一键生成");
        allButton.setPreferredSize(new Dimension(200, 20));
        allBox.setPreferredSize(new Dimension(200, 20));
        allBox.add(allButton);
        box.add(allBox);
        box.add(Box.createVerticalStrut(10));
        allButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    makeAll(moduleRootInput.getText(), moduleInput.getText());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        parent.add(box);
        this.add(parent);
        setSize(1000, 400);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        parent.requestFocusInWindow();
    }

    /**
     * @param moduleName 模块名（Index）
     */
    private void makeAll(String layerRoot, String layerName) throws IOException {
        File file = new File(MakerLayer.rootPath + File.separator +"PluginHolder");
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
        int response = JOptionPane.showConfirmDialog(null,
                "将在" + layerRoot + "下创建layer " + layerName + "，是否继续？", "warning", JOptionPane.YES_NO_OPTION);
        if (response == 0) {
            dismiss();
        }
        String bingDing = new LayoutResMarker(MakerLayer.rootPath, Module, layerName, 1).create();
        new LayerMaker(ROOT, layerRoot,layerName, 1).make(bingDing);
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


    }

    public void dismiss() {
        setVisible(false);
        dispose();
    }
}
