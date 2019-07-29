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
import flowdroid.Flowdroid;
import models.ComponentTransition;

/** Tester Class - Need to delete later!!!
 * @author - Tanjina Islam
 *
 * @date - 03-07-2019
 */
public class ConfigTest {

	/**
	 * @param args
	 */

	static List<ComponentTransition> componentTransitionGraph_aman;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//		try (OutputStream output = new FileOutputStream("./resources/config.properties")) {
		//
		//            Properties prop = new Properties();
		//
		//            // set the properties value
		//            prop.setProperty("db.url", "localhost");
		//            prop.setProperty("db.user", "mkyong");
		//            prop.setProperty("db.password", "password");
		//
		//            // save properties to project root folder
		//            prop.store(output, null);
		//
		//            System.out.println(prop);
		//
		//        } catch (IOException io) {
		//            io.printStackTrace();
		//        }

		ProjectConfig projectConfig = new ProjectConfig();

		try (InputStream input = new FileInputStream("./resources/config.properties")) {

			Properties prop = new Properties();

			// load a properties file
			prop.load(input);

			// get the property value and print it out

			String pathAndroidJars = prop.getProperty("project.android_jars");
			String inputFileDir = prop.getProperty("project.input_files");
			String outputDir = prop.getProperty("project.result");

			//			try (Stream<Path> walk = Files.walk(Paths.get(filePath))) {
			//
			//				//				List<String> result = walk.filter(Files::isRegularFile)
			//				//						.map(x -> x.toString()).collect(Collectors.toList());
			//				//
			//				//				result.forEach(System.out::println);
			//
			//				List<String> result = walk.map(x -> x.toString())
			//						.filter(f -> f.endsWith(".apk")).collect(Collectors.toList());
			//				
			//				result.forEach(System.out::println);

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
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		Flowdroid.getInstance().initFlowdroid(projectConfig.getPathAndroidJars(), projectConfig.getApkPath(), projectConfig.getIccModelPath());
	}
}
