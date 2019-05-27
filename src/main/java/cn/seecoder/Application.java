package cn.seecoder;

public class Application extends AST{
    AST lhs;//左树
    AST rhs;//右树

    Application(AST l, AST s){
        lhs = l;
        rhs = s;
    }

    Application(){

    }

    public AST getLhs() {
        return lhs;
    }

    public void setLhs(AST lhs) {
        this.lhs = lhs;
    }

    public AST getRhs() {
        return rhs;
    }

    public void setRhs(AST rhs) {
        this.rhs = rhs;
    }

    public String toString(){
        return lhs.toString()+" "+rhs.toString();
    }
}
