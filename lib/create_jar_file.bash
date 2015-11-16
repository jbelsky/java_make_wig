#!/bin/bash

# Enter the java_project_dir
java_project_dir="/mnt/sdb_mount/alchemy_data/illumina_pipeline/scripts/output_wig_scripts"

# Enter the package name
package_name="twodimplot"

# Enter the java_file
java_file_name='"*.java"'
java_file=$(find ${java_project_dir}/src -type f -name "*.java")

# Enter the new jar file
jar_file_name="make-wig-harpoon"

# Set the external jars
jbfunctions_jar="/mnt/sdb_mount/alchemy_data/illumina_pipeline/scripts/java_scripts/jar_files/jbfunctions.jar"
sam_jar="/mnt/sdb_mount/alchemy_data/illumina_pipeline/scripts/java_scripts/jar_files/sam-1.67.jar"

# Update the class files
# javac -verbose \
# -cp ${jbfunctions_jar}:${sam_jar} \
# -sourcepath ${java_project_dir}/src \
# -d ${java_project_dir}/bin \
# ${java_file[@]}

# Get the class files
java_class_file=$(find ${java_project_dir}/bin -type f -name "*.class")

# Create the jar
jar -cvmf \
${java_project_dir}/lib/make_wig.manifest \
/mnt/sdb_mount/alchemy_data/illumina_pipeline/scripts/output_wig_scripts/lib/${jar_file_name}.jar \
-C ${java_project_dir}/bin \
${java_class_file[@]}

# Add the source files to the jar
# jar -vuf \
# /data/illumina_pipeline/scripts/java_scripts/jar_files/${jar_file_name}.jar \
# -C ${java_project_dir}src/ ${package_name}/
