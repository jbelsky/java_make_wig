#!/bin/bash

# Update the class files
# javac -extdirs /data/illumina_pipeline/scripts/output_wig_scripts/ \
#-sourcepath /data/illumina_pipeline/scripts/output_wig_scripts/src/ \
#-d /data/illumina_pipeline/scripts/output_wig_scripts/class/ \
#/data/illumina_pipeline/scripts/output_wig_scripts/src/MakeBinWigFromBAM.java \

# Update the jar
# jar -cvfm /data/illumina_pipeline/scripts/output_wig_scripts/make_wig.jar \
# /data/illumina_pipeline/scripts/output_wig_scripts/make_wig.manifest \
# -C /data/illumina_pipeline/scripts/output_wig_scripts/class \
# *.class

# Set the parameters
bam_file_dir="/data/illumina_pipeline/aligned_experiments/"
output_file_dir="/home/jab112/public_html/ucsc_genome_browser_tracks/yeast/mcm/"
bin_size="500"
step_size="500"
wig_color="0,0,0"
max_y_lim="1000"
isYeast="true"

# Set the samples
id=($(seq 82 1 82))
wig_descr_arr=("DM82_mcm"
	      )

for(( i=0; i<${#id[@]}; i++ ))
do

	# Set the variable parameters
	# bam_file_name=${bam_file_dir}DM${id[$i]}/dm${id[$i]}.bam
	bam_file_name="/home/hkm/hkm_seq/DM82/DM82_sorted.bam"
	output_file_name=${output_file_dir}/dm${id[$i]}.wig
	bam_file_id="DM${id[$i]}_jb"
	wig_descr_str=${wig_descr_arr[$i]}

	echo "Making the wig for $wig_descr_str..."

	java -jar /data/illumina_pipeline/scripts/output_wig_scripts/lib/make_wig.jar \
	$bam_file_name $output_file_name $bin_size $step_size $bam_file_id $wig_descr_str $wig_color $max_y_lim $isYeast

done
