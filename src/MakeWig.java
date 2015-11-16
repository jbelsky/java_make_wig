import java.io.BufferedWriter;
import java.io.IOException;

public class MakeWig {

	// Write the browser header
	public static void write_browser_header_dros(BufferedWriter output) throws IOException{
		
		String header = "browser position chr2L:1-5,000,000\n" +
						"browser hide all\n" +
						"browser squish flyBaseGene\n";
		
		output.write(header);
		
	}

	// Write the yeast browser header
	public static void write_browser_header_yeast(BufferedWriter output) throws IOException{
		
		String header = "browser position chrI:1-50,000\n" +
						"browser hide all\n" +
						"browser pack sgdGene\n";
		
		output.write(header);
		
	}
	
	// Write the overall wig header
	public static void write_wig_header(BufferedWriter output, String wig_name, String wig_desc, 
										String upper_view_limit, String track_color) throws IOException{
		
		// Create the wig header string
		String wig_header_str = "track type=wiggle_0 name=" + wig_name + 
								" description='" + wig_desc + 
								"' visibility=full autoScale=off viewLimits=0:" + upper_view_limit + 
								" color=" + track_color + "\n";
		
		output.write(wig_header_str);
		
	}
	
	// Write the chromosomal track
	public static void write_chr_track_header(BufferedWriter output, String chr, 
											  int start_pos, int step_size, int span_size) throws IOException{
		
		// Get the Roman Chromosomal equivalent
		if(chr.matches("\\d+")){
		
				chr = ChrNameFunctions.get_ucsc_chr_names(Integer.parseInt(chr));
		
		}
				
		// Write the header
		String chr_header_str = "fixedStep chrom=chr" + chr +
								" start=" + start_pos + 
								" step=" + step_size +
								" span=" + span_size + "\n";
		
		output.write(chr_header_str);
		
	}
	
}
