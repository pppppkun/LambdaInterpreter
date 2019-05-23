package cn.seecoder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * (\x.\y.x)(\x.x)(\y.y)
 *
 * 处理 token 的辅助方法：
 *
 * nextToken(): return This.Token
 * takeToken(): return Token.value
 * fitToken(String Type): if FIT andix++ else return false;
 *
 * @author P君
 *
 */
public class Lexer{

    private static final String DEFAULT = "LCID";
    private static final String PATTERNSTRING = "[a-z][a-zA-Z]*";
    private String source;
    private char[] sources;
    private static int index = 0;
    private Token token = new Token();

    public Lexer(String s){
        source = s;
        sources = source.toCharArray();
    }

    /**
     *
     * nextToken: detect next String is what Type in Token.
     *
     * @return token
     */

    public Token nextToken(){
        Pattern pattern = Pattern.compile(PATTERNSTRING);
        Matcher matcher;
        char value = sources[index];
        StringBuilder temp = new StringBuilder();
        token.setType(value);
        if(token.getType().equals(DEFAULT)){
            do{
                temp.append(sources[index]);
                matcher = pattern.matcher(temp);
                index++;
            }while(matcher.matches());
            index=index-2;
            temp.deleteCharAt(temp.length()-1);
            token.setMyValue(temp.toString());
        }
        index++;
        return token;
    }

    public boolean fitType(String Type){
        if(token.getType().equals(Type)){
            index++;
            return true;
        }
        else{
            return false;
        }
    }

    public String takeToken(){
        String value = token.getMyValue();
        this.nextToken();
        return value;
    }


    /**
     * translate: Use this method will translate Lambda expression to Token's Type. This method will detect the
     *            correct of nextToken().
     *
     */
    private void translate(){
        while(index<sources.length){
            nextToken();
            if(!fitType("")) System.out.print(token.getType()+" ");
        }
    }

    public static void main(String[] args){
        Lexer lexer = new Lexer("(\\pO.\\y.y(y pO))(\\x.x)(\\y.y)");
        lexer.translate();
    }
}
