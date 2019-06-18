package cn.seecoder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.io.*;

public class InterpreterBuilder {

    private JTextArea attantion;
    private JTextArea question;
    private JTextArea answer;
    private JTextArea process;
    protected String lambda;
    private Interpreter interpreter;
    protected String result;
    private JFrame frame;

    public void go(){
        frame = new JFrame("LambdaInterpreter @pkun");
        JPanel mainPanel = new JPanel();

        question = new JTextArea(3,20);
        question.setLineWrap(true);
        question.setWrapStyleWord(true);

        answer = new JTextArea(3,20);
        answer.setLineWrap(true);
        answer.setWrapStyleWord(true);

        process = new JTextArea(20,50);
        process.setLineWrap(true);
        process.setWrapStyleWord(true);
        JScrollPane pScroller = new JScrollPane(process);
        pScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        pScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JButton interpreter = new JButton("Interpreter!");

        JLabel qLabel = new JLabel("Question:");
        JLabel aLabel = new JLabel("Answer:");
        JLabel pLabel = new JLabel("Process:");

        mainPanel.add(qLabel);
        mainPanel.add(question);
        mainPanel.add(aLabel);
        mainPanel.add(answer);
        mainPanel.add(interpreter);
        mainPanel.add(pScroller);

        interpreter.addActionListener(new InterpreterListener());

        frame.getContentPane().add(BorderLayout.CENTER,mainPanel);
        frame.setSize(750,500);
        frame.setVisible(true);
    }


    public class InterpreterListener implements ActionListener{
        public void actionPerformed(ActionEvent ev){
            lambda = question.getText();
            Lexer lexer = new Lexer(lambda);
            Parser parser = new Parser(lexer);
            AST p = parser.parse();
            interpreter = new Interpreter(lambda);
            result = interpreter.getResult().toShow();
            answer.setText(result);
            StringBuilder builder = new StringBuilder();
            builder.append(lambda+"\n");
            builder.append(lexer.builder.toString()+"\n");
            builder.append("Before interpreter De Bruijn: "+p.toString()+"\n");
            builder.append(interpreter.builder.toString());
            process.setText(builder.toString());
        }
    }

    public static void main(String[] args){
        InterpreterBuilder builder = new InterpreterBuilder();
        builder.go();
    }

}
