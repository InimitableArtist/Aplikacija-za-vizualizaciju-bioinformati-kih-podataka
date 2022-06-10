package fer.zavrsni.bioinformatics;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.ModuleLayer.Controller;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.JButton;
public class VisMenuBar extends JMenuBar {
	
	private final MainScreen screen;
	private JMenuItem SAM;
	private JMenuItem FASTA;
	private JMenuItem AnnotationMenu;
	private JFileChooser fileChooser = new JFileChooser();
	
	private JMenuItem BamIndex;
	
	public VisMenuBar(MainScreen screen) {
		this.screen = screen;
		BioFileReader.setScreen(screen);
		
		JMenu file = new JMenu("File");
		JMenu tools = new JMenu("Tools");

		
		//Adding an option in file menu to load a SAM file
		this.SAM = new JMenuItem("Load SAM/BAM file");
		this.SAM.setEnabled(false);
		loadSAM(this.SAM);
		
		//Adding an option in file menu to load a FASTA file
		this.FASTA = new JMenuItem("Load FASTA File");
		//FASTA.setEnabled(false);
		loadFASTA(this.FASTA);
		
		this.AnnotationMenu = new JMenuItem("Load annotation file (BED)");
		this.AnnotationMenu.setEnabled(false);
		openBED(this.AnnotationMenu);
		
		file.add(this.SAM);
		file.add(this.FASTA);
		file.add(this.AnnotationMenu);

		this.BamIndex = new JMenuItem("Generate a BAM index");
		IndexBAM(BamIndex);
		
		tools.add(this.BamIndex);
		
		

		add(file);
		add(tools);
		//add(view);
		//this.setVisible(true);
	}
	
	private int fileOpener(String text, String[] extensions, JMenuItem it) {
		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		
		FileNameExtensionFilter ff = new FileNameExtensionFilter(text, extensions);
		fileChooser.addChoosableFileFilter(ff);
		fileChooser.setFileFilter(ff);
		 
		int result = fileChooser.showOpenDialog(it);
		return result;
	}
	
	private void loadSAM(final JMenuItem it) {
		it.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				String[] extensions = {"sam", "bam"};
				int result = fileOpener("All SAM/BAM files", extensions, it);
				if (result == JFileChooser.APPROVE_OPTION) {
					File chosenFile = fileChooser.getSelectedFile();
					BioFileReader.loadSamFile(chosenFile);
				}
				
			}
			
		});
	}
	
	private void loadFASTA(final JMenuItem it) {
		it.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				
				//Setting a filter so that only FASTA files are able to be loaded
				String[] extensions = {"fasta", "fa", "fna", "ffn", "fq", "fastq"};
				int choice = fileOpener("All FASTA files", extensions, it);
				if (choice == JFileChooser.APPROVE_OPTION) {
					File chosenFile = fileChooser.getSelectedFile();
					try {
						System.out.println(chosenFile.getAbsolutePath());
						BioFileReader.loadFastaFile(chosenFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//System.out.println(e.getMessage());
					}
				}
				
			}
			
		});
	}
	private void openBED(final JMenuItem it) {
		it.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				String[] extensions = {"bed"};
				int choice = fileOpener("All annotation files", extensions, it);
				
				if (choice == JFileChooser.APPROVE_OPTION) {
					File chosenFile = fileChooser.getSelectedFile();
					try {
						BioFileReader.loadBedFile(chosenFile);
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		});
	}
	
	private void IndexBAM(final JMenuItem it) {
		it.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				String[] extensions = {"bam"};
				int choice = fileOpener("BAM files", extensions, it);
				
				if (choice == JFileChooser.APPROVE_OPTION) {
					File chosenFile = fileChooser.getSelectedFile();
					try {
						BioFileReader.generateIndexBamFile(chosenFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
		});
	}
	


	public void fastaLoaded(boolean isItLoaded) {
		SAM.setEnabled(true);
		//AnnotationMenu.setEnabled(true);
	}
	public void readsLoaded(boolean isItLoaded) {
		AnnotationMenu.setEnabled(true);
	}
	
	

}
