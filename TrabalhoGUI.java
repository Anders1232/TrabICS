import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.Font;

public class TrabalhoGUI extends JFrame
{
	
    BufferedImage backgroundPanel;
    
    ImageIcon playIcon = new ImageIcon("img/playnormal.png");
	ImageIcon playHover = new ImageIcon("img/playhover.png");
	ImageIcon playDisabled = new ImageIcon("img/playdisabled.png");
	
	ImageIcon pauseIcon = new ImageIcon("img/pausenormal.png");
	ImageIcon pauseHover = new ImageIcon("img/pausehover.png");
	ImageIcon pauseDisabled = new ImageIcon("img/pausedisabled.png");
	
	ImageIcon stopIcon = new ImageIcon("img/stopnormal.png");
	ImageIcon stopHover = new ImageIcon("img/stophover.png");
	ImageIcon stopDisabled = new ImageIcon("img/stopdisabled.png");
	
	ImageIcon gramophone = new ImageIcon("img/gramophone.png");
	
	ImageIcon frameIcon = new ImageIcon("img/frameicon.png");
	
	Color corFundo = new Color(5, 5, 5);
	Color corTexto = new Color(220, 222, 224);
	Color corTexto2 = new Color(142, 145, 151);
	Color corAzul = new Color(40, 102, 213);

	Font arial = new Font("Arial", java.awt.Font.PLAIN, 20);
	Font arialNarrow = new Font("Arial Narrow", java.awt.Font.ITALIC, 14);
	
