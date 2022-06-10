package fer.zavrsni.bioinformatics;

import java.awt.Color;
import java.awt.List;
import java.awt.desktop.ScreenSleepEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.jgrapht.alg.util.Pair;

import htsjdk.samtools.BAMIndexer;
import htsjdk.samtools.BAMRecord;
import htsjdk.samtools.Cigar;
import htsjdk.samtools.SAMException;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;
import htsjdk.samtools.fastq.FastqReader;
import htsjdk.samtools.fastq.FastqRecord;
import htsjdk.tribble.bed.BEDFeature;
import htsjdk.tribble.bed.SimpleBEDFeature;
import htsjdk.tribble.annotation.Strand;
import org.biojava.nbio.core.sequence.DNASequence;

public class BioFileReader {
	 
	private static MainScreen screen;
	private static boolean workingInBack = false;
	private static SamReader sr;
	private static ArrayList<String> sequencesList = new ArrayList<String>();
	private static LinkedHashMap<String, DNASequence> fastaFileFirst;
	private static LinkedHashMap<String, DNASequence> fastaFile = new LinkedHashMap<String, DNASequence>();
	private static JDialog dialog;
	private static boolean fastaMode = true;
	//Loads the entered fasta file into a LinkedHashMap
	public static LinkedHashMap<String, DNASequence> loadFastaFile(final File file) throws IOException {
		String[] fastaExtensions = {"fasta", "fa", "fna", "ffn", "faa", "frn", "fastq", "fq"};
		
		if (!checkExtensions(fastaExtensions, file)) {
			JOptionPane.showMessageDialog(null, "The file type is not correct");
			return null;
		}
		if (file.getName().toLowerCase().endsWith(".fq") || file.getName().toLowerCase().endsWith(".fastq")) {
			fastaMode = false;
		}
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

			@SuppressWarnings("unchecked")
			@Override
			protected Void doInBackground() throws Exception {
				BioFileReader.workingInBack = true;
				Controller.reset();
				if (fastaMode) {
					fastaFileFirst = FastaReaderHelper.readFastaDNASequence(file);
				} else {
					
					FastqReader reader = new FastqReader(file);
					Iterator<FastqRecord> iterator = reader.iterator();
					//fastaFileFirst = new FastqReader();
					fastaFileFirst = new LinkedHashMap<String, DNASequence>();
					while (iterator.hasNext()) {
						FastqRecord current = iterator.next();
						fastaFileFirst.put(current.getReadName(), new DNASequence(current.getReadString()));
					}
				}
				
				for (Entry entry : fastaFileFirst.entrySet()) {
					String[] words = entry.getKey().toString().split("\\s+");
					String refSequenceName = words[0];
					fastaFile.put(refSequenceName, (DNASequence) entry.getValue());
					
				}

				BioFileReader.screen.getRef().setRefSequences(fastaFile);
				//Controller.setCurrentSeq(sequencesList.get(0)); 
				Controller.setCurrentSeq(fastaFile.entrySet().iterator().next().getKey());
				screen.getRulerScreen().loadSeq(fastaFile);
				//BioFileReader.screen.getZoom().loadSeq(fastaFile);
				Controller.setFastaLoaded(true);
				return null;
			}
			
			
			@Override
			public void done() {
				BioFileReader.workingInBack = false;
				dialog.dispose();
			}
			
		};
		
		
		worker.execute();
		Object[] options = {"Cancle"};
		JOptionPane optionsP = new JOptionPane("Loading fasta file...", JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, options);
		dialog = optionsP.createDialog(null, "");
		dialog.setVisible(true);
		if(workingInBack) {
			worker.cancel(true);
		}
		
