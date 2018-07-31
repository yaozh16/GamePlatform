package ClinetGUI.Universal;

import ClientEngine.Configs.ClientConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginDialog extends JDialog {
    ClientConfig config;
    LoginSuccessNotifier loginSuccessNotifier;
    private JTextField account=new JTextField("yaozh16");
    private JPasswordField password=new JPasswordField("123456");
    private JTextField serverIP=new JTextField("127.0.0.1");
    private JTextField serverPort=new JTextField("2333",2);
    private JLabel msgBoard=new JLabel("");


    private JButton signUpBtn=new JButton("注册");
    private JButton loginBtn=new JButton("登录");
    private JButton closeBtn=new JButton("关闭");
    public LoginDialog(Frame parent,ClientConfig config,LoginSuccessNotifier loginSuccessNotifier){
        super(parent,"Login",true);
        this.config=config;
        this.loginSuccessNotifier=loginSuccessNotifier;
        setLayout(new GridBagLayout());

        add(new Label("Account:"),new GridBagConstraints(0,0,1,1,0,1,GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
        add(account,new GridBagConstraints(1,0,3,1,1,1,GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
        add(new Label("Password:"),new GridBagConstraints(0,1,1,1,0,1,GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
        add(password,new GridBagConstraints(1,1,3,1,1,1,GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
        add(new Label("serverIP"),new GridBagConstraints(0,2,1,1,0,1,GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
        add(serverIP,new GridBagConstraints(1,2,1,1,1,1,GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
        add(new Label("serverPort"),new GridBagConstraints(2,2,1,1,0,1,GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
        add(serverPort,new GridBagConstraints(3,2,1,1,1,1,GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
        JPanel msgPane=new JPanel(new FlowLayout());
        msgPane.add(msgBoard);
        add(msgPane,new GridBagConstraints(0,3,4,1,1,1,GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
        JPanel btnPane=new JPanel();
        btnPane.setLayout(new GridLayout(1,4,20,20));
        btnPane.add(signUpBtn);
        btnPane.add(loginBtn);
        btnPane.add(closeBtn);
        //btnPane.add(offlineBtn);
        add(btnPane,new GridBagConstraints(0,4,4,1,1,1,GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
        initOper();
    }
    public void run(){
        setMinimumSize(new Dimension(400,300));
        setMaximumSize(new Dimension(400,300));
        setSize(400,300);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initOper(){
        signUpBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            String error=config.signup(serverIP.getText(),Integer.parseInt(serverPort.getText()),account.getText(),String.valueOf(password.getPassword()));
                            msgBoard.setText(error);
                        }catch (Exception ex){
                            ex.printStackTrace();
                            msgBoard.setText("server config error!");
                            return;
                        }
                    }
                }).start();
            }
        });
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            String error=config.login(serverIP.getText(),Integer.parseInt(serverPort.getText()),account.getText(),String.valueOf(password.getPassword()));
                            msgBoard.setText(error);
                            if(config.getValidateCode()!=null){
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loginSuccessNotifier.loginSuccess();
                                    }
                                }).start();
                                dispose();
                            }
                        }catch (Exception ex){
                            ex.printStackTrace();
                            msgBoard.setText("server config error!");
                            return;
                        }
                    }
                }).start();

            }
        });
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        /*offlineBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });*/
    }
}