package cvic.anirevo.model;

public class ArShow {

    private TitleChangeListener mListener;

    private String title;
    private int age = 0;
    private String desc;

    public ArShow (String title) {
        this.title = title;
    }

    public void setListener(TitleChangeListener listener) {
        mListener = listener;
    }

    public void setTitle (String title) {
        this.title = title;
        if (mListener != null) {
            mListener.titleChanged(title);
        }
    }

    public void setAge (int age) {
        this.age = age;
    }

    public void setDescription (String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public int getAge() {
        return age;
    }

    public String getDescription() {
        return desc;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    public interface TitleChangeListener {

        void titleChanged(String newTitle);

    }

}
