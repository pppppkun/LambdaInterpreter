package cn.seecoder;

public class Abstraction extends AST {
    Identifier param;//变量
    AST body;//表达式

    Abstraction(Identifier p, AST b){
        param = p;
        body = b;
    }

    public String toString(){
        return "\\."+body.toString();
    }
}
