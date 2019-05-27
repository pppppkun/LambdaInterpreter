package cn.seecoder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class InterpreterTest {
    PrintStream console = null;
    ByteArrayOutputStream bytes = null;
    String lineBreak;

    Interpreter interpreter;
    String initInfo;
    String steps;

    static String ZERO = "(\\f.\\x.x)";
    static String SUCC = "(\\n.\\f.\\x.f (n f x))";
    static String ONE = app(SUCC, ZERO);
    static String TWO = app(SUCC, ONE);
    static String THREE = app(SUCC, TWO);
    static String FOUR = app(SUCC, THREE);
    static String FIVE = app(SUCC, FOUR);
    static String PLUS = "(\\m.\\n.((m " + SUCC + ") n))";
    static String POW = "(\\b.\\e.e b)";       // POW not ready
    static String PRED = "(\\n.\\f.\\x.n(\\g.\\h.h(g f))(\\u.x)(\\u.u))";
    static String SUB = "(\\m.\\n.n" + PRED + "m)";
    static String TRUE = "(\\x.\\y.x)";
    static String FALSE = "(\\x.\\y.y)";
    static String AND = "(\\p.\\q.p q p)";
    static String OR = "(\\p.\\q.p p q)";
    static String NOT = "(\\p.\\a.\\b.p b a)";
    static String IF = "(\\p.\\a.\\b.p a b)";
    static String ISZERO = "(\\n.n(\\x." + FALSE + ")" + TRUE + ")";
    static String LEQ = "(\\m.\\n." + ISZERO + "(" + SUB + "m n))";
    static String EQ = "(\\m.\\n." + AND + "(" + LEQ + "m n)(" + LEQ + "n m))";
    static String MAX = "(\\m.\\n." + IF + "(" + LEQ + " m n)n m)";
    static String MIN = "(\\m.\\n." + IF + "(" + LEQ + " m n)m n)";

    static String app(String func, String x) {
        return "(" + func + x + ")";
    }
    static String app(String func, String x, String y) {
        return "(" + "(" + func + x + ")" + y + ")";
    }
    static String app(String func, String cond, String x, String y) {
        return "(" + func + cond + x + y + ")";
    }

    String[] sources = {
            ZERO,//0
            ONE,//1
            TWO,//2
            THREE,//3
            app(PLUS, ZERO, ONE),//4
            app(PLUS, TWO, THREE),//5
            app(POW, TWO, TWO),//6
            app(PRED, ONE),//7
            app(PRED, TWO),//8
            app(SUB, FOUR, TWO),//9
            app(AND, TRUE, TRUE),//10
            app(AND, TRUE, FALSE),//11
            app(AND, FALSE, FALSE),//12
            app(OR, TRUE, TRUE),//13
            app(OR, TRUE, FALSE),//14
            app(OR, FALSE, FALSE),//15
            app(NOT, TRUE),//16
            app(NOT, FALSE),//17
            app(IF, TRUE, TRUE, FALSE),//18
            app(IF, FALSE, TRUE, FALSE),//19
            app(IF, app(OR, TRUE, FALSE), ONE, ZERO),//20
            app(IF, app(AND, TRUE, FALSE), FOUR, THREE),//21
            app(ISZERO, ZERO),//22
            app(ISZERO, ONE),//23
            app(LEQ, THREE, TWO),//24
            app(LEQ, TWO, THREE),//25
            app(EQ, TWO, FOUR),//26
            app(EQ, FIVE, FIVE),//27
            app(MAX, ONE, TWO),//28
            app(MAX, FOUR, TWO),//29
            app(MIN, ONE, TWO),//30
            app(MIN, FOUR, TWO),//31
    };

    public void testLexer(int n) {
        Lexer lexer = new Lexer(sources[n]);
        Parser parser = new Parser(lexer);
        parser.parse();
    }

    public AST testParser(int n) {
        Lexer lexer = new Lexer(sources[n]);
        Parser parser = new Parser(lexer);
        return parser.parse();
    }

    public AST testInterpreter(int n) {
        Lexer lexer = new Lexer(sources[n]);
        Parser parser = new Parser(lexer);
        interpreter = new Interpreter(parser);
        return interpreter.eval();
    }

    @Before
    public void setUp() {
        lineBreak = System.getProperty("line.separator");
        console = System.out;
        bytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bytes));
    }

    @After
    public void tearDown() {
        System.setOut(console);
    }

    @Test
    public void test0_ZERO_testLexer() {
        testLexer(0);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }

    @Test
    public void test0_ZERO_testParser() {
        AST ast = testParser(0);
        assertEquals("\\.\\.0", ast.toString());
    }

    @Test
    public void test0_ZERO_testInterpreter() {
        AST result = testInterpreter(0);
        assertEquals("\\.\\.0", result.toString());
    }

    @Test
    public void test1_SUCC_testLexer() {
        testLexer(1);
        assertEquals("LPAREN" + lineBreak +
                "LPAREN" + lineBreak +
                "LAMBDA" + lineBreak +
                "LCID" + lineBreak +
                "DOT" + lineBreak +
                "LAMBDA" + lineBreak +
                "LCID" + lineBreak +
                "DOT" + lineBreak +
                "LAMBDA" + lineBreak +
                "LCID" + lineBreak +
                "DOT" + lineBreak +
                "LCID" + lineBreak +
                "LPAREN" + lineBreak +
                "LCID" + lineBreak +
                "LCID" + lineBreak +
                "LCID" + lineBreak +
                "RPAREN" + lineBreak +
                "RPAREN" + lineBreak +
                "LPAREN" + lineBreak +
                "LAMBDA" + lineBreak +
                "LCID" + lineBreak +
                "DOT" + lineBreak +
                "LAMBDA" + lineBreak +
                "LCID" + lineBreak +
                "DOT" + lineBreak +
                "LCID" + lineBreak +
                "RPAREN" + lineBreak +
                "RPAREN" + lineBreak +
                "EOF" + lineBreak, bytes.toString());
    }

    @Test
    public void test1_SUCC_testParser() {
        AST ast = testParser(1);
        assertEquals("(\\.\\.\\.(1 ((2 1) 0)) \\.\\.0)", ast.toString());
    }

    @Test
    public void test1_SUCC_testInterpreter() {
        AST result = testInterpreter(1);
        assertEquals("\\.\\.(1 0)", result.toString());
    }

    @Test
    public void test2_SUCC_testLexer() {
        testLexer(2);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }

    @Test
    public void test2_SUCC_testParser() {
        AST ast = testParser(2);
        assertEquals("(\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0))", ast.toString());
    }

    @Test
    public void test2_SUCC_testInterpreter() {
        AST result = testInterpreter(2);
        assertEquals("\\.\\.(1 (1 0))", result.toString());
    }

    @Test
    public void test3_SUCC_testLexer() {
        testLexer(3);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }

    @Test
    public void test3_SUCC_testParser() {
        AST ast = testParser(3);
        assertEquals("(\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0)))", ast.toString());
    }

    @Test
    public void test3_SUCC_testInterpreter() {
        AST result = testInterpreter(3);
        assertEquals("\\.\\.(1 (1 (1 0)))", result.toString());
    }

    @Test
    public void test4_PLUS_testLexer() {
        testLexer(4);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test4_PLUS_testParser() {
        AST ast = testParser(4);
        assertEquals("((\\.\\.((1 \\.\\.\\.(1 ((2 1) 0))) 0) \\.\\.0) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0))", ast.toString());
    }

    @Test
    public void test4_PLUS_testInterpreter() {
        AST result = testInterpreter(4);
        assertEquals("\\.\\.(1 0)",result.toString());
    }

    @Test
    public void test5_PLUS_testLexer() {
        testLexer(5);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test5_PLUS_testParser() {
        AST ast = testParser(5);
        assertEquals("((\\.\\.((1 \\.\\.\\.(1 ((2 1) 0))) 0) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0))) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0))))", ast.toString());
    }

    @Test
    public void test5_PLUS_testInterpreter() {
        AST result = testInterpreter(5);
        assertEquals("\\.\\.(1 (1 (1 (1 (1 0)))))",result.toString());
    }

    @Test
    public void test6_POW_testLexer() {
        testLexer(6);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test6_POW_testParser() {
        AST ast = testParser(6);
        assertEquals("((\\.\\.(0 1) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0))) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0)))", ast.toString());
    }

    @Test
    public void test6_POW_testInterpreter() {
        AST result = testInterpreter(6);
        assertEquals("\\.\\.(1 (1 (1 (1 0))))",result.toString());
    }

    @Test
    public void test7_PRED_testLexer() {
        testLexer(7);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test7_PRED_testParser() {
        AST ast = testParser(7);
        assertEquals("(\\.\\.\\.(((2 \\.\\.(0 (1 3))) \\.1) \\.0) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0))", ast.toString());
    }

    @Test
    public void test7_PRED_testInterpreter() {
        AST result = testInterpreter(7);
        assertEquals("\\.\\.0",result.toString());
    }

    @Test
    public void test8_PRED_testLexer() {
        testLexer(8);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test8_PRED_testParser() {
        AST ast = testParser(8);
        assertEquals("(\\.\\.\\.(((2 \\.\\.(0 (1 3))) \\.1) \\.0) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0)))", ast.toString());
    }

    @Test
    public void test8_PRED_testInterpreter() {
        AST result = testInterpreter(8);
        assertEquals("\\.\\.(1 0)",result.toString());
    }

    @Test
    public void test9_SUB_testLexer() {
        testLexer(9);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test9_SUB_testParser() {
        AST ast = testParser(9);
        assertEquals("((\\.\\.((0 \\.\\.\\.(((2 \\.\\.(0 (1 3))) \\.1) \\.0)) 1) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0))))) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0)))", ast.toString());
    }

    @Test
    public void test9_SUB_testInterpreter() {
        AST result = testInterpreter(9);
        assertEquals("\\.\\.(1 (1 0))",result.toString());
    }

    @Test
    public void test10_AND_testLexer() {
        testLexer(10);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test10_AND_testParser() {
        AST ast = testParser(10);
        assertEquals("((\\.\\.((1 0) 1) \\.\\.1) \\.\\.1)", ast.toString());
    }

    @Test
    public void test10_AND_testInterpreter() {
        AST result = testInterpreter(10);
        assertEquals("\\.\\.1",result.toString());
    }

    @Test
    public void test11_AND_testLexer() {
        testLexer(11);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test11_AND_testParser() {
        AST ast = testParser(11);
        assertEquals("((\\.\\.((1 0) 1) \\.\\.1) \\.\\.0)", ast.toString());
    }

    @Test
    public void test11_AND_testInterpreter() {
        AST result = testInterpreter(11);
        assertEquals("\\.\\.0",result.toString());
    }

    @Test
    public void test12_AND_testLexer() {
        testLexer(12);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test12_AND_testParser() {
        AST ast = testParser(12);
        assertEquals("((\\.\\.((1 0) 1) \\.\\.0) \\.\\.0)", ast.toString());
    }

    @Test
    public void test12_AND_testInterpreter() {
        AST result = testInterpreter(12);
        assertEquals("\\.\\.0",result.toString());
    }

    @Test
    public void test13_OR_testLexer() {
        testLexer(13);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test13_OR_testParser() {
        AST ast = testParser(13);
        assertEquals("((\\.\\.((1 1) 0) \\.\\.1) \\.\\.1)", ast.toString());
    }

    @Test
    public void test13_OR_testInterpreter() {
        AST result = testInterpreter(13);
        assertEquals("\\.\\.1",result.toString());
    }

    @Test
    public void test14_OR_testLexer() {
        testLexer(14);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test14_OR_testParser() {
        AST ast = testParser(14);
        assertEquals("((\\.\\.((1 1) 0) \\.\\.1) \\.\\.0)", ast.toString());
    }

    @Test
    public void test14_OR_testInterpreter() {
        AST result = testInterpreter(14);
        assertEquals("\\.\\.1",result.toString());
    }

    @Test
    public void test15_OR_testLexer() {
        testLexer(15);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test15_OR_testParser() {
        AST ast = testParser(15);
        assertEquals("((\\.\\.((1 1) 0) \\.\\.0) \\.\\.0)", ast.toString());
    }

    @Test
    public void test15_OR_testInterpreter() {
        AST result = testInterpreter(15);
        assertEquals("\\.\\.0",result.toString());
    }

    @Test
    public void test16_NOT_testLexer() {
        testLexer(16);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test16_NOT_testParser() {
        AST ast = testParser(16);
        assertEquals("(\\.\\.\\.((2 0) 1) \\.\\.1)", ast.toString());
    }

    @Test
    public void test16_NOT_testInterpreter() {
        AST result = testInterpreter(16);
        assertEquals("\\.\\.0",result.toString());
    }

    @Test
    public void test17_NOT_testLexer() {
        testLexer(17);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test17_NOT_testParser() {
        AST ast = testParser(17);
        assertEquals("(\\.\\.\\.((2 0) 1) \\.\\.0)", ast.toString());
    }

    @Test
    public void test17_NOT_testInterpreter() {
        AST result = testInterpreter(17);
        assertEquals("\\.\\.1",result.toString());
    }

    @Test
    public void test18_IF_testLexer() {
        testLexer(18);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test18_IF_testParser() {
        AST ast = testParser(18);
        assertEquals("(((\\.\\.\\.((2 1) 0) \\.\\.1) \\.\\.1) \\.\\.0)", ast.toString());
    }

    @Test
    public void test18_IF_testInterpreter() {
        AST result = testInterpreter(18);
        assertEquals("\\.\\.1",result.toString());
    }

    @Test
    public void test19_IF_testLexer() {
        testLexer(19);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test19_IF_testParser() {
        AST ast = testParser(19);
        assertEquals("(((\\.\\.\\.((2 1) 0) \\.\\.0) \\.\\.1) \\.\\.0)", ast.toString());
    }

    @Test
    public void test19_IF_testInterpreter() {
        AST result = testInterpreter(19);
        assertEquals("\\.\\.0",result.toString());
    }

    @Test
    public void test20_IF_OR_testLexer() {
        testLexer(20);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test20_IF_OR_testParser() {
        AST ast = testParser(20);
        assertEquals("(((\\.\\.\\.((2 1) 0) ((\\.\\.((1 1) 0) \\.\\.1) \\.\\.0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0)) \\.\\.0)", ast.toString());
    }

    @Test
    public void test20_IF_OR_testInterpreter() {
        AST result = testInterpreter(20);
        assertEquals("\\.\\.(1 0)",result.toString());
    }

    @Test
    public void test21_IF_AND_testLexer() {
        testLexer(21);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test21_IF_AND_testParser() {
        AST ast = testParser(21);
        assertEquals("(((\\.\\.\\.((2 1) 0) ((\\.\\.((1 0) 1) \\.\\.1) \\.\\.0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0))))) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0))))", ast.toString());
    }

    @Test
    public void test21_IF_AND_testInterpreter() {
        AST result = testInterpreter(21);
        assertEquals("\\.\\.(1 (1 (1 0)))",result.toString());
    }

    @Test
    public void test22_ISZERO_testLexer() {
        testLexer(22);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test22_ISZERO_testParser() {
        AST ast = testParser(22);
        assertEquals("(\\.((0 \\.\\.\\.0) \\.\\.1) \\.\\.0)", ast.toString());
    }

    @Test
    public void test22_ISZERO_testInterpreter() {
        AST result = testInterpreter(22);
        assertEquals("\\.\\.1",result.toString());
    }

    @Test
    public void test23_ISZERO_testLexer() {
        testLexer(23);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test23_ISZERO_testParser() {
        AST ast = testParser(23);
        assertEquals("(\\.((0 \\.\\.\\.0) \\.\\.1) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0))", ast.toString());
    }

    @Test
    public void test23_ISZERO_testInterpreter() {
        AST result = testInterpreter(23);
        assertEquals("\\.\\.0",result.toString());
    }

    @Test
    public void test24_LEQ_testLexer() {
        testLexer(24);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test24_LEQ_testParser() {
        AST ast = testParser(24);
        assertEquals("((\\.\\.(\\.((0 \\.\\.\\.0) \\.\\.1) ((\\.\\.((0 \\.\\.\\.(((2 \\.\\.(0 (1 3))) \\.1) \\.0)) 1) 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0)))) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0)))", ast.toString());
    }

    @Test
    public void test24_LEQ_testInterpreter() {
        AST result = testInterpreter(24);
        assertEquals("\\.\\.0",result.toString());
    }

    @Test
    public void test25_LEQ_testLexer() {
        testLexer(25);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test25_LEQ_testParser() {
        AST ast = testParser(25);
        assertEquals("((\\.\\.(\\.((0 \\.\\.\\.0) \\.\\.1) ((\\.\\.((0 \\.\\.\\.(((2 \\.\\.(0 (1 3))) \\.1) \\.0)) 1) 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0))) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0))))", ast.toString());
    }

    @Test
    public void test25_LEQ_testInterpreter() {
        AST result = testInterpreter(25);
        assertEquals("\\.\\.1",result.toString());
    }

    @Test
    public void test26_EQ_testLexer() {
        testLexer(26);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test26_EQ_testParser() {
        AST ast = testParser(26);
        assertEquals("((\\.\\.((\\.\\.((1 0) 1) ((\\.\\.(\\.((0 \\.\\.\\.0) \\.\\.1) ((\\.\\.((0 \\.\\.\\.(((2 \\.\\.(0 (1 3))) \\.1) \\.0)) 1) 1) 0)) 1) 0)) ((\\.\\.(\\.((0 \\.\\.\\.0) \\.\\.1) ((\\.\\.((0 \\.\\.\\.(((2 \\.\\.(0 (1 3))) \\.1) \\.0)) 1) 1) 0)) 0) 1)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0))) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0)))))", ast.toString());
    }

    @Test
    public void test26_EQ_testInterpreter() {
        AST result = testInterpreter(26);
        assertEquals("\\.\\.0",result.toString());
    }

    @Test
    public void test27_EQ_testLexer() {
        testLexer(27);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test27_EQ_testParser() {
        AST ast = testParser(27);
        assertEquals("((\\.\\.((\\.\\.((1 0) 1) ((\\.\\.(\\.((0 \\.\\.\\.0) \\.\\.1) ((\\.\\.((0 \\.\\.\\.(((2 \\.\\.(0 (1 3))) \\.1) \\.0)) 1) 1) 0)) 1) 0)) ((\\.\\.(\\.((0 \\.\\.\\.0) \\.\\.1) ((\\.\\.((0 \\.\\.\\.(((2 \\.\\.(0 (1 3))) \\.1) \\.0)) 1) 1) 0)) 0) 1)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0)))))) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0))))))", ast.toString());
    }

    @Test
    public void test27_EQ_testInterpreter() {
        AST result = testInterpreter(27);
        assertEquals("\\.\\.1",result.toString());
    }

    @Test
    public void test28_MAX_testLexer() {
        testLexer(28);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test28_MAX_testParser() {
        AST ast = testParser(28);
        assertEquals("((\\.\\.(((\\.\\.\\.((2 1) 0) ((\\.\\.(\\.((0 \\.\\.\\.0) \\.\\.1) ((\\.\\.((0 \\.\\.\\.(((2 \\.\\.(0 (1 3))) \\.1) \\.0)) 1) 1) 0)) 1) 0)) 0) 1) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0)))", ast.toString());
    }

    @Test
    public void test28_MAX_testInterpreter() {
        AST result = testInterpreter(28);
        assertEquals("\\.\\.(1 (1 0))",result.toString());
    }

    @Test
    public void test29_MAX_testLexer() {
        testLexer(29);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test29_MAX_testParser() {
        AST ast = testParser(29);
        assertEquals("((\\.\\.(((\\.\\.\\.((2 1) 0) ((\\.\\.(\\.((0 \\.\\.\\.0) \\.\\.1) ((\\.\\.((0 \\.\\.\\.(((2 \\.\\.(0 (1 3))) \\.1) \\.0)) 1) 1) 0)) 1) 0)) 0) 1) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0))))) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0)))", ast.toString());
    }

    @Test
    public void test29_MAX_testInterpreter() {
        AST result = testInterpreter(29);
        assertEquals("\\.\\.(1 (1 (1 (1 0))))",result.toString());
    }

    @Test
    public void test30_MIN_testLexer() {
        testLexer(30);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test30_MIN_testParser() {
        AST ast = testParser(30);
        assertEquals("((\\.\\.(((\\.\\.\\.((2 1) 0) ((\\.\\.(\\.((0 \\.\\.\\.0) \\.\\.1) ((\\.\\.((0 \\.\\.\\.(((2 \\.\\.(0 (1 3))) \\.1) \\.0)) 1) 1) 0)) 1) 0)) 1) 0) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0)))", ast.toString());
    }

    @Test
    public void test30_MIN_testInterpreter() {
        AST result = testInterpreter(30);
        assertEquals("\\.\\.(1 0)",result.toString());
    }

    @Test
    public void test31_MIN_testLexer() {
        testLexer(31);
        assertEquals(
                "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "LPAREN" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LAMBDA" + lineBreak +
                        "LCID" + lineBreak +
                        "DOT" + lineBreak +
                        "LCID" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "RPAREN" + lineBreak +
                        "EOF" + lineBreak, bytes.toString());
    }
    @Test
    public void test31_MIN_testParser() {
        AST ast = testParser(31);
        assertEquals("((\\.\\.(((\\.\\.\\.((2 1) 0) ((\\.\\.(\\.((0 \\.\\.\\.0) \\.\\.1) ((\\.\\.((0 \\.\\.\\.(((2 \\.\\.(0 (1 3))) \\.1) \\.0)) 1) 1) 0)) 1) 0)) 1) 0) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0))))) (\\.\\.\\.(1 ((2 1) 0)) (\\.\\.\\.(1 ((2 1) 0)) \\.\\.0)))", ast.toString());
    }

    @Test
    public void test31_MIN_testInterpreter() {
        AST result = testInterpreter(31);
        assertEquals("\\.\\.(1 (1 0))",result.toString());
    }
}
