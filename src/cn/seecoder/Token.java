package cn.seecoder;

import java.util.HashMap;
import java.util.regex.*;

/**
 * LPAREN: '('
 * RPAREN: ')'
 * LAMBDA: '\' // 为了方便使用 “\”
 * DOT: '.'
 * LCID: /[a-z][a-zA-Z]* /
 *
 * 这个类将解析Lexer传进来的字符串/字符，并且解析成一个Token对象返回回去，这个Token对象应该包含type和value
 *
 * type 是上面五个名字，value 是后面的符号
 * @author P君
 */


public class Token {
    private static final HashMap<Character,String> types = new HashMap<>();
    private String type = "";
    private String myValue = "";
    private static final String patternString = "[a-z]";
    public enum Type{
        DOT,
        LPAREN,
        RPAREN,
        LAMBDA,
        LCID;

    }
    //通过键值对查找

    public Token(){
        types.put('.',Type.DOT.toString());
        types.put('(',Type.LPAREN.toString());
        types.put(')',Type.RPAREN.toString());
        types.put('\\',Type.LAMBDA.toString());
    }

    //设置Type

    public void setType(char s){
        if(types.containsKey(s)){
            type = types.get(s);
            myValue = String.valueOf(s);
            return;
        }
        setLCID(String.valueOf(s));
    }
    //判断LCID是一个单词的情况

    private void setLCID(String s){
        type = "";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(s);
        if(matcher.matches()){
            type = Type.LCID.toString();
            myValue = s;
        }
        else{
            myValue= "";
        }
    }

    //直接设置value
    public void setMyValue(String s){
        myValue = s;
        type = Type.LCID.toString();
    }

    public String getType(){
        return type;
    }

    public String getMyValue(){
        return myValue;
    }
}
