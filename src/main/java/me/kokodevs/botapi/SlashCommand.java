package me.kokodevs.botapi;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SlashCommand extends ListenerAdapter {

    protected final Bot bot;

    public SlashCommand(Bot jda) {
        this.bot = jda;
    }

    public abstract String getCommand();
    public abstract String getDescription();
    public List<Option> getOptions() {return List.of();}
    public List<String> getPermissions() {return List.of();}
    public abstract void execute(SlashCommandInteractionEvent e, Map<String, Object> options);

    protected String getPermissionMessage() {return "You don't have permission to execute this command!";}

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (e.getFullCommandName().equals(getCommand())) {
            if (!getPermissions().isEmpty()) {
                boolean hasPermission = false;
                for (Role role : e.getMember().getRoles()) {
                    if (getPermissions().contains(role.getName()) || getPermissions().contains(role.getId())) {
                        hasPermission = true;
                        break;
                    }
                }
                if (!hasPermission) {
                    e.reply(getPermissionMessage()).setEphemeral(true).queue();
                    return;
                }
            }
            Map<String, Object> options = new HashMap<>();
            for (Option option : getOptions())
                options.put(option.getName(), getOptionObject(option.getOptionType(), e.getOption(option.getName())));
            execute(e, options);
        }
    }

    private Object getOptionObject(OptionType type, OptionMapping option) {
        return switch (type) {
            case UNKNOWN, SUB_COMMAND, SUB_COMMAND_GROUP, STRING -> option.getAsString();
            case INTEGER, NUMBER -> option.getAsInt();
            case BOOLEAN -> option.getAsBoolean();
            case USER -> option.getAsMember();
            case CHANNEL -> option.getAsChannel();
            case ROLE -> option.getAsRole();
            case MENTIONABLE -> option.getAsMentionable();
            case ATTACHMENT -> option.getAsAttachment();
        };
    }
}
