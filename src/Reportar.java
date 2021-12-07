import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import java.util.regex.*;



public class Reportar extends JFrame implements ActionListener {
    private static JTextField email;
    private static JTextArea area;
    private JButton boton;
    private final Font font_menubar = new Font("Arial", Font.PLAIN, 13);
    private JPanel panel;
    private final GridBagConstraints gbc = new GridBagConstraints();
    private static Reportar obj=null;
    private Reportar(){
        setTitle("Reportar bug");
        setSize(400,250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        comp();
        setIcon();
    }
    private void setIcon(){
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/report.png")));
    }

    public static Reportar getObj(){
        if (obj==null){
            obj=new Reportar();
        }
        return obj;
    }

    private void comp(){
        panel();
        email();
        area();
        boton();

    }
    private void panel(){
        panel = new JPanel();
        panel.setBackground(new Color(76, 76, 76));
        panel.setLayout(new GridBagLayout());
        add(panel);


    }


    private void email(){
        JLabel labelemail = new JLabel("Email de contacto");
        labelemail.setForeground(new Color(236, 236, 236 ));
        labelemail.setFont(font_menubar);
        gbc.gridx =0;
        gbc.gridy =1;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.weightx=0;
        gbc.weighty=0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets=new Insets(7,8,0,5);
        panel.add(labelemail,gbc);
        email = new JTextField();
        email.setMargin(new Insets(3,3,3,3));
        gbc.gridx =0;
        gbc.gridy =2;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.weightx=0;
        gbc.weighty=0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets=new Insets(3,8,5,8);
        panel.add(email,gbc);


    }
    private void area(){
        JLabel labelarea = new JLabel("Reporte");
        labelarea.setForeground(new Color(236, 236, 236 ));
        labelarea.setFont(font_menubar);
        gbc.gridx =0;
        gbc.gridy =3;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.weightx=0;
        gbc.weighty=0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets=new Insets(5,8,0,5);
        panel.add(labelarea,gbc);
        area = new JTextArea();
        area.setLineWrap(true);
        area.setMargin(new Insets(4,5,5,5));
        JScrollPane sp = new JScrollPane(area,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        gbc.gridx =0;
        gbc.gridy =4;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.weightx=1;
        gbc.weighty=2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets=new Insets(4,8,0,9);
        panel.add(sp,gbc);

    }
    private void boton(){
        Border line = new LineBorder(Color.BLACK);
        Border margin = new EmptyBorder(5, 15, 5, 15);
        Border compound = new CompoundBorder(line, margin);
        boton = new JButton("Enviar");
        boton.setForeground(new java.awt.Color( 236, 236, 236 ));
        boton.setBackground(new java.awt.Color(110, 110, 110));
        boton.setFont(font_menubar);
        boton.setBorder(compound);
        boton.setFocusable(false);
        gbc.gridx =0;
        gbc.gridy =5;
        gbc.gridwidth=0;
        gbc.gridheight=0;
        gbc.weightx=0;
        gbc.weighty=0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.insets=new Insets(5,8,7,5);
        boton.setPreferredSize(new Dimension(75,30));
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(boton,gbc);
        boton.addActionListener(this);
    }

    private boolean verify(String email){
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(boton)) {
            if (area.getText().isBlank() || !verify(email.getText())) {
                UIManager.put("OptionPane.okButtonText", "OK");
                JOptionPane.showMessageDialog(this, "Rellene los campos para enviar el reporte.", "Rellene los campos", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    this.dispose();
                    sendMail();
                } catch (AuthenticationFailedException b) {
                    JOptionPane.showMessageDialog(this, "Ha habido un error al enviar tu reporte, por favor ponte en contacto con nosotros si el problema persiste.", "Error al enviar reporte", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
    }

    public static void sendMail() throws Exception{
        Properties properties = new Properties();
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.port","587");

        String email = "calculadorahml@gmail.com";
        String password = "xxxxxxxxxxxx";

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email,password);

            }
        });
        Message message = mensaje(session,email);
        if (message != null) {
            Transport.send(message);
        }
    }
    public static Message mensaje(Session session,String mail){
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(mail));
            message.setRecipient(Message.RecipientType.TO,new InternetAddress("javiaragoneses1@gmail.com"));
            message.setSubject("Reporte de bug");
            message.setText("Email de contacto: "+email.getText()+"\n"+area.getText());
            return message;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