		Controller.repaintAll();
		
		
		return fastaFile;
	} 
	
	public static void loadSamFile(final File file) {
		String[] extensions = {"sam", "bam"};
		
		
		if(!checkExtensions(extensions, file)) {
			JOptionPane.showMessageDialog(null, "Wrong file type.");
			return;
		}
		//SamReader Sr = SamReaderFactory.makeDefault().validationStringency(ValidationStringency.SILENT).open(file);
		
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				BioFileReader.workingInBack = true;
				BioFileReader.screen.getReads().reset();
				sr = SamReaderFactory.makeDefault().validationStringency(ValidationStringency.SILENT).open(file);
				SAMFileHeader header = sr.getFileHeader();
			
				

				BioFileReader.readSam(this, header);
				Controller.setReadsLoaded(true);
				Controller.setReadvPos(0);
				return null;
				
			}
			
			@Override
			public void done() {
				BioFileReader.workingInBack = false;
				BioFileReader.dialog.dispose();
			}
			
		};
		
		worker.execute();
		
		Object[] options = {"Cancle"};
		JOptionPane optionP = new JOptionPane("Loading SAM file...", JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, options);

		dialog = optionP.createDialog(null, "");
		dialog.setVisible(true);
		if (workingInBack) {
			worker.cancel(true);
		}
		Controller.repaintAll();
		
	}
	
	
	//Loads the entered SAM file and returns it's iterator
	public static void readSam(SwingWorker<Void, Void> worker, SAMFileHeader header) throws IOException {
		
		
		Map<String, ArrayList<Read>> mapOfReads = new TreeMap<String, ArrayList<Read>>();
		
		SAMRecordIterator r = sr.iterator();
		Map<String, Graph> graphMap = new TreeMap<String, Graph>();
		
		while(r.hasNext()) {
			SAMRecord current = r.next();
			
			String readName = current.getReadName();
			
			String refName = current.getReferenceName();
			
			String[] refNameComponents = refName.split("\\|");
			for (String component : refNameComponents) {
				
				if (fastaFile.get(component) != null) {
					refName = component;
				}
				
			}
			
			if (!sequenceExists(refName)) {
				continue;
				
			}
			System.out.println(refName);
			
			
			
			int pos = current.getAlignmentStart();
			int alignEnd = current.getAlignmentEnd();
			
			
			int mapQ = current.getMappingQuality();
			Cigar cigar = current.getCigar();
			String rNext = current.getMateReferenceName();
			int pNext = current.getMateAlignmentStart();
			String readString = current.getReadString();
			int unclippedStart = current.getUnclippedStart();
			int unclippedEnd = current.getUnclippedEnd();
			
			
			boolean reverseComplement = current.getReadNegativeStrandFlag();
			int readLen = current.getReadLength();
			
			if (mapOfReads.get(refName) == null) {
				mapOfReads.put(refName, new ArrayList<Read>());
			
			} 
			mapOfReads.get(refName).add(new Read(header, readName, refName, pos, cigar, reverseComplement, readLen, mapQ, alignEnd,
					unclippedStart, unclippedEnd));
			
			
			
		}
		
		for (Map.Entry<String, ArrayList<Read>> entry : mapOfReads.entrySet()) {
			
			ArrayList<Read> l = new ArrayList<Read>(entry.getValue());
			Collections.sort(l);
			//System.out.println("Referece name: " + entry.getKey());
			
			putReads(worker, l, entry.getKey());
			
			//Ovo ce sortirati očitanja prema poziciji sto ce kasnije olaksati izvođenje binarne pretrage
			//jer nece biti potrebno opet sortirati listu ocitanja.
			mapOfReads.put(entry.getKey(), l);
			Graph graph = new Graph(worker, screen.getRef().getSequenceLen(entry.getKey()), entry.getValue());
			graphMap.put(entry.getKey(), graph);
			
		
		}
		
		
		
		if (worker.isCancelled()) {
			r.close();
			sr.close();
			return;
		}
		
		screen.getReads().setGraphMap(graphMap);
		screen.getReads().setReadMap(mapOfReads);
		screen.getReads().buildScrollbar();
		r.close();
		sr.close();
		
		
	}
	
	public static void loadBedFile(File file) throws NumberFormatException, IOException {
		String[] extensions = {"bed"};
		if(!checkExtensions(extensions, file)) {
			JOptionPane.showMessageDialog(null, "Incorrect file type.");
			return;
		}
		screen.getAnnotations().reset();
		Map<String, ArrayList<BEDAnnotation>> bedMap = new TreeMap<String, ArrayList<BEDAnnotation>>();
		readBEDFile(bedMap, file);
		screen.getAnnotations().setBedMap(bedMap);
		Controller.setAnnotationsLoaded(true);
		Controller.setAnnotationvPos(0);
		screen.getAnnotations().buildScrollbar();
		Controller.repaintAll();
	}
	public static void readBEDFile(Map<String, ArrayList<BEDAnnotation>> bedMap, File file) throws NumberFormatException, IOException {
	
		String str;
		FileReader fr;
		
		fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		while ((str = br.readLine()) != null) {
			
			String[] current = str.split("\\s+");
			//check matches sequence
			
			ArrayList<BEDGap> gaps = new ArrayList<BEDGap>();
			String chromName = current[0];
			int chromStart = Integer.parseInt(current[1]);
			int chromEnd = Integer.parseInt(current[2]);
			String lineName = current[3];
			String strandString = "!";
			Strand strand = Strand.toStrand(strandString);
			int thickStart = chromStart;
			int thickEnd = chromStart;

			int[] rgb = new int[3];
			rgb[0] = 0;
			rgb[1] = 1;
			rgb[2] = 2;
			Color color = new Color(rgb[0], rgb[1], rgb[2]);
			if (current.length >= 6) {
				strandString = String.valueOf(current[5].charAt(0));
				strand = Strand.toStrand(strandString);
				//feature.setStrand(Strand.toStrand(strand));
			}
			if (current.length >= 8) { 
				thickStart = Integer.parseInt(current[6]);
				thickEnd = Integer.parseInt(current[7]);
			}
			if (current.length >= 9) {
				if (!current[8].equals("0")) {
					String[] clrs = current[8].split(",");
					for (int i = 0; i < clrs.length; i++) {
						rgb[i] = Integer.parseInt(clrs[i]);
					}
					color = new Color(rgb[0], rgb[1], rgb[2]);
					//feature.setColor(color);
				}
			}
			if (current.length >= 12) {
				int blockCount = Integer.parseInt(current[9]);
				if (blockCount != 1 && blockCount != 0) {
					String[] blockSizes = current[10].split(",");
					String[] blockStarts = current[11].split(",");
					for (int i = 1; i < blockCount; i++) {
						int pos = Integer.parseInt(blockStarts[i - 1]) + Integer.parseInt(blockSizes[i - 1]) + chromStart;
						int len = Integer.parseInt(blockStarts[i]) + chromStart - pos;
						BEDGap gap = new BEDGap(pos, len);
						gaps.add(gap);
					}
				}
			}
			Collections.sort(gaps);
			if(bedMap.get(chromName) == null) {
				bedMap.put(chromName, new ArrayList<BEDAnnotation>());
			}
			bedMap.get(chromName).add(new BEDAnnotation(chromName, chromStart, chromEnd, strand, rgb, lineName, thickStart, thickEnd));
		}
		br.close();
		
		for(Map.Entry<String, ArrayList<BEDAnnotation>> e : bedMap.entrySet()) {
			ArrayList<BEDAnnotation> l = new ArrayList<BEDAnnotation>(e.getValue());
			Collections.sort(l);
			setLines(l, e.getKey());
			bedMap.put(e.getKey(), l);
		}
	}
	
	public static void generateIndexBamFile(File file) throws IOException {
		sr = SamReaderFactory.makeDefault().validationStringency(ValidationStringency.SILENT).open(file);
		
		
		if (sr.getFileHeader().getSortOrder().equals(SAMFileHeader.SortOrder.coordinate)) {
			throw new SAMException("Input bam file must be sorted by coordinates.");
		}
		File indexFile = new File(file.getName() + ".bai");
		//indexFile.createNewFile();
		//BAMIndexer bamIndexer = new BAMIndexer(indexFile, sr.getFileHeader());
		BAMIndexer.createIndex(sr, indexFile);
	}
	
	private static void setLines(ArrayList<BEDAnnotation> list, String seqName) {
		SortedMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
		int nextAnn = 0;
		int amp = -1;
		int fFree = 0;
		int seqLen = BioFileReader.screen.getRef().getSequenceLen(seqName);
		for (int i = 0; i < seqLen; i++) {
			for (int j = nextAnn; j < list.size(); j++) {
				if (list.get(j).getStart() > i) {
					break;
				}
				nextAnn++;
				list.get(j).setGraphicalPosition(fFree);
				map.put(fFree, list.get(j).getStart() + list.get(j).getLen());
				for (int k = fFree + 1; k <= list.size(); k++) {
					if (!map.containsKey(k)) {
						fFree = k;
						break;
					}
				}
				if (amp < fFree) {
					amp = fFree;
				}
			}
			Set<Integer> keys = new HashSet<Integer>();
			for(Entry<Integer, Integer> e : map.entrySet()) {
				if (e.getValue().equals(i)) {
					keys.add(e.getKey());
				}
			}
			for (Integer k : keys) {
				if (k < fFree) {
					fFree = k;
				}
				map.remove(k);
			}
		}
		
		if (screen.getAnnotations().getAmp() < amp) {
			screen.getAnnotations().setAmp(amp);
		}
	}
	public static boolean checkExtensions(String[] extensions, File file) {
		for (String i : extensions) {
			if (file.getName().toLowerCase().endsWith("." + i))
				return true;
		}
		return false;
	}
	private static void putReads(SwingWorker<Void, Void> worker, ArrayList<Read> listOfReads, String refName) {
		SortedMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
		int nRead = 0;
		int fFree = 0;
		System.out.println("ref name: " + refName);
		int sequenceLen = BioFileReader.screen.getRef().getSequenceLen(refName);
		int listOfReadsLen = listOfReads.size();
		
		for (int j = 0; j < sequenceLen; j++) {
			if (worker.isCancelled()) { 
				return;
			}
			//System.out.println(j + " out of: " + sequenceLen);
			for (int k = nRead; k < listOfReadsLen; k++) {
				Read currentRead = listOfReads.get(k);
				int currentReadPos = currentRead.getPos();
				if (currentReadPos > j) {
					
					break;
				}
				nRead++;
				currentRead.setGraphicPosition(fFree);
				map.put(fFree, currentReadPos + currentRead.getReadLen());
				for (int h = fFree + 1; h <= listOfReadsLen; h++) {
					if(!map.containsKey(h)) {
						
						
						
						fFree = h;
						break;
					}
				}
			}
			Set<Integer> keys = new HashSet<Integer>();
			for (Entry<Integer, Integer> e : map.entrySet()) {
				if (e.getValue().equals(j)) {
					keys.add(e.getKey());
				}
			}
			for (Integer k : keys) {
				if (k < fFree) {
					fFree = k;
					
				}
				map.remove(k);
			}
		}
		
	}
	
	public static void setScreen(MainScreen screen) {
		BioFileReader.screen = screen;
	}
	
	public static boolean sequenceExists(String s) {
		for (String seqName : screen.getRef().getNamesOfSequences()) {
			if (s.equals(seqName)) {
				return true;
			}
		}
		return false;
	}
	public static void main(String[] args) throws IOException {
		
		//String fileName = args[0];
		//File file = new File("alignment.sam");
		//readSam(file);
		
	}
}
