package xiao.bai.plugin.holdermaker;

import com.intellij.psi.PsiFile;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class LayoutEntity extends JPanel {
    protected JCheckBox mCheck;
    protected JLabel mType;
    private Layout mLayout;

    public LayoutEntity(PsiFile file) {

        //显示layout文件
        mCheck = new JCheckBox();
        mType = new JLabel(file.getName());
        Box content = Box.createVerticalBox();
        Box layoutBox = Box.createHorizontalBox();
        layoutBox.add(mCheck);
        layoutBox.add(mType);

        mCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 2018/3/16 0016 反选
            }
        });


        //显示layout中每一个有Id的item
        Box idsBox = Box.createVerticalBox();
        mLayout = new Layout(file);
        for (int i = 0; i < mLayout.getIdItems().size(); i++) {
            add(mLayout.getIdItems().get(i));
        }
        content.add(layoutBox);
        //content.add(idsBox);
        add(content);
    }

    public ArrayList<Layout> getCheckLayouts() {
        ArrayList<Layout> list = new ArrayList<>();
        return list;
    }
}
