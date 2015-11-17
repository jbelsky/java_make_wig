import genomics_functions.BAMInput;
import genomics_functions.BinReads;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;


public class MakeBinBedGraphFromBAM {

	public static void 	WriteBedGraphOutput(BufferedWriter output, BinReads[] br_arr, int bin_size, int step_size,
											String chr, int chr_length, double rpkm_denom_fact) throws IOException{
	
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
				
				// Note: bedGraph is actually 0-based, so need N-1 here
				end_bin_pos = chr_length - 1;
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
			
			// Get the updated bin coordinates
			double start_bin_coord = (double) br_arr[start_b_idx].getStart();
			double end_bin_coord = (double) br_arr[start_b_idx].getEnd();
			
			double bin_midpoint_pos = (start_bin_coord + end_bin_coord) / 2;
			
			// Write the output
			output.write(chr + "\t" + 
						 bin_midpoint_pos + "\t" + 
						 bin_midpoint_pos + "\t" +
						 df.format(bin_read) + "\n"
						);
								
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

		// Check that the arguments are correct
		if(args.length != 6){
			
			System.out.print("Arg0:\tInput Bam File Name\n" +
							 "Arg1:\tOutput BedGraph File Name\n" +
							 "Arg2:\tBedGraph Name\n" +
							 "Arg3:\tBedGraph Description\n" +
							 "Arg4:\tBin Size (bp)\n" +
							 "Arg5:\tStep Size (bp)\n"
							);
			
		// Check that the Bam file exists
		}else if(!(new File(args[0])).exists()){
			
			System.out.println(args[0] + " is not found on system!");
			
		}else{


			// Set the BAM File Name
			String bam_file_name = args[0];
			String wig_file_name = args[1];
			String wig_name = args[2];
			String wig_descrip = args[3];
			int bin_size = Integer.parseInt(args[4]);
			int step_size = Integer.parseInt(args[5]);

			// Echo the input parameters
			System.out.print("Input Bam File Name:\t" + bam_file_name + "\n" +
							 "Output BedGraph File Name:\t" + wig_file_name + "\n" +
							 "BedGraph Name:\t\t" + wig_name + "\n" +
							 "BedGraph Description:\t" + wig_descrip + "\n" +
							 "Bin Size (bp):\t\t" + bin_size + "\n" +
							 "Step Size (bp):\t\t" + step_size + "\n"
							);
									
			// Open the output buffer
			BufferedWriter output = new BufferedWriter(new FileWriter(wig_file_name));

			// Write the browser header
			output.write(MakeWig.GetBrowserHeaderYeast());

			// Write the wig track header
			output.write(MakeWig.GetBedGraphHeader(wig_name, wig_descrip));

			
			
			
			// Open the bam file
			SAMFileReader bam_file = new SAMFileReader(new File(bam_file_name), new File(bam_file_name + ".bai"));
			
			// Get the total reads across the main chr
			int total_reads = BAMInput.get_number_aligned_reads(bam_file_name);
			
			// Get the RPKM factor
			double rpkm_denom_fact = ((double) bin_size / 1000) * ((double) total_reads / 1000000);
			
			
			
			// Get the chromosome list
			ArrayList<String> chr_list = BAMInput.get_chr_list(bam_file_name);
			
			
			
			// Iterate through each chr
			for(String chr : chr_list){

				// Echo the chromosome
				System.out.println("Finding the reads on chromosome " + chr + "...");
				
				// Write the header
				output.write(MakeWig.GetChrTrackHeader(chr, 1, step_size, step_size));
				
				// Get the chr length
				int chr_length = BAMInput.get_chr_length(bam_file_name, chr);
				
				// Create the bin reads array
				BinReads[] br_arr = BinReads.create_bin_reads_array(1, chr_length, step_size, step_size);
				
				// Iterate through each read on the chr
				SAMRecordIterator bam_itr = bam_file.queryOverlapping(chr, 1, chr_length);
				
				// Add all the reads to the bin
				add_reads_to_bin(bam_itr, br_arr, chr_length);
								
				// Write the output
				WriteBedGraphOutput(output, br_arr, bin_size, step_size, chr, chr_length, rpkm_denom_fact);
				
			}
				
			// Close the bam_file
			bam_file.close();
			
			// Close the output buffer
			output.close();

			System.out.println("\tComplete!");

		}
		
	}

}
