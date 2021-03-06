package cn.seecoder;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    public StringBuilder builder = new StringBuilder();
    public String source;
    public int index;
    public TokenType token;
    public String tokenvalue;
    private char[] sources;
    private static final String PATTERNSTRING = "[a-z][a-zA-Z]*";
    private String LCID = "";
    private boolean flag = true;

    private static final HashMap<Character, TokenType> types = new HashMap<>();

    static {
        types.put('.', TokenType.DOT);
        types.put('(', TokenType.LPAREN);
        types.put(')', TokenType.RPAREN);
        types.put('\\', TokenType.LAMBDA);
    }

    public Lexer(String s) {
        index = 0;
        source = s;
        sources = source.toCharArray();
        nextToken();
    }

    private void setType(char s) {
        if (types.containsKey(s)) {
            token = types.get(s);
            return;
        }
        setLCID(String.valueOf(s));
    }

    private void setLCID(String s) {
        token = null;
        Pattern pattern = Pattern.compile(PATTERNSTRING);
        Matcher matcher = pattern.matcher(s);
        if (matcher.matches()) {
            token = TokenType.LCID;
            tokenvalue = s;
        } else {
            tokenvalue = "";
        }
    }

    public void setMyValue(String s) {
        tokenvalue = s;
        token = TokenType.LCID;
    }

    //get next token
    private TokenType nextToken() {
        if (index == sources.length&&flag){
            flag = false;
            System.out.println(TokenType.EOF);
            builder.append("EOF\n");
            return TokenType.EOF;
        }
        else if(flag==false) return null;
        Pattern pattern = Pattern.compile(PATTERNSTRING);
        Matcher matcher;
        char value = sources[index];
        StringBuilder temp = new StringBuilder();
        setType(value);
        if (token == TokenType.LCID) {
            do {
                temp.append(sources[index]);
                matcher = pattern.matcher(temp);
                index++;
            } while (matcher.matches());
            index = index - 2;
            temp.deleteCharAt(temp.length() - 1);
            setMyValue(temp.toString());
            LCID = temp.toString();
        }
        index++;
        if(token != null) {
            System.out.println(token);
            builder.append(token.toString()+"\n");
        }
        return token;
    }

    //check token == t
    public boolean next(TokenType t) {
        if (token == t) return true;
        else return false;
    }

    //assert matching the token type, and move next token
    public boolean match(TokenType t) {
        if (token == t) {
            nextToken();
            return true;
        } else return false;
    }

    //skip token  and move next token
    public boolean skip(TokenType t) {
        if (token == t) {
            nextToken();
            return true;
        } else {
            nextToken();
            return false;
        }
    }
    // lexer's toString.
    private void translate() {
        while (index < sources.length) {
            nextToken();
        }
        System.out.println("EOF");
    }
}
