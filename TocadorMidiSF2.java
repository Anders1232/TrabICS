import javax.sound.midi.MidiSystem;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Sequence;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.InvalidMidiDataException;

import java.io.File;
import java.io.IOException;


public class TocadorMidiSF2
{                     
	static Soundbank    bancoSELECIONADO;        
	static Synthesizer  sintetizador = null;
	static Sequencer    sequenciador              = null;     
	static Sequence     sequencia;   


	public static void main(String args[]) 
	{        
                //----Arquivo de banco sf2---------
                //String  bancoSF2_externo = "PCLite.sf2";
                //String  bancoSF2_externo = "voiperpn.sf2";
                //String  bancoSF2_externo = "saron.sf2";  //---instrumentos asiáticos
                String  bancoSF2_externo = "merlin_gold.sf2";
                //String  bancoSF2_externo = "FluidR3_GM.sf2";

                //String  bancoSF2_externo = "inside03.sf2";
                //String  bancoSF2_externo = "ding.sf2";
                //String  bancoSF2_externo = "soundbank-emg.sf2";



                //----Arquivos midi---------
		//String            nomearq = "fuga6.mid";
		//String            nomearq = "pastoral.mid";
		//String            nomearq = "sinfoniapiloto1.mid";
		//String            nomearq = "sinfoniapiloto2.mid";
		//String            nomearq = "sinfoniapiloto3.mid";
		//String            nomearq = "mvioloncelo1.mid";
		String            nomearq = "paisagemintervalar.mid";
		//String            nomearq = "03AllegroBrandenburgoN6.mid";
		//String            nomearq = "01AllegroBrandenburgoN1.mid";

		if(args.length>0) nomearq = args[0];	
                nomearq = "mid/" + nomearq;
		File      arqmidi = new File(nomearq);
		

		try
	        { sequencia = MidiSystem.getSequence(arqmidi);
		  
		  //---preparar o sequenciador com o banco: ------
		  sequenciador = MidiSystem.getSequencer();
		  sequenciador.open();     

		  exibirDados(sequencia, nomearq);
		  carregarBANCO(bancoSF2_externo);

		  sequenciador.setSequence(sequencia);
		  sequenciador.start();  //--aqui começa a tocar.
		  //----------------------------------------------
		  

		  int i=0;
                  System.out.println("\nBanco carregado: " + bancoSF2_externo+"\n");
		  System.out.println("Instante em segundos: ");
					  
		  while(true)
		  { 
		    if(sequenciador.isRunning())
		    { 
		      retardo(1000); 
		    
		      //----exibir o instante real em segundos:---------
		      long  posicao = sequenciador.getMicrosecondPosition();
		      int   seg     = Math.round(posicao*0.000001f);
		      System.out.print(seg + " ");
		      i++;
		      if(i==20) { System.out.println("");
				  i=0;
				}            
		    } 
		    else break;        
		  }      
		  
		  System.out.println("");
		  System.out.println("* * * \n");
		  
		  retardo(1000); 
		  sequenciador.stop();
		  sequenciador.close();
		} 
		
		catch(MidiUnavailableException e1) { System.out.println(e1+" : Dispositivo midi não disponível.");}
		catch(InvalidMidiDataException e2) { System.out.println(e2+" : Erro nos dados midi."); }
		catch(IOException              e3) { System.out.println(e3+" : O arquivo midi não foi encontrado."); 
                                                     System.out.println("Sintaxe: "+"java TocaMidi arquivo.mid"); 		                                   
		                                   }
	}  
	


	
        //-------Carregador de bancos SF2
        static void carregarBANCO(String bancoSF2_externo)
        {         
          try { sintetizador = MidiSystem.getSynthesizer();
                sintetizador.open();
              }
          catch (Exception ex) { System.out.println("Erro em MidiSystem.getSynthesizer(): " + ex);                                  
                                 return; 
                               }
                    
          Soundbank bancodefault = sintetizador.getDefaultSoundbank();
          if(bancodefault != null)
          { sintetizador.unloadAllInstruments(bancodefault);          
          }         
          
          System.out.println("\nBanco: " + bancoSF2_externo +"\n");
          
          File arquivoSF2 = new File( "sf2/" + bancoSF2_externo ); 
          if(!arquivoSF2.exists()) { System.out.println("Arquivo inexistente: " + bancoSF2_externo + "\n");
                                     System.exit(0);
                                   }

          try { bancoSELECIONADO = MidiSystem.getSoundbank(arquivoSF2); }
          catch (Exception e) { e.printStackTrace(); }

          sintetizador.loadAllInstruments( bancoSELECIONADO);

          
          //-----Lista dos instrumenteos carregados:
          Instrument[] instrumentos = sintetizador.getLoadedInstruments();                  
          for(int i=0; i<instrumentos.length; i++)       
          { System.out.println("Instrumento "+ i + " = "+instrumentos[i].getName());
          }
          

          try{ sequenciador.getTransmitter().setReceiver(sintetizador.getReceiver());
             }
          catch (Exception e) { System.out.println("Erro no carregamento do banco: "+e); }              

        }
        //-------fim do método carregador de bancos SF2

        



	static void retardo(int miliseg)
	{ try { Thread.sleep(miliseg);
	      }
	  catch(InterruptedException e) { }
	}
	
		
	static void exibirDados(Sequence sequencia, String nome)
	{  
	  long duracao     = sequencia.getMicrosecondLength()/1000000;
	  int  resolucao   = sequencia.getResolution();
	  long totaltiques = sequencia.getTickLength();
			  
	  float durtique       = (float)duracao/totaltiques;
	  float durseminima    = durtique*resolucao;
	  float bpm            = 60/durseminima;
	  int   totalseminimas = (int)(duracao/durseminima);
	  
	  System.out.println("");
	  System.out.println("------------------------------------------");
	  System.out.println("--Introdução à Computação Sônica/CIC/UnB--");
	  System.out.println("------------------------------------------");
	  System.out.println("--------Arquivo Midi: " + nome + " ----");
	  System.out.println("------------------------------------------");
	  System.out.println("resolução            = "+resolucao+" tiques   (número de divisões da semínima)");
	  System.out.println("duração              = "+duracao+" s");
	  System.out.println("número de tiques     = "+totaltiques+" ");
	  System.out.println("duração do tique     = "+durtique+" s");
	  System.out.println("duração da semínima  = "+durseminima+" s");
	  System.out.println("total de seminimas   = "+totalseminimas);
	  System.out.println("andamento            = "+Math.round(bpm)+ " bpm");
	  System.out.println("---");
	  
	  System.out.println("");
	}


}   //--fim da classe TocadorMidiSF2
