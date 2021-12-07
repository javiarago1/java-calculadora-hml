
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;


import net.objecthunter.exp4j.*;
import net.objecthunter.exp4j.function.Function;



import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static javax.swing.BorderFactory.createEmptyBorder;


public class Window extends JFrame implements ActionListener,KeyListener {
    private final Font font_menubar = new Font("Arial", Font.PLAIN, 13);
    private final JList <String> list = new JList<>();
    private JMenuItem copiar;
    private JMenuItem eliminar;
    private JMenuItem eliminar_all;
    private JMenuItem est_dec;
    private JMenuItem abrir;
    private JMenuItem guardar;
    private JCheckBoxMenuItem redon,nota_bot;
    private int num_dec=5;
    private String Ans ="0";
    private final DefaultListModel <String> model = new DefaultListModel<>();
    private final String cero = "0";
    private JRadioButtonMenuItem deg,rad,grad;
    private JRadioButtonMenuItem dir,inv,hip;
    private boolean redon_act=false;
    private boolean resul,error;
    private boolean notacion = true;
    private boolean rad_val = false;
    private boolean grad_val = false;
    private boolean deg_val=true;
    private JTextField area;
    private final String [][]calc = {{"\u221A","x\u00B2","(",")","%","\u00F7"},
            {"MOD","x\u207F","7","8","9","x"},
            {"sen","log","4","5","6","-"},
            {"cos","n!","1","2","3","+"},
            {"tan","Ans",",","0","C","="}};
    private final JButton [][]boton = new JButton[calc.length][calc[0].length];
    private final GridBagConstraints gbc = new GridBagConstraints();
    private JButton retroceso;
    private JPanel superior, intermedio, inferior,footer;


