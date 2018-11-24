package xiao.bai.plugin.holdermaker;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


/**
 * 所有layout的所有信息
 */
public class LayoutView extends JPanel {


    protected JButton mConfirm;
    protected JButton mCancel;
    private ArrayList<LayoutItem> mAllItems
            = new ArrayList<>();
    private ArrayList<Layout> mAllLayouts = new ArrayList<>();

    public LayoutView(Project project, Editor editor, List<PsiFile> layoutFiles) {
        addLayouts(layoutFiles);
    }

    public ArrayList<LayoutItem> getAllItems() {
        mAllItems.clear();
        for (int i = 0; i < mAllLayouts.size(); i++) {
            Layout layout = mAllLayouts.get(i);
            if (layout.allUsed()) {
                mAllItems.addAll(layout.idItems);
            } else {
                for (int j = 0; j < layout.idItems.size(); j++) {
                    LayoutItem item = layout.idItems.get(j);
                    if (item.used) {
                        mAllItems.add(item);
                    }
                }
            }
        }
        return mAllItems;
    }

    private void addLayouts(List<PsiFile> layoutFiles) {
        mAllLayouts.clear();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        //每个layout文件的实体
        for (int i = 0; i < layoutFiles.size(); i++) {
            PsiFile layoutFile = layoutFiles.get(i);
            Layout layout = new Layout(layoutFile);
            add(layout);
            mAllLayouts.add(layout);
        }
    }
}
