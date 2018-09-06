package cvic.anirevo.model.anirevo;


import cvic.anirevo.Log;

public enum AgeRestriction {

    AGE_RESTRICTION_13(13, -1),
    AGE_RESTRICTION_18(18, -1);

    private static final String TAG = "anirevo.AgeRes";

    private final int age;
    private final int textColor;

    AgeRestriction(int age, int color) {
        this.age = age;
        this.textColor = color;
    }

    @Override
    public String toString() {
        return String.valueOf(age) + "+";
    }

    public int getTextColor() {
        return textColor;
    }

    /**
     * Compares two age restrictions
     * @param otherRestriction  restriction to compare with
     * @return  true if this restriction age >= other restriction age
     */
    public boolean allowsFor(AgeRestriction otherRestriction) {
        return otherRestriction == null || this.age >= otherRestriction.age;
    }

    public static AgeRestriction getRestriction(int age) {
        switch (age) {
            case 13: return AGE_RESTRICTION_13;
            case 18: return AGE_RESTRICTION_18;
            default:
                Log.i(TAG, "AgeRestriction " + age + " is not valid.");
                return null;
        }
    }

}
