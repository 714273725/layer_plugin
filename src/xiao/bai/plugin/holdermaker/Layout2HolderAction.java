package xiao.bai.plugin.holdermaker;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Layout2HolderAction extends AnAction {
    public static final String Layout = "R\\s*\\.\\s*layout\\s*\\.\\s*([a-z]|_|\\d)*";
    public static final String Empty = "\\s*";

    public static final String Adapter =
            "@AutoAdapter\\s*\\(\\s*id\\s*=\\s*R\\s*\\.\\s*layout\\s*\\.\\s*([a-z]|_|\\d)*\\s*," +
                    "\\s*value(=|[A-Z]|[a-z]|_|\\d|\\s|\\.)*\\.\\s*class\\)";

    protected JFrame mDialog;
    private JTextField mName;
    public static final String ClassNameHint = "请输入类名";
    LayoutView layoutView;

    @Override
    public void actionPerformed(AnActionEvent event) {
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        Project project = event.getData(PlatformDataKeys.PROJECT);
        String data = event.getData(PlatformDataKeys.FILE_TEXT);
        performe(editor, project, data);

    }

    private void performe(Editor editor, Project project, String data) {
        Map<String, String> layoutMap;
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        List<PsiFile> layouts = Utils.getLayoutFileFromCaret(project, file, layoutMap = getNewLayout(data));
        if (layouts.size() > 0) {
            showLayoutsDialog(editor, project, layouts, layoutMap);
        }
    }

    private Map<String, String> getNewLayout(String data) {
        Matcher slashMatcher = Pattern.compile(Adapter).matcher(data);
        ArrayList<String> target = new ArrayList<>();
        Pattern compile = Pattern.compile(Empty);
        //去除\\s符号
        while (slashMatcher.find()) {
            target.add(compile.matcher(slashMatcher.group()).replaceAll(""));
        }
        Map<String, String> item = new HashMap<>();
        for (int i = 0; i < target.size(); i++) {
            String layoutName = null;
            String className = null;
            try {
                String[] values = target.get(0).split(",");
                Matcher layoutMatcher = Pattern.compile(Layout).matcher(values[0]);
                if (layoutMatcher.find()) {
                    layoutName = layoutMatcher.group();
                }
                className = values[1].replaceAll("value=", "")
                        .replaceAll(".class\\)", "");
                int index = className.indexOf(".");
                if (index != -1) {
                    className = className.substring(index + 1);
                }
                item.put(layoutName, className);
            } catch (Exception e) {

            }
        }
        return item;
    }

    @NotNull
    private ArrayList<String> findMatches(String data) {
        Matcher slashMatcher = Pattern.compile(Layout).matcher(data);
        Pattern compile = Pattern.compile(Empty);
        ArrayList<String> layoutNames = new ArrayList<>();
        while (slashMatcher.find()) {
            layoutNames.add(compile.matcher(slashMatcher.group()).replaceAll(""));
        }
        return layoutNames;
    }

    @Override
    public void update(AnActionEvent e) {
        String data = e.getData(PlatformDataKeys.FILE_TEXT);
        if (findMatches(data).size() > 0)
            e.getPresentation().setEnabled(true);
        else
            e.getPresentation().setEnabled(false);


    }


    public void showLayoutsDialog(Editor editor, Project project,
                                  List<PsiFile> layoutFiles, Map<String, String> layoutMap) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        content.setPreferredSize(new Dimension(640, 360));
        layoutView = new LayoutView(project, editor, layoutFiles);

       /* Box nameBox = Box.createHorizontalBox();
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        mName = new JTextField(file.getName().replaceAll(".java", "Holder"), 10);
        mName.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (mName.getText().trim().equals(ClassNameHint)) {
                    mName.setText("");//让文本为空白
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (mName.getText().trim().equals("")) {
                    mName.setText(ClassNameHint);
                }
            }
        });*/
        //nameBox.add(mName);
        //content.add(nameBox);
        content.add(new JBScrollPane(layoutView));
        Box buttonBox = Box.createHorizontalBox();
        JButton cancel = new JButton("取消");
        JButton comfire = new JButton("确定");
        buttonBox.add(comfire);
        buttonBox.add(cancel);
        content.add(buttonBox);
        comfire.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < layoutFiles.size(); i++) {
                    String layoutName = layoutFiles.get(i).getName().replaceAll(".xml", "");
                    String className = layoutMap.get("R.layout." + layoutName);
                    System.out.println(layoutName + className);
                    generate(layoutName, className, editor, project);
                }
            }
        });
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeDialog();
            }
        });
        mDialog = new JFrame();
        mDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        mDialog.getRootPane().setDefaultButton(comfire);
        mDialog.getContentPane().add(content);
        mDialog.pack();
        mDialog.setLocationRelativeTo(null);
        mDialog.setVisible(true);
    }

    private void generate(String layoutName, String beanName, Editor editor, Project project) {
        closeDialog();
        String className = beanName + getFieldName(layoutName);
        Generator generator = new Generator(project,
                layoutView.getAllItems(), getFieldName(layoutName), beanName);
        boolean exists = generator.exists();
        if (exists) {
            int response = JOptionPane.showConfirmDialog(null,
                    className + "已存在，继续生成将覆盖原有文件，是否继续？", "warning", JOptionPane.YES_NO_OPTION);
            if (response == 0) {
                generator.generate();
            }
        } else {
            generator.generate();
        }
    }

    protected void closeDialog() {
        if (mDialog == null) {
            return;
        }
        mDialog.setVisible(false);
        mDialog.dispose();
    }


    private String getFieldName(String layoutName) {
        String[] words = layoutName.replaceAll("R.layout.", "")
                .split("_");
        StringBuilder sb = new StringBuilder();
        sb.append(Utils.getPrefix());

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            sb.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1));
        }
        return sb.toString();
    }
}
