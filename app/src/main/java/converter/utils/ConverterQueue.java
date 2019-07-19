package converter.utils;

import java.util.ArrayList;

public class ConverterQueue {
	
	private static ConverterQueue instance;
	private ArrayList<ConverterObject> list;
	
	private ConverterQueue() {
		list = new ArrayList<>();
	}
	
	public static ConverterQueue getInstance() {
		if (instance != null && instance.list != null) {
			return instance;
		} else {
			instance = new ConverterQueue();
			return instance;
		}
	}
	
	public void add(ConverterObject object) {
		list.add(object);
	}
	
	void remove(ConverterObject object) {
		list.remove(object);
	}
	
	ConverterObject[] getAvailableQueues() {
		return list.toArray(new ConverterObject[0]);
	}
}
