import genomics_functions.ChrNameFunctions;


public class MakeWig {

	// Write the browser header
	public static String get_browser_header_dros(){
		
		String header = "browser position chr2L:1-5,000,000\n" +
						"browser hide all\n" +
						"browser squish flyBaseGene\n";
		
		return(header);
		
	}

	// Write the yeast browser header
	public static String GetBrowserHeaderYeast(){
		
		String header = "browser position chrIV:1-500,000\n" +
						"browser hide all\n" +
						"browser pack sgdGene\n";
		
		return(header);
		
	}
	
	// Write the overall wig header
	public static String GetWigHeader(String wig_name, String wig_desc){
		
		// Create the wig header string
		String wig_header_str = "track type=wiggle_0 name=" + wig_name + 
								" description='" + wig_desc + 
								"' visibility=full autoScale=on " +  
								" color=0,0,0\n";
		
		return(wig_header_str);
		
	}
	
	// Write the overall bedGraph header
	public static String GetBedGraphHeader(String name, String desc){
			
		// Create the wig header string
		String header_str = "track type=bedGraph name=\"" + name + "\"" + 
							" description=\"" + desc + "\"" + 
							" visibility=full autoScale=on color=0,0,0\n";
			
		return(header_str);
			
	}
	
	// Write the chromosomal track
	public static String GetChrTrackHeader(String chr, int start_pos, int step_size, int span_size){
		
		// Get the Roman Chromosomal equivalent
		if(chr.matches("\\d+")){
		
				chr = ChrNameFunctions.get_ucsc_chr_names(Integer.parseInt(chr));
		
		}
				
		// Write the header
		String chr_header_str = "fixedStep chrom=chr" + chr +
								" start=" + start_pos + 
								" step=" + step_size +
								" span=" + span_size + "\n";
		
		return(chr_header_str);
		
	}
	
}
