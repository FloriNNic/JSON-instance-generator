package ro.sync.json.instance.generator;

public class JsonGeneratorOptions {
	private String downloadDirectory = null;
	private String uploadSystemID = null;
	private boolean generateRandomValues;
	
	public JsonGeneratorOptions(boolean generateRandomNumbers, String uploadSystemID, String downloadDirectory) {
		this.generateRandomValues = generateRandomNumbers;
		this.uploadSystemID = uploadSystemID;
		this.downloadDirectory = downloadDirectory;
	}
	public JsonGeneratorOptions() {}
	
	public boolean isGenerateRandomValues() {
		return generateRandomValues;
	}
	public String getDownloadDirectory() {
		return downloadDirectory;
	}
	public String getUploadSystemID() {
		return uploadSystemID;
	}
	public void setDownloadDirectory(String downloadDirectory) {
		this.downloadDirectory = downloadDirectory;
	}
	public void setUploadSystemID(String uploadSystemID) {
		this.uploadSystemID = uploadSystemID;
	}
	public void setGenerateRandomValues(boolean generateRandomValues) {
		this.generateRandomValues = generateRandomValues;
	}
	
}
