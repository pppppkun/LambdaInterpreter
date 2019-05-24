package cn.seecoder;

/**
 *
 * term ::= Application| LAMBDA LCID DOT term
 *
 * Application ::= Application Atom| Atom
 *
 * Atom ::= LPAREN term RPAREN| LCID
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
        ast = term();
        return ast;
    }

    /**
     * term ::= Application| LAMBDA LCID DOT term
     */
    private AST term(){
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
     *
     * Application ::= Application Atom| Atom
     * P.S: We needn't take more care of whether Left Recursion will cause unstoppable Recursion, because in the atom(),
     * We can return NULL that pass to a new Application, and in the create methon in Application, We will know that whether
     * Application.right is NULL. And we if right isn't null, we will do this:
     *
     *                  this.Left <= new Application(this.Left,this.right)
     *                  this.right = atom();
     *
     *              This is a powerful way to pass all lexer.
     *
     */

    private AST application(){
        Application application = new Application();
        application.setLeft(atom());
        application.setRight(atom());
        while (true){
            if(application.getRight()==null) return application.getLeft();
            else{
                application.setLeft(new Application(application.getLeft(),application.getRight()));
                application.setRight(atom());
            }
        }
    }

    /**
     * Atom ::= LPAREN term RPAREN| LCID
     */

    private AST atom(){
        if(lexer.fitType(Token.Type.LPAREN.toString())){
            return term();
        }else if(lexer.fitType(Token.Type.LCID.toString())){
            return new Identifier(lexer.returnLCID());
        }
        else if(lexer.fitType(Token.Type.RPAREN.toString())){
            return null;
        }
        return null;
    }
}
