package cn.seecoder;

public class Interpreter {

    public static AST eval(AST ast){
        return null;
    }

    public static void main(String[] args) {
        // write your code here
        String source = "(\\x.\\y.x)(\\x.x)(\\y.y)";

        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer);
        AST ast = parser.parse();
        AST result = Interpreter.eval(ast);

        System.out.println(result.toString());

    }
}
