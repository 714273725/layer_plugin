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

import javax.swing.*;
import java.awt.*;

/**
 * Created by GeJianYe on 2018/1/31 0031.
 * Description:
 * Function:
 */

public class HomeMenu2 extends JPanel {
    public HomeMenu2(String root) throws Exception {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (int i = 0; i < 4; i++) {
            JPanel jPanel = addItem("item" + i, "tips" + i);
            contentPanel.add(jPanel);
        }
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(contentPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel addItem(String name, String tips) {
        JPanel injectionsPanel = new JPanel();
        injectionsPanel.setLayout(new BoxLayout(injectionsPanel, BoxLayout.LINE_AXIS));
        JLabel nameLabel = new JLabel(name);
        nameLabel.setPreferredSize(new Dimension(50, 26));
        JTextField field = new JTextField(tips);
        field.setPreferredSize(new Dimension(100, 26));
        injectionsPanel.add(nameLabel);
        injectionsPanel.add(field);
        return injectionsPanel;
    }
}
