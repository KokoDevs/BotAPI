package me.kokodevs.botapi;

import lombok.Data;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public abstract class Bot {

    private final JDA jda;

    private final String name;

    private final Map<String, DiscordButton> buttons;

    public Bot(String token, String name) throws InterruptedException {
        this.name = name;
        this.jda = JDABuilder.createDefault(token).build().awaitReady();
        this.buttons = new HashMap<>();
        this.onEnable();
    }
    public Bot(String token, String name, List<GatewayIntent> gatewayIntents) throws InterruptedException {
        this.name = name;
        this.jda = JDABuilder.createDefault(token)
                .enableIntents(gatewayIntents)
                .build().awaitReady();
        this.buttons = new HashMap<>();
        this.onEnable();
    }

    public abstract void onEnable();
    protected final void setActivity(Activity.ActivityType type, String name) {
        jda.getPresence().setActivity(Activity.of(type, name));
    }
    protected final void registerEventListeners(ListenerAdapter adapter) {
        jda.addEventListener(adapter);
    }

    protected final void registerButton(DiscordButton button) {
        registerEventListeners(button);
        buttons.put(button.getButtonID(), button);
    }

    protected final void registerCommand(SlashCommand command) {
        registerEventListeners(command);
        SlashCommandData data = Commands.slash(command.getCommand(), command.getDescription());
        for (Option option : command.getOptions()) {
            data = data.addOption(option.getOptionType(), option.getName(), option.getDescription(), option.isRequired(), true);
        }
        for (Guild guild : jda.getGuilds())
            guild.upsertCommand(data).queue();
    }
    public final void log(String message) {
        System.out.println(String.format("[%s] %s", name, message));
    }
    public final void onDisable() {
        jda.shutdownNow();
        log("Shutting down....");
    }

}
