package utils;

/**
 * @author - Tanjina Islam
 *
 * @date - 04-07-2019
 */
public class ProjectConfig {

	private String pathAndroidJars;
	private String inputFileDir;
	private String outputDir;
	private String apkPath;
	private String iccModelPath;
	private String filePathAmandroid;
	private String iccConfig;

	/**
	 * @return the iccConfig
	 */
	public String getIccConfig() {
		return iccConfig;
	}
	/**
	 * @param iccConfig the iccConfig to set
	 */
	public void setIccConfig(String iccConfig) {
		this.iccConfig = iccConfig;
	}
	public String getPathAndroidJars() {
		return pathAndroidJars;
	}
	public void setPathAndroidJars(String pathAndroidJars) {
		this.pathAndroidJars = pathAndroidJars;
	}
	public String getInputFileDir() {
		return inputFileDir;
	}
	public void setInputFileDir(String inputFileDir) {
		this.inputFileDir = inputFileDir;
	}
	public String getOutputDir() {
		return outputDir;
	}
	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}
	public String getApkPath() {
		return apkPath;
	}
	public void setApkPath(String apkPath) {
		this.apkPath = apkPath;
	}
	public String getIccModelPath() {
		return iccModelPath;
	}
	public void setIccModelPath(String iccModelPath) {
		this.iccModelPath = iccModelPath;
	}
	public String getFilePathAmandroid() {
		return filePathAmandroid;
	}
	public void setFilePathAmandroid(String filePathAmandroid) {
		this.filePathAmandroid = filePathAmandroid;
	}
}
