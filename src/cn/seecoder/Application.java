package cn.seecoder;

/**
 *
 * It's believed that every application must have two nodes, and right node maybe is null.
 *
 */

public class Application extends AST{

    //Application ::= Application Atom| Atom

    public Application(AST left,AST right){
        this.left = left;
        this.right = right;
    }

    public Application(){
        ;
    }


    public void setLeft(AST left) {
        this.left = left;
    }

    public void setRight(AST right) {
        this.right = right;
    }
}
