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

    private Lexer lexer;

    public Parser(Lexer l){
        lexer = l;
    }

    public AST parse(){
        return null;
    }

    /**
     * Term ::= Application| LAMBDA LCID DOT Term
     */
    private void term(){

    }

    /**
     * Application ::= Application Atom| Atom
     */
    private void application(){

    }

    /**
     * Atom ::= LPAREN Term RPAREN| LCID
     */

    private void atom(){
        if(lexer.nextToken().getType().equals("LPAREN")){

        }
    }
}
