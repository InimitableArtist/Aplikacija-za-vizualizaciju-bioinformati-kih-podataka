package fer.zavrsni.bioinformatics;

import java.io.File;
import java.util.Comparator;

import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;
import htsjdk.samtools.util.CloserUtil;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;
import htsjdk.samtools.SAMRecord;

public class BAMSorter {
	
	File inputFile;
	File outputFile;

	public BAMSorter(File inputFile, File outputFile) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		
	}
	
	public void run() {
		SamReaderFactory.setDefaultValidationStringency(ValidationStringency.SILENT);
		 final SamReader reader = SamReaderFactory.makeDefault().open(inputFile);
		 
		 reader.getFileHeader().setSortOrder(SAMFileHeader.SortOrder.coordinate);
		 SAMFileWriterFactory samFileWriterFactory = new SAMFileWriterFactory();
		 
		 final SAMFileWriter writer = samFileWriterFactory.makeSAMOrBAMWriter(reader.getFileHeader(), false, outputFile);
		 
		 int count = 0;
	        for (final SAMRecord rec : reader) {
	            if (++count % 100000 == 0) {
	                System.out.println("" + count + " records processed"); 
	            }
	            writer.addAlignment(rec);
	        }
	        
	        CloserUtil.close(reader);
	        writer.close();  
	}
	



}
