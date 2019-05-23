package cn.seecoder;

public class Abstraction extends Node{

    private String param;

    public Abstraction(String a,Node body){
        param = a;
        right = body;
    }

}
