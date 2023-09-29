package me.kokodevs.botapi;

import lombok.Data;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.HashMap;
import java.util.Map;

@Data
public abstract class Bot {

    private final JDA jda;

    private final Map<String, DiscordButton> buttons;

    public Bot(String token) throws InterruptedException {
        jda = JDABuilder.createDefault(token).build().awaitReady();
        buttons = new HashMap<>();
        this.onEnable();
    }

    public abstract void onEnable();
    protected void setActivity(Activity.ActivityType type, String name) {
        jda.getPresence().setActivity(Activity.of(type, name));
    }
    protected void registerEventListeners(ListenerAdapter adapter) {
        jda.addEventListener(adapter);
    }

    protected void registerButton(DiscordButton button) {
        registerEventListeners(button);
        buttons.put(button.getButtonID(), button);
    }

    protected void registerCommand(SlashCommand command) {
        registerEventListeners(command);
        SlashCommandData data = Commands.slash(command.getCommand(), command.getDescription());
        for (Option option : command.getOptions()) {
            data = data.addOption(option.getOptionType(), option.getName(), option.getDescription(), option.isRequired(), true);
        }
        for (Guild guild : jda.getGuilds())
            guild.updateCommands().addCommands(data).queue();
    }
    public final void onDisable() {
        jda.shutdownNow();
        System.out.println("Shutting down....");
    }

}
