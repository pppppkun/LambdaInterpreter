package cn.seecoder;

/**
 *
 * It's believed that every application must have two nodes, and right node maybe is null.
 *
 */

public class Application extends Node{

    //Application ::= Application Atom| Atom

    public Application(Node left,Node right){
        this.left = left;
        this.right = right;
    }


    public void setLeft(Node left) {
        this.left = left;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public Node getRight() {
        return right;
    }
}
