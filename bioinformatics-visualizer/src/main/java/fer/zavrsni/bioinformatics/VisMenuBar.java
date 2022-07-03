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

import com.sun.xml.ws.message.stream.OutboundStreamHeader;

import htsjdk.samtools.BAMIndex;
import htsjdk.samtools.BAMIndexer;
import htsjdk.samtools.BamFileIoUtils;
import htsjdk.samtools.SAMException;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileSource;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.util.CloserUtil;
import htsjdk.samtools.util.IOUtil;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;

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
				String[] extensions = {"sam"};
				int choice = fileOpener("SAM files", extensions, it);
				if (choice == JFileChooser.APPROVE_OPTION) {
					File chosenFile = fileChooser.getSelectedFile();
					SamReader sr;
					sr = SamReaderFactory.makeDefault().validationStringency(ValidationStringency.SILENT).open(chosenFile);
					SAMRecordIterator iterator = sr.iterator();
					final SAMFileHeader header = sr.getFileHeader().clone();
					header.setSortOrder(SAMFileHeader.SortOrder.coordinate);
					final SAMFileWriterFactory fact = new SAMFileWriterFactory();
					
					File outputBAM;
					String path = chosenFile.getPath();
					int lastSlash = path.lastIndexOf("/");
					String baseFileName = path.substring(lastSlash + 1, path.length());
					int index = baseFileName.lastIndexOf(".");
					outputBAM = new File(baseFileName.substring(0, index) + BamFileIoUtils.BAM_FILE_EXTENSION);
					
					
					try (SAMFileWriter writer = fact.makeBAMWriter(header, false, outputBAM)) {
						while (iterator.hasNext()) {
							final SAMRecord record = iterator.next();

							writer.addAlignment(record);

						}
						writer.close();
					}
					System.out.println("Saved at: " + baseFileName.substring(0, index) + BamFileIoUtils.BAM_FILE_EXTENSION);
					
					
					
					File output;
					File bamSorted;
					path = outputBAM.getPath();
					lastSlash = path.lastIndexOf("/");
					baseFileName = path.substring(lastSlash + 1, path.length());
					
					if (baseFileName.endsWith(BamFileIoUtils.BAM_FILE_EXTENSION)) {
						index = baseFileName.lastIndexOf(".");
						output = new File(baseFileName.substring(0, index) + ".sorted" + BAMIndex.BAMIndexSuffix);
						bamSorted = new File(baseFileName.substring(0, index) + ".sorted" + BamFileIoUtils.BAM_FILE_EXTENSION);
					} else {
						output = new File(baseFileName + ".sorted" + BAMIndex.BAMIndexSuffix);
						bamSorted = new File(baseFileName + ".sorted" + BamFileIoUtils.BAM_FILE_EXTENSION);
					}
					
					IOUtil.assertFileIsWritable(output);
					final SamReader bam;
					IOUtil.assertFileIsReadable(outputBAM);
					
					BAMSorter sorter = new BAMSorter(outputBAM, bamSorted);
					sorter.run();
					
					
					bam = SamReaderFactory.makeDefault().validationStringency(ValidationStringency.SILENT).enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS).open(outputBAM);
					if (!bam.getFileHeader().getSortOrder().equals(SAMFileHeader.SortOrder.coordinate)) {
						throw new SAMException("Input bam file must be sorted by coordinates.");
						
					}
					BAMIndexer.createIndex(bam, output);
					CloserUtil.close(bam);
					CloserUtil.close(outputBAM);
					System.out.println("Index created!");
					System.out.println(output.getAbsolutePath());
					JOptionPane dialog = new JOptionPane();
					dialog.showMessageDialog(null, "Index created.");
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
