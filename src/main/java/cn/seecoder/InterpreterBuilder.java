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
    protected AST result;
    private JFrame frame;

    public void go(){
        frame = new JFrame("LambdaInterpreter ©pkun");
        JPanel mainPanel = new JPanel();
        //question
        question = new JTextArea(3,20);
        question.setLineWrap(true);
        question.setWrapStyleWord(true);
        //answer
        answer = new JTextArea(3,20);
        answer.setLineWrap(true);
        answer.setWrapStyleWord(true);
        answer.setEditable(false);
        //process
        process = new JTextArea(20,50);
        process.setLineWrap(true);
        process.setWrapStyleWord(true);
        process.setEditable(false);
        //scrollpane to process
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
        mainPanel.add(pLabel);
        mainPanel.add(pScroller);

        interpreter.addActionListener(new InterpreterListener());

        frame.getContentPane().add(BorderLayout.CENTER,mainPanel);
        frame.setSize(730,500);
        frame.setVisible(true);
    }


    public class InterpreterListener implements ActionListener{
        public void actionPerformed(ActionEvent ev){
            lambda = question.getText();
            Lexer lexer = new Lexer(lambda);
            Parser parser = new Parser(lexer);
            AST p = parser.parse();
            interpreter = new Interpreter(lambda);
            result = interpreter.getResult();
            answer.setText(result.toShow()+"\n"+result.toString());
            StringBuilder builder = new StringBuilder();
            builder.append(lambda+"\n");
            builder.append(lexer.builder.toString()+"\n");
            builder.append("Before interpreter De Bruijn: "+p.toString()+"\n");
            builder.append(interpreter.getBuilder().toString());
            process.setText(builder.toString()+"\n"+result.toString()+"\n");
        }
    }

    public static void main(String[] args){
        InterpreterBuilder builder = new InterpreterBuilder();
        builder.go();
    }

}
