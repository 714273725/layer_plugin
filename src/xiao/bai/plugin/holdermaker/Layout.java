package xiao.bai.plugin.holdermaker;

import com.intellij.psi.PsiFile;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.List;

//每个layout
public class Layout extends JPanel {
    public JCheckBox mAllBox;
    List<LayoutItem> idItems;
    private OnCheckBoxStateChangedListener mAllListener;

    private OnCheckBoxStateChangedListener allCheckListener = new OnCheckBoxStateChangedListener() {
        @Override
        public void changeState(boolean checked) {
            //遍历子item且不会触发singleCheckListener监听
            for (final LayoutItem entry : idItems) {
                entry.setListener(null);
                entry.getCheck().setSelected(checked);
                entry.setListener(singleCheckListener);
            }
        }
    };

    private OnCheckBoxStateChangedListener singleCheckListener = new OnCheckBoxStateChangedListener() {
        @Override
        public void changeState(boolean checked) {
            boolean result = true;
            for (LayoutItem entry : idItems) {
                result &= entry.used;
            }
            //防止互相循环调用
            Layout.this.setAllListener(null);
            Layout.this.mAllBox.setSelected(result);
            Layout.this.setAllListener(allCheckListener);
        }
    };


    public Layout(PsiFile file) {
        this.idItems = Utils.getIDsFromLayout(file);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        Box layout = Box.createHorizontalBox();
        layout.add(mAllBox = new JCheckBox(file.getName()));
        layout.add(Box.createHorizontalGlue());
        mAllBox.setSelected(true);
        add(layout);
        for (int i = 0; i < idItems.size(); i++) {
            LayoutItem item;
            add(item = idItems.get(i));
            item.setListener(singleCheckListener);
        }
        mAllBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                //点全部勾选
                if (mAllListener != null) {
                    mAllListener.changeState(mAllBox.isSelected());
                }
            }
        });
        setAllListener(allCheckListener);
    }

    public List<LayoutItem> getIdItems() {
        return idItems;
    }

    public void setAllListener(OnCheckBoxStateChangedListener mListener) {
        this.mAllListener = mListener;
    }

    public boolean allUsed(){
        return mAllBox.isSelected();
    }
}
