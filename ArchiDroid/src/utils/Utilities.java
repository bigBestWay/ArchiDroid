package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author - Tanjina Islam
 *
 * @date - 23-06-2019
 */
public class Utilities {

	public static enum CompType{
		Activity, Service, Receiver, Provider, Fragment, PlainJava
	}

	public static enum LinkType{
		ICC, Direct, ParentChild, FragmentLink
	}
	
	public static enum Style{
		IMPLICIT, EXPLICIT
	}
	
	public static enum ICCConfig{
		ENABLE, DISABLE
	}

	static ProjectConfig projectConfig;

	public static ProjectConfig loadConfig() {

		projectConfig = new ProjectConfig();

		try (InputStream input = new FileInputStream("./resources/config.properties")) {

			Properties prop = new Properties();

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			
			String iccConfig = prop.getProperty("project.icc_cofig");
			String pathAndroidJars = prop.getProperty("project.android_jars");
			String inputFileDir = prop.getProperty("project.input_files");
			String outputDir = prop.getProperty("project.result");

			projectConfig.setIccConfig(iccConfig);
			System.out.println("ICC Configuration set as -> " + iccConfig);
			projectConfig.setPathAndroidJars(pathAndroidJars);
			System.out.println("Path to Android SDK Jars -> " + pathAndroidJars);
			projectConfig.setInputFileDir(inputFileDir);
			System.out.println("Input Directory -> " + inputFileDir);
			projectConfig.setOutputDir(outputDir);
			System.out.println("Output Directory -> " + outputDir);
			try {

				List<File> filesInFolder = Files.walk(Paths.get(inputFileDir))
						.filter(Files::isRegularFile)
						.map(Path::toFile)
						.collect(Collectors.toList());

				for(File f : filesInFolder) {
					if(f.getName().endsWith(".apk")) {
						System.out.println("Apk file -> " + f.toString());
						projectConfig.setApkPath(f.toString());

					}
					if(f.getName().endsWith(".xml")) {
						System.out.println("Amandroid file -> " + f.getName());
						projectConfig.setFilePathAmandroid(f.toString());
						//						componentTransitionGraph_aman = XmlParser.getInstance().parseXml(f.toString());
						//						PrintMethods.printComponentTransitionList("ConfigTest ", componentTransitionGraph_aman);
					}
					if(f.getName().endsWith(".txt")) {
						System.out.println("ICC Model file -> " + f.getName());
						projectConfig.setIccModelPath(f.toString());
					}
				}

				// check for output directory and if not present create the directory
				Path path = Paths.get(outputDir);
				// Check If dir exists
				if(! Files.exists(path)) {
					try {
						Files.createDirectories(path);
					} catch (IOException e) {
						e.printStackTrace();
						// Failed to create directory
						System.out.println("Output directory not found!");
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return projectConfig;
		//		Flowdroid.getInstance().initFlowdroid(projectConfig.getPathAndroidJars(), projectConfig.getApkPath(), projectConfig.getIccModelPath());
	}


}
