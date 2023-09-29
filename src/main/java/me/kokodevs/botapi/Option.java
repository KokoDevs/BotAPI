package me.kokodevs.botapi;

import lombok.Data;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Data
public class Option {
    private final OptionType optionType;
    private final String name;
    private final String description;
    private final boolean required;

    public Option(OptionType optionType, String name, String description, boolean required) {
        this.optionType = optionType;
        this.name = name;
        this.description = description;
        this.required = required;
    }

    public Option(OptionType optionType, String name, String description) {
        this.optionType = optionType;
        this.name = name;
        this.description = description;
        this.required = false;
    }
}
