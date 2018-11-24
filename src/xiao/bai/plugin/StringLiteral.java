package xiao.bai.plugin;
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

import java.util.Formatter;

/**
 * Created by GeJianYe on 2018/1/31 0031.
 * Description:
 * Function:
 */

public class StringLiteral {
    private final String value;
    private final String literal;

    public static StringLiteral forValue(String value) {
        return new StringLiteral(value, stringLiteral(value));
    }

    private static String stringLiteral(String value) {
        StringBuilder result = new StringBuilder();
        result.append('"');

        for(int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            switch(c) {
                case '\b':
                    result.append("\\b");
                    break;
                case '\t':
                    result.append("\\t");
                    break;
                case '\n':
                    result.append("\\n");
                    break;
                case '\f':
                    result.append("\\f");
                    break;
                case '\r':
                    result.append("\\r");
                    break;
                case '"':
                    result.append("\\\"");
                    break;
                case '\\':
                    result.append("\\\\");
                    break;
                default:
                    if(Character.isISOControl(c)) {
                        (new Formatter(result)).format("\\u%04x", new Object[]{Integer.valueOf(c)});
                    } else {
                        result.append(c);
                    }
            }
        }

        result.append('"');
        return result.toString();
    }

    private StringLiteral(String value, String literal) {
        this.value = value;
        this.literal = literal;
    }

    public String value() {
        return this.value;
    }

    public String literal() {
        return this.literal;
    }

    public String toString() {
        return this.literal;
    }

    public boolean equals(Object obj) {
        return obj == this?true:(obj instanceof StringLiteral
                ?this.value.equals(((StringLiteral)obj).value):false);
    }

    public int hashCode() {
        return this.value.hashCode();
    }
}