    public Window() {

        super("Calculadora HML");
        this.getContentPane().setBackground(new Color(61, 61, 61  ));
        setSize(450,400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        comp();
    }

    private void comp(){
        setIcon();
        addPan();
        numeros();
        sup();
        menu();


    }

    private void setIcon(){
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/icon.png")));
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(nota_bot)) {
            if (nota_bot.isSelected()) {
                notacion = true;
            } else if (!nota_bot.isSelected()) {
                notacion = false;
            }
        }
        if(e.getSource().equals(abrir)){
            JFileChooser chooser = new JFileChooser();
            chooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "txt", "txt");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    FileReader re = new FileReader(chooser.getSelectedFile().getPath());
                    System.out.println(re);
                    BufferedReader bf = new BufferedReader(re);
                    String line;
                    while((line=bf.readLine())!=null){
                        model.addElement(line);
                    }
                }catch (IOException ab){
                    JOptionPane.showMessageDialog(this, "La ruta es incorrecta","Error",JOptionPane.ERROR_MESSAGE);
                }

            }
        }
        else if(e.getSource().equals(guardar)) {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(tiempo()));
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {

                    File f = chooser.getSelectedFile();
                    FileWriter fw = new FileWriter(f);
                    StringBuilder data = new StringBuilder();
                    if (model.getSize() <= 0) {
                        data = new StringBuilder();
                    } else {
                        for (int i = 0; i < model.getSize(); i++) {
                            data.append(model.getElementAt(i)).append("\n");
                        }
                    }
                    fw.write(data.toString());
                    fw.close();
                } catch (IOException ev) {
                    JOptionPane.showMessageDialog(this, "La ruta o nombre introducidos son incorrectos", "Error en la ruta o nombre de archivo", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        if(e.getSource()==redon){
            if(redon.isSelected()){
                est_dec.setEnabled(false);
                redon_act=true;
            }
            else{
                est_dec.setEnabled(true);
                redon_act=false;
            }
        }
        if(e.getSource()==est_dec){
            JPanel ventana = new JPanel();
            ventana.setPreferredSize(new Dimension(240,90));
            ventana.setLayout(new GridLayout(2,1,2,0));
            JLabel decim = new JLabel("Indique los decimales a mostrar: ");
            decim.setFont(font_menubar);
            ventana.add(decim);
            JSlider slider = new JSlider(0, 9, num_dec);
            slider.setFont(font_menubar);
            slider.setMajorTickSpacing(10);
            slider.setMinorTickSpacing(0);
            slider.setMajorTickSpacing(1);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            ventana.add(slider);
            UIManager.put("OptionPane.cancelButtonText", "Cancelar");
            UIManager.put("OptionPane.okButtonText", "Establecer");
            int respuesta = JOptionPane.showOptionDialog(this, ventana, "Establecer decimales", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null,null);
            if(JOptionPane.OK_OPTION==respuesta){
                num_dec = slider.getValue();
            }
        }
        if (e.getSource().equals(eliminar)){
            if(list.getSelectedIndex()>=0) {
                model.remove(list.getSelectedIndex());
            }
        }
        else if(e.getSource().equals(copiar)){
            StringSelection stringSelection = new StringSelection(list.getSelectedValue());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        }
        else if(e.getSource().equals(eliminar_all)){
            model.removeAllElements();
        }

        for(int i=0;i<boton.length;i++){
            for(int j=0;j<boton[i].length;j++){
                if((i==4&&j==2)||(i==4&&j==5)){
                    continue;
                }
                if (e.getSource().equals(boton[i][j])&&area.getText().equals("0")){
                    area.setText(null);
                }
            }
        }

        AbstractButton modo = (AbstractButton) e.getSource();
        if (modo.equals(deg)) {
            deg_val=true;
            rad_val=false;
            grad_val=false;
        }
        else if (modo.equals(rad)){
            rad_val=true;
            deg_val=false;
            grad_val=false;
        }
        else if (modo.equals(grad)){
            grad_val=true;
            rad_val=false;
            deg_val=false;
        }
        else if(modo.equals(dir)){
            for(int i=2;i<calc.length;i++){
                boton[i][0].setText(calc[i][0]);
            }
        }
        else if (modo.equals(inv)){
            for(int i=2;i<calc.length;i++){
                boton[i][0].setText("a"+calc[i][0]);
            }
        }
        else if(modo.equals(hip)){
            for(int i=2;i<calc.length;i++){
                boton[i][0].setText(calc[i][0]+"h");
            }
        }
        if (resul) {
            for (int i = 1; i < boton.length; i++) {
                for (int j = 0; j < boton[i].length-1; j++) {
                    if((i==1&&j==0)||(i==1&&j==1)){
                        continue;
                    }
                    if (((e.getSource() == boton[i][j] || e.getSource()==boton[0][0]) && !(e.getSource()==boton[4][2]))||error) {
                        area.setText(null);
                    }
                }
            }
        }
        error=false;
        resul = false;
        if (e.getSource() == boton[4][4]) {
            area.setText(cero);
        } else if ((e.getSource().equals(retroceso)) && (area.getText().length() >= 1)&&(!area.getText().equals(cero))) {
            area.setText(area.getText().substring(0, area.getText().length() - 1));
            if (area.getText().isEmpty()){
                area.setText(cero);
            }
        } else if (e.getSource().equals(boton[1][1])) {
            area.setText(area.getText() + "\u005E(");
        } else if (e.getSource().equals(boton[1][0])) {
            area.setText(area.getText() + "MOD");
        } else if (e.getSource().equals(boton[3][1])) {
            area.setText(area.getText() + "fact(");
        }else if (e.getSource().equals(boton[0][1])) {
            area.setText(area.getText() + "\u005E(2)");
        } else if (e.getSource().equals(boton[2][0])) {
            area.setText(area.getText() +boton[2][0].getText()+"(");
        } else if (e.getSource().equals(boton[2][1])) {
            area.setText(area.getText() +boton[2][1].getText()+"(");
        } else if (e.getSource().equals(boton[4][1])) {
            area.setText(area.getText() +boton[4][1].getText());
        } else if (e.getSource().equals(boton[3][0])) {
            area.setText(area.getText() +boton[3][0].getText()+"(");
        } else if (e.getSource().equals(boton[4][0])) {
            area.setText(area.getText() +boton[4][0].getText()+"(");
        } else if (e.getSource() == boton[0][0]) {
            area.setText(area.getText() + boton[0][0].getText() + "(");
        } else if (e.getSource().equals(boton[4][5])) {
            area.setText(oper());
        } else {
            for (JButton[] jButtons : boton) {
                for (int j = 2; j < jButtons.length; j++) {
                    if (e.getSource().equals(jButtons[j])) {
                        area.setText(area.getText() + jButtons[j].getText());
                    }
                }
            }
        }
    }

    private String tiempo(){
        String tiempo = "calc-";
        tiempo += new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Calendar.getInstance().getTime());
        tiempo +=".txt";
        return tiempo;
    }


    private void numeros() {
        Font font_numeros = new Font("Helvetica", Font.PLAIN, 13);
        retroceso = new JButton("\u2B05");
        retroceso.setPreferredSize(new Dimension(70, 27));
        intermedio.add(retroceso);
        Border line = new LineBorder(Color.BLACK);
        Border margin = new EmptyBorder(5, 15, 5, 15);
        Border compound = new CompoundBorder(line, margin);
        for (int i = 0; i < boton.length; i++) {
            for (int j = 0; j < boton[i].length; j++) {
                boton[i][j] = new JButton();
                boton[i][j].setFocusable(false);
                boton[i][j].setText(calc[i][j]);
                boton[i][j].setForeground(new java.awt.Color( 236, 236, 236 ));
                boton[i][j].setBackground(new java.awt.Color(76, 76, 76));
                boton[i][j].setFont(font_numeros);
                boton[i][j].setBorder(compound);
                inferior.add(boton[i][j]);
            }
        }
        boton[4][5].setBackground(new java.awt.Color(  25, 138, 0 ));
        retroceso.addActionListener(this);
        retroceso.setFocusPainted(false);
        retroceso.addKeyListener(this);
        retroceso.setBackground(new java.awt.Color( 171, 0, 0 ));
        retroceso.setForeground(new java.awt.Color( 236, 236, 236 ));


        retroceso.setBorder(compound);
        for (JButton[] jButtons : boton) {
            for (JButton jButton : jButtons) {
                jButton.addActionListener(this);
                jButton.addKeyListener(this);
            }

        }
    }





    private String trans(String reci){
        reci = reci.replace("x", "*");
        reci = reci.replace("\u00F7", "/");
        reci = reci.replace(",", ".");
        reci = reci.replace("\u221A", "sqrt");
        reci = reci.replace("%", "P");
        reci = reci.replace("MOD", "%");
        reci = reci.replace("Ans",Ans);
        return reci;
    }

    private String paren(String icer){
        int izq=0;
        int der=0;
        for(int i=0;i<icer.length();i++){
            if (icer.charAt(i)=='('){
                izq++;
            }
        }
        for(int i=0;i<icer.length();i++){
            if (icer.charAt(i)==')'){
                der++;
            }
        }
        if (der<izq) {
            StringBuilder icerBuilder = new StringBuilder(icer);
            while (der!=izq){
                der++;
                icerBuilder.append(")");
            }
            icer = icerBuilder.toString();
        }
        return icer;
    }



    private final Function[] myFunctions = {
            new Function("P"){public double apply(double... args){return args[0]*0.01;}},
            new Function("sen"){public double apply(double... args){if(deg_val)return Math.sin(Math.toRadians(args[0]));else if(rad_val){return Math.sin(args[0]);}else{return Math.sin(Math.PI*args[0]/200);}}},
            new Function("cos"){public double apply(double... args){if(deg_val)return Math.cos(Math.toRadians(args[0]));else if(rad_val){return Math.cos(args[0]);}else{return Math.cos(Math.PI*args[0]/200);}}},
            new Function("tan"){public double apply(double... args){if(deg_val)return Math.tan(Math.toRadians(args[0]));else if(rad_val){return Math.tan(args[0]);}else{return Math.tan(Math.PI*args[0]/200);}}},
            new Function("asen"){public double apply(double... args){if(deg_val)return Math.toDegrees(Math.asin(args[0]));else if(rad_val){return Math.asin(args[0]);}else{return Math.toDegrees(Math.asin(args[0])*200/180);}}},
            new Function("acos"){public double apply(double... args){if(deg_val)return Math.toDegrees(Math.acos(args[0]));else if(rad_val){return Math.acos(args[0]);}else{return Math.toDegrees(Math.acos(args[0])*200/180);}}},
            new Function("atan"){public double apply(double... args){if(deg_val)return Math.toDegrees(Math.atan(args[0]));else if(rad_val){return Math.atan(args[0]);}else{return Math.toDegrees(Math.atan(args[0])*200/180);}}},
            new Function("senh"){public double apply(double... args){return Math.sinh(args[0]);}},
            new Function("cosh"){public double apply(double... args){return Math.cosh(args[0]);}},
            new Function("tanh"){public double apply(double... args){return Math.sinh(args[0]);}},
            new Function("fact"){public double apply(double... args){
                double result=1;
                if(args[0]<0){
                    return 0;
                }
                for(int i=1;i<=args[0];i++){
                    result*=i;
                }
                return result;
            }},

    };


    private String pattern(){
        StringBuilder pattern= new StringBuilder("#0.");
        if (num_dec==0){
            pattern = new StringBuilder("#0");
        } else {
            pattern.append("#".repeat(Math.max(0, num_dec)));
        }
        return pattern.toString();
    }

    private String oper() {
        resul = true;
        String reci = area.getText();
        reci=paren(reci);
        String linea = reci;
        reci=trans(reci);

        try {
            Expression e = new ExpressionBuilder(reci)
                    .functions(myFunctions)
                    .build();
            double result = e.evaluate();
            if(redon_act){
                result = Math.round(result);
            }
            String res_fin;
            if (notacion) {
                long x = (long) result;
                int longit = String.valueOf(x).length();
                res_fin = String.valueOf(result);
                if (longit <= 10) {
                    DecimalFormat format = new DecimalFormat(pattern());
                    res_fin = format.format(result);
                }
            }
            else {
                DecimalFormat format = new DecimalFormat(pattern());
                res_fin = format.format(result);
            }
            Ans = res_fin;
            Ans = Ans.replace(",",".");
            res_fin = res_fin.replace(".", ",");

            linea +=" = "+res_fin;
            model.add(0, linea);
            list.setModel(model);

            return res_fin;
        } catch (Exception a) {
            error = true;
            return "Syntax error";
        }

    }


    private void menu(){
        ButtonGroup grutri = new ButtonGroup();
        ButtonGroup grumod = new ButtonGroup();
        JMenuBar menubar = new JMenuBar();
        setJMenuBar(menubar);

        JMenu menu_arc = new JMenu("<html><p style='margin-top:2'>Archivo");
        menubar.add(menu_arc);
        menu_arc.setFont(font_menubar);
        JMenu modo = new JMenu("<html><p style='margin-top:2'>Trigonometr\u00eda");
        modo.setFont(font_menubar);
        menubar.add(modo);
        JMenu decimales = new JMenu("<html><p style='margin-top:2'>Decimales");
        decimales.setFont(font_menubar);
        menubar.add(decimales);

        menubar.add(Box.createHorizontalGlue());

        JMenu ayuda = new JMenu("<html><p style='margin-top:2'>Ayuda");
        ayuda.setFont(font_menubar);
        menubar.add(ayuda);


        abrir = new JMenuItem("Abrir");
        abrir.setFont(font_menubar);
        menu_arc.add(abrir);
        abrir.addActionListener(this);

        guardar = new JMenuItem("Guardar");
        guardar.setFont(font_menubar);
        menu_arc.add(guardar);
        menu_arc.addSeparator();
        guardar.addActionListener(this);
        JMenuItem salir = new JMenuItem("Salir");
        salir.setFont(font_menubar);
        menu_arc.add(salir);
        salir.addActionListener((event)->System.exit(0));



        est_dec = new JMenuItem("Establecer decimales");
        decimales.add(est_dec);
        est_dec.setFont(font_menubar);
        est_dec.addActionListener(this);
        nota_bot = new JCheckBoxMenuItem("Notaci\u00f3n cient\u00edfica",true);
        decimales.add(nota_bot);
        nota_bot.setFont(font_menubar);
        nota_bot.addActionListener(this);

        redon = new JCheckBoxMenuItem("Redondear");
        decimales.add(redon);
        redon.addActionListener(this);
        redon.setFont(font_menubar);




        deg = new JRadioButtonMenuItem("DEG",true);
        deg.setFont(font_menubar);
        modo.add(deg);
        grumod.add(deg);
        deg.addActionListener(this);
        rad = new JRadioButtonMenuItem("RAD");
        rad.setFont(font_menubar);
        modo.add(rad);
        grumod.add(rad);
        rad.addActionListener(this);
        grad = new JRadioButtonMenuItem("GRAD");
        grad.setFont(font_menubar);
        modo.add(grad);
        grumod.add(grad);
        grad.addActionListener(this);
        modo.addSeparator();
        JMenu trigo = new JMenu("Funciones trigonom\u00e9tricas");
        modo.add(trigo);
        trigo.setFont(font_menubar);
        dir = new JRadioButtonMenuItem("Directas",true);
        dir.setFont(font_menubar);
        trigo.add(dir);
        grutri.add(dir);
        dir.addActionListener(this);
        inv = new JRadioButtonMenuItem("Inversas");
        inv.setFont(font_menubar);
        trigo.add(inv);
        grutri.add(inv);
        inv.addActionListener(this);
        hip = new JRadioButtonMenuItem("Hiperb\u00f3licas");
        hip.setFont(font_menubar);
        trigo.add(hip);
        grutri.add(hip);
        hip.addActionListener(this);

        JMenuItem exp4j = new JMenuItem("exp4j");
        exp4j.setFont(font_menubar);
        ayuda.add(exp4j);
        exp4j.addActionListener(e -> {
            try {
                URI uri= new URI("https://www.objecthunter.net/exp4j/");
                Desktop.getDesktop().browse(uri);
            } catch (Exception ignored) {}
        });
        JMenuItem report = new JMenuItem("Reportar un bug");
        report.setFont(font_menubar);
        ayuda.add(report);
        report.addActionListener(e -> Reportar.getObj().setVisible(true));
        Icon icon = new ImageIcon("src/images/calculator.png");
        ayuda.addSeparator();
        JMenuItem acercade = new JMenuItem("Acerca de Calculadora HML");
        acercade.setFont(font_menubar);
        JLabel aboutmelabel = new JLabel("<html><b>Calculadora HML</b> 1.0.0Alpha<br><br><b>Creador</b>: Javier Aragoneses Fernández<br><br>Lenguaje:<i> JAVA</i><br>Librer\u00edas: javax.mail | exp4j<br><br></html>");
        aboutmelabel.setFont(font_menubar);
        ayuda.add(acercade);
        acercade.addActionListener(e -> {
            UIManager.put("OptionPane.okButtonText", "Aceptar");
            JOptionPane.showMessageDialog(
                    null,
                    aboutmelabel,
                    "Acerca de Calculadora HML",
                    JOptionPane.PLAIN_MESSAGE,icon);
        });

    }

    private void sup(){

        //
        JLabel copy = new JLabel("GPL Free Software");
        copy.setForeground(new java.awt.Color(218, 218, 218));
        Font font2 = new Font("FUTURE", Font.PLAIN, 10);
        copy.setFont(font2);
        footer.add(copy);

        //
        JScrollPane almc= new JScrollPane(list);
        almc.setBorder(createEmptyBorder());
        superior.add(almc);
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) list.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.RIGHT);



        JPopupMenu popmenu = new JPopupMenu();
        copiar = new JMenuItem("Copiar");
        copiar.setFont(font_menubar);
        copiar.addActionListener(this);
        eliminar = new JMenuItem("Eliminar");
        eliminar.addActionListener(this);
        eliminar.setFont(font_menubar);

        eliminar_all = new JMenuItem("Limpiar todo");
        eliminar_all.setFont(font_menubar);
        eliminar_all.addActionListener(this);
        popmenu.add(copiar);
        popmenu.add(eliminar);
        popmenu.addSeparator();
        popmenu.add(eliminar_all);
        Font font_list = new Font("Arial", Font.PLAIN, 15);
        list.setFont(font_list);
        list.setComponentPopupMenu(popmenu);
        list.setBorder(new EmptyBorder(2,10, 1, 10));
        list.setModel(model);
        list.addKeyListener(this);


        //

        Font font_area = new Font("Arial", Font.PLAIN, 28);
        area = new JTextField(cero);
        area.setFont(font_area);
        area.setHorizontalAlignment(SwingConstants.RIGHT);
        area.setBorder(BorderFactory.createCompoundBorder(
                area.getBorder(),
                createEmptyBorder(0, 5, 0, 5)));
        area.setEditable(false);
        area.setFocusable(false);
        area.addKeyListener(this);
        superior.add(area);






    }
    private void addPan(){

        superior = new JPanel();
        superior.setLayout(new GridLayout(2,1,2,3));
        superior.setBackground(new java.awt.Color(61, 61, 61   ));

        intermedio = new JPanel();
        intermedio.setLayout(new FlowLayout(FlowLayout.RIGHT));
        intermedio.setBackground(new java.awt.Color(61, 61, 61  ));



        inferior = new JPanel();
        inferior.setBackground(new java.awt.Color(   61, 61, 61  ));
        inferior.setLayout(new GridLayout(5,6,3,3));




        footer= new JPanel();
        footer.setLayout(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(new java.awt.Color(61, 61, 61  ));


        inferior.addKeyListener(this);
        intermedio.addKeyListener(this);
        superior.addKeyListener(this);
        footer.addKeyListener(this);

        //superior
        gbc.gridx =0;
        gbc.gridy =0;
        gbc.gridwidth=0;
        gbc.gridheight=1;
        gbc.weightx=1;
        gbc.weighty=1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets=new Insets(10,8,0,9);
        add(superior,gbc);

        //intermedio
        gbc.gridx =0;
        gbc.gridy =1;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.weightx=0;
        gbc.weighty=0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets=new Insets(3,3,0,3);
        add(intermedio,gbc);

        //inferior
        gbc.gridx =0;
        gbc.gridy =2;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.weightx=1;
        gbc.weighty=1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0,6,0,6);
        add(inferior,gbc);

        //footer
        gbc.gridx =0;
        gbc.gridy =3;
        gbc.gridwidth=0;
        gbc.gridheight=0;
        gbc.weightx=0;
        gbc.weighty=0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets=new Insets(0,3,2,7);
        add(footer,gbc);

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
    @Override
    public void keyPressed(KeyEvent e) {
        char a = e.getKeyChar();
        switch (a) {
            case '0' -> boton[4][3].doClick();
            case '1' -> boton[3][2].doClick();
            case '2' -> boton[3][3].doClick();
            case '3' -> boton[3][4].doClick();
            case '4' -> boton[2][2].doClick();
            case '5' -> boton[2][3].doClick();
            case '6' -> boton[2][4].doClick();
            case '7' -> boton[1][2].doClick();
            case '8' -> boton[1][3].doClick();
            case '9' -> boton[1][4].doClick();
            case ',','.' -> boton[4][2].doClick();
            case '+' -> boton[3][5].doClick();
            case '-' -> boton[2][5].doClick();
            case '*' -> boton[1][5].doClick();
            case '/' -> boton[0][5].doClick();
            case '(' -> boton[0][2].doClick();
            case ')' -> boton[0][3].doClick();
            case '=' -> boton[4][5].doClick();
        }
        int b = e.getKeyCode();
        switch (b) {
            case KeyEvent.VK_ENTER -> boton[4][5].doClick();
            case KeyEvent.VK_BACK_SPACE -> retroceso.doClick();
            case KeyEvent.VK_DELETE -> boton[4][4].doClick();
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {

    }
    public static void main(String[] args) {
        Window v = new Window();
        v.setVisible(true);
    }
}

