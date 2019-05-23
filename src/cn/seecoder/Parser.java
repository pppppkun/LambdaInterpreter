package cn.seecoder;

/**
 *
 * Term ::= Application| LAMBDA LCID DOT Term
 *
 * Application ::= Application Atom| Atom
 *
 * Atom ::= LPAREN Term RPAREN| LCID
 *
 * @author P君
 *
 */

public class Parser {

    private AST ast = new AST();
    private Lexer lexer;
    public Parser(Lexer l){
        lexer = l;
    }

    public AST parse(){
        ast.node = term();
        return ast;
    }

    /**
     * Term ::= Application| LAMBDA LCID DOT Term
     */
    private Node term(){
        if(lexer.fitType(Token.Type.LAMBDA.toString())){
            String param = lexer.takeToken();
            lexer.takeToken();
            return new Abstraction(param, term());
        }
        else{
            return application();
        }
    }

    /**
     * Application ::= Application Atom| Atom
     */

    private Node application(){
        return new Application(atom(),atom());
    }

    /**
     * Atom ::= LPAREN Term RPAREN| LCID
     */

    private Node atom(){
        if(lexer.fitType(Token.Type.LPAREN.toString())){
            return term();
        }else if(lexer.fitType(Token.Type.LCID.toString())){
            return new Identifier(lexer.takeToken());
        }
        else if(lexer.fitType(Token.Type.RPAREN.toString())){
            return null;
        }
        return null;
    }
}
