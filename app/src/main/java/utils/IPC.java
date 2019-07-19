package utils;

import java.io.Serializable;

public class IPC implements Serializable {
	private String message;
	
	public String getMessage() {
		return message;
	}
	
	public IPC setMessage(String message) {
		this.message = message;
		return this;
	}
}
