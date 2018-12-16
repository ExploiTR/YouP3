package utils;

import java.util.ArrayList;

public class FileHolder extends ArrayList<HolderObject> {

    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_VIDEO = 2;
    public static final int TYPE_VIDEO_SILENT = 3;

    @Override
    public boolean add(HolderObject object) {
        return super.add(object);
    }
}
