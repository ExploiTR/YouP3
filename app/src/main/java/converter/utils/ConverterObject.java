package converter.utils;


import java.io.File;

import cafe.adriel.androidaudioconverter.model.AudioFormat;

public class ConverterObject {
	private AudioFormat format;
	private File file;
	private File outputDir;
	private boolean isCompleted;
	
	AudioFormat getFormat() {
		return format;
	}
	
	public ConverterObject setFormat(AudioFormat format) {
		this.format = format;
		return this;
	}
	
	public File getFile() {
		return file;
	}
	
	public ConverterObject setFile(File file) {
		this.file = file;
		return this;
	}
	
	File getOutputDir() {
		return outputDir;
	}
	
	public ConverterObject setOutputDir(File outputDir) {
		this.outputDir = outputDir;
		return this;
	}
	
	public boolean isCompleted() {
		return isCompleted;
	}
	
	public ConverterObject setCompleted(boolean completed) {
		isCompleted = completed;
		return this;
	}
}
