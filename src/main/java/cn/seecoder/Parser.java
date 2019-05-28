package cn.seecoder;

import java.util.ArrayList;

/**
 * term ::= Application| LAMBDA LCID DOT term
 * <p>
 * Application ::= Application Atom| Atom
 * <p>
 * Atom ::= LPAREN term RPAREN| LCID
 *
 * @author P君
 */

public class Parser {
    Lexer lexer;

    public Parser(Lexer l) {
        lexer = l;
    }

    public AST parse() {
        AST ast = term(new ArrayList<>());
//        System.out.println(lexer.match(TokenType.EOF));
        return ast;
    }

    private AST term(ArrayList<String> ctx) {
        if (lexer.match(TokenType.LAMBDA)) {
            String Sparam = lexer.tokenvalue;
            lexer.skip(TokenType.LCID);
            lexer.match(TokenType.DOT);
            ctx.add(Sparam);
            AST body = term(ctx);
            Identifier param = new Identifier(Sparam, String.valueOf(ctx.size()-ctx.indexOf(Sparam)));
            ctx.remove(Sparam);
            return new Abstraction(param, body);
        } else {
            return application(ctx);
        }
    }

    /**
     * Application ::= Application Atom| Atom
     * P.S: We needn't take more care of whether Left Recursion will cause unstoppable Recursion, because in the atom(),
     * We can return NULL that pass to a new Application, and in the create methon in Application, We will know that whether
     * Application.right is NULL. And we if right isn't null, we will do this:
     * <p>
     * this.Left <= new Application(this.Left,this.right)
     * this.right = atom();
     * <p>
     * This is a powerful way to pass all lexer.
     */

    private AST application(ArrayList<String> ctx) {
        Application application = new Application();
        application.setLhs(atom(ctx));
        application.setRhs(atom(ctx));
        while (true) {
            if (application.getRhs() == null) return application.getLhs();
            else {
                application.setLhs(new Application(application.getLhs(), application.getRhs()));
                application.setRhs(atom(ctx));
            }
        }
    }

    private AST atom(ArrayList<String> ctx) {
        if (lexer.match(TokenType.LPAREN)) {
            return term(ctx);
        } else if (lexer.next(TokenType.LCID)) {
            String tvalue = lexer.tokenvalue;
            lexer.skip(TokenType.LCID);
            return new Identifier(tvalue, String.valueOf(ctx.size()-1-ctx.indexOf(tvalue)));
        } else if (lexer.match(TokenType.RPAREN)) {
            return null;
        } else if (lexer.match(null)) {
            lexer.match(null);
            return atom(ctx);
        } else return null;
    }

    public static void main(String[] args) {
//        Lexer lexer = new Lexer("(\\x.\\y.(x y)x)(\\x.x)(\\y.y)");
//        Lexer lexer = new Lexer("(\\n.\\f.\\x.n(\\g.\\h.h(g f))(\\u.x)(\\u.u))");
//        Lexer lexer = new Lexer("(\\x.\\y.(x y))");
        Lexer lexer = new Lexer("((\\n.\\f.\\x.f (n f x))(\\f.\\x.x))");
        Parser parser = new Parser(lexer);
        AST ast = parser.parse();
        System.out.println(ast.toString());
    }
    //\.\.\.(((2 \.\.(0 ((1 3) 3))) \.1) \.0)
}
