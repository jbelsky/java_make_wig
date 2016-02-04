#!/bin/bash

# Set the begraph parameters
bin_size=500
step_size=100

# Set the UCSC out file
ucsc_out_file="/home/jab112/public_html/ucsc_genome_browser_tracks/bedgraph_tracks/DM616-DM627_bedgraph.ucsc"

# Set the dm_array
dm_arr=($(seq 616 627))

# Set the dm_desc
dm_desc_arr=(
			 "Ino80 120' Ctrl" 
			 "Ino80 120' AA" 
			 "Sth1 120' Ctrl" 
			 "Sth1 120' AA"
			 "Ino80 60' Ctrl" 
			 "Ino80 60' AA" 
			 "Sth1 60' Ctrl" 
			 "Sth1 60' AA"
			 "Snf2 60' Ctrl" 
			 "Snf2 60' AA"
			 "Snf2 120' Ctrl" 
			 "Snf2 120' AA"
			)

# Iterate through each bedgraph
for(( i=0; i<${#dm_arr[@]}; i++ ))
do

	# Get the DM id
	dm_id=${dm_arr[$i]}

	# Update the parameters
	bam_file_name="/data/illumina_pipeline/aligned_experiments/DM${dm_id}/DM${dm_id}_-m1_sacCer2_numChr.bam"
	bedgraph_file_name="/home/jab112/public_html/ucsc_genome_browser_tracks/bedgraph_tracks/DM${dm_id}_-m1.bedgraph"
	bedgraph_name="DM${dm_id}"
	bedgraph_descript=${dm_desc_arr[$i]}

	# Create the bedgraph
	java -jar /opt/jar_files/make-bedGraph.jar $bam_file_name $bedgraph_file_name $bedgraph_name "$bedgraph_descript" $bin_size $step_size

	# Write the bedgraph file location to a UCSC file
	echo $bedgraph_file_name >> $ucsc_out_file

done
