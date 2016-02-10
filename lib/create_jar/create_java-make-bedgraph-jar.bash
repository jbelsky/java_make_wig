#!/bin/bash

# Enter the parameters
java_project_dir="/data/scripts/java_make_wig"
package_name=""																		# Enter the java package name
java_src_dir="${java_project_dir}/src/${package_name}"								# Set the Java src directory
java_class_dir="${java_project_dir}/bin/${package_name}"							# Set the Java bin directory
jar_file_name="${java_project_dir}/lib/make-bedGraph.jar"							# Enter the output jar file name
manifest_file_name="${java_project_dir}/lib/create_jar/make-bedGraph.manifest"		# Enter the manifest file name

# Set the external jars
genomics_functions_jar="/data/java-library/genomics-functions.jar"
sam_jar="/data/java-library/sam-1.67.jar"

# Enter the java_file
java_src_file=($(find $java_src_dir -type f -name "*.java"))





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
java_class_file=($(find $java_class_dir -name "*.class"))
java_base_name=($(basename -a ${java_class_file[@]}))

# Create the java_class_str
java_class_str=""

for(( i=0; i<${#java_base_name[@]}; i++ ))
do
	java_class_str="$java_class_str -C ${java_project_dir}/bin ${package_name}/${java_base_name[$i]}"
done

echo -e "The java_class_str is\n\t${java_class_str}"

# Create the jar
jar -cvmf $manifest_file_name $jar_file_name $java_class_str
