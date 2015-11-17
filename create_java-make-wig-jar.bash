#!/bin/bash

# Enter the java_project_dir
java_project_dir="/mnt/sdb_mount/alchemy_data/illumina_pipeline/scripts/GitHub/java_make_wig"

# Enter the package name
package_name=""


# Enter the new jar file
jar_file_name="make-wig"

# Set the external jars
genomics_functions_jar="/mnt/sdb_mount/alchemy_data/illumina_pipeline/scripts/java_scripts/lib/genomics-functions.jar"
sam_jar="/mnt/sdb_mount/alchemy_data/illumina_pipeline/scripts/java_scripts/lib/sam-1.67.jar"

# Enter the java_file
java_src_file=($(find ${java_project_dir}/src -type f -name "*.java"))

# Clear the bin directory

# Get the java class files
java_class_file=($(find ${java_project_dir}/bin -type f -name "*.class"))
rm -v ${java_class_file[@]}

# Update the class files
javac -verbose \
-cp ${sam_jar}:${genomics_functions_jar} \
-sourcepath ${java_project_dir}/src \
-d ${java_project_dir}/bin \
${java_src_file[@]}

# Get the Java classes
java_class_file=($(find ${java_project_dir}/bin -name "*.class"))
java_base_name=($(basename -a ${java_class_file[@]}))

# Create the java_class_str
java_class_str=""

for(( i=0; i<${#java_base_name[@]}; i++ ))
do
	java_class_str="$java_class_str -C ${java_project_dir}/bin ${java_base_name[$i]}"
done

echo -e "The java_class_str is\n\t${java_class_str}"

# Create the jar
jar -cvmf \
${java_project_dir}/make-wig.manifest \
/mnt/sdb_mount/alchemy_data/illumina_pipeline/scripts/java_scripts/lib/${jar_file_name}.jar \
$java_class_str
# -C ${java_project_dir}/bin ${java_base_name[0]} -C ${java_project_dir}/bin ${java_base_name[1]}
# ${java_base_name[@]}






# Add the source files to the jar
# jar -vuf \
# /data/illumina_pipeline/scripts/java_scripts/jar_files/${jar_file_name}.jar \
# -C ${java_project_dir}src/ ${package_name}/
