package cn.seecoder;

import java.util.ArrayList;

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
    Lexer lexer;

    public Parser(Lexer l){
        lexer = l;
    }

    public AST parse(){
        AST ast = term(new ArrayList<>());
//        System.out.println(lexer.match(TokenType.EOF));
        return ast;
    }

    private AST term(ArrayList<String> ctx){
        if(lexer.match(TokenType.LAMBDA)){
            String Sparam = lexer.tokenvalue;
            lexer.skip(TokenType.LCID);
            lexer.match(TokenType.DOT);
            ctx.add(Sparam);
            AST body = term(ctx);
            Identifier param = new Identifier(Sparam, String.valueOf(ctx.indexOf(Sparam)));
            return new Abstraction(param,body);
        }
        else{
            return application(ctx);
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

    private AST application(ArrayList<String> ctx){
        Application application = new Application();
        application.setLhs(atom(ctx));
        application.setRhs(atom(ctx));
        while (true){
            if(application.getRhs()==null) return application.getLhs();
            else{
                application.setLhs(new Application(application.getLhs(),application.getRhs()));
                application.setRhs(atom(ctx));
            }
        }
    }

    private AST atom(ArrayList<String> ctx){
        if(lexer.match(TokenType.LPAREN)){
            return term(ctx);
        }
        else if(lexer.next(TokenType.LCID)){
            String tvalue = lexer.tokenvalue;
            lexer.skip(TokenType.LCID);
            return new Identifier(tvalue, String.valueOf(ctx.indexOf(lexer.tokenvalue)));
        }
        else if(lexer.match(TokenType.RPAREN)){
            return null;
        }
        return null;
    }

    public static void main(String[] args){
        Lexer lexer = new Lexer("(\\x.\\y.x)(\\x.x)(\\y.y)");
        Parser parser = new Parser(lexer);
        AST ast = parser.parse();
        System.out.println(ast.toString());
    }

}
