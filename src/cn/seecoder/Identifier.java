package cn.seecoder;

public class Identifier extends AST{



    public Identifier(String a){
        value = a;
    }

    public String getValue() {
        return value;
    }
}
