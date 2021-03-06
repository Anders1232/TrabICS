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
import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.JFileChooser;
import javax.swing.JSlider;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import javax.sound.midi.Track;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.InvalidMidiDataException;

import java.util.List;
import java.util.LinkedList;

import java.text.DecimalFormat;

import java.io.File;
import java.io.IOException;

import java.lang.Math;

import javax.imageio.ImageIO;

public class PrimeiroTocador extends JFrame implements Runnable {
	
    JFrame frame = new JFrame("Primeiro Tocador");

    PanelPersonalizado panelConteudo = new PanelPersonalizado();
    GroupLayout layout = new GroupLayout(panelConteudo);

    JPanel panelTopo      = new JPanel();
    JPanel panelBotoes    = new JPanel();
    JPanel panelProgresso = new JPanel();
    JPanel panelArquivos  = new JPanel();
    JPanel panelRodape    = new JPanel();

    BufferedImage backgroundPanel;
    
    final ImageIcon playIcon         = new ImageIcon("img/playnormal.png");
	final ImageIcon playHover        = new ImageIcon("img/playhover.png");
	final ImageIcon pauseIcon        = new ImageIcon("img/pausenormal.png");
	final ImageIcon pauseHover       = new ImageIcon("img/pausehover.png");
	final ImageIcon stopIcon         = new ImageIcon("img/stopnormal.png");
	final ImageIcon stopHover        = new ImageIcon("img/stophover.png");
    final ImageIcon fowardIcon       = new ImageIcon("img/fowardnormal.png");
    final ImageIcon fowardHover      = new ImageIcon("img/fowardhover.png");
    final ImageIcon backwardIcon     = new ImageIcon("img/backwardnormal.png");
    final ImageIcon backwardHover    = new ImageIcon("img/backwardhover.png");
    final ImageIcon menosIcon        = new ImageIcon("img/menosnormal.png");
    final ImageIcon maisIcon         = new ImageIcon("img/maisnormal.png");
	final ImageIcon frameIcon        = new ImageIcon("img/frameicon.png");
	
	Color corFundo  = new Color(5, 5, 5);
    Color corFundo2  = new Color(199, 199, 199);
	Color corTexto  = new Color(230, 232, 234);
	Color corTexto2 = new Color(142, 145, 151);
	Color corAzul   = new Color(40, 102, 213);

	Font font1 = new Font("Gill Sans MT Condensed", java.awt.Font.PLAIN, 25);
	Font font2 = new Font("Arial Narrow", java.awt.Font.ITALIC, 18);

    JLabel nomeArquivo   = new JLabel("Tocador MIDI");
    JLabel nomeArquivo2  = new JLabel("Orquestra Padrao");
    JLabel volumeLabel   = new JLabel("", SwingConstants.CENTER);
    JLabel tempoTotal    = new JLabel("00:00:00");
    JLabel tempoCorrente = new JLabel("00:00:00");

    BotaoIcone botaoStop     = new BotaoIcone(stopIcon);
    BotaoIcone botaoPlay     = new BotaoIcone(playIcon);
    BotaoIcone botaoPause    = new BotaoIcone(pauseIcon);
    BotaoIcone botaoFoward   = new BotaoIcone(fowardIcon);
    BotaoIcone botaoBackward = new BotaoIcone(backwardIcon);
    BotaoIcone botaoMenos    = new BotaoIcone(menosIcon);
    BotaoIcone botaoMais     = new BotaoIcone(maisIcon);

    BotaoSimples botaoPadrao  = new BotaoSimples("Resetar Banco");
    BotaoSimples botaoAbrir    = new BotaoSimples("Abrir");
    BotaoSimples botaoAbrir2   = new BotaoSimples("Abrir");
    BotaoSimples botaoInfo     = new BotaoSimples("Info");
    BotaoSimples botaoEventos  = new BotaoSimples("Eventos");
    
    JButton botaoFantasma = new JButton();

    JProgressBar progresso = new JProgressBar(0);

    JTextField caminhoTextField = new JTextField("  Escolha um arquivo MIDI...");    
    JTextField caminhoTextField2 = new JTextField("  Escolha um arquivo SF2...");

