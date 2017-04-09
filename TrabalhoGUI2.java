import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.JFileChooser;
import javax.swing.JSlider;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.InvalidMidiDataException;

import java.text.DecimalFormat;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TrabalhoGUI2 extends JFrame implements Runnable {
	
    BufferedImage backgroundPanel;
    
    ImageIcon playIcon         = new ImageIcon("img/playnormal.png");
	ImageIcon playHover        = new ImageIcon("img/playhover.png");
	ImageIcon pauseIcon        = new ImageIcon("img/pausenormal.png");
	ImageIcon pauseHover       = new ImageIcon("img/pausehover.png");
	ImageIcon stopIcon         = new ImageIcon("img/stopnormal.png");
	ImageIcon stopHover        = new ImageIcon("img/stophover.png");
    ImageIcon fowardIcon       = new ImageIcon("img/fowardnormal.png");
    ImageIcon fowardHover      = new ImageIcon("img/fowardhover.png");
    ImageIcon backwardIcon     = new ImageIcon("img/backwardnormal.png");
    ImageIcon backwardHover    = new ImageIcon("img/backwardhover.png");
    ImageIcon menosIcon        = new ImageIcon("img/menosnormal.png");
    ImageIcon maisIcon         = new ImageIcon("img/maisnormal.png");
	ImageIcon frameIcon        = new ImageIcon("img/frameicon.png");
	
	Color corFundo  = new Color(5, 5, 5);
	Color corTexto  = new Color(220, 222, 224);
	Color corTexto2 = new Color(142, 145, 151);
	Color corAzul   = new Color(40, 102, 213);

	Font arial       = new Font("Arial", java.awt.Font.PLAIN, 25);
	Font arialNarrow = new Font("Arial Narrow", java.awt.Font.ITALIC, 18);

    JLabel nomeArquivo   = new JLabel("Titulo do Arquivo MIDI");
    JLabel nomeArquivo2  = new JLabel("Titulo da Orquestra SF2");
    JLabel volumeLabel       = new JLabel("", SwingConstants.CENTER);
    JLabel tempoTotal    = new JLabel("00:00:00");
    JLabel tempoCorrente = new JLabel("00:00:00");

    JButton botaoStop     = new JButton(stopIcon);
    JButton botaoPlay     = new JButton(playIcon);
    JButton botaoPause    = new JButton(pauseIcon);
    JButton botaoFoward   = new JButton(fowardIcon);
    JButton botaoBackward = new JButton(backwardIcon);
    JButton botaoMenos    = new JButton(menosIcon);
    JButton botaoMais     = new JButton(maisIcon);
    JButton botaoAbrir    = new JButton("Abrir");
    JButton botaoAbrir2   = new JButton("Abrir");
    JButton botaoFantasma = new JButton();

    JProgressBar progresso = new JProgressBar(0);

    JTextField caminhoTextField = new JTextField("  Escolha um arquivo MIDI...");    
    JTextField caminhoTextField2 = new JTextField("  Escolha um arquivo SF2...");

    ShortMessage mudancaVolume = new ShortMessage();
    

    static Soundbank   bancoDefault;      
    static Soundbank   bancoSelecionado;
    private Sequence   sequencia;  
    private Sequencer  sequenciador = null;
    static Synthesizer sintetizador = null;
    private Receiver   receptor     = null;
    private long       inicioAudio  = 0;
    private boolean    soando       = false; 
    private int        volumeAtual  = 64;

    String  caminhoMIDI;
    String  caminhoSF2;

    //-----------------------------------------------
    // Classe que Define um panel com imagem de fundo
    //-----------------------------------------------
    public class PanelPersonalizado extends JPanel {
        
        public PanelPersonalizado() {
            try {
                backgroundPanel = ImageIO.read(new File("img/background.jpg"));
            } catch(IOException e) {
                e.printStackTrace();
            }
            setOpaque(true);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundPanel, 0, 0, getWidth(), getHeight(), this);
        }
    }

    //-------------------------------------------------
    // Metodo que apresenta a interface grafica na tela
    //-------------------------------------------------
    public void displayGUI(){
    	    	
    	JFrame frame = new JFrame("Primeiro Tocador");

    	PanelPersonalizado panelConteudo = new PanelPersonalizado();
    	GroupLayout layout = new GroupLayout(panelConteudo);

    	JPanel panelTopo = new JPanel();
        JPanel panelCentro = new JPanel();
        JPanel panelProgresso = new JPanel();
        JPanel panelRodape = new JPanel();
         
        //---------------------
        // Painel Topo Esquerdo 
        //---------------------
        panelTopo.setPreferredSize(new Dimension (400, 90));
        panelTopo.setOpaque(false);

        nomeArquivo.setForeground(corTexto);
        nomeArquivo.setPreferredSize(new Dimension (300, 25));
        nomeArquivo.setFont(arial);

        nomeArquivo2.setForeground(corTexto2);
        nomeArquivo2.setPreferredSize(new Dimension (300, 20));
        nomeArquivo2.setFont(arialNarrow);

        panelTopo.add(nomeArquivo);
        panelTopo.add(nomeArquivo2);
        
        panelTopo.setLayout(new FlowLayout(FlowLayout.LEFT));

        //--------------------
        // Painel Centro
        //-------------------- 
        panelCentro.setPreferredSize(new Dimension (380, 50));
        panelCentro.setOpaque(false);
               
        botaoStop.setContentAreaFilled(false);
        botaoStop.setBorderPainted(false);
        botaoStop.setPreferredSize(new Dimension (40, 40));
        botaoStop.setFocusable(false);
        
        botaoStop.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent evt) {
            	botaoStop.setIcon(stopHover);
            	botaoStop.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent evt) {
            	botaoStop.setIcon(stopIcon);
            }
        });
        
        botaoPlay.setContentAreaFilled(false);
        botaoPlay.setBorderPainted(false);
        botaoPlay.setPreferredSize(new Dimension (48, 48));
        botaoPlay.setFocusable(false);
        
        botaoPlay.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent evt) {
            	botaoPlay.setIcon(playHover);
            	botaoPlay.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent evt) {
            	botaoPlay.setIcon(playIcon);
            }
        });
        
        botaoPause.setContentAreaFilled(false);
        botaoPause.setBorderPainted(false);
        botaoPause.setPreferredSize(new Dimension (40, 40));
        botaoPause.setFocusable(false);
        
        botaoPause.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent evt) {
            	botaoPause.setIcon(pauseHover);
            	botaoPause.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent evt) {
            	botaoPause.setIcon(pauseIcon);
            }
        });
      
        botaoFoward.setContentAreaFilled(false);
        botaoFoward.setBorderPainted(false);
        botaoFoward.setPreferredSize(new Dimension (40, 40));
        botaoFoward.setFocusable(false);
        
        botaoFoward.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent evt) {
                botaoFoward.setIcon(fowardHover);
                botaoFoward.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent evt) {
                botaoFoward.setIcon(fowardIcon);
            }
        });

        botaoBackward.setContentAreaFilled(false);
        botaoBackward.setBorderPainted(false);
        botaoBackward.setPreferredSize(new Dimension (40, 40));
        botaoBackward.setFocusable(false);
        
        botaoBackward.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent evt) {
                botaoBackward.setIcon(backwardHover);
                botaoBackward.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent evt) {
                botaoBackward.setIcon(backwardIcon);
            }
        });
      
        botaoFantasma.setContentAreaFilled(false);
        botaoFantasma.setPreferredSize(new Dimension(30, 25));
        botaoFantasma.setOpaque(false);
        botaoFantasma.setBorder(BorderFactory.createEmptyBorder());
        botaoFantasma.setFocusable(false);

        botaoMenos.setContentAreaFilled(false);
        botaoMenos.setPreferredSize(new Dimension(24, 24));
        botaoMenos.setBorder(BorderFactory.createEmptyBorder());
        botaoMenos.setOpaque(false);
        botaoMenos.setFocusable(false);
        botaoMenos.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        volumeLabel.setPreferredSize(new Dimension(30, 24));
        volumeLabel.setBorder(BorderFactory.createEmptyBorder());
        volumeLabel.setOpaque(false);
        volumeLabel.setForeground(corTexto);
         
        botaoMais.setContentAreaFilled(false);
        botaoMais.setPreferredSize(new Dimension(24, 24));
        botaoMais.setBorder(BorderFactory.createEmptyBorder());
        botaoMais.setOpaque(false);
        botaoMais.setFocusable(false);
        botaoMais.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelCentro.add(botaoBackward);
        panelCentro.add(botaoPause);
        panelCentro.add(botaoPlay);
        panelCentro.add(botaoStop);
        panelCentro.add(botaoFoward);
        panelCentro.add(botaoFantasma);
        panelCentro.add(botaoMenos);
        panelCentro.add(volumeLabel);
        panelCentro.add(botaoMais);

        panelCentro.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

        //--------------------
        // Painel Progresso
        //-------------------- 
        panelProgresso.setPreferredSize(new Dimension (400, 60));
        panelProgresso.setOpaque(false);
        
        tempoTotal.setForeground(corTexto);
        tempoCorrente.setForeground(corTexto);
        
        progresso.setPreferredSize(new Dimension(250,12));
        progresso.setBackground(corFundo);
        progresso.setForeground(corAzul);
        progresso.setBorderPainted(false);
        progresso.setBorder(BorderFactory.createEmptyBorder());      
        progresso.setStringPainted(false);
        
        panelProgresso.add(tempoCorrente);
        panelProgresso.add(progresso);
        panelProgresso.add(tempoTotal);
        
        //--------------
        // Painel Rodape
        //--------------
        panelRodape.setPreferredSize(new Dimension (400, 80));
        panelRodape.setOpaque(false);
        
        caminhoTextField.setEditable(false);
        caminhoTextField.setBackground(corFundo);
        caminhoTextField.setForeground(corTexto);
        caminhoTextField.setBorder(BorderFactory.createEmptyBorder());
        caminhoTextField.setPreferredSize(new Dimension(335, 25));   
        
        botaoAbrir.setContentAreaFilled(false);
        botaoAbrir.setPreferredSize(new Dimension(55, 25));
        botaoAbrir.setBorder(BorderFactory.createEmptyBorder());
        botaoAbrir.setOpaque(true);
        botaoAbrir.setBackground(corAzul);
        botaoAbrir.setForeground(corTexto);
        botaoAbrir.setFocusable(false);
        botaoAbrir.setCursor(new Cursor(Cursor.HAND_CURSOR));
          
        caminhoTextField2.setEditable(false);
        caminhoTextField2.setBackground(corFundo);
        caminhoTextField2.setForeground(corTexto);
        caminhoTextField2.setBorder(BorderFactory.createEmptyBorder());
        caminhoTextField2.setPreferredSize(new Dimension(335, 25));   
        
        botaoAbrir2.setContentAreaFilled(false);
        botaoAbrir2.setPreferredSize(new Dimension(55, 25));
        botaoAbrir2.setBorder(BorderFactory.createEmptyBorder());
        botaoAbrir2.setOpaque(true);
        botaoAbrir2.setBackground(corAzul);
        botaoAbrir2.setForeground(corTexto);
        botaoAbrir2.setFocusable(false);
        botaoAbrir2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        panelRodape.add(caminhoTextField); 
        panelRodape.add(botaoAbrir);      
        panelRodape.add(caminhoTextField2); 
        panelRodape.add(botaoAbrir2);
        
        panelRodape.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
          
        //---------
        // Layout
        //---------        
        layout.setHorizontalGroup(layout.createSequentialGroup()
        		.addComponent(panelTopo)
                .addComponent(panelCentro)
                .addComponent(panelProgresso)
                .addComponent(panelRodape)
        );
        
        //------------------
        // Settings do Frame
        //------------------
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension (450, 350));
        frame.setResizable(false);
        frame.setContentPane(panelConteudo);
        frame.setIconImage(frameIcon.getImage());
        frame.paintComponents(this.getGraphics());
        frame.pack();
        frame.setVisible(true);
    }
 
    //-------------------------------------------------
    // Metodo que prepara as funcionalidades do Tocador
    //-------------------------------------------------
    public void preparaTocador(){

        try{    
              
            botaoPlay.setEnabled(false);
            botaoPause.setEnabled(false);
            botaoStop.setEnabled(false);
            botaoFoward.setEnabled(false);
            botaoBackward.setEnabled(false);  
            botaoMenos.setEnabled(false); 
            botaoMais.setEnabled(false); 
            botaoAbrir.setEnabled(true);

            volumeLabel.setText("" + ((volumeAtual*100)/127) + "");

            botaoAbrir.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    abrirMIDI();
                }
            }); 

            botaoAbrir2.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    abrirSF2();
                }
            }); 

            botaoPlay.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    tocar(nomeArquivo.getText(),inicioAudio);
                }
            }); 

            botaoPause.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    inicioAudio = sequenciador.getMicrosecondPosition();
                    pausar();
                }
            });

            botaoStop.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    parar();
                }
            }); 
  
            botaoMenos.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    if(volumeAtual > 0){
                        
                        volumeAtual--;
                        
                        for(int i=0; i<16; i++){
                            try { 
                                mudancaVolume.setMessage(ShortMessage.CONTROL_CHANGE, i, 7, volumeAtual);
                                receptor.send(mudancaVolume, -1);
                            } catch (InvalidMidiDataException e1) {}
                        }
                        volumeLabel.setText("" + ((volumeAtual*100)/127) + "");
                        if(volumeAtual == 0){
                            botaoMenos.setEnabled(false);
                        }
                        if(volumeAtual < 127){
                            botaoMais.setEnabled(true);   
                        }
                    }
                }
            });

            botaoMenos.addMouseListener(new MouseAdapter(){
                public void mousePressed(MouseEvent evt) {
                    if(evt.getClickCount() >= 2){
                        if(volumeAtual > 4){
                        
                            volumeAtual-=5;
                            
                            for(int i=0; i<16; i++){
                                try { 
                                    mudancaVolume.setMessage(ShortMessage.CONTROL_CHANGE, i, 7, volumeAtual);
                                    receptor.send(mudancaVolume, -1);
                                } catch (InvalidMidiDataException e1) {}
                            }
                            volumeLabel.setText("" + ((volumeAtual*100)/127) + "");
                            if(volumeAtual == 0){
                                botaoMenos.setEnabled(false);
                            }
                            if(volumeAtual < 127){
                                botaoMais.setEnabled(true);   
                            }
                        }
                    }
                }
            });

            botaoMais.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    if(volumeAtual < 127){
                        
                        volumeAtual++;
                        
                        for(int i=0; i<16; i++){
                            try { 
                                mudancaVolume.setMessage(ShortMessage.CONTROL_CHANGE, i, 7, volumeAtual);
                                receptor.send(mudancaVolume, -1);
                            } catch (InvalidMidiDataException e1) {}
                        }
                        volumeLabel.setText("" + ((volumeAtual*100)/127) + "");
                        if(volumeAtual == 127){
                            botaoMais.setEnabled(false);
                        }
                        if(volumeAtual > 0){
                            botaoMenos.setEnabled(true);
                        }
                    }
                }
            });  

            botaoMais.addMouseListener(new MouseAdapter(){
                public void mousePressed(MouseEvent evt) {
                    if(evt.getClickCount() >= 2){
                        if(volumeAtual < 123){
                        
                            volumeAtual+=5;
                            
                            for(int i=0; i<16; i++){
                                try { 
                                    mudancaVolume.setMessage(ShortMessage.CONTROL_CHANGE, i, 7, volumeAtual);
                                    receptor.send(mudancaVolume, -1);
                                } catch (InvalidMidiDataException e1) {}
                            }
                            volumeLabel.setText("" + ((volumeAtual*100)/127) + "");
                            if(volumeAtual == 127){
                                botaoMais.setEnabled(false);
                            }
                            if(volumeAtual > 0){
                                botaoMenos.setEnabled(true);
                            }
                        }
                    }
                }
            }); 

        } catch(Exception e){
            System.out.println(e.getMessage());
        }    
    }

    //-------------------------------------------------------------
    // Metodo que carrega um arquivo MIDI e o prepara para execucao
    //-------------------------------------------------------------
    public void abrirMIDI(){

        JFileChooser arqMIDI = new JFileChooser(".");  
        arqMIDI.setFileSelectionMode(JFileChooser.FILES_ONLY);              
        arqMIDI.setFileFilter(new FileFilter(){
            
            public boolean accept(File f){

                if (!f.isFile()) return true;
                String name = f.getName().toLowerCase();
                if (name.endsWith(".mid"))  return true;
                if (name.endsWith(".midi")) return true;
                return false;
            }

            public String getDescription(){ 
                return "Arquivo Midi (*.mid,*.midi)";
            }
        });

        arqMIDI.showOpenDialog(this);  
        caminhoMIDI = arqMIDI.getSelectedFile().toString();
        caminhoTextField.setText(caminhoMIDI);

        File arqSeqNovo = arqMIDI.getSelectedFile();

        try { 
            if(sequenciador!=null && sequenciador.isRunning()){ 
                sequenciador.stop();
                sequenciador.close();
                sequenciador = null;
            }
              
            Sequence sequeciaNova = MidiSystem.getSequence(arqSeqNovo);           
            double duracao = sequeciaNova.getMicrosecondLength()/1000000.0d;
            
            // if(caminhoSF2 != ""){
            //     carregarBanco(caminhoSF2); 
            // }         

            nomeArquivo.setText(arqSeqNovo.getName());                
            tempoTotal.setText(formataInstante(duracao));  
    
            botaoPlay.setEnabled(true);
            botaoStop.setEnabled(false);
            botaoFoward.setEnabled(false); 
            botaoMenos.setEnabled(false); 
            botaoMais.setEnabled(false);

        }catch (Throwable e1) { 
            System.out.println("Erro em carregaArquivoMidi: "+ e1.toString());
        }
    }

    public void abrirSF2(){

        JFileChooser arqSF2 = new JFileChooser(".");  
        arqSF2.setFileSelectionMode(JFileChooser.FILES_ONLY);              
        arqSF2.setFileFilter(new FileFilter(){
            
            public boolean accept(File f){

                if (!f.isFile()) return true;
                String name = f.getName().toLowerCase();
                if (name.endsWith(".sf2"))  return true;
                return false;
            }

            public String getDescription(){ 
                return "Arquivo SF2 (*.sf2)";
            }
        });

        arqSF2.showOpenDialog(this);  
        caminhoSF2 = arqSF2.getSelectedFile().toString();
        caminhoTextField2.setText(caminhoSF2);

        File arqSeqNovo = arqSF2.getSelectedFile();

        try { 
            if(sequenciador!=null && sequenciador.isRunning()){ 
                sequenciador.stop();
                sequenciador.close();
                sequenciador = null;
            }
              
            Sequence sequeciaNova = MidiSystem.getSequence(arqSeqNovo);           
            double duracao = sequeciaNova.getMicrosecondLength()/1000000.0d;
            carregarBanco(caminhoSF2); 

            nomeArquivo2.setText(arqSeqNovo.getName());                
            tempoTotal.setText(formataInstante(duracao));                   
              
            botaoPlay.setEnabled(true);
            botaoStop.setEnabled(false);
            botaoFoward.setEnabled(false); 
            botaoMenos.setEnabled(false); 
            botaoMais.setEnabled(false);

        }catch (Throwable e1) { 
            System.out.println("Erro em carregaArquivoMidi: "+ e1.toString());
        }
    }

    public void carregarBanco(String caminhoSF2) {         
        try { 
            sintetizador = MidiSystem.getSynthesizer();
            sintetizador.open();
        } catch (Exception e) { 
            System.out.println("Erro em MidiSystem.getSynthesizer(): " + e);                                  
            return; 
        }
                    
        bancoDefault = sintetizador.getDefaultSoundbank();
        if(bancoDefault != null){ 
            sintetizador.unloadAllInstruments(bancoDefault);          
        }         
        
        File arquivoSF2 = new File(caminhoSF2);

        try { 
            bancoSelecionado = MidiSystem.getSoundbank(arquivoSF2); 
        } catch (Exception e) { e.printStackTrace(); }

        sintetizador.loadAllInstruments(bancoSelecionado);

        try{ 
            sequenciador.getTransmitter().setReceiver(sintetizador.getReceiver());
        }catch (Exception e) { System.out.println("Erro no carregamento do banco: "+ e); }              
    }

    public void tocar(String caminho, long inicio) {  
        try {  
            
            File arquivoMIDI = new File(caminho);
            sequencia    = MidiSystem.getSequence(arquivoMIDI);  
            sequenciador = MidiSystem.getSequencer();  

            sequenciador.setSequence(sequencia); 
            sequenciador.open();  
            retardo(300);
            sequenciador.start();  
            
            receptor = sequenciador.getTransmitters().iterator().next().getReceiver();
            sequenciador.getTransmitter().setReceiver(receptor);
                                                    
            long duracao  = sequencia.getMicrosecondLength()/1000000;
            tempoCorrente.setText(formataInstante(0));                
                                            
            sequenciador.setMicrosecondPosition(inicioAudio);

            if (sequenciador.isRunning()){
                duracao = sequenciador.getMicrosecondLength();
                soando = true;
            } else { 
                soando = false; 
                sequenciador.stop();  
                sequenciador.close();
                inicio = 0L;
                duracao = 0;
            }  
            botaoAbrir.setEnabled(false);
            botaoAbrir.setBackground(corTexto);
            botaoAbrir2.setEnabled(false);
            botaoAbrir2.setBackground(corTexto);
            botaoPlay.setEnabled(false);
            botaoPause.setEnabled(true);
            botaoStop.setEnabled(true);                
            botaoBackward.setEnabled(true);
            botaoFoward.setEnabled(true);
            botaoMenos.setEnabled(true);
            botaoMais.setEnabled(true);
            
        }
        catch(MidiUnavailableException e1) { System.out.println(e1+" : Dispositivo midi nao disponivel.");}
        catch(InvalidMidiDataException e2) { System.out.println(e2+" : Erro nos dados midi.");}
        catch(IOException              e3) { System.out.println(e3+" : O arquivo midi nao foi encontrado.");}
        catch(Exception e)                 { System.out.println(e.toString());}   
    }

    public void pausar(){
            
        soando = false;
        sequenciador.stop();  
        
        botaoPlay.setEnabled(true);
        botaoPause.setEnabled(false);                       
    }

    public void parar(){

        soando = false;
        sequenciador.stop();  
        sequenciador.close();
        sequenciador = null;
        inicioAudio = 0L;
        
        botaoPlay.setEnabled(true);
        botaoPause.setEnabled(false);
        botaoStop.setEnabled(false);  
        botaoBackward.setEnabled(false);
        botaoAbrir.setEnabled(true);
        botaoAbrir.setBackground(corAzul);
        botaoAbrir2.setEnabled(true);
        botaoAbrir2.setBackground(corAzul);
        
        progresso.setValue(0);             
        tempoCorrente.setText(formataInstante(0));      
    }
    
    public String formataInstante(double t1){
        String inicio    = "";

        //--------início
        double h1  = (int)(t1/3600.0);
        double m1  = (int)((t1 - 3600*h1)/60);
        double s1  = (t1 - (3600*h1 +60*m1));

        double h1r  = t1/3600.0;
        double m1r  = (t1 - 3600*h1)/60.0f;
        double s1r  = (t1 - (3600*h1 +60*m1));

        String sh1="";
        String sm1="";
        String ss1="";

        if     (h1 ==0) sh1 = "00";
        else if(h1 <10) sh1 = "0"+reformata(h1, 0);
        else if(h1<100) sh1 = "" +reformata(h1, 0);
        else            sh1 = "" +reformata(h1, 0);

        if     (m1 ==0) sm1 = "00";
        else if(m1 <10) sm1= "0"+reformata(m1, 0);
        else if(m1 <60) sm1 = ""+reformata(m1, 0);

        if     (s1 ==0) ss1 = "00";
        else if(s1 <10) ss1 = "0"+reformata(s1r, 2);
        else if(s1 <60) ss1 = reformata(s1r, 2);

        return inicio = sh1 + ":" + sm1 + ":" + ss1;
    }

    public String reformata(double x, int casas){ 
        DecimalFormat df = new DecimalFormat() ;
        df.setGroupingUsed(false);
        df.setMaximumFractionDigits(casas);
        return df.format(x);
    }

    void retardo(int miliseg){  
        try { 
            Thread.sleep(miliseg);
        } catch(InterruptedException e) { }
    }

    public void run(){ 
        
        double duracao;
        double tempo;
        int    posicao = 0;
        
        while(true) {                      
            if (soando) { 
                duracao = sequenciador.getMicrosecondLength()/1000000;
                tempo   = sequenciador.getMicrosecondPosition()/1000000;
                posicao = (int) ((tempo*100)/duracao);
                try {         
                    progresso.setValue(posicao);                              
                    tempoCorrente.setText(formataInstante(tempo));     
                    retardo(1000);
                    if(tempo >= duracao){  
                        progresso.setValue(0);                              
                        tempoCorrente.setText(formataInstante(0));   
                        
                        botaoAbrir.setEnabled(true);
                        botaoAbrir.setBackground(corAzul);
                        botaoAbrir2.setEnabled(true);
                        botaoAbrir2.setBackground(corAzul);
                        botaoPlay.setEnabled(true);
                        botaoPause.setEnabled(false);
                        botaoStop.setEnabled(false);  
                        botaoFoward.setEnabled(false);                      
                    }
                } catch(Exception e) { System.out.println(e.getMessage());}  
            } else { 
                try { 
                    retardo(1000); 
                } catch(Exception e) { System.out.println(e.getMessage());}
            }                                       
        }
    }
              
    public static void main(String... args){
    	
            
        TrabalhoGUI2 tocador =  new TrabalhoGUI2();
        tocador.displayGUI();
        tocador.preparaTocador();
        Thread     thread  = new Thread(tocador);
        thread.start();


        // SwingUtilities.invokeLater(new Runnable(){
        	
        //     @Override
        //     public void run(){
        //         TrabalhoGUI2 tocador =  new TrabalhoGUI2();
        //         tocador.displayGUI();
        //         tocador.preparaTocador();

        //     }
        // });
    }
}