    private void displayGUI()
    {
    	    	
    	JFrame frame = new JFrame("Primeiro Tocador");

    	PanelPersonalizado panelConteudo = new PanelPersonalizado();
    	GroupLayout layout = new GroupLayout(panelConteudo);

    	JPanel panelTopo = new JPanel();
        JPanel panelCentro = new JPanel();
        JPanel panelCentroDir = new JPanel();
        JPanel panelProgresso = new JPanel();
        JPanel panelRodape = new JPanel();
         
        //---------------------
        // Painel Topo Esquerdo 
        //---------------------
        panelTopo.setPreferredSize(new Dimension (400, 80));
        panelTopo.setOpaque(false);
        
        JLabel nomeArquivo = new JLabel("Titulo do Arquivo MIDI");
        nomeArquivo.setForeground(corTexto);
        nomeArquivo.setPreferredSize(new Dimension (300, 25));
        nomeArquivo.setFont(arial);
        
        JLabel nomeArquivo2 = new JLabel("Titulo da Orquestra SF2");
        nomeArquivo2.setForeground(corTexto2);
        nomeArquivo2.setPreferredSize(new Dimension (300, 20));
        nomeArquivo2.setFont(arialNarrow);

        panelTopo.add(nomeArquivo);
        panelTopo.add(nomeArquivo2);
        
        panelTopo.setLayout(new FlowLayout(FlowLayout.LEFT));

        //--------------------
        // Painel Centro
        //-------------------- 
        panelCentro.setPreferredSize(new Dimension (250, 70));
        panelCentro.setOpaque(false);
        
        JButton botaoStop = new JButton(stopIcon);
        botaoStop.setContentAreaFilled(false);
        botaoStop.setBorderPainted(false);
        botaoStop.setPreferredSize(new Dimension (48, 48));
        botaoStop.setFocusable(false);
        
        botaoStop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
            	botaoStop.setIcon(stopHover);
            	botaoStop.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
            	botaoStop.setIcon(stopIcon);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
            	botaoStop.setIcon(stopDisabled);
            }
        });
        
        JButton botaoPlay = new JButton(playIcon);
        botaoPlay.setContentAreaFilled(false);
        botaoPlay.setBorderPainted(false);
        botaoPlay.setPreferredSize(new Dimension (48, 48));
        botaoPlay.setFocusable(false);
        
        botaoPlay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
            	botaoPlay.setIcon(playHover);
            	botaoPlay.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
            	botaoPlay.setIcon(playIcon);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
            	botaoPlay.setIcon(playDisabled);
            }
        });
        
        JButton botaoPause = new JButton(pauseIcon);
        botaoPause.setContentAreaFilled(false);
        botaoPause.setBorderPainted(false);
        botaoPause.setPreferredSize(new Dimension (48, 48));
        botaoPause.setFocusable(false);
        
        botaoPause.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
            	botaoPause.setIcon(pauseHover);
            	botaoPause.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
            	botaoPause.setIcon(pauseIcon);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
            	botaoPause.setIcon(pauseDisabled);
            }
        });
        
        panelCentro.add(botaoPause);
        panelCentro.add(botaoPlay);
        panelCentro.add(botaoStop);
        
        panelCentro.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
      
        
        //----------------------
        // Painel Centro Direita
        //----------------------
        panelCentroDir.setPreferredSize(new Dimension (150, 70));
        panelCentroDir.setOpaque(false);
        
        JSlider sliderVolume = new JSlider(JSlider.HORIZONTAL,0, 127, 50); 
        //sliderVolume.setUI(new mySliderUI(sliderVolume));
        sliderVolume.setPreferredSize(new Dimension(120, 25));
        sliderVolume.setOpaque(true);
        sliderVolume.setForeground(corAzul);
        sliderVolume.setBackground(corFundo);
        
        JLabel volume = new JLabel("50%");
        volume.setPreferredSize(new Dimension(30, 25));
        volume.setOpaque(true);
        volume.setForeground(corTexto);
        volume.setBackground(corFundo);
        
        panelCentroDir.add(sliderVolume);
        panelCentroDir.add(volume);
        
        panelCentroDir.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));

        //--------------------
        // Painel Progresso
        //-------------------- 
        panelProgresso.setPreferredSize(new Dimension (400, 50));
        panelProgresso.setOpaque(false);
        
        JLabel tempoTotal = new JLabel("00:00");
        JLabel tempoCorrente = new JLabel("00:00");
        tempoTotal.setForeground(corTexto);
        tempoCorrente.setForeground(corTexto);
        
        JProgressBar progresso = new JProgressBar(0);
        progresso.setPreferredSize(new Dimension(300,12));
        progresso.setBackground(corFundo);
        progresso.setForeground(corAzul);
        progresso.setBorderPainted(false);
        progresso.setBorder(BorderFactory.createEmptyBorder());
        progresso.setValue(10);        
        progresso.setStringPainted(false);
        
        panelProgresso.add(tempoTotal);
        panelProgresso.add(progresso);
        panelProgresso.add(tempoCorrente);
        
        //--------------
        // Painel Rodape
        //--------------
        panelRodape.setPreferredSize(new Dimension (400, 150));
        panelRodape.setOpaque(false);
        
        JTextField caminhoTextField = new JTextField("  Escolha um arquivo MIDI...");
        caminhoTextField.setEditable(false);
        caminhoTextField.setBackground(corFundo);
        caminhoTextField.setForeground(corTexto);
        caminhoTextField.setBorder(BorderFactory.createEmptyBorder());
        caminhoTextField.setPreferredSize(new Dimension(300, 25));   
        
        JButton botaoAbrir = new JButton("Abrir");
        botaoAbrir.setContentAreaFilled(false);
        botaoAbrir.setPreferredSize(new Dimension(55, 25));
        botaoAbrir.setBorder(BorderFactory.createEmptyBorder());
        botaoAbrir.setOpaque(true);
        botaoAbrir.setBackground(corAzul);
        botaoAbrir.setForeground(corTexto);
        botaoAbrir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JTextField caminhoTextField2 = new JTextField("  Escolha um arquivo SF2...");
        caminhoTextField2.setEditable(false);
        caminhoTextField2.setBackground(corFundo);
        caminhoTextField2.setForeground(corTexto);
        caminhoTextField2.setBorder(BorderFactory.createEmptyBorder());
        caminhoTextField2.setPreferredSize(new Dimension(300, 25));   
        
        JButton botaoAbrir2 = new JButton("Abrir");
        botaoAbrir2.setContentAreaFilled(false);
        botaoAbrir2.setPreferredSize(new Dimension(55, 25));
        botaoAbrir2.setBorder(BorderFactory.createEmptyBorder());
        botaoAbrir2.setOpaque(true);
        botaoAbrir2.setBackground(corAzul);
        botaoAbrir2.setForeground(corTexto);
        botaoAbrir2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        
        panelRodape.add(caminhoTextField); 
        panelRodape.add(botaoAbrir);      
        panelRodape.add(caminhoTextField2); 
        panelRodape.add(botaoAbrir2);
        
        panelRodape.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
          
        //---------
        // Layout
        //---------
        layout.setVerticalGroup(layout.createParallelGroup()
        		.addComponent(panelTopo));
        
        layout.setVerticalGroup(layout.createParallelGroup()
        		.addComponent(panelCentro)
        		.addComponent(panelCentroDir));
        
        layout.setHorizontalGroup(layout.createSequentialGroup()
        		.addComponent(panelProgresso)
        		.addComponent(panelRodape));
        
        
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
    
    private class mySliderUI extends BasicSliderUI {

        Image knobImage;

        public mySliderUI( JSlider aSlider ) {
            super( aSlider );
            try {
                this.knobImage = ImageIO.read( new File( "img/knob.png") );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        public void paintThumb(Graphics g)  {        
        	g.drawImage( this.knobImage, thumbRect.x, thumbRect.y, 16, 16, null );
        }

    }
          
    public static void main(String... args){
    	
        SwingUtilities.invokeLater(new Runnable(){
        	
            @Override
            public void run(){
                new TrabalhoGUI().displayGUI();
            }
        });
    }
}