    final JDialog dialogInfo = new JDialog(frame, "Informacoes MIDI");
    final JDialog dialogEventos = new JDialog(frame, "Eventos MIDI");

    ShortMessage mudancaVolume = new ShortMessage();

    static Soundbank   bancoDefault;      
    static Soundbank   bancoSelecionado;
    private Sequence   sequencia;
    private Sequence   sequenciaNova;
    private Sequencer  sequenciador = null;
    static Synthesizer sintetizador = null;
    private Receiver   receptor     = null;
    private long       inicioAudio  = 0;
    private boolean    soando       = false; 
    private int        volumeAtual  = 64;

    String  caminhoMIDI;
    String  caminhoSF2;

    

    //-----------------------------------------------
    // Classe que define um panel com imagem de fundo
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

    //------------------------------------------
    // Classe que define os boteos com ImageIcon
    //------------------------------------------
    public class BotaoIcone extends JButton{

        public BotaoIcone(ImageIcon botaoNormal){
            setContentAreaFilled(false);
            setBorderPainted(false);
            setPreferredSize(new Dimension (40, 40));
            setFocusable(false);
            setOpaque(false);
            setIcon(botaoNormal);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        public void efeitoHover(final ImageIcon botaoNormal, final ImageIcon botaoHover){

            addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent evt) {
                    setIcon(botaoHover);
                }
                public void mouseExited(MouseEvent evt) {
                    setIcon(botaoNormal);
                }
            });
        }
    }

    //--------------------------------------
    // Classe que define os botoes sem icone
    //--------------------------------------
    public class BotaoSimples extends JButton{

        public BotaoSimples(String label) {
            setText(label);
            setContentAreaFilled(false);
            setBorder(BorderFactory.createEmptyBorder());
            setOpaque(true);
            setBackground(corAzul);
            setForeground(corTexto);
            setFocusable(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }

    //-----------------------------------------------
    // Classe que define os botoes sem icone
    //-----------------------------------------------
    public class EventoMidi{
        
        public long tique;
        public int trilha;
        public String mensagem;
        public EventoMidi(long tique, int trilha, String mensagem){
            this.tique=tique;
            this.trilha=trilha;
            this.mensagem=mensagem;
        }
    }

    //-------------------------------------------------
    // Metodo que apresenta a interface grafica na tela
    //-------------------------------------------------
    public void displayGUI(){
         
        //---------------------
        // Painel Topo Esquerdo 
        //---------------------
        panelTopo.setPreferredSize(new Dimension (400, 90));
        panelTopo.setOpaque(false);

        nomeArquivo.setPreferredSize(new Dimension (280, 25));
        nomeArquivo.setFont(font1);
        nomeArquivo.setForeground(corTexto);

        nomeArquivo2.setPreferredSize(new Dimension (280, 20));
        nomeArquivo2.setFont(font2);
        nomeArquivo2.setForeground(corTexto2);

        botaoPadrao.setPreferredSize(new Dimension(100, 25));

        panelTopo.add(nomeArquivo);
        panelTopo.add(nomeArquivo2);
        panelTopo.add(botaoPadrao);
        panelTopo.setLayout(new FlowLayout(FlowLayout.LEFT));

        //--------------------
        // Painel Centro
        //-------------------- 
        panelBotoes.setPreferredSize(new Dimension (380, 50));
        panelBotoes.setOpaque(false);
           
        botaoPlay.setPreferredSize(new Dimension (48, 48));
        botaoMenos.setPreferredSize(new Dimension (24, 24));
        botaoMais.setPreferredSize(new Dimension (24, 24));

        botaoFantasma.setContentAreaFilled(false);
        botaoFantasma.setPreferredSize(new Dimension(30, 25));
        botaoFantasma.setOpaque(false);
        botaoFantasma.setBorder(BorderFactory.createEmptyBorder());
        botaoFantasma.setFocusable(false);

        volumeLabel.setForeground(corTexto);
        volumeLabel.setPreferredSize(new Dimension(30, 24));

        panelBotoes.add(botaoBackward);
        panelBotoes.add(botaoPause);
        panelBotoes.add(botaoPlay);
        panelBotoes.add(botaoStop);
        panelBotoes.add(botaoFoward);
        panelBotoes.add(botaoFantasma);
        panelBotoes.add(botaoMenos);
        panelBotoes.add(volumeLabel);
        panelBotoes.add(botaoMais);

        panelBotoes.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

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
        
        //----------------
        // Painel Arquivos
        //----------------
        panelArquivos.setPreferredSize(new Dimension (400, 80));
        panelArquivos.setOpaque(false);

        caminhoTextField.setEditable(false);
        caminhoTextField.setBackground(corFundo);
        caminhoTextField.setForeground(corTexto);
        caminhoTextField.setBorder(BorderFactory.createEmptyBorder());
        caminhoTextField.setPreferredSize(new Dimension(335, 25));   

        botaoAbrir.setPreferredSize(new Dimension(55, 25));
          
        caminhoTextField2.setEditable(false);
        caminhoTextField2.setBackground(corFundo);
        caminhoTextField2.setForeground(corTexto);
        caminhoTextField2.setBorder(BorderFactory.createEmptyBorder());
        caminhoTextField2.setPreferredSize(new Dimension(335, 25));   
        
        botaoAbrir2.setPreferredSize(new Dimension(55, 25));

        panelArquivos.add(caminhoTextField); 
        panelArquivos.add(botaoAbrir);      
        panelArquivos.add(caminhoTextField2); 
        panelArquivos.add(botaoAbrir2);

        panelArquivos.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));

        //--------------
        // Painel Rodape
        //--------------
        panelRodape.setPreferredSize(new Dimension (400, 50));
        panelRodape.setOpaque(false);

        botaoInfo.setPreferredSize(new Dimension(55, 25));
        botaoInfo.setToolTipText("Visualizar informacoes do conteudo MIDI");
            
        botaoEventos.setPreferredSize(new Dimension(65, 25));
        botaoEventos.setToolTipText("Visualizar eventos MIDI");
        
        panelRodape.add(botaoInfo);
        panelRodape.add(botaoEventos);
        
        panelRodape.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
          
        //---------
        // Layout
        //---------        
        layout.setHorizontalGroup(layout.createSequentialGroup()
        		.addComponent(panelTopo)
                .addComponent(panelBotoes)
                .addComponent(panelProgresso)
                .addComponent(panelArquivos)
                .addComponent(panelRodape)
        );
        
        //------------------
        // Configuração do Frame
        //------------------
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension (450, 400));
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
            botaoAbrir.setEnabled(true);

            botaoBackward.efeitoHover(backwardIcon, backwardHover);
            botaoPause.efeitoHover(pauseIcon, pauseHover);
            botaoPlay.efeitoHover(playIcon, playHover);
            botaoStop.efeitoHover(stopIcon, stopHover);
            botaoFoward.efeitoHover(fowardIcon, fowardHover);

            botaoBackward.setEnabled(false);
            botaoPause.setEnabled(false);
            botaoPlay.setEnabled(false);
            botaoStop.setEnabled(false);
            botaoFoward.setEnabled(false);
            botaoMenos.setEnabled(false);
            botaoMais.setEnabled(false);

            botaoPadrao.setEnabled(false);
            botaoPadrao.setBackground(corFundo2);
            botaoAbrir2.setEnabled(false);
            botaoAbrir2.setBackground(corFundo2);
            botaoInfo.setEnabled(false);
            botaoInfo.setBackground(corFundo2);
            botaoEventos.setEnabled(false);
            botaoEventos.setBackground(corFundo2);

            volumeLabel.setText("" + ((volumeAtual*100)/127) + "");

            botaoPadrao.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    carregaPadrao();
                }
            });

            botaoAbrir.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    if (dialogInfo.isVisible()) {
                        dialogInfo.setVisible(false);
                        dialogInfo.dispose();    
                    }
                    if (dialogEventos.isVisible()) {
                        dialogEventos.setVisible(false);
                        dialogEventos.dispose();    
                    }
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

            botaoBackward.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    retroceder();
                }
            }); 

            botaoFoward.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    avancar();
                }
            }); 

            botaoMenos.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    diminuiVolume(0);
                }
            });

            botaoMenos.addMouseListener(new MouseAdapter(){
                public void mousePressed(MouseEvent evt) {
                    if(evt.getClickCount() >= 2){
                        diminuiVolume(1);
                    }
                }
            });

            botaoMais.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    aumentaVolume(0);
                }
            });  

            botaoMais.addMouseListener(new MouseAdapter(){
                public void mousePressed(MouseEvent evt) {
                    if(evt.getClickCount() >= 2){
                        aumentaVolume(1);
                    }
                }
            }); 

            botaoInfo.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    mostraInfo(frame);
                }
            });

            botaoEventos.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    mostraEventos(frame);
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
              
            sequenciaNova = MidiSystem.getSequence(arqSeqNovo);           
            double duracao = sequenciaNova.getMicrosecondLength()/1000000.0d;

            nomeArquivo.setText(arqSeqNovo.getName());                
            tempoTotal.setText(formataInstante(duracao));  
    
            botaoPlay.setEnabled(true);
            botaoAbrir2.setEnabled(true);
            botaoAbrir2.setBackground(corAzul);
            botaoInfo.setEnabled(true);
            botaoInfo.setBackground(corAzul);
            botaoEventos.setEnabled(true);
            botaoEventos.setBackground(corAzul);
            
            File arquivoMIDI = new File(nomeArquivo.getText());
            sequencia    = MidiSystem.getSequence(arquivoMIDI);  
            sequenciador = MidiSystem.getSequencer();  

            sequenciador.setSequence(sequencia); 
            sequenciador.open();  

        }
        catch(InvalidMidiDataException e2) { System.out.println(e2+" : Erro nos dados midi.");}
        catch(IOException              e3) { System.out.println(e3+" : O arquivo midi nao foi encontrado.");}
        catch(Throwable e1) { System.out.println("Erro ao carregar arquivo Midi: "+ e1.toString()); }
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
        nomeArquivo2.setText(arqSF2.getSelectedFile().getName());  

        try { 
            
            carregarBanco(caminhoSF2);              

        }catch (Throwable e1) { 
            System.out.println("Erro em carregaArquivoSF2: "+ e1.toString());
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
            botaoPadrao.setEnabled(true);
            botaoPadrao.setBackground(corAzul);
        }catch (Exception e) { System.out.println("Erro no carregamento do banco: "+ e); }
    }

    public void carregaPadrao(){
        
        bancoDefault = sintetizador.getDefaultSoundbank();
        if(bancoDefault != null){ 
            sintetizador.unloadAllInstruments(bancoSelecionado);
            sintetizador.loadAllInstruments(bancoDefault);
        }         
        
        try{ 
            botaoPadrao.setEnabled(false);
            botaoPadrao.setBackground(corFundo2);
            nomeArquivo2.setText("Orquesta Padrao");
            caminhoTextField2.setText("  Escolha um arquivo SF2...");
        }catch (Exception e) { System.out.println("Erro no carregamento do banco: "+ e); }   
    }

    public void tocar(String caminho, long inicio) {
        try {  
            
            sequenciador.start();  
            
            receptor = sequenciador.getTransmitters().iterator().next().getReceiver();
            sequenciador.getTransmitter().setReceiver(receptor);
                                                    
            long duracao  = sequencia.getMicrosecondLength()/1000000;
            tempoCorrente.setText(formataInstante(0));                
                                            
            sequenciador.setMicrosecondPosition(inicioAudio);

            for(int i=0; i<16; i++){
                try { 
                    mudancaVolume.setMessage(ShortMessage.CONTROL_CHANGE, i, 7, volumeAtual);
                    receptor.send(mudancaVolume, -1);
                } catch (InvalidMidiDataException e1) {}
            }

            if (sequenciador.isRunning()){
                duracao = sequenciador.getMicrosecondLength();
                soando = true;
            } else { 
                soando = false; 
                sequenciador.stop();  
                //sequenciador.close();
                progresso.setValue(0);             
                tempoCorrente.setText(formataInstante(0));  
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
            botaoPadrao.setEnabled(false);
            botaoPadrao.setBackground(corTexto);

            volumeAtual = (50*100/127);
            volumeLabel.setText("" + ((volumeAtual*100)/127) + "");

            
        }
        catch(MidiUnavailableException e1) { System.out.println(e1+" : Dispositivo midi nao disponivel.");}
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
        inicioAudio = 0L;
        
        botaoPlay.setEnabled(true);
        botaoPause.setEnabled(false);
        botaoStop.setEnabled(false);  
        botaoBackward.setEnabled(false);
        botaoFoward.setEnabled(true);
        botaoAbrir.setEnabled(true);
        botaoAbrir.setBackground(corAzul);
        botaoAbrir2.setEnabled(true);
        botaoAbrir2.setBackground(corAzul);
        botaoPadrao.setEnabled(true);
        botaoPadrao.setBackground(corAzul);
        
        progresso.setValue(0);             
        tempoCorrente.setText(formataInstante(0));      
    }

    public void diminuiVolume(int duploClique){

        if((volumeAtual > 9) && (duploClique == 1)){        
            volumeAtual-=10;
        } else if (volumeAtual < 9){
            volumeAtual = 0;
        }
        for(int i=0; i<16; i++){
            try { 
                mudancaVolume.setMessage(ShortMessage.CONTROL_CHANGE, i, 7, volumeAtual);
                receptor.send(mudancaVolume, -1);
            } catch (InvalidMidiDataException e1) {}
        }
        if(volumeAtual < 2){
            botaoMenos.setEnabled(false);
        }
        if(volumeAtual < 127){
            botaoMais.setEnabled(true);   
        }

        if((volumeAtual > 0) && (duploClique == 0)){
            volumeAtual--;
            for(int i=0; i<16; i++){
                try { 
                    mudancaVolume.setMessage(ShortMessage.CONTROL_CHANGE, i, 7, volumeAtual);
                    receptor.send(mudancaVolume, -1);
                } catch (InvalidMidiDataException e1) {}
            }
            if(volumeAtual < 2){
                botaoMenos.setEnabled(false);
            }
            if(volumeAtual < 127){
                botaoMais.setEnabled(true);   
            }
        }
        volumeLabel.setText("" + ((volumeAtual*100)/127) + "");
    }

    public void aumentaVolume(int duploClique){

        if((volumeAtual < 123) && (duploClique == 1)){    
            volumeAtual+=10;
        } else if (volumeAtual > 123){
            volumeAtual = 127;
        }
        for(int i=0; i<16; i++){
            try { 
                mudancaVolume.setMessage(ShortMessage.CONTROL_CHANGE, i, 7, volumeAtual);
                receptor.send(mudancaVolume, -1);
            } catch (InvalidMidiDataException e1) {}
        }
        if(volumeAtual == 127){
            botaoMais.setEnabled(false);
        }
        if(volumeAtual > 0){
            botaoMenos.setEnabled(true);
        }
        if((volumeAtual < 127) && (duploClique == 0)){         
            volumeAtual++;
            for(int i=0; i<16; i++){
                try { 
                    mudancaVolume.setMessage(ShortMessage.CONTROL_CHANGE, i, 7, volumeAtual);
                    receptor.send(mudancaVolume, -1);
                } catch (InvalidMidiDataException e1) {}
            }
            if(volumeAtual == 127){
                botaoMais.setEnabled(false);
            }
            if(volumeAtual > 0){
                botaoMenos.setEnabled(true);
            }
        }
        volumeLabel.setText("" + ((volumeAtual*100)/127) + "");
    }

    public void retroceder(){

        long novaPosicao = 0;

        novaPosicao = sequenciador.getMicrosecondPosition();
        if (novaPosicao >= 1000000) {
            novaPosicao -= 1000000;   
            sequenciador.setMicrosecondPosition(novaPosicao);
            retardo(200);
        }
    }

    public void avancar(){

        long novaPosicao = 0;
        long duracao     = 0;

        novaPosicao = sequenciador.getMicrosecondPosition();
        duracao  = sequenciador.getMicrosecondLength();

        if (novaPosicao <= duracao) {
            novaPosicao += 1000000;   
            sequenciador.setMicrosecondPosition(novaPosicao);
            retardo(200);
        }  
    }

    public void mostraInfo(JFrame frame){

        long duracao      = sequenciaNova.getMicrosecondLength() / 1000000;
        int resolucao     = sequenciaNova.getResolution();
        long totalTitques = sequenciaNova.getTickLength();

        float duracaoTique    = (float) duracao / totalTitques;
        float duracaoSeminima = duracaoTique * resolucao;
        float bpm             = 60 / duracaoSeminima;
        int totalSeminimas    = (int) (duracao / duracaoSeminima);

        JLabel  nome = new JLabel(nomeArquivo.getText());
        nome.setFont(font1);
        nome.setForeground(corTexto);

        JLabel informacoes = new JLabel(
            "<html><table border='0'>"
                + "<tr><td> Resolucao:           </td><td></td><td align='right'>" + resolucao       + "</td></tr><br>"
                + "<tr><td> Duracao:             </td><td></td><td align='right'>" + duracao         + "</td></tr><br>"
                + "<tr><td> Numero de Tiques:    </td><td></td><td align='right'>" + totalTitques    + "</td></tr><br>"
                + "<tr><td> Duracao do Tique:    </td><td></td><td align='right'>" + duracaoTique    + "</td></tr><br>"
                + "<tr><td> Duracao da Seminima: </td><td></td><td align='right'>" + duracaoSeminima + "</td></tr><br>"
                + "<tr><td> Total de Seminimas:  </td><td></td><td align='right'>" + totalSeminimas  + "</td></tr><br>"
                + "<tr><td> Andamento:           </td><td></td><td align='right'>" + Math.round(bpm) + "</td></tr><br>"
            + "</table></html>"
        );
        informacoes.setForeground(corTexto);

        PanelPersonalizado contentPanel = new PanelPersonalizado();
        contentPanel.setOpaque(false);
        
        JPanel nomePanel = new JPanel();
        nomePanel.setPreferredSize(new Dimension (400, 40));
        nomePanel.setOpaque(true);
        nomePanel.setBackground(corFundo);
        nomePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        JPanel infoPanel = new JPanel();
        infoPanel.setPreferredSize(new Dimension (350, 180));
        infoPanel.setOpaque(false);
        
        nomePanel.add(nome);
        infoPanel.add(informacoes);

        contentPanel.add(nomePanel);
        contentPanel.add(infoPanel);
        contentPanel.setOpaque(true);
        
        dialogInfo.setContentPane(contentPanel);

        //Show it.
        dialogInfo.setSize(new Dimension(400, 320));
        dialogInfo.setIconImage(frameIcon.getImage());
        dialogInfo.setResizable(false);
        dialogInfo.setLocation(450, 0);
        dialogInfo.setVisible(true);
    }
    
    public void mostraEventos(JFrame frame){

        JLabel nome = new JLabel(nomeArquivo.getText());
        nome.setFont(font1);
        nome.setForeground(corTexto);

        String[] colunas = {"Tique", "Trilha", "Mensagem"};

        Object[][] dados = obterMatrizDeEventosMidi();

        JTable tabela = new JTable(dados, colunas); 
        TableColumnModel tcm = tabela.getColumnModel();
        tabela.setOpaque(false);
        tcm.getColumn(0).setPreferredWidth(80);
        tcm.getColumn(1).setPreferredWidth(80);
        tcm.getColumn(2).setPreferredWidth(540);

        JScrollPane panelTabela = new JScrollPane(tabela);
        panelTabela.setPreferredSize(new Dimension (700, 450));
        panelTabela.setOpaque(false);
        panelTabela.getViewport().setOpaque(false);
        panelTabela.setBorder(BorderFactory.createEmptyBorder());

        tabela.setFillsViewportHeight(true);

        PanelPersonalizado contentPanel = new PanelPersonalizado();
        contentPanel.setOpaque(false);
        
        JPanel nomePanel = new JPanel();
        nomePanel.setPreferredSize(new Dimension (800, 40));
        nomePanel.setOpaque(true);
        nomePanel.setBackground(corFundo);
        nomePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        
        nomePanel.add(nome);
        
        contentPanel.add(nomePanel);
        contentPanel.add(panelTabela);
        contentPanel.setOpaque(true);

        contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));
        
        dialogEventos.setContentPane(contentPanel);

        //Show it.
        dialogEventos.setPreferredSize(new Dimension(800, 600));
        dialogEventos.setMinimumSize(new Dimension(800, 600));
        dialogEventos.setIconImage(frameIcon.getImage());
        dialogEventos.setLocation(450, 0);
        dialogEventos.setVisible(true);
    }

    public List<EventoMidi> obterEventos()  {
        Track[] trilhas = sequencia.getTracks();
        List<EventoMidi> listaDeEventos= new LinkedList<EventoMidi>();

        for(int i=0; i<trilhas.length; i++){
            Track trilha =  trilhas[i];
            for(int j=0; j<trilha.size(); j++){
                MidiEvent e = trilha.get(j);
                MidiMessage mensagem = e.getMessage();
                long tique = e.getTick();
                int n = mensagem.getStatus();
                String nomecomando = ""+n;
            
                switch(n)
                {
                    case 128: nomecomando = "noteON"; break;
                    case 144: nomecomando = "noteOFF"; break;
                    case 255:
                    {
                        nomecomando = "MetaMensagem:";
                        switch(mensagem.getMessage()[1])
                        {
                            case 0:
                            {
                                if(2 == mensagem.getMessage()[1])
                                {
                                    nomecomando+= " Número de sequência "+ mensagem.getMessage()[2]+mensagem.getMessage()[3];
                                }
                                break;
                            }
                            case 1:
                            {
                                nomecomando+= " Texto de tamanho: "+ mensagem.getMessage()[2]+ "\t Texto: ";
                                for(int k=0; k < mensagem.getMessage()[2]; k++)
                                {
                                    nomecomando+= (char) mensagem.getMessage()[k+3];
                                }
                                break;
                            }
                            case 2:
                            {
                                nomecomando+= " Mensagem de copyright, tamanho: "+ mensagem.getMessage()[2]+ "\t Texto: ";
                                for(int l=0; l < mensagem.getMessage()[2]; l++)
                                {
                                    nomecomando+= (char) mensagem.getMessage()[l+3];
                                }
                                break;
                            }
                            case 3:
                            {
                                nomecomando+= " Nome da sequência/trilha, tamanho "+ mensagem.getMessage()[2]+ "\t Nome: ";
                                for(int m=0; m < mensagem.getMessage()[2]; m++)
                                {
                                    nomecomando+= (char) mensagem.getMessage()[m+3];
                                }
                                break;
                            }
                            case 4:
                            {
                                nomecomando+= " Nome do instrumento, tamanho: "+ mensagem.getMessage()[2]+ "\t Nome: ";
                                for(int r=0; r < mensagem.getMessage()[2]; r++)
                                {
                                    nomecomando+= (char) mensagem.getMessage()[r+3];
                                }
                                break;
                            }
                            case 5:
                            {
                                nomecomando+= " Letra da música, tamanho: "+ mensagem.getMessage()[2]+ "\t Letra: ";
                                for(int o=0; o < mensagem.getMessage()[2]; o++)
                                {
                                    nomecomando+= (char) mensagem.getMessage()[o+3];
                                }
                                break;
                            }
                            case 6:
                            {
                                nomecomando+= " Marcador, tamanho: "+ mensagem.getMessage()[2]+ "\t Marcador: ";
                                for(int p=0; p < mensagem.getMessage()[2]; p++)
                                {
                                    nomecomando+= (char) mensagem.getMessage()[p+3];
                                }
                                break;
                            }
                            case 7:
                            {
                                nomecomando+= " Ponto de indicação(cue point), tamanho: "+ mensagem.getMessage()[2]+ "\t Ponto de indicação: ";
                                for(int q=0; q < mensagem.getMessage()[2]; q++)
                                {
                                    nomecomando+= (char) mensagem.getMessage()[q+3];
                                }
                                break;
                            }
                            case 0x58:
                            {
                                nomecomando+= "Compasso: "+ mensagem.getMessage()[3] + "/" + (int)Math.pow(2,mensagem.getMessage()[4]);
                                break;
                            }
                            case 0x59:
                            {
                                nomecomando+= "Tonalidade: "+ getTonalidade(mensagem.getMessage()[3],mensagem.getMessage()[4]);
                                break;
                            }
                            case 0x2F:
                            {
                                nomecomando+= " Fim da Trilha.";
                                break;
                            }
                            default:
                                nomecomando = " MetaMensagem." + n;
                        }
                        break; 
                    }
                    //---(introduzir outros casos)
                }
                listaDeEventos.add(new EventoMidi(tique, i, nomecomando));
            }
        }
        return listaDeEventos;
    }

    public Object[][] obterMatrizDeEventosMidi(){
        List<EventoMidi> lista= obterEventos();

        Object[][] data= new Object[lista.size()][];
        for(int i=0;i < lista.size(); i++){
            data[i]= new Object[3];
        }
        for(int i=0;i < lista.size(); i++){
            data[i][0]= lista.get(i).tique;
            data[i][1]= lista.get(i).trilha;
            data[i][2]= lista.get(i).mensagem;
        }
        return data;
    }

    //-------------------
    // Metodos Auxiliares
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
        else if(h1 <10) sh1 = "0"+reformata(h1);
        else if(h1<100) sh1 = "" +reformata(h1);
        else            sh1 = "" +reformata(h1);

        if     (m1 ==0) sm1 = "00";
        else if(m1 <10) sm1= "0"+reformata(m1);
        else if(m1 <60) sm1 = ""+reformata(m1);

        if     (s1 ==0) ss1 = "00";
        else if(s1 <10) ss1 = "0"+reformata(s1r);
        else if(s1 <60) ss1 = reformata(s1r);

        return inicio = sh1 + ":" + sm1 + ":" + ss1;
    }

    public String reformata(double x){
        DecimalFormat df = new DecimalFormat() ;
        df.setGroupingUsed(false);
        df.setMaximumFractionDigits(0);
        return df.format(x);
    }

    String getTonalidade(byte tonalidade, byte maior) {
    
        String stonalidade = "";

        String       smaior = "Maior";
            if(maior==1) smaior = "Menor";

            if(smaior.equalsIgnoreCase("Maior")) {
                switch (tonalidade)
                {
                    case -7: stonalidade = "Dob Maior"; break;
                    case -6: stonalidade = "Solb Maior"; break;
                    case -5: stonalidade = "Reb Maior"; break;
                    case -4: stonalidade = "Lab Maior"; break;
                    case -3: stonalidade = "Mib Maior"; break;
                    case -2: stonalidade = "Sib Maior"; break;
                    case -1: stonalidade = "Fa Maior"; break;
                    case  0: stonalidade = "Do Maior"; break;
                    case  1: stonalidade = "Sol Maior"; break;
                    case  2: stonalidade = "Re Maior"; break;
                    case  3: stonalidade = "La Maior"; break;
                    case  4: stonalidade = "Mi Maior"; break;
                    case  5: stonalidade = "Si Maior"; break;
                    case  6: stonalidade = "Fa# Maior"; break;
                    case  7: stonalidade = "Do# Maior"; break;
                }
            }

            else if(smaior.equalsIgnoreCase("Menor"))
            {
                switch (tonalidade)
                {
                    case -7: stonalidade = "Lab Menor"; break;
                    case -6: stonalidade = "Mib Menor"; break;
                    case -5: stonalidade = "Sib Menor"; break;
                    case -4: stonalidade = "Fa Menor"; break;
                    case -3: stonalidade = "Do Menor"; break;
                    case -2: stonalidade = "Sol Menor"; break;
                    case -1: stonalidade = "Re Menor"; break;
                    case  0: stonalidade = "La Menor"; break;
                    case  1: stonalidade = "Mi Menor"; break;
                    case  2: stonalidade = "Si Menor"; break;
                    case  3: stonalidade = "Fa# Menor"; break;
                    case  4: stonalidade = "Do# Menor"; break;
                    case  5: stonalidade = "Sol# Menor"; break;
                    case  6: stonalidade = "Re# Menor"; break;
                    case  7: stonalidade = "La# Menor"; break;
                }
            }
      
      return stonalidade;
    }

    void retardo(int miliseg){
        try { 
            Thread.sleep(miliseg);
        } catch(InterruptedException e) { }
    }
    // Metodos Auxiliares
    //-------------------

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
                        botaoBackward.setEnabled(false);                      
                    }
                } catch(Exception e) { System.out.println(e.getMessage());}  
            } else { 
                try { 
                    retardo(1000); 
                } catch(Exception e) { System.out.println(e.getMessage());}
            }                                       
        }
    }
              
    public static void main(String[] args){
            
        PrimeiroTocador tocador =  new PrimeiroTocador();
        tocador.displayGUI();
        tocador.preparaTocador();
        Thread thread = new Thread(tocador);
        thread.start();
    }
}
