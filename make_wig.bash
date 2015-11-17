#!/bin/bash

# Set the parameters
bam_file_name="/mnt/sdb_mount/alchemy_data/illumina_pipeline/aligned_experiments/DM510/dm510.bam"
wig_file_name="/home/jab112/DM510_new.wig"
wig_name="jb"
wig_descript="jb_new"
bin_size=500
step_size=100

java -jar /mnt/sdb_mount/alchemy_data/illumina_pipeline/scripts/java_scripts/lib/make-wig.jar \
$bam_file_name $wig_file_name $wig_name $wig_descript $bin_size $step_size
