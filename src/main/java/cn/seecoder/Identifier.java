package cn.seecoder;

public class Identifier extends AST {

    String name; //名字
    String value;//De Bruijn index值
    int DBindex;

    public Identifier(String n,String v){

        name = n;
        value = v;
    }

    public int getDBindex() {
        return Integer.valueOf(value);
    }

    public String toString(){
        return value;
    }
}
