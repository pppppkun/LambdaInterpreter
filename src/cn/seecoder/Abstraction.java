package cn.seecoder;

public class Abstraction extends AST{

    private String param;

    public Abstraction(String a,AST body){
        param = a;
        right = body;
    }

}
