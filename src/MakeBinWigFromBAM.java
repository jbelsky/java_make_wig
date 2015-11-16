import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;


public class MakeBinWigFromBAM {

	public static void 	write_wig_output(BufferedWriter output, BinReads[] br_arr, int chr_length,
										 int bin_size, int step_size, double rpkm_denom_fact) throws IOException{
	
		// Set the starting and ending bin_position
		int start_bin_pos = 1;
		int end_bin_pos = start_bin_pos + bin_size - 1;
	
		// Set the output DecimalFormat
		DecimalFormat df = new DecimalFormat("#.####");
			
		// Set the loop boolean
		boolean loopB = true;
		
		while(loopB){
		
			if(end_bin_pos > chr_length){
				loopB = false;
				end_bin_pos = chr_length;
			}
			
			// Get the bin_idx corresponding to the start and end bin pos
			int start_b_idx = Arrays.binarySearch(br_arr, start_bin_pos, BinReads.posCompare);
			int end_b_idx = Arrays.binarySearch(br_arr, end_bin_pos, BinReads.posCompare);
		
			// 	Get the total bin count
			double total_count = 0;
		
			// Get the count over this region
			for(int b = start_b_idx; b <= end_b_idx; b++){
			
				total_count += br_arr[b].getCount();
			
			}
		
			// Get the RPKM count over the bin read
			double bin_read = total_count / rpkm_denom_fact;
			
			// Write the output
			output.write(df.format(bin_read) + "\n");
								
			// Update the start_bin_pos and end_bin_pos
			start_bin_pos += step_size;
			end_bin_pos += step_size;
			
		}
		
	}
	
	
	public static void add_reads_to_bin(SAMRecordIterator bam_itr, BinReads[] br_arr, int chr_length){
		
		while(bam_itr.hasNext()){
			
			// Get the read
			SAMRecord read = bam_itr.next();
			
			// Get the position
			int pos = 0;
			
			if(read.getReadNegativeStrandFlag()){
				pos = read.getAlignmentEnd() - 75;
			}else{
				pos = read.getAlignmentStart() + 75;
			}
			
			// Ensure that the position falls within the chr range
			pos = Math.max(pos, 1);
			pos = Math.min(pos, chr_length);
			
			// Find the BinRead associated with that position
			int br_arr_idx = Arrays.binarySearch(br_arr, pos, BinReads.posCompare);
			
			// Update the count of the BinRead at this position
			br_arr[br_arr_idx].increment_count();
							
		}
		
		// Close the bam_itr
		bam_itr.close();
		
	}
	
	
	public static void main(String[] args) throws IOException {

		// Set the BAM File Name
		String bam_file_name = args[0];
		String output_file_name = args[1];
		int bin_size = Integer.parseInt(args[2]);
		int step_size = Integer.parseInt(args[3]);
		String bam_file_id = args[4];
		String wig_descr_str = args[5];
		String wig_color = args[6];
		String max_y_lim = args[7];
		boolean isYeast = Boolean.parseBoolean(args[8]);
		
		// Open the output buffer
		BufferedWriter output = new BufferedWriter(new FileWriter(output_file_name));

		// Initialize the wig_chr_arr
		String[] wig_chr_arr = null;

		// Set the wig chromosome array
		if(isYeast){
			wig_chr_arr = new String[16];
			for(int c = 0; c < 16; c++){
				wig_chr_arr[c] = Integer.toString(c+1);
			}	

			// Write the browser header
			MakeWig.write_browser_header_yeast(output);

		}else{
			wig_chr_arr = new String[]{"2L", "2R", "3L", "3R", "X"};

			// Write the browser header
			MakeWig.write_browser_header_dros(output);

		}
		
		
		// Write the overall wig header
		MakeWig.write_wig_header(output, bam_file_id, wig_descr_str, max_y_lim, wig_color);

		// Get the sam file
		SAMFileReader bam_file = new SAMFileReader(new File(bam_file_name), new File(bam_file_name + ".bai"));
		
		// Get the total reads across the main chr
		int total_reads = BAMInput.get_number_aligned_reads(bam_file_name, wig_chr_arr);
		
		// Get the RPKM factor
		double rpkm_denom_fact = ((double) bin_size / 1000) * ((double) total_reads / 1000000);
					
		// Iterate through each chr
		for(String c : wig_chr_arr){
			
			// Write the header
			MakeWig.write_chr_track_header(output, c, bin_size / 2, step_size, step_size);
			
			// Get the chr length
			int chr_length = BAMInput.get_chr_length(bam_file_name, c);
			
			// Create the bin reads array
			BinReads[] br_arr = BinReads.create_bin_reads_array(1, chr_length, step_size, step_size);
			
			// Iterate through each read on the chr
			SAMRecordIterator bam_itr = bam_file.queryOverlapping(c, 1, chr_length);
			
			// Add all the reads to the bin
			add_reads_to_bin(bam_itr, br_arr, chr_length);
			
			// Write the output
			write_wig_output(output, br_arr, chr_length, bin_size, step_size, rpkm_denom_fact);
			
		}
			
		// Close the bam_file
		bam_file.close();
		
		// Close the output buffer
		output.close();
		
	}

}
