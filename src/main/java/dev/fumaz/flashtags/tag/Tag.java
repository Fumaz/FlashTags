package dev.fumaz.flashtags.tag;

import net.md_5.bungee.api.ChatColor;

import java.util.Objects;

public class Tag {

    private final String name;
    private final String display;

    public Tag(String name, String display) {
        this.name = name;
        this.display = ChatColor.translateAlternateColorCodes('&', display);
    }

    public String getName() {
        return name;
    }

    public String getDisplay() {
        return display;
    }

    public String getPermission() {
        return "flashtags.tag." + name;
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
