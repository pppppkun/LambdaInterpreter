# Lambda Interpreter

## 语法规则

```
Term ::= Application| LAMBDA LCID DOT Term

Application ::= Application Atom| Atom

Atom ::= LPAREN Term RPAREN| LCID

```

## Tokens

```
LPAREN: '('
RPAREN: ')'
LAMBDA: '\' // 为了方便使用 “\”
DOT: '.'
LCID: /[a-z][a-zA-Z]*/ 
```
### Lexer

处理 token 的辅助方法：(可以自行定义)
+ next(Token): 返回下一个 token 是否匹配 Token
+ skip(Token): 和 next 一样, 但如果匹配的话会跳过
+ match(Token): 断言 next 方法返回 true 并 skip
+ token(Token): 断言 next 方法返回 true 并返回 token

Lexer向控制台输出每次检测到的Token类型+换行。

### 抽象语法树 AST
```
 抽象语法树 ( AST ) 。lambda 演算的 AST 非常简单，因为我们只有 3 种节点： Abstraction （抽象）， Application （应用）以及 Identifier （标识符）
Abstraction 
    Identifier param;//变量
    AST body;//表达式
toString显示为：
\.body.toString()

Application
    AST lhs;左树
    AST rhs;右树
toString显示为：
(lhs.toString()空格rhs.toString()

Indentifier
    String value；//De Bruijn index
toString显示为：
value

```
### De Bruijn index
```aidl
(\x.\y.x \f.\g.g)
首先转化为：（变量保持不变，数字从0开始代码同层变量，1代表上一层次变量。）
(\x.\y.1 \f.\g.0)
toString显示为：（为了防止alpha变换造成的不一致，去掉变量）
(\.\.1 \.\.0)
```



### 求值
首先，我们需要定义，什么是形式（terms）（从语法可以推断），什么是值（values）。

我们的 term 是:
```

t1 t2   # Application
 
\x. t1  # Abstraction
 
x       # Identifier
```


value 是最终的形式，也就是说，它们不能再被求值了。在这个例子中，唯一的既是 term 又是 value 的是 abstraction（不能对函数求值，除非它被调用）。

实际的求值规则如下：

1)       t1 -> t1'

    t1 t2 -> t1' t2

2)       t2 -> t2'

    v1 t2 -> v1 t2'

3)    (\x. t12) v2 -> [x -> v2]t12


我们可以这样解读每一条规则：

+ 如果 t1 是值为 t1' 的项， t1 t2 求值为 t1' t2。即一个 application 的左侧先被求值。
+ 如果 t2 是值为 t2' 的项， v1 t2 求值为 v1 t2'。注意这里左侧的是 v1 而非 t1， 这意味着它是 value，不能再一步被求值，也就是说，只有左侧的完成之后，才会对右侧求值。
+ application (\x. t12) v2 的结果，和 t12 中出现的所有 x 被有效替换之后是一样的。注意在对 application 求值之前，两侧必须都是 value。

### 解释器

规则：

+ 首先检测其是否为 application，如果是，则对其求值：
	- 若 abstraction 的两侧都是值，只要将所有出现的 x 用给出的值替换掉； (3)
	- 否则，若左侧为值，给右侧求值；(2)
	- 如果上面都不行，只对左侧求值；(1)
+ 现在，如果下一个节点是 identifier，我们只需将它替换为它所表示的变量绑定的值。
+ 最后，如果没有规则适用于AST，这意味着它已经是一个 value，我们将它返回。

### Main方法

```
public static void main(String[] args) {

        String source = "(\\x.\\y.x)(\\x.x)(\\y.y)";

        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer);
        AST ast = parser.parse();
        AST result = Interpreter.eval(ast);


}
```
### Test
```aidl

    @Test
    public void testLexer() {
        Lexer lexer = new Lexer(sources[1]);
        Parser parser = new Parser(lexer);
        AST ast = parser.parse();

        assertEquals("LPAREN" + lineBreak+
                "LPAREN" + lineBreak+
                "LAMBDA" + lineBreak+
                "LCID" + lineBreak+
                "DOT" + lineBreak+
                "LAMBDA" + lineBreak+
                "LCID" + lineBreak+
                "DOT" + lineBreak+
                "LAMBDA" + lineBreak+
                "LCID" + lineBreak+
                "DOT" + lineBreak+
                "LCID" + lineBreak+
                "LPAREN" + lineBreak+
                "LCID" + lineBreak+
                "LCID" + lineBreak+
                "LCID" + lineBreak+
                "RPAREN" + lineBreak+
                "RPAREN" + lineBreak+
                "LPAREN" + lineBreak+
                "LAMBDA" + lineBreak+
                "LCID" + lineBreak+
                "DOT" + lineBreak+
                "LAMBDA" + lineBreak+
                "LCID" + lineBreak+
                "DOT" + lineBreak+
                "LCID" + lineBreak+
                "RPAREN" + lineBreak+
                "RPAREN"+lineBreak+
                "EOF"+lineBreak,bytes.toString());

    }

    @Test
    public void testParser() {
        Lexer lexer = new Lexer(sources[1]);
        Parser parser = new Parser(lexer);
        AST ast = parser.parse();
        assertEquals("(\\.\\.\\.(1 ((2 1) 0)) \\.\\.0)",ast.toString());


    }

    @Test
    public void testInterpreter() {
        Lexer lexer = new Lexer(sources[1]);
        Parser parser = new Parser(lexer);
        interpreter = new Interpreter(parser);

        AST ast = parser.parse();

        AST result = interpreter.eval(ast);

        assertEquals("\\.\\.(1 0)",result.toString());


    }
```