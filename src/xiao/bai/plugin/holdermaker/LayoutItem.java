package xiao.bai.plugin.holdermaker;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//一个id对应的item
public class LayoutItem extends JPanel {
    private static final Pattern sIdPattern
            = Pattern.compile("@\\+?(android:)?id/([^$]+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern sValidityPattern =
            Pattern.compile("^([a-zA-Z_\\$][\\w\\$]*)$", Pattern.CASE_INSENSITIVE);
    public String id;
    public boolean isAndroidNS = false;
    public String nameFull; // element name with package
    public String name; // element name
    public String fieldName; // name of variable
    //是否被选中
    public boolean used = true;

    protected JCheckBox mCheck;
    protected JLabel mType;




    private OnCheckBoxStateChangedListener mListener;



    public LayoutItem(String name, String id) {
        // id
        final Matcher matcher = sIdPattern.matcher(id);
        if (matcher.find() && matcher.groupCount() > 0) {
            this.id = matcher.group(2);

            String androidNS = matcher.group(1);
            this.isAndroidNS = !(androidNS == null || androidNS.length() == 0);
        }

        // name
        String[] packages = name.split("\\.");
        if (packages.length > 1) {
            this.nameFull = name;
            this.name = packages[packages.length - 1];
        } else {
            this.nameFull = null;
            this.name = name;
        }

        this.fieldName = getFieldName();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));


        //check  id  name
        Box box = Box.createHorizontalBox();
        mCheck = new JCheckBox(this.id);
        mType = new JLabel(fieldName);
        box.add(Box.createHorizontalStrut(20));
        box.add(mCheck);
        box.add(Box.createHorizontalGlue());
        box.add(mType);
        box.add(Box.createHorizontalStrut(20));
        mCheck.setSelected(used);
        mCheck.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                used = mCheck.isSelected();
                if (mListener != null) {
                    mListener.changeState(used);
                }
            }
        });
        add(box);
    }


    /**
     * Generate field name if it's not done yet
     *
     * @return
     */
    private String getFieldName() {
        String[] words = this.id.split("_");
        StringBuilder sb = new StringBuilder();
        sb.append(Utils.getPrefix());

        for (int i = 0; i < words.length; i++) {
            String[] idTokens = words[i].split("\\.");
            char[] chars = idTokens[idTokens.length - 1].toCharArray();
            if (i > 0 || !Utils.isEmptyString(Utils.getPrefix())) {
                chars[0] = Character.toUpperCase(chars[0]);
            }

            sb.append(chars);
        }

        return sb.toString();
    }

    public void setListener(OnCheckBoxStateChangedListener mListener) {
        this.mListener = mListener;
    }

    public JCheckBox getCheck() {
        return mCheck;
    }

}
