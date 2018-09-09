package module;

/**
 * @author airsaid
 */
public class AndroidString {

    private String name;
    private String value;
    private boolean translatable;

    public AndroidString(String name, String value, boolean translatable) {
        this.name = name;
        this.value = value;
        this.translatable = translatable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isTranslatable() {
        return translatable;
    }

    public void setTranslatable(boolean translatable) {
        this.translatable = translatable;
    }

    @Override
    public String toString() {
        return "AndroidString{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", translatable=" + translatable +
                '}';
    }
}
