package cn.seecoder;


import java.util.List;

/**
 *
 * 抽象语法树 ( AST ) 。lambda 演算的 AST 非常简单，因为我们只有 3 种节点： Abstraction （抽象）， Application （应用）以及 Identifier （标识符）
 *
 *
 * t1 t2   # Application
 *
 * \x. t1  # Abstraction
 *
 * x       # Identifier
 *
 * @author P君
 *
 */

public class AST {

    public List<Leaf> leaves;

    public void add(Leaf l){
        leaves.add(l);
    }

    public void delete(Leaf l){
    }

}
