package dev.fumaz.flashtags.tag;

import java.util.Objects;

public class Tag {

    private final String name;
    private final String display;

    public Tag(String name, String display) {
        this.name = name;
        this.display = display;
    }

    public String getName() {
        return name;
    }

    public String getDisplay() {
        return display;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name) && Objects.equals(display, tag.display);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, display);
    }


}
