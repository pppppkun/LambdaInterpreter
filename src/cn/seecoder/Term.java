package cn.seecoder;

public class Term extends Leaf {

    private Term term;
    private Application application;
    private String LCID;

    public Term(String param,Term term){
        this.LCID = param;
        this.term = term;
    }

    public Term(Application p){
        this.application = p;
    }

}
