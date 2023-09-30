package me.kokodevs.botapi;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;

public abstract class DiscordButton extends ListenerAdapter {

    public abstract String getButtonID();

    public abstract Button getButton();

    public List<String> getPermissions() {return List.of();}

    public abstract void onClick(ButtonInteractionEvent e);
    protected String getPermissionMessage() {
        return "You don't have permission to click this button!";
    }
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getButton().getId().equals(getButtonID())) return;
        if (!getPermissions().isEmpty()) {
            boolean hasPermission = false;
            for (Role role : event.getMember().getRoles()) {
                if (getPermissions().contains(role.getName()) || getPermissions().contains(role.getId())) {
                    hasPermission = true;
                    break;
                }
            }
            if (!hasPermission) {
                event.reply(getPermissionMessage()).setEphemeral(true).queue();
                return;
            }
        }
        onClick(event);
    }
}
