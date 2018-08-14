package ClinetGUI.Universal;

import ClientEngine.Configs.ClientConfig;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class LoginDialog extends JDialog {
    private int Height=400;
    private int Width =500;
    ClientConfig config;
    LoginSuccessNotifier loginSuccessNotifier;
    private JTextField account=new JTextField("yaozh16");
    /*private JTextField account=new JTextField("yaozh16"){
        {
            setBackground(new Color(255,255,255,50));
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    System.out.println("F");
                    account.repaint();
                    account.invalidate();
                    //super.focusGained(e);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    account.invalidate();
                    System.out.println("L");
                    //super.focusLost(e);
                }
            });
        }
    };*/
    private JPasswordField password=new JPasswordField("123456");
    private JTextField serverIP=new JTextField("127.0.0.1");
    private JTextField serverPort=new JTextField("2333",2);
    private JLabel msgBoard=new ColoredLabel("请注册/登录",new Color(0,0,0,127),10,10,Color.WHITE,20,20){
        {setFont(new Font("方正行楷_GBK",Font.ITALIC,20));}
    };
    private Point pressPoint=new Point();

    private JButton signUpBtn=new ColoredButton("注册",new Color(21,12,13,127),30,10,Color.WHITE);
    private JButton loginBtn=new ColoredButton("登录",new Color(21,12,13,127),30,10,Color.WHITE);
    private JButton closeBtn=new ColoredButton("退出",new Color(21,12,13,127),30,10,Color.WHITE);

    private JLabel backgroundLabel;
    private ImageIcon backgroundImage=new ImageIcon();
    private BufferedImage backgroundOriginal;
    public LoginDialog(Frame parent,ClientConfig config,LoginSuccessNotifier loginSuccessNotifier){
        super(parent,"Login",true);
        setResizable(false);
        this.config=config;
        this.loginSuccessNotifier=loginSuccessNotifier;

        backgroundLabel= getBackgroundLabel();
        getLayeredPane().add(backgroundLabel, new Integer(-30001));
        JPanel topPanel= getTopPanel();

        JPanel contentPanel=(JPanel)getContentPane();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(topPanel,BorderLayout.CENTER);
        contentPanel.setOpaque(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                if(config.getValidateCode()==null){
                    System.exit(0);
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println(pressPoint);
                pressPoint = e.getPoint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                Rectangle r = LoginDialog.this.getBounds();
                setLocation(r.x + (e.getX() - pressPoint.x),
                        r.y + (e.getY() - pressPoint.y));
            }
        });
    }
    private JLabel getBackgroundLabel(){
        JLabel label=null;
        try {
            //basic background
            backgroundOriginal = ImageIO.read(getClass().getResource("/images/loginBackground.jpg"));
            backgroundImage.setImage(backgroundOriginal.getScaledInstance(Width,Height,Image.SCALE_SMOOTH));
            label=new JLabel(backgroundImage);
            label.setBounds(0,0, Width,Height);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return label;
    }
    private JPanel getTopPanel(){
        JPanel panel=new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        JLabel titleLabel=new ColoredLabel("登录",new Color(127,127,127,0),10,10,new Color(255, 255, 255,200),30,40);
        panel.add(titleLabel,BorderLayout.NORTH);
        JPanel blankPanel=new JPanel();
        blankPanel.setOpaque(false);
        panel.add(blankPanel,BorderLayout.WEST);
        blankPanel=new JPanel();
        blankPanel.setOpaque(false);
        panel.add(blankPanel,BorderLayout.EAST);
        JPanel centerPanel=new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        JPanel msgPane=new JPanel(new GridLayout(1,1));
        msgPane.setOpaque(false);
        msgBoard.setOpaque(false);
        msgBoard.setForeground(Color.WHITE);
        msgPane.add(msgBoard);
        centerPanel.add(msgPane,new GridBagConstraints(0,0,4,1,1,1,GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
        centerPanel.add(
                new ColoredLabel("帐号",new Color(21,12,13,127),30,10,Color.WHITE,5),
                new GridBagConstraints(0,1,1,1,0,1,GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
        centerPanel.add(account,
                new GridBagConstraints(1,1,1,1,1,1,GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
        centerPanel.add(
                new ColoredLabel("密码",new Color(21,12,13,127),30,10,Color.WHITE,5),
                new GridBagConstraints(2,1,1,1,0,1,GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
        centerPanel.add(password,
                new GridBagConstraints(3,1,1,1,1,1,GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
        centerPanel.add(
                new ColoredLabel("服务器IP",new Color(21,12,13,127),30,10,Color.WHITE,5),
                new GridBagConstraints(0,2,1,1,0,1,GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
        centerPanel.add(serverIP,
                new GridBagConstraints(1,2,1,1,1,1,GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
        centerPanel.add(new ColoredLabel("服务器端口",new Color(21,12,13,127),30,10,Color.WHITE,5),
                new GridBagConstraints(2,2,1,1,0,1,GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
        centerPanel.add(serverPort,
                new GridBagConstraints(3,2,1,1,1,1,GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,
                        new Insets(0,0,0,0),0,0));
        centerPanel.add(getButtonPanel(),
                new GridBagConstraints(0,3,4,1,1,1,GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0));
        panel.add(centerPanel,BorderLayout.CENTER);

        return panel;
    }
    private JPanel getButtonPanel(){
        JPanel btnPane=new JPanel();
        btnPane.setOpaque(false);
        btnPane.setLayout(new GridLayout(1,4,20,20));
        btnPane.add(signUpBtn);
        btnPane.add(loginBtn);
        btnPane.add(closeBtn);
        initOper();
        return btnPane;
    }
    public void run(){
        setMinimumSize(new Dimension(Width,Height));
        setMaximumSize(new Dimension(Width,Height));
        setSize(Width,Height);
        setUndecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setModal(true);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("try to hide");
                LoginDialog.this.setVisible(true);
            }
        });

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
                                        System.out.println("\033[1;32mlogin success\033[0m");
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
                dispose();
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