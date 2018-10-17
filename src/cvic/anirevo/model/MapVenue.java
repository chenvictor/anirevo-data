package cvic.anirevo.model;

public class MapVenue {

    private NameChangeListener mListener;

    private String name;
    private String imagePath;

    public MapVenue(String name) {
        this.name = name;
    }

    public void setListener(NameChangeListener listener) {
        mListener = listener;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setName(String name) {
        this.name = name;
        if (mListener != null) {
            mListener.venueNameChanged();
        }
    }

    public void setImagePath (String path) {
        imagePath = path;
    }

    public interface NameChangeListener {

        void venueNameChanged();

    }

}