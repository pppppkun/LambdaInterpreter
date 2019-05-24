package cn.seecoder;

public class Interpreter {

    /**
     *
     * First we should judge the highest node whether is application, than we should judge the it's
     * left and right. We will use Recursive Descent to analyze every nodes from right to left because
     * we should recursion from the right.
     * if highest node isn't application, they only reference to the abstraction and Identifier. so we
     * needn't judge.
     *
     * @param ast
     * @return AST
     */

    public static AST eval(AST ast){
        while(true){
            if(ast instanceof Application){
                if(ast.getRight()!=null&&ast.getLeft()!=null){
//                    ast = substitute(ast.getRight(),ast.getLeft().getRight());
                }
                else if(ast.getLeft()!=null){
                    ((Application) ast).setRight(eval(ast.getRight()));
                }
                else{
                    ((Application) ast).setLeft(eval(ast.getLeft()));
                }
            }
            else if(ast!=null){
                return ast;
            }
        }
    }


    public static void main(String[] args) {
        // write your code here
        String source = "(\\x.\\y.xy)(\\x.x)(\\y.y)";

        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer);
        AST ast = parser.parse();
//        AST result = Interpreter.eval(ast);
        ast.print();
    }
}
