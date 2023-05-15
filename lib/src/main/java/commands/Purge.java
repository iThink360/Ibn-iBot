/** Event listener for purging messages in a server. */

package commands;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nonnull;

import main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Purge extends ListenerAdapter
{
	private static final Color PURGE_EMBED = new Color(0x00ffaa);
	
	// Standard command
	@Override
	public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
	{
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		Member sender = event.getMember();
		
		if (!args[0].equalsIgnoreCase(Main.getPrefix() + "purge"))
			return;
		
		if (!sender.isOwner() && !sender.hasPermission(Permission.ADMINISTRATOR) && !sender.hasPermission(Permission.MESSAGE_MANAGE))
		{
			// Denied
			EmbedBuilder denied = new EmbedBuilder()
					.setColor(PURGE_EMBED)
					.setDescription("❌ " + sender.getUser().getAsTag() + ", nice try. You can't use that.");
			event.getChannel().sendMessage(sender.getAsMention()).setEmbeds(denied.build()).queue();
			return;
		}
		
		if (args.length != 2)
			return;
		
		if (args[1].equalsIgnoreCase("help"))
		{
			// Help
			EmbedBuilder help = new EmbedBuilder()
					.setColor(PURGE_EMBED)
					.setTitle("🧼 Purge")
					.setDescription("Purges a specified number of messages.")
					.addField("Command:", "`" + Main.getPrefix() + "purge [# of messages]`", false)
					.addField("`[# of messages]`", "-The amount of messages desired to be purged (must be between 2 and 100, inclusive).\n-Messages must not be older than 2 weeks.\n-Omit [] when specifying this value.", true)
					.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
					.setFooter(event.getGuild().getName(), event.getGuild().getIconUrl());
			event.getChannel().sendMessageEmbeds(help.build()).queue();
			return;
		}
		
		try
		{
			// Success
			int count = Integer.parseInt(args[1]);
			
			event.getMessage().delete().complete();
			List<Message> messages = event.getChannel().getHistory().retrievePast(count).complete();
			event.getChannel().deleteMessages(messages).queue();
		}
		catch (Exception e)
		{
			// Error
			EmbedBuilder error = new EmbedBuilder()
					.setColor(PURGE_EMBED)
					.setTitle("❗ Purge Error")
					.setDescription("You may have used the command incorrectly. Please try again. For help on purging messages, send `" + Main.getPrefix() + "purge help`.")
					.addField("Exception Thrown:", e.toString(), false);
			event.getChannel().sendMessageEmbeds(error.build()).queue();
		}
	}
}
